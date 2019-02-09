import java.util.ArrayList;
import java.util.List;

/**
 * TestRunner is in charge to run tests on the target project
 * defined in the Main
 * <p>
 * The execute() call is sent by the Mutator
 */
public class TestRunner {

//    private static final Logger logger = java.util.logging.Logger.getLogger(TestRunner.class);

    private List<Class> classes;

    private List<Class<?>> testClasses;

    private String rootProjectPath;

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

}
