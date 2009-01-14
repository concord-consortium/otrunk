package org.concord.otrunk.asm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class GeneratedClassLoader extends ClassLoader
{	
	public Class generateClass(Class abstractClass) throws IOException {
		String name = abstractClass.getName() + "_Generated";
		Class<?> existingClass = findLoadedClass(name);
		if(existingClass != null){
			return existingClass;
		}
		
		URL resource = abstractClass.getClassLoader().getResource(abstractClass.getName().replace(".","/") + ".class");
				
		InputStream resourceAsStream = resource.openStream();
		
		ClassWriter cw = new ClassWriter(0);
//		TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out)); 
		ClassAdapter ca = new ImplementOTClassAdapter(cw, abstractClass);
//		TraceClassVisitor cv2 = new TraceClassVisitor(ca, new PrintWriter(System.out)); 
		ClassReader cr = new ClassReader(resourceAsStream);
		 
		cr.accept(ca, 0);
		byte[] b = cw.toByteArray(); 
		return defineClass(name, b, 0, b.length); 
	} 
}
