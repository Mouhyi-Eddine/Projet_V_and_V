package main;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mutator.ClassParser;
import mutator.Mutator;
import results.Result;
import testRunner.TestRunner;

import java.util.List;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	// to define the rootPath
	private static String rootPath = "./";
	private static String classesPath = rootPath + "target/classes";
	private static String testClassesPath = rootPath + "target/classes";

	public static void main(String[] args) {
		definePaths(args);

		logger.info("==================== V&V PROJECT : Mahmoud & Mouhyi ====================");
		logger.info("Mutation testing for project :");
		logger.info("  | Classes root directory : {}", classesPath);
		logger.info("  | Test classes root directory : {}", testClassesPath);
		logger.info("========================================================================");

		
		ClassParser classParser = new ClassParser();
		List<Class> classList = classParser.getClassesFromDirectory(classesPath);
		List<Class> testClassList = classParser.getClassesFromDirectory(testClassesPath);

		// Report service initialization
		Result result = new Result();
		result.setProjectName(rootPath);

		// Test Runner initialization
		TestRunner testRunner = new TestRunner();
		testRunner.setRootProjectPath(rootPath);
		testRunner.setClasses(classList);
		testRunner.setTestClasses(testClassList);
		testRunner.setResult(result);

		// Mutator initialization
		Mutator mutator = new Mutator(classList, testRunner, classesPath);
		try {
			result.startMutationTesting();
			mutator.mutate();
		} catch (Exception e) {
			logger.error("Error start mutation", e);
		}
		result.stopMutationTesting();

		logger.info("");
		logger.info("Reporting Generation");
		result.generateCSV();
	}

	
	private static void definePaths(String[] args) {
		if (args != null && args.length >= 1 && args[0] != null) {
			rootPath = args[0];
			classesPath = rootPath + "/target/classes";
			testClassesPath = rootPath + "/target/test-classes";
		} else {
			logger.warn("Main parameters does not define project to test. Default is set.");
		}
	}
}
