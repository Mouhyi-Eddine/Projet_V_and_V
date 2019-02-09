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
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

public class InstructionModifier extends ClassVisitor {

	private int opcode;

	public InstructionModifier(int api, ClassVisitor classVisitor) {

		super(api, classVisitor);

	}
	public InstructionModifier(int api, ClassVisitor classVisitor,int opcode) {
		super(api, classVisitor);
		this.opcode=opcode;

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
		//if (name.equals("twicee")) { // Too specific uh? It is better to have a parameter
		return new StartByAddModifier(api, visitor,opcode);
		//}

		//return visitor;
	}

	static class StartByAddModifier extends MethodVisitor {

		private int opcode;

		public StartByAddModifier(int api, MethodVisitor methodVisitor) {
			super(api, methodVisitor);
		}
		public StartByAddModifier(int api, MethodVisitor methodVisitor,int opcode) {
			super(api, methodVisitor);
			this.opcode=opcode;
		}

		@Override
		public void visitInsn(int opcode) {
			//			if (opcode == Opcodes.DADD) {
			//				opcode = Opcodes.DSUB;
			//			} else if (opcode == Opcodes.LAND) {
			//				opcode = Opcodes.LOR;
			//			}
			//			super.visitInsn(opcode);
			switch(opcode) {
			case Opcodes.DADD : opcode = Opcodes.DSUB;
			case Opcodes.DMUL : opcode = Opcodes.DDIV;
			case Opcodes.DSUB : opcode = Opcodes.DADD;
			case Opcodes.DDIV : opcode = Opcodes.DMUL;
			case Opcodes.LAND : opcode = Opcodes.LOR;
			case Opcodes.LOR : opcode = Opcodes.LAND;
			case Opcodes.IF_ICMPGE: opcode = Opcodes.IF_ICMPGT;
			case Opcodes.IF_ICMPLE: opcode = Opcodes.IF_ICMPLT;
			case Opcodes.IF_ICMPGT: opcode = Opcodes.IF_ICMPGE;
			case Opcodes.IF_ICMPLT: opcode = Opcodes.IF_ICMPLE;

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
				//				System.out.println("File " + file.getName());
				files.add(file);
			} else if (file.isDirectory()) {
				//				System.out.println("Directory " + file.getName());
				files.addAll(getFiles(target + file.getName() + "/"));
			}
		}
		return files;
	}

	public static void main(String... args) throws Exception {
		ArrayList<File> files = getFiles("/home/moutarajji/Documents/code-manipulation/manipulation-target/target/");
		final String target = "/home/moutarajji/Documents/code-manipulation/manipulation-target/target/";
		//		final String outputFile = "/home/mj/vv-workspace/manipulation-target/target/classes/Test.class";
		

		for (File f : files) {
			FileInputStream file = new FileInputStream(f);
			FileInputStream initFile = file;
			List<Integer> listOpcodes = new ArrayList<Integer>();

			Integer[] arrayOpcodes = new Integer[] {Opcodes.DADD,Opcodes.DDIV,Opcodes.DMUL,Opcodes.DSUB,Opcodes.LOR,Opcodes.LAND,Opcodes.IF_ICMPGE,Opcodes.IF_ICMPLE,Opcodes.IF_ICMPLT,Opcodes.IF_ICMPGT};

			listOpcodes = Arrays.asList(arrayOpcodes);

			for(int opcode: listOpcodes) {
				
					ClassReader reader = new ClassReader(file);

					//Result result = JUnitCore.runClasses(reader.getClass());
					System.out.println("------->"+reader.getClassName() + "  ");
					//			for(Constructor c: reader.getClass().getConstructors()) {
					//				System.out.println(c.getName());
					//			}
					//			for(Failure fail: result.getFailures()) {
					//				System.out.println(fail.toString());
					//			}
					//int opcodes = Opcodes.DADD;
					//			System.out.println(result.wasSuccessful());

					ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
					reader.accept(new InstructionModifier(ASM5, writer,opcode), ClassReader.EXPAND_FRAMES);
					FileOutputStream output = new FileOutputStream(f);
					output.write(writer.toByteArray());
					output.close();
					//lancer les tests
					file = initFile;

				
			}
		}
	}
}
