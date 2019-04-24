package readexcel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import exceptions.DatabaseExceptions;
import exceptions.FrameworkExceptions;
import exceptions.MissingFileException;
import exceptions.SeleniumExceptions;
import helper.WebdriverHelper;

/**
 * This class contains method which act as initial point when any test case is executed.
 */


public class TestcaseFlow extends WebdriverHelper{
	
	public void runSingleTest(String testcaseID) throws FrameworkExceptions, SeleniumExceptions, IOException, SQLException, InterruptedException, DatabaseExceptions, MissingFileException {
		for (Map<String, String> testSteps : ReadTestcaseFile.testcases_persuite.get(testcaseID)) {
			String testdata[] = new String[0];
				/* if test data is present in the testcase */
			
			if (!(testSteps.get("testdata").isEmpty())) {
					testdata =testSteps.get("testdata").split("\\|");
					
					/* if test data contains the range */

					if (testdata[2].contains("-")) {
						int startRange = Integer.parseInt(testdata[2].split("-")[0]);
						int endRange = Integer.parseInt(testdata[2].split("-")[1]);
						for (int i = startRange; i <= endRange; i++) {
							testdata[2] = String.valueOf(i);
							ReadProjectReusables.getInstance().getActionSteps1(testSteps.get("methodname").trim(), testdata);
						}
					}
					/* if test data does not contains the range */
					else {
						ReadProjectReusables.getInstance().getActionSteps1(testSteps.get("methodname").trim(), testdata);
					}
				} else {
					ReadProjectReusables.getInstance().getActionSteps1(testSteps.get("methodname").trim(), testdata);
				}
		}
	}
}