package org.concord.otrunk.asm;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.concord.otrunk.OTInvocationHandler;
import org.concord.otrunk.OTObjectInternal;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
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
	private String[] interfaces;
	private Class abstractClass;
	
	
	public ImplementOTClassAdapter(ClassVisitor cv, Class abstractClass)
    {
	    super(cv);
	    this.abstractClass = abstractClass;
    }

	@Override
	public void visit(int version, int access, String name, String signature,
	    String superName, String[] interfaces)
	{
		// this assumes the class being visited is an interface
		className = name + "_Generated";
		cv.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, null, "org/concord/otrunk/OTObjectInternal", new String[] { name });
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
			System.out.println("skipping non abstract method: " + className + "." + method.toString());
			return;
		}

		// check if OTOjectInternal implements this method
		Method[] methods = OTObjectInternal.class.getMethods();
		for (Method method2 : methods) {
	        if(method2.getName().equals(method.getName())){
				// skip this method
				// System.out.println("skipping already implemented method: " + className + "." + method.toString());
				return;
	        }
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
		} else if(name.startsWith("set")){
			type = SETTER;
			propertyName = OTInvocationHandler.getResourceName(3, name);						
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

		mv = cv.visitMethod(ACC_PUBLIC, name, desc, null, null);
		
		if(type == GETTER){
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			
			mv.visitLdcInsn(propertyName);
						
			Class<?> boxedType = boxedType(returnType);
			if(boxedType == null){
				// asm handles putting .classS on the stack for non primitives
				mv.visitLdcInsn(returnType);
			} else {
				// for primitives the static .TYPE field of the boxedType has to be used instead
				mv.visitFieldInsn(GETSTATIC, Type.getInternalName(boxedType), "TYPE", "Ljava/lang/Class;");
			}

			mv.visitMethodInsn(INVOKEVIRTUAL, "org/concord/otrunk/OTObjectInternal", "getResourceChecked", "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;");
			
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
						
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/concord/otrunk/OTObjectInternal", "setResource", "(Ljava/lang/String;Ljava/lang/Object;)Z");
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
	
	@Override
	public void visitEnd()
	{
		MethodVisitor mv;
		// Add the null constructor
		{
			mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "org/concord/otrunk/OTObjectInternal", "<init>", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		
		Method[] methods = abstractClass.getMethods();
		for (Method method : methods) {
	        processMethod(method);
        }
		
		// TODO need to handle the internal* methods equals, hashCode, toString
		
	    super.visitEnd();
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
	
	
	public static byte[] dump () throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, "org/concord/otrunk/test/AMSTestImpl", null, "org/concord/otrunk/OTObjectInternal", new String[] { "org/concord/otrunk/test/ASMTestInterface" });

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "org/concord/otrunk/OTObjectInternal", "<init>", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getNumber", "()I", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn("number");
			mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/concord/otrunk/test/AMSTestImpl", "getResourceChecked", "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;");
			mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
			mv.visitInsn(IRETURN);
			mv.visitMaxs(3, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "setNumber", "(I)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn("number");
			mv.visitVarInsn(ILOAD, 1);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/concord/otrunk/test/AMSTestImpl", "setResource", "(Ljava/lang/String;Ljava/lang/Object;)Z");
			mv.visitInsn(POP);
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}

}
