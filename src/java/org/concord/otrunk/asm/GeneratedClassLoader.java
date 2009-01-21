package org.concord.otrunk.asm;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.otrunk.AbstractOTObject;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class GeneratedClassLoader extends ClassLoader
{	
	public GeneratedClassLoader(ClassLoader parent)
	{
		super(parent);
	}
	
	@SuppressWarnings("unchecked")
    public Class<? extends AbstractOTObject> generateClass(Class<? extends OTObject> abstractClass, OTClass otClass) throws IOException {
		String name = abstractClass.getName() + "_Generated";
		Class<? extends AbstractOTObject> existingClass = 
			(Class<? extends AbstractOTObject>)findLoadedClass(name);
		if(existingClass != null){
			return existingClass;
		}
		
		URL resource = abstractClass.getClassLoader().getResource(abstractClass.getName().replace(".","/") + ".class");
				
		InputStream resourceAsStream = resource.openStream();
		
		ClassWriter cw = new ClassWriter(0);
//		TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out)); 
		ClassAdapter ca = new ImplementOTClassAdapter(cw, abstractClass, otClass);
//		TraceClassVisitor cv2 = new TraceClassVisitor(ca, new PrintWriter(System.out)); 
		ClassReader cr = new ClassReader(resourceAsStream);
		 
		cr.accept(ca, 0);
		byte[] b = cw.toByteArray(); 
		Class<? extends AbstractOTObject> klass = (Class<? extends AbstractOTObject>) defineClass(name, b, 0, b.length);
		
		// initialize the static fields with the OTClassProperties
		ArrayList<OTClassProperty> allClassProperties = otClass.getOTAllClassProperties();
		for (OTClassProperty classProperty : allClassProperties) {
	        String staticFieldName = ImplementOTClassAdapter.getStaticFieldName(classProperty.getName());
	        try {
	            Field declaredField = klass.getDeclaredField(staticFieldName);
	            declaredField.set(null, classProperty);
            } catch (SecurityException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            continue;
            } catch (NoSuchFieldException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            continue;
            } catch (IllegalArgumentException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (IllegalAccessException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }	        
        }
				
		return klass;
	} 
}
