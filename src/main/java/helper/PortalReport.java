package helper;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

/** 
 *Class contains the method to update the automation execution information in portal database
 **/

public class PortalReport {
	private List<ITestResult> allResults;
	private int primaryKey_ProjectExecutionDetailID;
	private int primaryKey_TestCaseExecutionSummaryID;
	private static PortalReport portalReport;
	private static final Logger LOG = LoggerFactory
			.getLogger(PortalReport.class);
	
	private PortalReport() {
	}
	
	public static PortalReport getInstance() {
		if(portalReport == null) {
			portalReport = new PortalReport();
		}
		return portalReport;
	}
		
	private void tblProjectExecutionDetails() throws SQLException {
		float passPercent;
		float failPercent;
		int executionType = 0;
		int total = WebdriverHelper.totalPassCount + WebdriverHelper.totalFailCount + WebdriverHelper.totalSkipCount;

		if (total != 0) {
			passPercent = ((float) WebdriverHelper.totalPassCount) / (total);
			passPercent = passPercent * 100;
			failPercent = 100 - passPercent;
		} else {
			passPercent = 0;
			failPercent = 0;
		}
		if(WebdriverHelper.runRegression.equalsIgnoreCase("yes")){
			executionType = 1;
		}
		else if(WebdriverHelper.runSmoke.equalsIgnoreCase("yes")){
			executionType = 2;
		}
		PreparedStatement st = null;
		ResultSet rs = null;
		if (DatabaseConnection.getCon() != null) {
			st = DatabaseConnection.getCon().prepareStatement(
					"insert into [AdviserAutotmation].[dbo].[tblProjectExecutionDetails] values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setObject(1, WebdriverHelper.projectName);
			st.setObject(2, Launcher.DBstartTime);
			st.setObject(3, Launcher.startTime);
			st.setObject(4, Launcher.endTime);
			st.setObject(5, getExecutionTime(Launcher.totalTime));
			st.setObject(6, Launcher.ProjectFolder + File.separator + Launcher.reportFolder);
			st.setObject(7, passPercent);
			st.setObject(8, failPercent);
			st.setObject(9, "0");
			st.setObject(10, WebdriverHelper.totalCount);
			st.setObject(11, WebdriverHelper.totalPassCount);
			st.setObject(12, WebdriverHelper.totalFailCount);
			st.setObject(13, WebdriverHelper.totalSkipCount);
			st.setObject(14, executionType);
			try {
				st.executeUpdate();
				rs = st.getGeneratedKeys();
				if (rs.next()) {
					primaryKey_ProjectExecutionDetailID = rs.getInt(1);
				}
			} catch (Exception e) {
				LOG.debug(e.getMessage());
			} finally {
				st.close();
				rs.close();

			}
		}
	}
	
	private void tblTestCaseExecutionSummaries() throws SQLException {
		Object[] keys = WebdriverHelper.finalReportingMap.keySet().toArray();
		Arrays.sort(keys);
		for (Object modName : keys) {
			String moduleName = modName.toString();
			allResults = WebdriverHelper.finalReportingMap.get(modName.toString());
			m2(moduleName, allResults);
		}
	}
	
	private void m2(String moduleName, List<ITestResult> moduleResults) throws SQLException {
		Iterator<ITestResult> skippedTestCases =moduleResults.iterator();
		List<ITestResult> skipTestcase = new ArrayList<>();
			 while (skippedTestCases.hasNext()) {
				 ITestResult testCase = skippedTestCases.next();
				 if(testCase.getStatus() == ITestResult.SKIP) {
					 skipTestcase.add(testCase);
				 }   
			 }
		moduleResults.removeAll(skipTestcase);
		int moduleWisePassedTest = 0;
		int moduleWiseFailedTest = 0;
		int moduleWiseSkippedTest = 0;
		String status = "Pass";
		int browserID = 0;
		int statusID = 1;
		List<Long> time = new ArrayList<>();
		PreparedStatement st  ;
		ResultSet rs = null;
		moduleName = moduleName.replace("_", " ");
		String directory = WebdriverHelper.IISReportFolder + File.separator + "Report"+ File.separator + Launcher.reportFolder + File.separator + "Detailed Report" + File.separator + moduleName + ".html";
		st = DatabaseConnection.getCon().prepareStatement("insert into [AdviserAutotmation].[dbo].[tblModuleExecutionSummaries] values(?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		st.setString(1, moduleName);
		st.setObject(2, primaryKey_ProjectExecutionDetailID);
		st.setObject(3, "00:00:00.0000000");
		st.setObject(4, "00:00:00.0000000");
		st.setObject(5, "00:00:00.0000000");
		
		st.setObject(6, 0);
		st.setObject(7, 0);
		st.setObject(8, 0);
		st.setObject(9, 0);
		st.setObject(10, 0);
		st.setObject(11, 0);
		st.setObject(12, WebdriverHelper.serverName);
		st.setObject(13, directory);
		try {
			st.executeUpdate();
			rs = st.getGeneratedKeys();
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}
        if(rs != null && rs.next()){
            primaryKey_TestCaseExecutionSummaryID = rs.getInt(1);
        }
		for (ITestResult result : moduleResults) {
			String browser = result.getTestContext().getSuite().getParameter("browser");
			if(browser.equalsIgnoreCase("IE")) {
				browserID = 1;
			}
			else if(browser.equalsIgnoreCase("firefox")) {
				browserID = 2;
			}
			else if(browser.equalsIgnoreCase("chrome")) {
				browserID = 3;
			}else if(browser.equalsIgnoreCase("phantomjs")) {
				browserID = 4;
			}
			time.add(result.getStartMillis());
			time.add(result.getEndMillis());
			String testcaseIDName = result.getMethod().getMethodName() + " : "+ result.getMethod().getDescription();
			testcaseIDName = testcaseIDName.replace("_", " ");
			moduleWisePassedTest = moduleWisePassedTest + result.getTestContext().getPassedTests().size();
			moduleWiseFailedTest = moduleWiseFailedTest + result.getTestContext().getFailedTests().size();
			moduleWiseSkippedTest = moduleWiseSkippedTest + result.getTestContext().getSkippedTests().size();
			st = DatabaseConnection.getCon().prepareStatement("insert into [AdviserAutotmation].[dbo].[tblTestCaseExecutionDetails] values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			st.setInt(4,primaryKey_TestCaseExecutionSummaryID);
			if (result.getStatus() == ITestResult.SUCCESS) {
				st.setInt(1, 1);
				st.setString(2, "");
			}
			else if (result.getStatus() == ITestResult.FAILURE) {
				st.setInt(1, 2);
				status = "fail";
				st.setString(2, result.getThrowable().getMessage());
			}
			st.setString(3, testcaseIDName);
			st.executeUpdate();
		}
		if(status.equalsIgnoreCase("fail")){
			statusID = 2;
		}
		int moduleWiseTotalCount = moduleWisePassedTest + moduleWiseFailedTest + moduleWiseSkippedTest;
		Collections.sort(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.get(0));
		String startTime = (calendar.get(Calendar.HOUR_OF_DAY)) + ":"
				+ (calendar.get(Calendar.MINUTE)) + ":"
				+ (calendar.get(Calendar.SECOND));
		
		calendar.setTimeInMillis(time.get(time.size()-1));
		String endTime = calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + ":"
				+ calendar.get(Calendar.SECOND);
		String duration = getExecutionTime(time.get(time.size()-1) - time.get(0));
		String query = "update [AdviserAutotmation].[dbo].[tblModuleExecutionSummaries] set StartTime = '"+startTime +"', EndTime = '" + endTime
				 + "', Duration = '"+ duration + "', Passed = " + moduleWisePassedTest + ", Failed = " + moduleWiseFailedTest + ", Skipped = " + moduleWiseSkippedTest
				 + ", Total = " + moduleWiseTotalCount + ", StatusID = " + statusID + ", BrowserID = "+browserID+" where ModuleExecutionSummaryID = " + primaryKey_TestCaseExecutionSummaryID +
				 " and Name = '" + moduleName +"'";
		try{
			st = DatabaseConnection.getCon().prepareStatement(query);
			st.executeUpdate();
		}
		catch (Exception e) {
			LOG.debug(e.getMessage());
		  }
		finally {
			st.close();
			rs.close();
		}
	}
	
	private static String getExecutionTime(long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
	
	public void updatePortalDB() throws SQLException {
		tblProjectExecutionDetails();
		tblTestCaseExecutionSummaries();
	}

}
