package helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import helper.Constants;
import helper.DynamicClassCreator;
import readexcel.ReadTestcaseFile;

/**
 * This class creates the dynamic testng XMLs for each excel test suite. Once
 * dynamic testng XMLs are created it executes them.
 */

public class DynamicSuiteFileCreator {

	public static XmlSuite createTestngXMLSuite(String suiteName, String tcID) throws Exception {
		Map<String, String> browser = new HashedMap<>();
		browser.put("browser", WebdriverHelper.browserConfig);
		XmlSuite suite = new XmlSuite();
		suiteName = suiteName.replaceAll("\\s+", "");
		suite.setName(suiteName);
		suite.addListener("helper.AnnotationTransformer");
		suite.addListener("helper.MyTestListenerAdapter");
		String className = suiteName;
		suite.setParameters(browser);
		XmlClass packageName = null;
		if (tcID.contains(",")) {
			for (String id : tcID.split(",")) {
				id = id.replace(".", "_").trim();
				if(checkTestcaseID(id)) {
					writeXML(suite,"invalidmethodname",packageName, className );
				}
				else{
					writeXML(suite, id, packageName, className);
				}
			}
		} else {
			tcID = tcID.replace(".", "_").trim();
			if(checkTestcaseID(tcID)) {
				writeXML(suite,"invalidmethodname",packageName, className );
			}
			else{
				writeXML(suite, tcID, packageName, className);
			}
		}
		return suite;
	}

	public static Map<String, XmlSuite> createRegressionXML() throws Exception {
		File ExcelFileToRead = new File(Constants.EXCELTESTCASEPATH.toString());
		File[] files = ExcelFileToRead.listFiles();
		Map<String, XmlSuite> regressionSuites = new HashMap<String, XmlSuite>();
		for (File f : files) {
			Map<String, String> browser = new HashedMap<>();
			String className = FilenameUtils.removeExtension(f.getName());
			className = className.replaceAll("\\s+", "");
			if(Readgridfile.gridvalues.containsKey(className)) {
				browser.put("browser", Readgridfile.gridvalues.get(className));
			}
			XmlSuite suite = new XmlSuite();
			suite.setName(className);
			suite.addListener("helper.AnnotationTransformer");
			suite.addListener("helper.MyTestListenerAdapter");
			suite.setParameters(browser);
			XmlClass packageName = null;
			for (String TCid : ReadTestcaseFile.testcases_persuite.keySet()) {
				if(className.equalsIgnoreCase(TCid.split("\\.")[0])) {
					writeXML(suite, className, TCid, packageName, regressionSuites);
				}
			}
			browser.clear();
		}
		return regressionSuites;
	}
	
	public static XmlSuite createFinalRegressionxml() throws IOException {
		File singleXML = new File(Constants.DYNAMICXMLDIR.toString());
		File[] files = singleXML.listFiles();
		List<String> path = new ArrayList<>();
		XmlSuite suite = new XmlSuite();
		for (File f : files) {
			path.add(f.getAbsolutePath());		
		}
		suite.setName("All Suites");
		suite.setSuiteFiles(path);
		FileWriter writer = new FileWriter(new File(System.getProperty("user.dir") + "\\src\\main\\resources\\dynamicXML", "regression" + ".xml"));
		writer.write(suite.toXml());
		writer.flush();
		writer.close();
		return suite;
	}
	
	/*public static Map<String, XmlSuite> createSmokeXML() throws Exception {
		File ExcelFileToRead = new File(Constants.EXCELTESTCASEPATH.toString());
		File[] files = ExcelFileToRead.listFiles();
		Map<String, XmlSuite> smokeSuites = new HashMap<String, XmlSuite>();
		for (File f : files) {
			Map<String, String> browser = new HashedMap<>();
			String className = FilenameUtils.removeExtension(f.getName());
			if(Readgridfile.gridvalues.containsKey(className)) {
				browser.put("browser", Readgridfile.gridvalues.get(className));
			}
			XmlSuite suite = new XmlSuite();
			suite.setName(className);
			suite.addListener("com.SSPWorldWide.Framework.Adviser.Helper.AnnotationTransformer");
			suite.addListener("com.SSPWorldWide.Framework.Adviser.Helper.MyTestListenerAdapter");
			suite.setParameters(browser);
			suite.setConfigFailurePolicy(FailurePolicy.SKIP);
			XmlClass packageName = null;
			for (String TCid : ReadTestcaseFile.testcases_persuite.keySet()) {
				if (ReadTestcaseFile.smokeTestCases.get(TCid) != null
						&& ReadTestcaseFile.smokeTestCases.get(TCid).equalsIgnoreCase("smoke")&&(className.equalsIgnoreCase(TCid.split("\\.")[0]))) {
					writeXML(suite, className, TCid, packageName, smokeSuites);
				}
			}
		}
		return smokeSuites;
	}*/

	private static void createXMLsuite(String suiteName,Map<String, String> browser, Map<String, XmlSuite> regressionSuites) throws IOException {
		XmlSuite suite = new XmlSuite();
		suiteName = suiteName.replaceAll("\\s+", "");
		suiteName = suiteName.trim();
		String className = suiteName;
		suite.setName(className);
		suite.addListener("helper.AnnotationTransformer");
		suite.addListener("helper.MyTestListenerAdapter");
		suite.setParameters(browser);
		XmlClass packageName = null;
		for (String TCid : ReadTestcaseFile.testcases_persuite.keySet()) {
		if(checkTestcaseID(TCid)) {
			writeXML(suite, className, "invalidmethodname", packageName, regressionSuites);
		}
//			if(className.equalsIgnoreCase(TCid.split("\\.")[0])){
				writeXML(suite, className, TCid, packageName, regressionSuites);
//			}
		}
	}
	
	private static boolean checkTestcaseID(String id) {
		return Character.isDigit(id.charAt(0));
	}
	
	public static Map<String, XmlSuite> createSingleTestSuiteRegressionXML(String testSuiteName) throws IOException {
		Map<String, XmlSuite> regressionSuites = new HashMap<String, XmlSuite>();
		testSuiteName = testSuiteName.replaceAll("\\s+", "");
		Map<String, String> browser = new HashedMap<>();
		browser.put("browser", WebdriverHelper.browserConfig);
		if (testSuiteName.contains(",")) {
			for (String suiteName : testSuiteName.split(",")) {
				createXMLsuite(suiteName, browser, regressionSuites);
				
			}
		} else {			
			createXMLsuite(testSuiteName, browser, regressionSuites);
		}
		return regressionSuites;
	}

	private static void writeXML(XmlSuite suite, String className, String tcid, XmlClass packageName,
		Map<String, XmlSuite> regressionSuites) throws IOException {
		XmlTest test = new XmlTest(suite);
		tcid = tcid.replace(".", "_").trim();
		test.setName(tcid);
		List<XmlClass> classes = new ArrayList<>();
		packageName = new XmlClass(Constants.PACKAGENAME.toString() + className);
		XmlInclude method = null;
		List<XmlInclude> inmethods = new ArrayList<>();
		method = new XmlInclude(tcid);
		inmethods.add(method);
		packageName.setIncludedMethods(inmethods);
		classes.add(packageName);
		test.setXmlClasses(classes);
		regressionSuites.put(className, suite);
		try(FileWriter writer = new FileWriter(new File(Constants.DYNAMICXMLDIR.toString(), className + ".xml"));){
		writer.write(suite.toXml());
		writer.flush();
		}
	}

	private static void writeXML(XmlSuite suite, String TCid, XmlClass packageName, String className)
			throws IOException {
		XmlTest test = new XmlTest(suite);
		TCid = TCid.replace(".", "_").trim();
		test.setName(TCid);
		List<XmlClass> classes = new ArrayList<>();
		packageName = new XmlClass(Constants.PACKAGENAME.toString() + className);
		XmlInclude method = null;
		List<XmlInclude> inmethods = new ArrayList<>();
		method = new XmlInclude(TCid);
		inmethods.add(method);
		packageName.setIncludedMethods(inmethods);
		classes.add(packageName);
		test.setXmlClasses(classes);
		try(FileWriter writer = new FileWriter(new File(Constants.DYNAMICXMLDIR.toString(), className + ".xml"));){
		writer.write(suite.toXml());
		writer.flush();
		}
	}

	public static void runTestNG(XmlSuite suite) {
		TestNG tng = new TestNG();
		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(suite);
		tng.setXmlSuites(suites);
		TestListenerAdapter adapter = new TestListenerAdapter();
		tng.addListener(adapter);
	    tng.setParallel("parallel");
	    tng.setSuiteThreadPoolSize(WebdriverHelper.NODEINSTANCES);
		tng.run();
	}

	public static void runSingleTestCase(String testSuiteName, String testcaseID) throws Exception {
		DynamicClassCreator.createSingleTestCaseClass(testSuiteName, testcaseID);
		DynamicClassCreator.makeDirectory(Constants.DYNAMICXMLDIR.toString());
		XmlSuite suite = createTestngXMLSuite(testSuiteName, testcaseID);
		runTestNG(suite);
	}

	public static void runSingleSuite(String testSuiteName) throws Exception {
		DynamicClassCreator.createSingleSuiteClasse(testSuiteName);
		DynamicClassCreator.makeDirectory(Constants.DYNAMICXMLDIR.toString());
		createSingleTestSuiteRegressionXML(testSuiteName);
		XmlSuite suite = createFinalRegressionxml();
		runTestNG(suite);
	}

	public static void runRegressionSuite() throws Exception {
		DynamicClassCreator.createWholeSuiteClasses();
		DynamicClassCreator.makeDirectory(Constants.DYNAMICXMLDIR.toString());
		createRegressionXML();
		XmlSuite suite = createFinalRegressionxml();
		runTestNG(suite);
	}

	public static void runSmokeSuite() throws Exception {
		DynamicClassCreator.createWholeSuiteClasses();
		DynamicClassCreator.makeDirectory(Constants.DYNAMICXMLDIR.toString());
		createRegressionXML();
		XmlSuite suite = createFinalRegressionxml();
		runTestNG(suite);
	}

}