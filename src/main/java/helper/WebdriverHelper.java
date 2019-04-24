package helper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import exceptions.FrameworkExceptions;
import helper.PropReader;
import helper.TakeScreenshotUtility;
import readexcel.ReadProjectReusables;

/**
 * Class contains the method to instantiate the webdriver.
 * */


public class WebdriverHelper {
	private static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();
	public static final String HUBURL ;
	private DesiredCapabilities caps;
	public static final String JDBCURL;
	public static final String JDBCDRIVER;
	public static final String platformName;
	public static String browserDriverPath;
	public static String testcaseID;
	public static String testSuiteName;
	public static String suiteType;
	public static final String runRegression;
	public static final String runSmoke;
	public static final String siteURL;
	public static final String DBUser;
	public static final String DBPassword;
	public static ListMultimap<String, ITestResult> finalReportingMap =  Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
	public static ListMultimap<String, ISuiteResult> finalReportingMap1 =  Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
	public static ListMultimap<String, ExtentTest> testcasewithsteps =  Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
	public static Map<String, String> getObjectRepo = new HashMap<String, String>();
	public static int totalPassCount = 0;
	public static int totalFailCount = 0;
	public static int totalCount = 0;
	public static int totalSkipCount = 0;
	public static final String browserConfig;
	public static final String projectName;
	public static final String serverName;
	public static final int retry;
	public static final String FILEUPLOADPATH;
	public static final int NODEINSTANCES;
	public static final String RUNONGRID;
	public static final String IISReportFolder;
	private static final Logger LOG = LoggerFactory
			.getLogger(WebdriverHelper.class);
	static {
		serverName = PropReader.readWebdriverConfig("server.name");
		projectName = PropReader.readWebdriverConfig("project.name");
		platformName = PropReader.readWebdriverConfig("platform");
		runRegression = PropReader.readWebdriverConfig("runRegressionSuite");
		testSuiteName = PropReader.readWebdriverConfig("suiteName").trim();
		testcaseID = PropReader.readWebdriverConfig("testcaseID").trim();
		runSmoke = PropReader.readWebdriverConfig("runSmokeSuite");
		siteURL = PropReader.readWebdriverConfig("site.url");
		JDBCURL = PropReader.readWebdriverConfig("jdbcUrl");
		JDBCDRIVER = PropReader.readWebdriverConfig("jdbcDriver");
		DBUser = PropReader.readWebdriverConfig("DBUser");
		DBPassword = PropReader.readWebdriverConfig("DBPassword");
		browserConfig = PropReader.readWebdriverConfig("browser.name");
		retry = Integer.parseInt(PropReader.readWebdriverConfig("retry"));
		HUBURL = PropReader.readWebdriverConfig("hubURL");
		suiteType = PropReader.readWebdriverConfig("suiteType");
		FILEUPLOADPATH = PropReader.readWebdriverConfig("fileUploadPath");
		NODEINSTANCES =  Integer.parseInt(PropReader.readWebdriverConfig("nodeInstances"));
		RUNONGRID =  PropReader.readWebdriverConfig("runOnGrid");
		IISReportFolder =  PropReader.readWebdriverConfig("IISReportFolder");
	}

	public void launchDriver(String browserName) throws MalformedURLException {
		try {
			if (platformName.equalsIgnoreCase("windows") && browserName.equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.gecko.driver",
						Constants.FIREFOXDRIVER.toString());
				launchFirefoxDriver();
			} else if (platformName.equalsIgnoreCase("windows") && browserName.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver",
						Constants.CHROMEDRIVER.toString());
				launchChromeDriver();
			} else if (platformName.equalsIgnoreCase("windows") && browserName.equalsIgnoreCase("IE")) {
				System.setProperty("webdriver.ie.driver",
						Constants.INTERNETEXPLORERDRIVER.toString());
				launchIEDriver();
			} 
			else if (platformName.equalsIgnoreCase("windows") && browserName.equalsIgnoreCase("phantomjs")) {
				launchPhantomJSDriver();
			}
			else {
				throw new FrameworkExceptions(
						"Please check Platform : " + platformName + " and browser : " + browserName);
			}
		} catch (FrameworkExceptions e) {
			LOG.error(e.getMessage());
		}
	}

	private void launchIEDriver() throws MalformedURLException {
			LOG.info("launching IE driver");
			if(RUNONGRID.equalsIgnoreCase("false")) {
				driver.set(new InternetExplorerDriver());
			}
			else {
				driver.set(new RemoteWebDriver(new URL(HUBURL), internetExplorercapabilities()));
			}
	}

	private void launchChromeDriver() throws MalformedURLException {
			LOG.info("launching Chrome driver");
			if(RUNONGRID.equalsIgnoreCase("false")) {
				driver.set(new ChromeDriver()); 
			}
			else {
				driver.set(new RemoteWebDriver(new URL(HUBURL), chromecapabilities()));	
			}
	}

	private void launchFirefoxDriver() throws MalformedURLException{
			LOG.info("launching Firefox driver");
			if(RUNONGRID.equalsIgnoreCase("false")) {
				driver.set(new FirefoxDriver()); 
			}
			else{
				driver.set(new RemoteWebDriver(new URL(HUBURL), firefoxDesiredCapabilities()));
			}
	}
	
	private void launchPhantomJSDriver() throws MalformedURLException{
		if(RUNONGRID.equalsIgnoreCase("false")) {
			driver.set(new RemoteWebDriver(new URL(HUBURL), phantomJSDesiredCapabilities()));
		}
		else {
			driver.set(new PhantomJSDriver(phantomJSDesiredCapabilities()));
		}
	}

	private DesiredCapabilities chromecapabilities() {
		caps = DesiredCapabilities.chrome();
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--disable-extensions");
		chromeOptions.addArguments("--disable-web-security");
		chromeOptions.addArguments("--test-type");
		caps.setCapability("chrome.verbose", false);
		caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		return caps;
	}

	protected DesiredCapabilities internetExplorercapabilities() {
		caps = DesiredCapabilities.internetExplorer();
		caps.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);
		caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		caps.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
		caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		caps.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, UnexpectedAlertBehaviour.IGNORE);
		caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		caps.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
		caps.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
		caps.setCapability("ignoreProtectedModeSettings", true);
		caps.setCapability("ignoreZoomSetting", true);
		caps.setCapability("disable-popup-blocking", true);
		caps.setCapability("enablePersistentHover", true);
		caps.setCapability("requireWindowFocus", true);
		return caps;
	}

	private DesiredCapabilities firefoxDesiredCapabilities() {
		caps = DesiredCapabilities.firefox();
		caps.setCapability("disable-restore-session-state", true);
		caps.setCapability("marionette", true);
		caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		caps.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		return caps;
	}
	
	private DesiredCapabilities phantomJSDesiredCapabilities() {
		caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		String[] phantomargs = new  String[] {"--webdriver-loglevel=none" }; 
		caps.setCapability("cookiesEnabled", false);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomargs);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, "--logLevel=NONE");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX,"y");
		caps.setCapability("phantomjs.page.settings.useragent", "mozilla/5.0 (windows nt 6.1; win64; x64; rv:16.0) gecko/20121026 firefox/16.0");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
		System.getProperty("user.dir") + "\\drivers\\phantomJS\\phantomjs.exe");
		return caps;
	}

	public static synchronized WebDriver getWebDriver() {
		return driver.get();
	}

	public void closeBrowser() {
		getWebDriver().quit();
	}
	
	@BeforeSuite @Parameters("browser") 
	public void beforeSuite(ITestContext ctx, String browser){
		String suitename = ctx.getSuite().getName();
		suitename = suitename.replace("_", " ");
		LOG.info("Suite name : " + suitename + "           browser name : " + browser);
		try {			
			Reporting.createReport(suitename, browser);
		} catch (Exception e) {
			Reporting.extentTestMap.get((int)  (Thread.currentThread().getId())).log(Status.FAIL, suitename + "<br />"
					+ "<b>Failure reason :&emsp;</b>" + e.getStackTrace());
		}
	}

	@Parameters("browser") 	@BeforeMethod(alwaysRun = true)
	public void beforeMethod(Method method, String browser) {
		Test test = method.getAnnotation(Test.class);
		if(Reporting.extentTestMap.get((int) (Thread.currentThread().getId()))!=null&&Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).getModel().getName().contains(method.getName())) {
			Reporting.extentReportsMap.get((int) (Thread.currentThread().getId())).removeTest(Reporting.extentTestMap.get((int) (Thread.currentThread().getId())));
		}
		
		Reporting.createExtentTest(method.getName() + "&ensp;:&ensp;" + test.description());
		try{
			launchDriver(browser);
			if(RUNONGRID.equalsIgnoreCase("false")&& browserConfig.equalsIgnoreCase("IE")) {
				Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 255");
			}
			Thread.sleep(5000);
		}
		catch (Exception e) {
			Reporting.extentTestMap.get((int)  (Thread.currentThread().getId())).log(Status.FAIL, "Error" + "<br />"
					+ "<b>Failure reason :&emsp;</b>" + e.toString());
		}
	}
	
	@AfterMethod(alwaysRun = true)
	public synchronized void tearDown(ITestResult result) throws IOException {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis((result.getStartMillis()));
			Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).getModel()
					.setStartTime(calendar.getTime());
			calendar.setTimeInMillis((result.getEndMillis()));
			Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).getModel()
					.setEndTime(calendar.getTime());
			
			
			if (result.getStatus() == ITestResult.FAILURE) {
				String screenshotPath = TakeScreenshotUtility.captureScreenshot(getWebDriver(), result.getName());
				screenshotPath  = screenshotPath.replace(Launcher.currDir, "..");
				Reporting.extentTestMap.get((int) (Thread.currentThread().getId()))
						.addScreenCaptureFromPath(screenshotPath);
				Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).log(Status.FAIL,
						ReadProjectReusables.failuremethodName.get((int) (Thread.currentThread().getId())) + "<br />"
								+ "<b>Failure reason :&emsp;</b>" + result.getThrowable());
			}
		} finally {
			try{
				if(getWebDriver().toString()!=null){
					closeBrowser();
				}
			}catch (Exception e) {
				LOG.error(e.getMessage());
			}
			finalReportingMap.put(result.getTestContext().getCurrentXmlTest().getSuite().getName(), result);
		}
	}
	

	@AfterSuite
	public void afterSuite(ITestContext ctx) {
		try{
			totalCount = totalCount + ctx.getSuite().getAllMethods().size();
		
		Map<String, ISuiteResult> suiteResults = ctx.getSuite().getResults();
		for (ISuiteResult sr : suiteResults.values()) {
            ITestContext tc = sr.getTestContext();
            totalPassCount = totalPassCount + tc.getPassedTests().getAllResults().size();
            totalFailCount = totalFailCount + tc.getFailedTests().getAllResults().size();
            totalSkipCount = totalSkipCount + tc.getSkippedTests().getAllResults().size();
         }
		Reporting.flushReport();
		}
		catch (Exception e) {
		LOG.error(e.getMessage());
		}
	}

	public static void launch() throws Exception {
		if (runRegression.equalsIgnoreCase("yes")) {
			DynamicSuiteFileCreator.runRegressionSuite();
		}

		else if (runSmoke.equalsIgnoreCase("yes")) {
			DynamicSuiteFileCreator.runSmokeSuite();
		}

		else if (!(testSuiteName.isEmpty()) && !(testcaseID.isEmpty())) {
				DynamicSuiteFileCreator.runSingleTestCase(testSuiteName, testcaseID);
		}

		else if (!(testSuiteName.isEmpty()) && (testcaseID.isEmpty())) {
				DynamicSuiteFileCreator.runSingleSuite(testSuiteName);
		}
		
		else if (testSuiteName.isEmpty() && !(testcaseID.isEmpty())) {
			throw new FrameworkExceptions("suiteName is not defined in config file");
	}
	}

}
