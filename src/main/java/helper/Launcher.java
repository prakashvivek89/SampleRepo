package helper;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import exceptions.FrameworkExceptions;
import readexcel.ReadObjectRepo;
import readexcel.ReadProjectReusables;
import readexcel.ReadTestcaseFile;
import readexcel.TestDataReader;

/**
 * This class is a start point of the automation execution.
 */

public class Launcher {
	static String currDir = null;
	public static String startTime;
	public static String endTime;
	public static long totalTime;
	public static Timestamp DBstartTime;
	private static final Logger LOG = LoggerFactory
			.getLogger(Launcher.class);
	public static String reportFolder;
	public static String  ProjectFolder;
	public static Instant start;
	public static Instant end;

	@Test
	public void launchAutomation() throws Exception {
		WebdriverHelper.launch();
	}
		/**  In this method, report folder is created and all the excel files are copied to a temp folder */	
	
	@BeforeSuite
	public static void beforeSuite(ITestContext ctx){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		DBstartTime = getDateWithDay();
		Calendar cal = Calendar.getInstance();
		startTime = sdf.format(cal.getTime());
		start = Instant.now();
		try {
		createReportFolder();
		DatabaseConnection.getInstance().createDBConnection(WebdriverHelper.JDBCURL, WebdriverHelper.JDBCDRIVER, WebdriverHelper.DBUser, WebdriverHelper.DBPassword);
				
		checkSuiteType(WebdriverHelper.suiteType);
		
		Readgridfile.getInstance().getbrowser();
		
		if (WebdriverHelper.runRegression.equalsIgnoreCase("yes")) {
			WebdriverHelper.suiteType = "Regression";
			CopyFiles.copyFileForRegression();
		} 
		
		else if (WebdriverHelper.runSmoke.equalsIgnoreCase("yes")) {
			WebdriverHelper.suiteType = "Smoke";
			CopyFiles.copyFileForRegression();
		}
		
		else if (!(WebdriverHelper.testSuiteName.isEmpty())&&!(WebdriverHelper.testcaseID.isEmpty())) {
			CopyFiles.copySingleFile(WebdriverHelper.testSuiteName);
		} 
		else if (!(WebdriverHelper.testSuiteName.isEmpty())&&(WebdriverHelper.testcaseID.isEmpty())) {
			CopyFiles.copySingleFile(WebdriverHelper.testSuiteName);
		} 
		
		else if (WebdriverHelper.testSuiteName.isEmpty()&&!(WebdriverHelper.testcaseID.isEmpty())
				&&!(WebdriverHelper.runSmoke.equalsIgnoreCase("yes"))&&!(WebdriverHelper.runRegression.equalsIgnoreCase("yes"))) {
			throw new FrameworkExceptions("&ensp;&ensp; suiteName is not defined in config file");
		}
		
		ReadObjectRepo.getInstance().getORData();
		
		ReadProjectReusables.getInstance().getMethodActions();
		
		ReadTestcaseFile.getInstance().readAllFiles(WebdriverHelper.suiteType);
		
		TestDataReader.getInstance().readAllFiles();
		}
		catch (Exception e) {
			Reporting.createReport("Configuaration Error","");
			Reporting.createExtentTest("config Error");
			Reporting.extentTestMap.get((int)(Thread.currentThread().getId())).log(Status.FAIL, "<b>Failure Message   :   </b> " + e.toString());
			Reporting.flushReport();
			LOG.error(e.toString());
			System.exit(0);
		}
	}

	/**  In this method, final excel report is generated and the temp folder containing the copied excel file is deleted
	 * @throws IOException */
	
	@AfterSuite
	public static void afterSuite() throws IOException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		endTime = sdf.format(cal.getTime());
		end = Instant.now();
		totalTime = Duration.between(start, end).toMillis();
		try {
			if((WebdriverHelper.runRegression.equalsIgnoreCase("yes"))||(WebdriverHelper.runSmoke.equalsIgnoreCase("yes"))){
				PortalReport.getInstance().updatePortalDB();
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}	
			finally {
//				CopyFiles.deleteFlder();
				DatabaseConnection.getInstance().closeDBConnection();
			}
	}
	
	private static Timestamp getDateWithDay() {
		Calendar cal = Calendar.getInstance();  
		return new java.sql.Timestamp(cal.getTimeInMillis());
	}

	private static void createReportFolder() {
		reportFolder = getCurrentDate();
		ProjectFolder = System.getProperty("user.dir");
		String sy [] = ProjectFolder.split("\\\\");
		ProjectFolder = sy[sy.length-1];
		currDir = System.getProperty("user.dir") + File.separator + "Report" + File.separator + reportFolder;
		DynamicClassCreator.makeDirectory(currDir);
		DynamicClassCreator.makeDirectory(currDir + File.separator + "Screenshots");
		DynamicClassCreator.makeDirectory(currDir + File.separator + "Detailed Report");
	}

	private static String getCurrentDate() {
		final DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String currentDate = sdf.format(cal.getTime());
		currentDate = currentDate.replace("/", "-").replace(":", "-");
		currentDate = currentDate.split("-")[0] +" " + getMonth(currentDate.split("-")[1]) +" " + currentDate.split("-")[2] + "-"
				+ currentDate.split("-")[3] + "-" + currentDate.split("-")[4];
		return currentDate;
	}

	public static String getMonth(String mnth) {
		Map<String, String> monthMap = new HashedMap<>();
		monthMap.put("01", "Jan");
		monthMap.put("02", "Feb");
		monthMap.put("03", "Mar");
		monthMap.put("04", "Apr");
		monthMap.put("05", "May");
		monthMap.put("06", "Jun");
		monthMap.put("07", "Jul");
		monthMap.put("08", "Aug");
		monthMap.put("09", "Sep");
		monthMap.put("10", "Oct");
		monthMap.put("11", "Nov");
		monthMap.put("12", "Dec");
		return monthMap.get(mnth);
	}
	
	
	private static void checkSuiteType(String suiteType) throws FrameworkExceptions {
		if(suiteType.isEmpty()) {
			throw new FrameworkExceptions("Suite type is not defined in the config file. SuiteType must contain values either Regression or Smoke");
		}
		else if(!(suiteType.equalsIgnoreCase("Smoke")||suiteType.equalsIgnoreCase("Regression"))) {
			throw new FrameworkExceptions("Invalid suiteType '<b>"+suiteType+"'</b> is defined in Config file. SuiteType must contain values either Regression or Smoke");
		}
		
		else if(suiteType.equalsIgnoreCase("Smoke")||suiteType.equalsIgnoreCase("Regression")) {
			WebdriverHelper.suiteType = suiteType;
		}
	}

}