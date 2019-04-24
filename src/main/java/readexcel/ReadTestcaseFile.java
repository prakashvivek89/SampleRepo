package readexcel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import exceptions.ProjectReusableException;
import exceptions.FrameworkExceptions;
import helper.Constants;

/**
 * This class reads the object repository file.
 */

public class ReadTestcaseFile extends ExcelReusables {
	public static final ListMultimap<String, Map<String, String>> testcases_persuite = ArrayListMultimap.create();
	public static final ListMultimap<String, String[]> testcases_persuite1 = ArrayListMultimap.create();
	public static Map<String, String> scenarioNames = new HashMap<>();

	private static ReadTestcaseFile readTC;

	private ReadTestcaseFile() {
	}

	public static ReadTestcaseFile getInstance() {
		if (readTC == null) {
			readTC = new ReadTestcaseFile();
		}
		return readTC;
	}

	public static List<Map<String, String>> getTestCaseSteps(String testcaseID) throws FrameworkExceptions {
		if (!(testcases_persuite.get(testcaseID).isEmpty())) {
			return testcases_persuite.get(testcaseID);
		} else {
			throw new FrameworkExceptions("Testcase ID '" + testcaseID + "' does not exist");
		}
	}

	/** Reads the excel file  */
	
	private Sheet verifySheetPresent(Workbook workbook, String suiteType) {
		Sheet sheet = null;
		int totalnumberofSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < totalnumberofSheets; i++) {
			if (workbook.getSheetName(i).toLowerCase().contains(suiteType.toLowerCase())) {
				if (suiteType.contains("regression")) {
					sheet = workbook.getSheet("RegressionTestcases");
				} else if (suiteType.contains("smoke")) {
					sheet = workbook.getSheet("SmokeTestcases");
				}
			}
		}
		return sheet;
	}
	

	private void readTestcaseFile(String suiteName, String suitetype)
			throws IOException, ProjectReusableException, InvalidFormatException {
		Workbook workbook = readFile("TestCases", suiteName);
		suitetype = suitetype.toLowerCase();
		if (verifySheetPresent(workbook, suitetype)!=null) {
			Sheet sheet = verifySheetPresent(workbook, suitetype);
			int totalnumberrow = sheet.getLastRowNum();
			for (int i = 1; i <= totalnumberrow; i++) {

					if (!(isRowEmpty(sheet.getRow(i)))
							&&!(getCellValueString(sheet.getRow(i).getCell(1)).isEmpty())) {
						String testcaseID = getCellValueString(sheet.getRow(i).getCell(1));
						if (testcases_persuite.containsKey(testcaseID)) { 
							throw new ProjectReusableException("Testestcase '" + testcaseID + "' already exist in the module '" + suiteName + "'");
						}
						else {
							for (int j = i; j <= totalnumberrow; j++) {

								if (!(isRowEmpty(sheet.getRow(j)))) {

									String scenarioName = getCellValueString(sheet.getRow(i).getCell(2));
									String methodName = getCellValueString(sheet.getRow(j).getCell(4));
									String testdata = getCellValueString(sheet.getRow(j).getCell(5));

									Map<String, String> methodNameWithTestdata = new HashMap<>();
									methodNameWithTestdata.put("methodname", methodName);
									methodNameWithTestdata.put("testdata", testdata);
									
									scenarioNames.put(testcaseID, scenarioName);
									testcases_persuite.put(testcaseID, methodNameWithTestdata);
								} else {
									break;
								}
							}
						}
					}

			}
		}
		workbook.close();
	}
	
	/**private void readTestcaseFile(String suiteName, String suitetype)
			throws IOException, DuplicateIDException, InvalidFormatException {
		Workbook workbook = readFile("TestCases", suiteName);
		suitetype = suitetype.toLowerCase();
		Sheet sheet = null;

		if (verifySheetPresent(workbook, suitetype)) {
			if (suitetype.contains("regression")) {
				sheet = workbook.getSheet("RegressionTestcases");
			} else if (suitetype.contains("smoke")) {
				sheet = workbook.getSheet("SmokeTestcases");
			}
			int totalnumberrow = sheet.getLastRowNum();
			for (int i = 1; i <= totalnumberrow; i++) {

				try {
					
					if (!(isRowEmpty(sheet.getRow(i)))
							&& !(getCellValueString(sheet.getRow(i).getCell(1)).isEmpty())&&!(testcases_persuite.containsKey(getCellValueString(sheet.getRow(i).getCell(1))))) {
						String testcaseID = getCellValueString(sheet.getRow(i).getCell(1));
							for (int j = i; j <= totalnumberrow; j++) {
								
								if (!(isRowEmpty(sheet.getRow(j)))) {

									String scenarioName = getCellValueString(sheet.getRow(i).getCell(2));
									String methodName = getCellValueString(sheet.getRow(j).getCell(4));
									String testdata = getCellValueString(sheet.getRow(j).getCell(5));

									Map<String, String> methodNameWithTestdata = new HashMap<>();
									methodNameWithTestdata.put("methodname", methodName);
									methodNameWithTestdata.put("testdata", testdata);

									System.out.println(testcaseID + "   :    " + methodName + "   " + testdata);
									
									String[] stepsAndTestdata = new String[2];
									stepsAndTestdata[0] = methodName;
									stepsAndTestdata[1] = testdata;
									
									scenarioNames.put(testcaseID, scenarioName);
									testcases_persuite.put(testcaseID, methodNameWithTestdata);
								} else {
									break;
								}
							}
						}
					else{
						throw new DuplicateIDException(
							"Testestcase '" + getCellValueString(sheet.getRow(i).getCell(1)) + "' already exist in the module '" + suiteName + "'");
					}

				} catch (Exception e) {
					Reporting.createReport(suiteName, "");
					Reporting.createExtentTest(sheet.getRow(i).getCell(1).toString() + "&ensp;:&ensp;"
							+ sheet.getRow(i).getCell(2).toString());
					Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).log(Status.FAIL,
							suiteName + "<br />" + "<b>Failure reason :&emsp;</b>" + e.toString());
					Reporting.flushReport();
				}
			}
		}
		workbook.close();
	}*/

	public void readAllFiles(String suitetype) throws InvalidFormatException, IOException, ProjectReusableException {
		File excelFileToRead = new File(Constants.COPIEDFILESDIR.toString() + File.separator + "TestCases");
		File[] files = excelFileToRead.listFiles();
		for (File f : files) {
			if (!(f.getName().contains("~$"))) {
				readTestcaseFile(FilenameUtils.removeExtension(f.getName()), suitetype);
			}
		}
	}
}

/**
 * public static Map<String, String> smokeTestCases = new HashMap<>();
 * 
 * if (WebdriverHelper.runSmoke.equalsIgnoreCase("yes") &&
 * (testcasetype.equalsIgnoreCase("smoke"))) {
 * smokeTestCases.put(getCellValueString(sheet.getRow(j).getCell(1)),
 * testcasetype); }
 */
