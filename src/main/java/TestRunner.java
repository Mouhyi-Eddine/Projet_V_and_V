import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TestRunner is in charge to run tests on the target project defined in the
 * Main
 * <p>
 * The execute() call is sent by the Mutator
 */
public class TestRunner {

//    private static final Logger logger = java.util.logging.Logger.getLogger(TestRunner.class);

	private List<Class> classes;

	private List<Class<?>> testClasses;

	private String rootProjectPath;

	private MutantContainer mutantContainer;

	/**
	 * Constructor instantiates list classes
	 */
	public TestRunner() {
		classes = new ArrayList<>();
		testClasses = new ArrayList<>();
	}

	public List<Class> getClasses() {
		return classes;
	}

	public void setClasses(List<Class> classes) {
		for (Class classString : classes) {
			addClass(classString);
		}
	}

	private void addClass(Class clazz) {
		if (classes == null) {
			classes = new ArrayList<>();
		}
		classes.add(clazz);
	}

	public List<Class<?>> getTestClasses() {
		return testClasses;
	}

	public void setTestClasses(List<Class> testClasses) {
		for (Class testClass : testClasses) {
			addTestClass(testClass);
		}
	}

	private void addTestClass(Class testClass) {
		if (testClasses == null) {
			testClasses = new ArrayList<>();
		}
		testClasses.add(testClass);
	}

	public void setRootProjectPath(String rootProjectPath) {
		this.rootProjectPath = rootProjectPath;
	}

	public MutantContainer getMutantContainer() {
		return mutantContainer;
	}

	public void setMutantContainer(MutantContainer mutantContainer) {
		this.mutantContainer = mutantContainer;
	}

	private void verifyTestRunnerForExecution() throws Exception {
		if (classes == null || classes.isEmpty()) {
			throw new Exception("Project classes are not in TestRunner");
		}
		if (testClasses == null || testClasses.isEmpty()) {
			throw new Exception("Project test classes are not in TestRunner");
		}
		if (mutantContainer == null) {
			throw new Exception("Mutated class is not in TestRunner");
		}
		if (rootProjectPath == null || rootProjectPath.equalsIgnoreCase("")) {
			throw new Exception("Project path is not in TestRunner");
		}

	}

	public void execute() throws Exception{
		verifyTestRunnerForExecution();

		runTest();
	}

	private void runTest() throws Exception {
		ProcessBuilder ps = new ProcessBuilder("mvn", "surefire:test");
		ps.redirectErrorStream(true);
		if (rootProjectPath.substring(0, 1).equalsIgnoreCase(".")) {
			ps.directory(new File(System.getProperty("user.dir") + Mutator.PATH_DELIMITER + rootProjectPath));
		} else {
			ps.directory(new File(rootProjectPath));
		}

		Process process = ps.start();

		if (!process.waitFor(5, TimeUnit.MINUTES)) {
			process.destroy();
		} else {
			process.waitFor();
			int returnValue = process.exitValue();
		}
	}

}
