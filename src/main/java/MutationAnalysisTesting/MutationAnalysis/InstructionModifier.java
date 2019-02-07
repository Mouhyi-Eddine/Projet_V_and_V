package MutationAnalysisTesting.MutationAnalysis;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.ASM5;

public class InstructionModifier extends ClassVisitor {

	public InstructionModifier(int api, ClassVisitor classVisitor) {
		super(api, classVisitor);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//		MethodVisitor visitor = new MethodVisitor(api) {
//			@Override
//			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
//				// TODO Auto-generated method stub
//				System.out.println("desc: " + desc);
//				return super.visitAnnotation(desc, visible);
//			}
//		};
		MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);;
		if (name.equals("twicee")) { // Too specific uh? It is better to have a parameter
			return new StartByAddModifier(api, visitor);
		}

		return visitor;
	}

	static class StartByAddModifier extends MethodVisitor {

		public StartByAddModifier(int api, MethodVisitor methodVisitor) {
			super(api, methodVisitor);
		}

		@Override
		public void visitInsn(int opcode) {
			if (opcode == Opcodes.DMUL) {
				opcode = Opcodes.DADD;
			} else if (opcode == Opcodes.LAND) {
				opcode = Opcodes.LOR;
			}
			super.visitInsn(opcode);
		}
	}

	public static ArrayList<File> getFiles(String target) {
		File folder = new File(target);
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> files = new ArrayList<File>();
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				System.out.println("File " + file.getName());
				files.add(file);
			} else if (file.isDirectory()) {
				System.out.println("Directory " + file.getName());
				files.addAll(getFiles(target + file.getName() + "/"));
			}
		}
		return files;
	}

	public static void main(String... args) throws Exception {
		ArrayList<File> files = getFiles("/home/mj/vv-workspace/manipulation-target/target/classes/");
		final String target = "/home/mj/vv-workspace/manipulation-target/target/classes/";
//		final String outputFile = "/home/mj/vv-workspace/manipulation-target/target/classes/Test.class";
		for (File f : files) {
			FileInputStream file = new FileInputStream(f);
			ClassReader reader = new ClassReader(file);
			ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
			Result result = JUnitCore.runClasses(reader.getClass());
			System.out.println(reader.getClassName() + "  ");
			for(Constructor c: reader.getClass().getConstructors()) {
				System.out.println(c.getName());
			}
			for(Failure fail: result.getFailures()) {
				System.out.println(fail.toString());
			}
			System.out.println(result.wasSuccessful());
			reader.accept(new InstructionModifier(ASM5, writer), ClassReader.EXPAND_FRAMES);

			FileOutputStream output = new FileOutputStream(f);
			output.write(writer.toByteArray());
			output.close();
		}
	}
}
