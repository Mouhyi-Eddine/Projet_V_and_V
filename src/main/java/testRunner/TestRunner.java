package testRunner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mutator.MutantContainer;
import mutator.Mutator;
import results.Report;
import results.Result;



public class TestRunner {

	private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

	private List<Class> classes;

	private List<Class<?>> testClasses;

	private String rootProjectPath;

	private MutantContainer mutantContainer;

	private Result result;

	
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
		if (classes.isEmpty()) {
			logger.warn("No classes loaded during this setter call");
		} else {
			logger.debug("{} classes are loaded in TestRunner", classes.size());
		}
	}

	private void addClass(Class clazz) {
		if (classes == null) {
			classes = new ArrayList<>();
		}
		logger.trace("Adding {} to TestRunner classes collection", clazz);

		classes.add(clazz);
	}

	public List<Class<?>> getTestClasses() {
		return testClasses;
	}

	public void setTestClasses(List<Class> testClasses) {
		for (Class testClass : testClasses) {
			addTestClass(testClass);
		}
		if (testClasses.isEmpty()) {
			logger.warn("No test classes loaded during this setter call");
		} else {
			logger.debug("{} test classes are loaded in TestRunner", classes.size());
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
        logger.trace("TestRunner checking");

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

	public void execute() throws Exception {
		verifyTestRunnerForExecution();

		runTest();
	}

	private void runTest() throws Exception {
        logger.debug("Starting testing with MAVEN on {}", rootProjectPath);

		ProcessBuilder ps = new ProcessBuilder("mvn", "surefire:test");
		ps.redirectErrorStream(true);
		if (rootProjectPath.substring(0, 1).equalsIgnoreCase(".")) {
			ps.directory(new File(System.getProperty("user.dir") + Mutator.PATH_DELIMITER + rootProjectPath));
		} else {
			ps.directory(new File(rootProjectPath));
		}

		Process process = ps.start();

		if(!process.waitFor(5, TimeUnit.MINUTES)){
            logger.warn("The process takes too long : may be an infinite loop. It was destroyed.");
            process.destroy();
        }
        else{
            process.waitFor();
            int returnValue = process.exitValue(); 
            result.addReport(new Report(returnValue == 0, mutantContainer));
        }
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
