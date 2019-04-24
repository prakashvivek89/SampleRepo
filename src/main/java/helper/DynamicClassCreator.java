package helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import helper.Constants;
import readexcel.ReadTestcaseFile;

/**
 * This class creates the dynamic class for each test case excel file. Methods
 * in the dynamic class are the test case id mentioned in the excel file.
 */

public class DynamicClassCreator {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(DynamicClassCreator.class);

	public static void createWholeSuiteClasses() throws Exception {
		makeDirectory(Constants.DYNAMICCLASSDIR.toString());
		File excelFileToRead = new File(Constants.EXCELTESTCASEPATH.toString());
		File[] files = excelFileToRead.listFiles();
		for (File f : files) {
			dynamicMethodCreation(f);
		}
		compile();
		dynamicClassLoader();
	}
	
	
	static String message = "1. All Testcase Id names must begin with a letter of the alphabet, an underscore, or ( _ ), or a dollar sign ($). The convention is to always use a letter of the alphabet.<br /> 2. No spaces or special characters are allowed.<br />3. You cannot use a java keyword (reserved word) for a testcase id name";

	/**public static void createSmokeSuiteClasses() throws Exception {
		makeDirectory(Constants.DYNAMICCLASSDIR.toString());
		File excelFileToRead = new File(Constants.EXCELTESTCASEPATH.toString());
		File[] files = excelFileToRead.listFiles();
		for (File f : files) {
			dynamicMethodCreation(f);
		}
		compile();
		dynamicClassLoader();
	}
*/
	public static void createSingleSuiteClasse(String suiteName) throws Exception {
		makeDirectory(Constants.DYNAMICCLASSDIR.toString());
		if (suiteName.contains(",")) {
			for (String suite : suiteName.split(",")) {
				suiteName = suiteName.replaceAll("\\s+", "");
				suite = suite.trim();
				dynamicMethodCreation(suite);
			}
		} else {
			suiteName = suiteName.trim();
			suiteName = suiteName.replaceAll("\\s+", "");
			dynamicMethodCreation(suiteName);
		}
		compile();
		dynamicClassLoader();
	}
	
	
	private static void createErrorMethod(BufferedWriter bufferedWriter, String testCaseID, String suiteName)
			throws IOException {
		bufferedWriter.newLine();
		bufferedWriter.write(" @Test");
		bufferedWriter.newLine();
		bufferedWriter.write(" public void " + testCaseID.replace(".", "_") + "() throws FrameworkExceptions{");
		bufferedWriter.newLine();
		bufferedWriter.write("throw new FrameworkExceptions (\"&ensp;:&ensp; TestcaseID '<b>" + testCaseID
				+ "'</b> does not exist in the test suite <b>" + suiteName + "</b> \");");
		bufferedWriter.newLine();
		bufferedWriter.write("}");
	}
	
	private static void invalidMethodName(BufferedWriter bufferedWriter, String testCaseID, String suiteName)
			throws IOException {
		bufferedWriter.newLine();
		bufferedWriter.write(" @Test");
		bufferedWriter.newLine();
		bufferedWriter.write(" public void invalidmethodname() throws FrameworkExceptions{");
		bufferedWriter.newLine();
		bufferedWriter.write("throw new FrameworkExceptions (\"&ensp;&ensp; TestcaseID '<b>" + testCaseID
				+ "'</b> is invalid in <b>" + suiteName + "</b>. Kindly follow the below rules to define the testcase ID name : <br />" +message+"\");");
		bufferedWriter.newLine();
		bufferedWriter.write("}");
	}

	public static void createSingleTestCaseClass(String suiteName, String tcID) throws Exception {
		makeDirectory(Constants.DYNAMICCLASSDIR.toString());
		suiteName = suiteName.replaceAll("\\s+", "");
		String fileName = suiteName + ".java";
		File myFIle = new File(Constants.DYNAMICCLASSDIR.toString(), fileName);
		FileWriter fileWriter = new FileWriter(myFIle);
		try(BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);){
		bufferedWriter.write(Constants.CLASSTEXT.toString());
		bufferedWriter.newLine();
		bufferedWriter.write("public class " + suiteName + " extends TestcaseFlow{");
		if(tcID.contains(",")) {
			for(String id : tcID.split(",")) {
				id = id.trim();
					if (!(ReadTestcaseFile.testcases_persuite.get(id).isEmpty())&&!(checkTestcaseID(id))) {
					dynamicMethodCreation(bufferedWriter, id);
				} else if (ReadTestcaseFile.testcases_persuite.get(id).isEmpty()&&!(checkTestcaseID(id))) {
					createErrorMethod(bufferedWriter, id, suiteName);
				}
				else if(checkTestcaseID(id)) {
					invalidMethodName(bufferedWriter, id, suiteName);
				}
				}
		}
		else {
			if (!(ReadTestcaseFile.testcases_persuite.get(tcID).isEmpty())&&!(checkTestcaseID(tcID))) {
				dynamicMethodCreation(bufferedWriter, tcID);
			} else if (ReadTestcaseFile.testcases_persuite.get(tcID).isEmpty()&&!(checkTestcaseID(tcID))) {
				createErrorMethod(bufferedWriter, tcID, suiteName);
			}
			else if(checkTestcaseID(tcID)) {
				invalidMethodName(bufferedWriter, tcID, suiteName);
			}
			}
		
		bufferedWriter.newLine();
		bufferedWriter.write("}");
		bufferedWriter.newLine();
		}
		compile();
		dynamicClassLoader();
		
		
	}

	public static void makeDirectory(String path) {
		File theDir = new File(path);

		if (!theDir.exists()) {
			LOG.info("creating directory: " + theDir.getName());
			boolean result = false;
			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
			}
			if (result) {
				LOG.info("DIR created");
			}
		}
	}

	public static void dynamicClassLoader(){
		try {
			File file = new File(Constants.CLASSLOADER.toString());
			File[] files = file.listFiles();
			for (File f : files) {
				URL url = f.toURI().toURL();
				URL[] urls = new URL[] { url };
				URLClassLoader cl = URLClassLoader.newInstance(urls);
				cl.loadClass(Constants.PACKAGENAME.toString() + f.getName().split("\\.")[0].trim());
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}
	}

	public static void compile() throws IOException {
		System.setProperty("java.home", Constants.JAVA_HOME.toString());
		File file = new File(Constants.DYNAMICCLASSDIR.toString());
		File[] files = file.listFiles();
		for (File f : files) {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
					Arrays.asList(new File(System.getProperty("user.dir") + "/target/test-classes")));
			Iterable<? extends JavaFileObject> compilationUnits = fileManager
					.getJavaFileObjectsFromFiles(Arrays.asList(f));
			compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
			fileManager.close();
		}
	}

	public static void dynamicMethodCreation(File f) throws Exception {
		if (!(f.getName().contains("~"))) {
			String fileName = FilenameUtils.removeExtension(f.getName()) + ".java";
			String className = FilenameUtils.removeExtension(f.getName());
			className = className.replaceAll("\\s+", "");
			File myFIle = new File(Constants.DYNAMICCLASSDIR.toString(), fileName);
			FileWriter fileWriter = new FileWriter(myFIle);
			try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);) {
				bufferedWriter.write(Constants.CLASSTEXT.toString());
				bufferedWriter.newLine();
				bufferedWriter.write("public class " + className + " extends TestcaseFlow{");
				for (String testCaseID : ReadTestcaseFile.testcases_persuite.keySet()) {
						dynamicMethodCreation(bufferedWriter, testCaseID);
				}
				bufferedWriter.newLine();
				bufferedWriter.write("}");
				bufferedWriter.newLine();
			}
		}
	}

	public static void dynamicMethodCreation(String suiteName) throws IOException {
		if (!(suiteName.contains("~"))) {
			suiteName = suiteName.replaceAll("\\s+", "");
			String fileName = suiteName + ".java";
			String className = suiteName;
			File myFIle = new File(Constants.DYNAMICCLASSDIR.toString(), fileName);
			FileWriter fileWriter = new FileWriter(myFIle);
			try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);) {
			bufferedWriter.write(Constants.CLASSTEXT.toString());
			bufferedWriter.newLine();
			bufferedWriter.write("public class " + className + " extends TestcaseFlow{");
			for (String testCaseID : ReadTestcaseFile.testcases_persuite.keySet()) {
					dynamicMethodCreation(bufferedWriter, testCaseID);
			}
			bufferedWriter.newLine();
			bufferedWriter.write("}");
			bufferedWriter.newLine();
		}
		}
	}
	
	public static void dynamicMethodCreation(BufferedWriter bufferedWriter, String testCaseID) throws IOException {
		bufferedWriter.newLine();
		bufferedWriter
				.write(" @Test(description = \"" + ReadTestcaseFile.scenarioNames.get(testCaseID) + "\")");
		bufferedWriter.newLine();
		bufferedWriter.write(" public void " + testCaseID.replace(".", "_") + "() throws Exception{");
		bufferedWriter.newLine();
		bufferedWriter.write("runSingleTest(\"" + testCaseID + "\");");
		bufferedWriter.newLine();
		bufferedWriter.write("}");
	}
	
	private static boolean checkTestcaseID(String id) {
		return Character.isDigit(id.charAt(0));
	}
	
	
	/**bufferedWriter.newLine();
	bufferedWriter.write(" @Test(description = \"" + ReadTestcaseFile.scenarioNames.get(tcID) + "\")");
	bufferedWriter.write(" @Test");
	bufferedWriter.newLine();
	bufferedWriter.write(" public void " + tcID.replace(".", "_") + "() throws FrameworkExceptions{");
	bufferedWriter.newLine();
	bufferedWriter.write("throw new FrameworkExceptions(\"&ensp;:&ensp; TestcaseID '<b>" + tcID+ "'</b> does not exist in the test suite <b>" +suiteName + "</b> \");");
	bufferedWriter.newLine();
	bufferedWriter.write("}");*/
	
	
	/**		bufferedWriter.newLine();
	bufferedWriter.write(" @Test(description = \"" + ReadTestcaseFile.scenarioNames.get(id) + "\")");
	bufferedWriter.write(" @Test");
	bufferedWriter.newLine();
	bufferedWriter.write(" public void " + id.replace(".", "_") + "() throws FrameworkExceptions{");
	bufferedWriter.newLine();
	bufferedWriter.write("throw new FrameworkExceptions (\"&ensp;:&ensp; TestcaseID '<b>"  + id+ "'</b> does not exist in the test suite <b>" +suiteName + "</b> \");");
	bufferedWriter.newLine();
	bufferedWriter.write("}");
	*/
}