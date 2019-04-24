package readexcel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.aventstack.extentreports.Status;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import exceptions.DatabaseExceptions;
import exceptions.ProjectReusableException;
import exceptions.FrameworkExceptions;
import exceptions.MissingFileException;
import exceptions.SeleniumExceptions;
import helper.CommonKeywords;
import helper.FindWebELement;
import helper.Reporting;

/**
 * This class reads all the reusable methods and performs action as mentioned in
 * methods.
 */

public class ReadProjectReusables extends ExcelReusables {

	public static final ListMultimap<String, Map<String, String>> actionsPerMethod = ArrayListMultimap.create();
	protected static ArrayList<String> sheetsInWorkBook = new ArrayList<>();
	public static final  Map<Integer, String> failuremethodName = new HashMap<>();
	private static ReadProjectReusables readPR;
	
	private ReadProjectReusables() {
	}

	public static ReadProjectReusables getInstance() {
		if (readPR == null) {
			readPR = new ReadProjectReusables();
		}
		return readPR;
	}

	public void getMethodActions() throws ProjectReusableException, IOException, InvalidFormatException   {
		Workbook workbook = ExcelReusables.readFile("ProjectResuables", "ProjectReusables");
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheetsInWorkBook.add(workbook.getSheetAt(i).getSheetName());
		}
		for (String sheetName : sheetsInWorkBook) {
			Sheet sheet = workbook.getSheet(sheetName);
			int totalnumberow = sheet.getLastRowNum();
			for (int i = 1; i <= totalnumberow; i++) {
				if (!(isRowEmpty(sheet.getRow(i))) && !(getCellValueString(sheet.getRow(i).getCell(0)).isEmpty())) {

					if (actionsPerMethod.containsKey(
							sheetName.toLowerCase() + "." + sheet.getRow(i).getCell(0).toString().toLowerCase())) {
						throw new ProjectReusableException(
								"In Project Reusables, duplicate methodName <b>" + sheet.getRow(i).getCell(0).toString()+"</b> is defined in sheet <b>" + sheetName +"</b>");
					} else {
						for (int j = i; j <= totalnumberow; j++) {
							if (!(isRowEmpty(sheet.getRow(j)))) {

								String methodName = getCellValueString(sheet.getRow(i).getCell(0)).toLowerCase();
								String actionName = getCellValueString(sheet.getRow(j).getCell(1));
								String webElementName = getCellValueString(sheet.getRow(j).getCell(2));
								String testdata = getCellValueString(sheet.getRow(j).getCell(3));

								Map<String, String> reusable = new HashMap<>();

								reusable.put("action", actionName);
								reusable.put("webelement", webElementName);
								reusable.put("testdata", testdata);

								actionsPerMethod.put(sheetName.toLowerCase() + "." + methodName.toLowerCase(),
										reusable);
							} else {
								break;
							}
						}
					}
				}
			}
		}
		workbook.close();
		for (int i = 0, l = sheetsInWorkBook.size(); i < l; ++i) {
			sheetsInWorkBook.set(i, sheetsInWorkBook.get(i).toLowerCase());
		}
	}
	
	
	
	public String getDataFromTDataMap(String [] testdata, String key) throws FrameworkExceptions {
		String data = null;
		
		if (testdata.length > 0) {

			String testdataWbookName = testdata[0].toLowerCase();
			String testdataSheetName = testdata[1].toLowerCase();
			int testdataRowNo = Integer.parseInt(testdata[2]);
			
			String testdataKeyInTestcase = testdataWbookName + "|" + testdataSheetName + "|" + testdataRowNo;
			if(TestDataReader.testdataMap.containsKey(testdataKeyInTestcase)) {
			if (TestDataReader.testdataMap.get(testdataKeyInTestcase).containsKey(key)) {
				data = TestDataReader.testdataMap.get(testdataKeyInTestcase).get(key);
			} else {
				throw new FrameworkExceptions("Column '<b>" + key
						+ "</b>' is not present in the testdata sheet '<b>" + testdataSheetName + "</b>' of excel work book '<b>" + testdataWbookName + "</b>'");
			}
			}
			else {
				throw new FrameworkExceptions("Data passed in test case sheet does not exist. kindly check the data in test data column of test case sheet.");
			}
		} else if (testdata.length == 0) {
			throw new FrameworkExceptions("Data is not defined in TestData column of Test case sheet");
		}
		
		return data;
	}
	
	private String[] getDataFromTestdata(Map<String, String> step, String [] testdata) throws FrameworkExceptions {
		String[] data = new String[2];
		/**
		 * Test data column in reusable sheet contains two variables separated by a PIPE
		 * For example : TaxRegime|{$[td]Tax_Regime}
		 */
		if (step.get("testdata").contains("|")) {

			data = step.get("testdata").split("\\|");
			if (data[1].contains("{$[td]")) {
				String key = data[1];
				key = key.substring(6, key.length() - 1).trim();
				data [1] = getDataFromTDataMap(testdata, key);
			}
		}

		/**
		 * Test data column in reusable sheet contains only one variable For example :
		 * {$[td]Tax_Regime}
		 */

		else if (step.get("testdata").contains("{$[td]") && !(step.get("testdata").contains("|"))) {
			String key = step.get("testdata");
			key = key.substring(6, key.length() - 1).trim();
			data [0] = getDataFromTDataMap(testdata, key);
		} else {
			data[0] = step.get("testdata").trim();
		}
		return data;
	}

	public void getActionSteps1(String reusableMethodWithSheetName, String[] testdata) throws FrameworkExceptions, SeleniumExceptions, IOException, SQLException, DatabaseExceptions, MissingFileException{
		FindWebELement findWE = new FindWebELement();
		CommonKeywords comKey = new CommonKeywords();
		HashMap<String, String> mapToStoreVariable = new HashMap<>();
		comKey.storeVariables.put((int)(Thread.currentThread().getId()), mapToStoreVariable);
		comKey.storeDBValues.put((int)(Thread.currentThread().getId()), mapToStoreVariable);
		comKey.val_Thread.put((int)(Thread.currentThread().getId()), mapToStoreVariable);
		String sheetName = null ;
		String methodName = null;
			try {
				 sheetName = reusableMethodWithSheetName.split("\\.")[0].toLowerCase();
				 methodName = reusableMethodWithSheetName.split("\\.")[1];
			}
			catch (Exception e) {
				throw new FrameworkExceptions("Either sheetname or the method name is missing in testcase sheet. Kindly check the <b>"+reusableMethodWithSheetName+"</b>");
			}

			reusableMethodWithSheetName = reusableMethodWithSheetName.toLowerCase();
			
			failuremethodName.put((int)(Thread.currentThread().getId()), methodName);
			
			String[] data = new String[2];
			if (sheetsInWorkBook.contains(sheetName)) {
				
				if (actionsPerMethod.containsKey(reusableMethodWithSheetName)) {
//					Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).log(Status.INFO, "<a href=\"#\" class=\"visible\" data-toggle=\"#list\">"+methodName+"</a>");
					for (Map<String, String> step : actionsPerMethod.get(reusableMethodWithSheetName)) {

						/** Test data column in reusable sheet contains some testdata */
						if (!(step.get("testdata").isEmpty())) {
							data = getDataFromTestdata(step, testdata);
						}
						else {
							data [0] = "";
							data [1] = "";
						}
						performAction(step, data, findWE, comKey);
					}
					String js = "$('.more').hide()\r\n" + 
							"$('.readmore').on('click', function()\r\n" + 
							"{\r\n" + 
							"  $(this).closest('tr').find('.more').show();\r\n" + 
							"})";
					Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).log(Status.PASS, "<b>"+methodName+"</b>  "+js);
				} else {
					throw new FrameworkExceptions("Project Reusable's sheet '<b>" + sheetName + "</b>' does not contain the method '<b>" + methodName + "</b>'.");
				}
			} else {
				throw new MissingFileException("Sheet '<b>" + sheetName + "</b>' does not exist in project reusable workbook.");
			}
	}
	
	private void performAction(Map<String, String> step, String [] data, FindWebELement findWE, CommonKeywords comKey ) throws SeleniumExceptions, IOException, SQLException, DatabaseExceptions, FrameworkExceptions {
		
		String webelement  = step.get("webelement").trim();
		String action  = step.get("action").trim();
		String testdata = step.get("testdata").trim();
		
		/** if both Web element column and test data column in reusable sheet are not empty */
		if (!(webelement.isEmpty())&&!(testdata.isEmpty())) {
			comKey.getWebelementAction(action.trim(), findWE.getWebElement(webelement.trim(), data), data);
		
		/** if Web element column is empty but test data column in reusable sheet is not empty */	
		} else if(webelement.isEmpty()&&!(testdata.isEmpty())) {
			comKey.getWebelementAction(action.trim(), null, data);
		}
		
		/** if Web element column is not empty but test data column in reusable sheet is empty */	
		else if (!(webelement.isEmpty()) && (testdata.isEmpty())) {
			comKey.getWebelementAction(action.trim(), findWE.getWebElement(webelement.trim(), data), data);
		} 
		
		/** if both Web element column and test data column in reusable sheet are empty */
		else if ((webelement.isEmpty()) && (testdata.isEmpty())) {
			comKey.getWebelementAction(action.trim(), null, null);
		}
	}
	
	private void addStep() {
		String htmlcode = "<a name=\"16:10\" onclick=\"showText('text1')\" href=\"javascript:void(0);\">16:10</a>\r\n" + 
				"\r\n" + 
				"<script language=\"JavaScript\">\r\n" + 
				"   function showText(id)\r\n" + 
				"    {\r\n" + 
				"        document.getElementById(id).style.display = \"block\";\r\n" + 
				"    }\r\n" + 
				"</script>\r\n" + 
				"\r\n" + 
				"<div id=\"text1\" style=\"display:none;\">This is your monitors aspect ratio.</div>";
	}
	
}
