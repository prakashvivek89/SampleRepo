package helper;

import java.util.Iterator;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
Class contains the method to delete the duplicate retried failed test cases. */

public class MyTestListenerAdapter extends TestListenerAdapter {

	@Override
	public void onFinish(ITestContext context) {

		Iterator<ITestResult> skippedTestCases = context.getSkippedTests().getAllResults().iterator();

		while (skippedTestCases.hasNext()) {
			ITestResult skippedTestCase = skippedTestCases.next();
			
			ITestNGMethod method = skippedTestCase.getMethod();
			
			if (!(context.getSkippedTests().getResults(method).isEmpty())) {
				skippedTestCases.remove();
			}
		}
	}
}