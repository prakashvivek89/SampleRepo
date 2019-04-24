package helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;


/**
This class is implementing the IAnnotationTransformer interface. 
It uses the public void transform(ITestAnnotation annotation, Class testClass,Constructor testConstructor, Method testMethod) method.
This method will be invoked by TestNG to give a chance to modify a TestNG annotation read from your test classes. 
Then the Listener is for calling the Retrytestcase.class to reRun the Failed Test.
 */
public class AnnotationTransformer implements IAnnotationTransformer{

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(Retrytestcase.class);
	}
}
