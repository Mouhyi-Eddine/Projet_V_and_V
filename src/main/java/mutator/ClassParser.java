package main;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassParser {

	private static final String PACKAGE_SEPARATOR = ".";

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public List<Class> getClassesFromDirectory(String directoryPath) {
		logger.info("Getting classes from directory");
		File classDirectory = new File(directoryPath);

		List<String> classesName = getClassesNameFromDirectory(classDirectory);
		return loadClassFromDirectory(classDirectory, classesName);
	}

	public List<String> getClassesNameFromDirectory(File directory) {
		return getClassesNameFromDirectory(directory, "");
	}

	private List<String> getClassesNameFromDirectory(File directory, String parentPackage) {

		List<String> classesName = new ArrayList<>();
		if (directory.isDirectory()) {
			for (File classFile : directory.listFiles()) {
				if (classFile.isDirectory()) {
					String packageArchitecture = parentPackage.equals("") ? classFile.getName()
							: parentPackage + PACKAGE_SEPARATOR + classFile.getName();
					classesName.addAll(getClassesNameFromDirectory(classFile, packageArchitecture));
				} else if (classFile.isFile()) {
					String prefix = "";
					if (!parentPackage.equalsIgnoreCase("")) {
						prefix = parentPackage + PACKAGE_SEPARATOR;
					}
					classesName.add(prefix + getClassName(classFile));
				}
			}
		}
		return classesName;
	}

	private List<Class> loadClassFromDirectory(File directoryClass, List<String> classesName) {
		List<Class> loadedClasses = new ArrayList<>();
		try {
			logger.trace("Get URL from directory");
			URL url;
			url = directoryClass.toURI().toURL();
			URL[] urls = new URL[] { url };

			loadingDirectoryClasses(directoryClass, classesName, loadedClasses, urls);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return loadedClasses;
	}

	private void loadingDirectoryClasses(File directoryClass, List<String> classesName, List<Class> loadedClasses,
			URL[] urls) {
		logger.trace("Loading folder into classLoader");
		try (URLClassLoader classLoader = new URLClassLoader(urls)) {
			logger.trace("Loading classes located in {}", directoryClass.getAbsolutePath());
			for (String className : classesName) {
				loadClass(loadedClasses, classLoader, className);
			}
		} catch (IOException e) {
			logger.warn("Errors occured during classLoader closing");
		}

	}

	private void loadClass(List<Class> loadedClasses, URLClassLoader classLoader, String className) {
		try{
            Class theClass = classLoader.loadClass(className);
            logger.debug("Loading class : {}", className);
            loadedClasses.add(theClass);
        }
        catch(ClassNotFoundException | NoClassDefFoundError e){
            logger.debug("The file {} can not be loaded",className,e);
        }

	}

	private String getClassName(File classFile) {
		if (classFile.getName().lastIndexOf('.') != -1) {
			return classFile.getName().substring(0, classFile.getName().lastIndexOf('.'));
		} else {
			return classFile.getName();
		}
	}
}