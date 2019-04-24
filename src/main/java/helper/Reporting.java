package helper;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.*;

/**
 * Class instantiate the extent report and contains method the configure the extent report properties 
 * */

public class Reporting{

	public static Map<Integer, ExtentReports> extentReportsMap = new HashMap<Integer, ExtentReports>();
	public static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
	
	private static Reporting rep;
	
	private Reporting() {
	}

	public static Reporting getInstance() {
		if (rep == null) {
			rep = new Reporting();
		}
		return rep;
	}
	
	public static synchronized void createReport(String suiteName, String browserName) {
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(Launcher.currDir + File.separator + "Detailed Report" + File.separator + suiteName + ".html");
		htmlReporter.config().setChartVisibilityOnOpen(true);
		htmlReporter.config().setDocumentTitle("Adviser Automation Report");
		if(WebdriverHelper.runRegression.equalsIgnoreCase("yes")) {
		htmlReporter.config().setReportName("Regression cycle");
		}
		else if (WebdriverHelper.runSmoke.equalsIgnoreCase("yes")) {
			htmlReporter.config().setReportName("Smoke cycle");
		}
		htmlReporter.getSystemAttributeContext();
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
		htmlReporter.config().setTheme(Theme.STANDARD);
		ExtentReports report = new ExtentReports();
		report.attachReporter(htmlReporter);
		report.setReportUsesManualConfiguration(true);
		report.setSystemInfo("Browser : ", browserName);
		report.setSystemInfo("Environment", "QA");
		report.setAnalysisStrategy(AnalysisStrategy.TEST);
		try {
			report.setSystemInfo("Machine", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
		}
		extentReportsMap.put((int) (long) (Thread.currentThread().getId()), report);
	}

	public static synchronized void flushReport() {
		extentReportsMap.get((int) (long) (Thread.currentThread().getId())).flush();
	}

	public static synchronized void createExtentTest(String testName) {
		ExtentTest test = extentReportsMap.get((int) (long) (Thread.currentThread().getId())).createTest(testName);
		test.assignAuthor("ADVISOR&emsp;AUTOMATION");
		extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
	}
}
