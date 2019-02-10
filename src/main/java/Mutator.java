
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Mutator {

	public static final String PATH_DELIMITER = "/";
	private List<Class> classes;
	private TestRunner testRunner;
	private String classesPath;
	private int indexMutation;
	private int indexMutationClass;
	private HashMap<String, String> hashMap = new HashMap<>();
	private final List<Mutation> mutations = getMutations();
	
	public Mutator(List<Class> classes, TestRunner testRunner, String classesPath) {
		this.classes = classes;
		this.testRunner = testRunner;
		this.classesPath = classesPath;
		this.hashMap.put("ADDITION", "+ is replaced by -");
		this.hashMap.put("SUBTRACTION", "- is replaced by +");
		this.hashMap.put("DIVISION", "/ is replaced by *");
		this.hashMap.put("MULTIPLICATION", "* is replaced by /");
		this.hashMap.put("CONDITION_EQ", "ifeq is replaced by ifneq");
		this.hashMap.put("CONDITION_NEQ", "ifneq is replaced by ifeq");
	}

	public void mutate() throws Exception {
		if (classes == null) {
			throw new Exception("Classes are not set correctly in Mutator");
		}

		if (testRunner == null) {
			throw new Exception("Test runner is not defined in Mutator");
		}

		if (classesPath == null) {
			throw new Exception("Classes path is not defined in Mutator");
		}

		for (Class cl : classes) {
			String classPath = classesPath + PATH_DELIMITER + cl.getName().replaceAll("\\.", "/") + ".class";

			CtClass ctClass;
			ClassPool cp = ClassPool.getDefault();
			ctClass = cp.makeClass(new FileInputStream(classPath));
			ctClass.stopPruning(true);

			if (!ctClass.isInterface()) {
				ClassFile cf = ctClass.getClassFile();
				double progression = ((double) indexMutationClass / (double) classes.size()) * 100;

				CtMethod[] methods = ctClass.getDeclaredMethods();
				mutationTestingForMethods(methods, cf, ctClass);

				ctClass.writeFile(classesPath);
				ctClass.defrost();
			}
			indexMutationClass++;

		}
	}

	private void mutationTestingForMethods(CtMethod[] methods, ClassFile cf, CtClass ctClass) throws Exception {
		for (CtMethod method : methods) {
			CodeAttribute ca = method.getMethodInfo().getCodeAttribute();

			mutationTestingForAMethod(ca, cf, ctClass, method);
		}
	}

	private void mutationTestingForAMethod(CodeAttribute ca, ClassFile cf, CtClass ctClass, CtMethod method)
			throws Exception {
		if (ca != null) {
			CodeIterator ci = ca.iterator();
			while (ci.hasNext()) {
				int index = ci.next();
				int op = ci.byteAt(index);

				mutateOp(cf, ci, index, op, ctClass, method);

			}
		}
	}

	private void mutateOp(ClassFile cf, CodeIterator ci, int index, int op, CtClass ctClass, CtMethod method)
			throws Exception {
		for (Mutation mutation : mutations) {
			if (Mnemonic.OPCODE[op].equalsIgnoreCase(mutation.getTargetOperation())) {
				indexMutation++;
				Bytecode bytecode = new Bytecode(cf.getConstPool());
				bytecode.add(mutation.getMutationOperationCode());
				ci.write(bytecode.get(), index);

				// Writing file into the physical file
				ctClass.writeFile(classesPath);
				ctClass.defrost();

				runTestsAndUndoMutation(ctClass, mutation.getTargetOperationCode(), index, ci, cf, method,
						mutation.getMutationType());
			}
		}
	}

	private void runTestsAndUndoMutation(CtClass ctClass, int baseCode, int index, CodeIterator ci, ClassFile cf,
			CtMethod method, String m) throws Exception {

		runTest(ctClass.getName(), method, m);
		ctClass.defrost();

		// Perform the undo
		Bytecode baseMutant = new Bytecode(cf.getConstPool());
		baseMutant.add(baseCode);
		ci.write(baseMutant.get(), index);
	}

	private void runTest(String classMutant, CtMethod method, String mutationType) throws Exception {
		MutantContainer mutantContainer = createMutantContainer(classMutant, method.getName(), mutationType);
		this.testRunner.setMutantContainer(mutantContainer);
		this.testRunner.execute();
	}

	public MutantContainer createMutantContainer(String classMutant, String method, String mutationType) {
		MutantContainer m = new MutantContainer();
		m.setMutatedClass(classMutant);
		m.setMutationMethod(method);
		m.setMutantType(mutationType);
		return m;
	}

	public List<Mutation> getMutations() {
		List<Mutation> mutations = new ArrayList<>();
		String addition = this.hashMap.get("ADDITION");
		String substraction = this.hashMap.get("SUBTRACTION");
		String division = this.hashMap.get("DIVISION");
		String multiplication = this.hashMap.get("MULTIPLICATION");
		String condition_eq = this.hashMap.get("CONDITION_EQ");
		String condition_neq = this.hashMap.get("CONDITION_NEQ");
		// Arithmetic mutations
		for (String prefix : Arrays.asList("i", "d", "f", "l")) { // For all number types
			// Addition -> Subtraction
			mutations.add(new Mutation(prefix + "add", indexOf(prefix + "add"), prefix + "sub", indexOf(prefix + "sub"),
					 addition));

			// Subtraction -> Addition
			mutations.add(new Mutation(prefix + "sub", indexOf(prefix + "sub"), prefix + "add", indexOf(prefix + "add"),
					substraction));

			// Multiplication -> Division
			mutations.add(new Mutation(prefix + "mul", indexOf(prefix + "mul"), prefix + "div", indexOf(prefix + "div"),
					multiplication));

			// Division -> Multiplication
			mutations.add(new Mutation(prefix + "div", indexOf(prefix + "div"), prefix + "mul", indexOf(prefix + "mul"),
					division));
		}

		mutations.add(new Mutation("ifeq", indexOf("ifeq"), "ifne", indexOf("ifne"),
				condition_eq));

		mutations.add(new Mutation("ifne", indexOf("ifne"), "ifeq", indexOf("ifeq"),
				condition_neq));

		return mutations;
	}

	private static int indexOf(String operation) {
		int index = Arrays.asList(Mnemonic.OPCODE).indexOf(operation);
		if (index == -1) {
			System.err.println("The " + operation + " was not found in Mnemonic.OPCODE table");
		}
		return index;
	}

	public HashMap<String, String> getHashMap() {
		return hashMap;
	}

	public void setHashMap(HashMap<String, String> hashMap) {
		this.hashMap = hashMap;
	}

}