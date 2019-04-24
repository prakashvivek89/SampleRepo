package helper;


/**
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuiteResult;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

 * This class creates the final excel report containing the module wise execution report.
 */

public class ExcelReport {
	/**private static FileOutputStream fos = null;
	private static XSSFWorkbook workbook = null;
	private static XSSFSheet sheet = null;
	private static XSSFRow row = null;
	private static XSSFCell cell = null;
	private static XSSFFont font = null;
	private static XSSFFont headerFont = null;
	private static XSSFCellStyle style = null;
	private static XSSFCellStyle headerStyle = null;
	private static String summarySheetName = "Summary";
	private static int colCount = 0;
	private static int executionRowCount = 21;
	private static List<ITestResult> allResults;

	private static final Logger LOG = LoggerFactory.getLogger(WebdriverHelper.class);

	private static boolean setCellData(String sheetName, int colNumber, int rowNum, String value, Short index) {
		try {
			sheet = workbook.getSheet(sheetName);
			row = sheet.getRow(rowNum);
			if (row == null) {
				row = sheet.createRow(rowNum);
			}

			cell = row.getCell(colNumber);
			if (cell == null) {
				cell = row.createCell(colNumber);
			}

			applyCellStyle(index.shortValue());
			cell.setCellValue(value);
			return true;
		} catch (Exception arg5) {
			arg5.printStackTrace();
			return false;
		}
	}

	private static boolean setCellData(String sheetName, int colNumber, int rowNum, int value, Short index) {
		try {
			sheet = workbook.getSheet(sheetName);
			row = sheet.getRow(rowNum);
			if (row == null) {
				row = sheet.createRow(rowNum);
			}

			cell = row.getCell(colNumber);
			if (cell == null) {
				cell = row.createCell(colNumber);
			}

			applyCellStyle(index.shortValue());
			cell.setCellValue(value);
			return true;
		} catch (Exception arg5) {
			arg5.printStackTrace();
			return false;
		}
	}

	private static boolean setCellHeaderData(String sheetName, int colNumber, int rowNum, String value, Short index) {
		try {
			sheet = workbook.getSheet(sheetName);
			row = sheet.getRow(rowNum);
			if (row == null) {
				row = sheet.createRow(rowNum);
			}

			cell = row.getCell(colNumber);
			if (cell == null) {
				cell = row.createCell(colNumber);
			}

			applyCellHeaderStyle(index.shortValue());
			cell.setCellValue(value);
			return true;
		} catch (Exception arg5) {
			arg5.printStackTrace();
			return false;
		}
	}

	private static String addSheet(String sheetname) {
		sheet = workbook.createSheet(sheetname);
		sheet.setDisplayGridlines(true);
		sheet.setPrintGridlines(true);
		return sheetname;
	}

	private static void applyCellStyle(short index) {
		style = workbook.createCellStyle();
		font.setFontName("Calibri");
		font.setFontHeight(9.0D);
		style.setFont(font);
		style.setFillForegroundColor(index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		cell.setCellStyle(style);
	}

	private static void applyCellHeaderStyle(short index) {
		headerStyle = workbook.createCellStyle();
		headerFont.setFontName("Calibri");
		headerFont.setFontHeight(12.0D);
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setFillForegroundColor(index);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderBottom(BorderStyle.THICK);
		headerStyle.setBorderTop(BorderStyle.THICK);
		headerStyle.setBorderRight(BorderStyle.THICK);
		headerStyle.setBorderLeft(BorderStyle.THICK);
		cell.setCellStyle(headerStyle);
	}

	private static int getColumnCount(String sheetName) {
		sheet = workbook.getSheet(sheetName);
		row = sheet.getRow(0);
		short colCount = row.getLastCellNum();
		return colCount;
	}

	private static String getDateWithDay() {
		final DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		((SimpleDateFormat) sdf).applyPattern("EEEE d MMM yyyy");
		String currentDate = sdf.format(cal.getTime());
		return currentDate;
	}

	private static void copyFileAndEditUsingStream() throws Exception {
		File source = new File(Constants.REPROTINGFORMATDIR.toString());
		File dest = new File(Launcher.currDir + File.separator + "Summarized Report");
		FileUtils.copyFileToDirectory(source, dest);
		FileInputStream file = new FileInputStream(dest + "/Final Report.xlsx");
		workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = (XSSFSheet) workbook.getSheet("Summary");
		sheet.setForceFormulaRecalculation(true);
		font = workbook.createFont();
		headerFont = workbook.createFont();
		style = workbook.createCellStyle();
		headerStyle = workbook.createCellStyle();
	}

	public static void generateFinalReportExcelSheet() throws Exception {
		int total = WebdriverHelper.totalCount;
		int passed = WebdriverHelper.totalPassCount;
		int failed = WebdriverHelper.totalFailCount;
		int skipped = WebdriverHelper.totalSkipCount;
		workbook.getSheet(summarySheetName);
		setCellData("Summary", 2, 5, "Adviser11", Short.valueOf((short) 9));
		setCellData("Summary", 2, 6, getDateWithDay(), Short.valueOf((short) 9));
		setCellData("Summary", 2, 7, Launcher.startTime.toString(), Short.valueOf((short) 9));
		setCellData("Summary", 2, 8, Launcher.endTime.toString(), Short.valueOf((short) 9));
		setCellData("Summary", 2, 9, getExecutionTime(Launcher.totalTime), Short.valueOf((short) 9));
		setCellData("Summary", 2, 11, Launcher.currDir + File.separator + "Detailed Report", Short.valueOf((short) 9));
		setCellData("Summary", 5, 6, passed, Short.valueOf((short) 9));
		setCellData("Summary", 5, 7, failed, Short.valueOf((short) 9));
		setCellData("Summary", 5, 8, skipped, Short.valueOf((short) 9));
		setCellData("Summary", 5, 9, total, Short.valueOf((short) 9));
	}

	private static void createExecutionDetailsChart() throws Exception {
		File dest = new File(Launcher.currDir + File.separator + "Summarized Report");
		FileInputStream file = new FileInputStream(dest + "/Final Report.xlsx");
		Workbook workbook = new Workbook(file);
		WorksheetCollection worksheets = workbook.getWorksheets();
		Worksheet worksheet = worksheets.get(summarySheetName);
		ChartCollection charts = worksheet.getCharts();
		Chart chart = charts.get(1);
		int ran = 21 + WebdriverHelper.finalReportingMap.keySet().size();
		String finalRange = String.valueOf(ran);
		String range = "=Summary!$B$21:$E$" + finalRange;
		chart.setChartDataRange(range, true);
		chart.setShowDataTable(true);
		for (int i = 1; i < worksheets.getCount(); i++) {
			colCount = getColumnCount(worksheets.get(i).getName());
			for (int colPosition = 1; colPosition < colCount; ++colPosition) {
				worksheets.get(i).autoFitColumn(colPosition);
			}
		}
		workbook.save(dest + "/Final Report.xlsx");
		file.close();
	}

	private static void deleteAsposeSheet() throws Exception {
		File dest = new File(Launcher.currDir + File.separator + "Summarized Report");
		FileInputStream file = new FileInputStream(dest + "/Final Report.xlsx");
		workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = (XSSFSheet) workbook.getSheet("Evaluation Warning");
		workbook.removeSheetAt(workbook.getSheetIndex(sheet));
		fos = new FileOutputStream(
				Launcher.currDir + File.separator + "Summarized Report" + File.separator + "Final Report.xlsx");
		workbook.write(fos);
		workbook.close();
		fos.close();
	}

	private static String getExecutionTime(long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	public static void generateReport() throws Exception {
		copyFileAndEditUsingStream();
		Object[] keys = WebdriverHelper.finalReportingMap.keySet().toArray();
		Arrays.sort(keys);
		for (Object moduleName : keys) {
			allResults = WebdriverHelper.finalReportingMap.get(moduleName.toString());
			generateModuleWiseReportExcelSheet(moduleName.toString(), allResults);
		}

		generateFinalReportExcelSheet();
		fos = new FileOutputStream(
				Launcher.currDir + File.separator + "Summarized Report" + File.separator + "Final Report.xlsx");
		workbook.write(fos);
		workbook.close();
		fos.close();
		createExecutionDetailsChart();
		deleteAsposeSheet();
		LOG.info("Excel Report Generated");
	}

	private static void generateModuleWiseReportExcelSheet(String moduleName, List<ITestResult> module) {
		addSheet(moduleName);
		setCellHeaderData(moduleName, 0, 0, "TestcaseID", Short.valueOf((short) 13));
		setCellHeaderData(moduleName, 1, 0, "TestCaseName", Short.valueOf((short) 13));
		setCellHeaderData(moduleName, 2, 0, "Status", Short.valueOf((short) 13));
		setCellHeaderData(moduleName, 3, 0, "Exception", Short.valueOf((short) 13));
		setCellHeaderData(moduleName, 4, 0, "StartTime", Short.valueOf((short) 13));
		setCellHeaderData(moduleName, 5, 0, "EndTime", Short.valueOf((short) 13));
		setCellHeaderData(moduleName, 6, 0, "Duration", Short.valueOf((short) 13));
		int moduleWisePassedTest = 0;
		int moduleWiseFailedTest = 0;
		int moduleWiseSkippedTest = 0;
		int r = 1;
		Iterator<ITestResult> skippedTestCases = module.iterator();
		List<ITestResult> skipTestcase = new ArrayList<>();
		while (skippedTestCases.hasNext()) {
			ITestResult testCase = skippedTestCases.next();
			if (testCase.getStatus() == ITestResult.SKIP) {
				skipTestcase.add(testCase);
			}
		}
		module.removeAll(skipTestcase);

		for (ITestResult result : module) {
			String testcaseID = result.getMethod().getMethodName();
			String testCaseName = result.getMethod().getDescription();
			moduleWisePassedTest = moduleWisePassedTest + result.getTestContext().getPassedTests().size();
			moduleWiseFailedTest = moduleWiseFailedTest + result.getTestContext().getFailedTests().size();
			moduleWiseSkippedTest = moduleWiseSkippedTest + result.getTestContext().getSkippedTests().size();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis((result.getStartMillis()));
			String startTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":"
					+ calendar.get(Calendar.SECOND);
			calendar.setTimeInMillis((result.getEndMillis()));
			String endTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":"
					+ calendar.get(Calendar.SECOND);
			String duration = getExecutionTime(result.getEndMillis() - result.getStartMillis());
			setCellData(moduleName, 0, r, testcaseID, Short.valueOf((short) 9));
			setCellData(moduleName, 1, r, testCaseName, Short.valueOf((short) 9));
			setCellData(moduleName, 4, r, startTime, Short.valueOf((short) 9));
			setCellData(moduleName, 5, r, endTime, Short.valueOf((short) 9));
			setCellData(moduleName, 6, r, duration, Short.valueOf((short) 9));
			if (result.getStatus() == ITestResult.SUCCESS) {
				setCellData(moduleName, 2, r, "PASS", Short.valueOf((short) 11));
				setCellData(moduleName, 3, r, "", Short.valueOf((short) 9));
			}
			if (result.getStatus() == ITestResult.FAILURE) {
				setCellData(moduleName, 2, r, "FAIL", Short.valueOf((short) 10));
				setCellData(moduleName, 3, r, result.getThrowable().getMessage(), Short.valueOf((short) 10));
			}
			r++;
		}
		workbook.getSheet(summarySheetName);
		setCellData(summarySheetName, 1, executionRowCount, moduleName, Short.valueOf((short) 9));
		setCellData(summarySheetName, 2, executionRowCount, moduleWisePassedTest, Short.valueOf((short) 9));
		setCellData(summarySheetName, 3, executionRowCount, moduleWiseFailedTest, Short.valueOf((short) 9));
		setCellData(summarySheetName, 4, executionRowCount, moduleWiseSkippedTest, Short.valueOf((short) 9));
		executionRowCount = executionRowCount + 1;
	}*/

}