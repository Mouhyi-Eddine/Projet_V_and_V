import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClassParser {



	private static final String PACKAGE_SEPARATOR = ".";


	public List<Class> getClassesFromDirectory(String directoryPath) throws Exception{

		File classDirectory = new File(directoryPath);

		List<String> classesName = getClassesNameFromDirectory(classDirectory);

		return loadClassFromDirectory(classDirectory,classesName);
	}


	public List<String> getClassesNameFromDirectory(File directory){
		return getClassesNameFromDirectory(directory,"");
	}


	private List<String> getClassesNameFromDirectory(File directory, String parentPackage){

		List<String> classesName = new ArrayList<>();
		if (directory.isDirectory()){
			for (File classFile : directory.listFiles()) {
				if(classFile.isDirectory()){
					String packageArchitecture = parentPackage.equals("") ? classFile.getName() : parentPackage + PACKAGE_SEPARATOR + classFile.getName();
					classesName.addAll(getClassesNameFromDirectory(classFile,packageArchitecture));
				}
				else if(classFile.isFile()){
					String prefix = "";
					if(!parentPackage.equalsIgnoreCase("")){
						prefix = parentPackage+PACKAGE_SEPARATOR;
					}
					classesName.add(prefix+getClassName(classFile));
				}
			}
		}
		return classesName;
	}



	private List<Class> loadClassFromDirectory(File directoryClass,List<String> classesName) throws Exception{
		List<Class> loadedClasses = new ArrayList<>();

		URL url = directoryClass.toURI().toURL();
		URL[] urls = new URL[]{url};

		loadingDirectoryClasses(directoryClass, classesName, loadedClasses, urls);
		return loadedClasses;
	}

	private void loadingDirectoryClasses(File directoryClass, List<String> classesName, List<Class> loadedClasses, URL[] urls) throws Exception{
		URLClassLoader classLoader = new URLClassLoader(urls);

		for (String className : classesName) {
			loadClass(loadedClasses, classLoader, className);
		}

	}

	private void loadClass(List<Class> loadedClasses, URLClassLoader classLoader, String className) throws Exception {
		
			Class theClass = classLoader.loadClass(className);

			loadedClasses.add(theClass);
		
	}

	private String getClassName(File classFile){
		if(classFile.getName().lastIndexOf('.')!=-1){
			return classFile.getName().substring(0, classFile.getName().lastIndexOf('.'));
		}
		else{
			return classFile.getName();
		}
	}
}