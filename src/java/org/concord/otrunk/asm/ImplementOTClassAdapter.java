package org.concord.otrunk.asm;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.otrunk.AbstractOTObject;
import org.concord.otrunk.OTInvocationHandler;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ImplementOTClassAdapter extends ClassAdapter
	implements Opcodes
{	
	private static final int UNKNOWN = 0;
	private static final int GETTER = 1;
	private static final int SETTER = 2;

	private String className;
	private Class abstractClass;
	private HashMap<String, String> internalMethodMap;
	private String parentName;
	private OTClass otClass;
	
	public ImplementOTClassAdapter(ClassVisitor cv, Class abstractClass, OTClass otClass)
    {
	    super(cv);
	    this.abstractClass = abstractClass;
	    this.otClass = otClass;
		Method[] internalMethods = AbstractOTObject.class.getMethods();
		internalMethodMap = new HashMap<String, String>();
		for (Method method : internalMethods) {
	        internalMethodMap.put(method.getName(), "");
        }
    }

	@Override
	public void visit(int version, int access, String name, String signature,
	    String superName, String[] interfaces)
	{
		className = name + "_Generated";
		String [] updatedInterfaces = null;
		if((access & ACC_INTERFACE) == 0){
			// this should be an abstract class
			parentName = name;			
		} else {
			// this is an interface
			parentName = "org/concord/otrunk/AbstractOTObject";
			updatedInterfaces = new String[] { name };
		}
		cv.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, null, 
			parentName, updatedInterfaces);

	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
	    String signature, String[] exceptions)
	{
		return null;
	}
	
	public void processMethod(Method method)
	{
		if(!Modifier.isAbstract(method.getModifiers())){
			// skip this method
			// System.out.println("skipping non abstract method: " + className + "." + method.toString());
			return;
		}

		// check if OTOjectInternal implements this method
		if(internalMethodMap.get(method.getName()) != null){
			// skip this method
			// System.out.println("skipping already implemented method: " + className + "." + method.toString());
			return;			
		}
		
		String name = method.getName();
		String desc = Type.getMethodDescriptor(method);
		Type returnType = Type.getReturnType(desc);
		Type[] argumentTypes = Type.getArgumentTypes(desc);
		
		MethodVisitor mv;

		int type = UNKNOWN;
		String propertyName  = "";
		if(name.startsWith("get")){
			type = GETTER;
			propertyName = OTInvocationHandler.getResourceName(3, name);
		} else if(name.startsWith("is")){
			type = GETTER;
			propertyName = OTInvocationHandler.getResourceName(2, name);			
		} else if(name.startsWith("_get")){
			type = GETTER;
			propertyName = OTInvocationHandler.getResourceName(4, name);			
		} else if(name.startsWith("_is")){
			type = GETTER;
			propertyName = OTInvocationHandler.getResourceName(3, name);			
		} else if(name.startsWith("set")){
			type = SETTER;
			propertyName = OTInvocationHandler.getResourceName(3, name);	
		} else if(name.startsWith("_set")){
			type = SETTER;
			propertyName = OTInvocationHandler.getResourceName(4, name);	
		}
		
		if(type == SETTER){
			if(returnType != Type.VOID_TYPE){
				System.err.println("setter has a non void return type: " + method.toString());
				// don't process this method, which should create a semi valid class that has
				// an abtract method in it.
				return;
			}

			if(argumentTypes.length != 1){
				System.err.println("setter wrong number of arguments: " + method.toString());
				// don't process this method, which should create a semi valid class that has
				// an abtract method in it.
				return;				
			}
		}

		if(type == UNKNOWN){
			System.err.println("unknown abstract method: " + method.toString() );
			return;			
		}

		// verify this property is a valid class property
		OTClassProperty classProperty = otClass.getProperty(propertyName);
		if(classProperty == null){
			System.err.println("unknown classProperty: " + propertyName + 
				" method: " + method.toString() );
			return;			
		}
		
		// figure out the field name with this class prop		
		String staticFieldName = getStaticFieldName(propertyName);
				
		int modifiers = ACC_PUBLIC;
			
		// allow protected methods if the name starts with _
		if(Modifier.isProtected(method.getModifiers())){
			if(name.startsWith("_")){
				modifiers = ACC_PROTECTED;
			} else {
				// error
				System.err.println("OTrunk property method cannot be protected unless it starts " +
					" with '_'.  method: " + method.toString() );
				return;			
			}
		}
		
		mv = cv.visitMethod(modifiers, name, desc, null, null);
		
		if(type == GETTER){
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			
			mv.visitFieldInsn(GETSTATIC, className, staticFieldName, "Lorg/concord/framework/otrunk/otcore/OTClassProperty;");
						
			Class<?> boxedType = boxedType(returnType);
			if(boxedType == null){
				// asm handles putting .classS on the stack for non primitives
				mv.visitLdcInsn(returnType);
			} else {
				// for primitives the static .TYPE field of the boxedType has to be used instead
				mv.visitFieldInsn(GETSTATIC, Type.getInternalName(boxedType), "TYPE", "Ljava/lang/Class;");
			}

			mv.visitMethodInsn(INVOKEVIRTUAL, "org/concord/otrunk/AbstractOTObject", "getResourceChecked", "(Lorg/concord/framework/otrunk/otcore/OTClassProperty;Ljava/lang/Class;)Ljava/lang/Object;");
			
			// Only do the following if the return type is a primitive
			if(boxedType != null){
				String asmTypeString = Type.getInternalName(boxedType);
				mv.visitTypeInsn(CHECKCAST, asmTypeString);
				mv.visitMethodInsn(INVOKEVIRTUAL, asmTypeString, 
					returnType.getClassName() + "Value", "()" + returnType.getDescriptor());				
			} else {
				mv.visitTypeInsn(CHECKCAST, returnType.getInternalName());				
			}
						
			mv.visitInsn(returnType.getOpcode(IRETURN));
			mv.visitMaxs(3, 1);
			mv.visitEnd();			
		}
		
		if(type == SETTER){

			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn(propertyName);
			Type argType = argumentTypes[0];
			
			
			mv.visitVarInsn(argType.getOpcode(ILOAD), 1);

			Class <?> boxedType = boxedType(argType); 
			if(boxedType != null){
				String asmTypeString = Type.getInternalName(boxedType);
				mv.visitMethodInsn(INVOKESTATIC, asmTypeString, "valueOf", 
					"(" + argType.getDescriptor() + ")L" + asmTypeString + ";");
			}
						
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/concord/otrunk/AbstractOTObject", "setResource", "(Ljava/lang/String;Ljava/lang/Object;)Z");
			mv.visitInsn(POP);
			mv.visitInsn(RETURN);
			
			// the max size of the stack is 1 for the "this" plus 1 for the property name
			//  plus the size of the argument 
			// the max size of the locals is 1 for "this" plus the size of the single argument
			//  which is normal 1 except for doubles and longs
			mv.visitMaxs(2 + argType.getSize(), 1 + argType.getSize());
			mv.visitEnd();
			
		}	
	}

	public static String getStaticFieldName(String propertyName)
    {
	    return "OT_PROP_" + propertyName.toUpperCase();
    }
	
	@Override
	public void visitEnd()
	{
		// create static fields for all of the properties of this class
		FieldVisitor fv;
		ArrayList<OTClassProperty> allClassProperties = otClass.getOTAllClassProperties();
		for (OTClassProperty classProperty : allClassProperties) {
			String name = getStaticFieldName(classProperty.getName()); 
			fv = cv.visitField(ACC_STATIC | ACC_PUBLIC, name, "Lorg/concord/framework/otrunk/otcore/OTClassProperty;", null, null);
			fv.visitEnd();
        }
		
		MethodVisitor mv;
		// Add the null constructor
		{
			mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, parentName, "<init>", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}

		abstractClass.getSuperclass();
		abstractClass.getInterfaces();
		
		ArrayList<Method> methods = getAbstractMethods(abstractClass);
		for (Method method : methods) {
	        processMethod(method);
        }
						
	    super.visitEnd();
	}

	/**
	 * Find all the abstract methods, of any of the classes in this class tree.  
	 * However, this should not return methods that are implemented within this class tree.
	 * 
	 * @param klass
	 * @return
	 */
	public static ArrayList<Method> getAbstractMethods(Class<?> klass){
		ArrayList<Method> abstractMethods = new ArrayList<Method>();
		ArrayList<Method> definedMethods = new ArrayList<Method>();
		ArrayList<Class<?>> classesToVisit = new ArrayList<Class<?>>();
		
		classesToVisit.add(klass);
		
		for(int i=0; i < classesToVisit.size(); i++){
			Class<?> currentClass = classesToVisit.get(i);
			Method[] declaredMethods = currentClass.getDeclaredMethods();
			for (Method method : declaredMethods) {
		        if(Modifier.isAbstract(method.getModifiers())){
		        	boolean skip = false;
		        	for (Method definedMethod : definedMethods) {
		        		if(method.getName().equals(definedMethod.getName()) &&
		        				Arrays.equals(method.getParameterTypes(),definedMethod.getParameterTypes())){
		        			// this abstract method is defined within this class tree so skip it
		        			skip = true;
		        			break;
		        		}
		        	}
		        	for (Method abstractMethod : abstractMethods) {
		        		if(method.getName().equals(abstractMethod.getName()) &&
		        				Arrays.equals(method.getParameterTypes(),abstractMethod.getParameterTypes())){
		        			// this abstract method is already defined elsewhere so don't add it twice
		        			skip = true;
		        			break;
		        		}
		        	}
		        	if(skip){
		        		continue;
		        	}
		        	abstractMethods.add(method);
		        } else {
		        	definedMethods.add(method);
		        }
	        }
			
			Class<?> superclass = currentClass.getSuperclass();
			if(superclass != null && !classesToVisit.contains(superclass)){
				classesToVisit.add(superclass);
			}
			
			Class<?>[] interfaces = currentClass.getInterfaces();
			for (Class<?> _interface : interfaces) {
				if(!classesToVisit.contains(_interface)){
					classesToVisit.add(_interface);
				}	            
            }
		}
				
		return abstractMethods;		
	}
	
	public static Class<?> boxedType(Type type) {
		switch(type.getSort()){
        case Type.VOID:
        	return null;
        case Type.BOOLEAN:
            return Boolean.class;
        case Type.CHAR:
        	return Character.class;
        case Type.BYTE:
        	return Byte.class;
        case Type.SHORT:
        	return Short.class;
        case Type.INT:
        	return Integer.class;
        case Type.FLOAT:
        	return Float.class;
        case Type.LONG:
        	return Long.class;
        case Type.DOUBLE:
        	return Double.class;
        default:
        	return null;
		}
	}
}
