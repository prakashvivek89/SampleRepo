package helper;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retrytestcase implements IRetryAnalyzer{
	private int count = 0;
	private static int maxTry = WebdriverHelper.retry;
		
	/**
	 * This method decides how many times a test needs to be rerun. TestNg will
	 * call this method every time a test fails. So we can put some code in here
	 * to decide when to rerun the test.
	  
	 * Note: This method will return true if a tests needs to be retried and
	 * false it not.
	 */
	@Override
	public boolean retry(ITestResult result) {
		if(!(result.isSuccess())){
			if(count<maxTry) {
				count++;
				result.setStatus(ITestResult.FAILURE);
				return true;
			}
			else {
				result.setStatus(ITestResult.FAILURE);
			}
		}
		else {
			result.setStatus(ITestResult.FAILURE);
		}
		return false;
	}
	
}
