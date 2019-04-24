package helper;


/**
This Enum has all the constant values used in the framework.
 */

public enum Constants {

	/* Path for different browser drivers */
	FIREFOXDRIVER(System.getProperty("user.dir") + "\\drivers\\Firefox\\geckodriver.exe"),

	CHROMEDRIVER(System.getProperty("user.dir") + "\\drivers\\Chrome\\chromedriver.exe"),

	INTERNETEXPLORERDRIVER(System.getProperty("user.dir") + "\\drivers\\IE11\\IEDriverServer.exe"),

	/* Path for different excels used */
	EXCELTESTCASEPATH(System.getProperty("user.dir") + "\\ProjectAutomationFiles\\TestCases"),

	EXCELREUSABLEPATH(System.getProperty("user.dir") + "\\ProjectAutomationFiles\\ProjectResuables"),

	EXCELTESTDATAPATH(System.getProperty("user.dir") + "\\ProjectAutomationFiles\\TestData"),

	EXCELOBJECTREPOPATH(System.getProperty("user.dir") + "\\ProjectAutomationFiles\\ObjectRepository"),

	REPROTINGFORMATDIR(System.getProperty("user.dir") + "\\src\\test\\resources\\Report Format\\Final Report.xlsx"),

	COPIEDFILESDIR(System.getProperty("user.dir") + "\\src\\test\\resources\\CopiedFiles"),

	/* Path for different dynamic class and XMLs created */
	DYNAMICCLASSDIR(System.getProperty("user.dir") + "\\src\\main\\java\\testcases"),

	JAVA_HOME("C:/Program Files/Java/jdk1.8.0_102"),
	
	DYNAMICXMLDIR(System.getProperty("user.dir") + "\\src\\main\\resources\\dynamicXML\\XMLs"),

	CLASSLOADER(System.getProperty("user.dir") + "\\target\\test-classes\\testcases"),
	
	/* Text to write on dynamic XMLs and classes */
	PACKAGENAME("testcases."),

	CLASSTEXT("package testcases;\r\n" + "\r\n" + "import org.testng.annotations.Test;\r\n"
			+ "import readexcel.*;\r\n" + "import exceptions.FrameworkExceptions;"),
	
	SQLDIRECTORY(System.getProperty("user.dir") +"\\SQLQueries");

	private final String constants;

	private Constants(String constants) {
		this.constants = constants;
	}

	public String getConstants() {
		return this.constants;
	}

	public String toString() {
		return this.constants;
	}
}
