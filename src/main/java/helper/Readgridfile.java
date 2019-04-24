package helper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import readexcel.ExcelReusables;


/**Class contains the method to read grid excel file containing browser information for each test module*/

public class Readgridfile extends ExcelReusables {
	protected static Map<String, String> gridvalues = new HashedMap<>();

	private static Readgridfile readGrid;

	private Readgridfile() {
	}

	public static Readgridfile getInstance() {
		if (readGrid == null) {
			readGrid = new Readgridfile();
		}
		return readGrid;
	}

	public void getbrowser() throws InvalidFormatException, IOException {
		String fileloc = System.getProperty("user.dir") + File.separator + "Grid" + File.separator + "Grid.xlsx";
		File excelFileToRead = new File(fileloc);
		Workbook grid = new XSSFWorkbook(excelFileToRead);
		Sheet sheet = grid.getSheetAt(0);
		int totalnumberrow = sheet.getLastRowNum();
		for (int i = 3; i <= totalnumberrow; i++) {
			if (!(isRowEmpty(sheet.getRow(i))) && !(getCellValueString(sheet.getRow(i).getCell(1)).isEmpty())) {
				if (WebdriverHelper.runSmoke.equalsIgnoreCase("yes")) {
					gridvalues.put(sheet.getRow(i).getCell(1).toString(), sheet.getRow(i).getCell(2).toString());
				}
				else if(WebdriverHelper.runRegression.equalsIgnoreCase("yes")){
					gridvalues.put(sheet.getRow(i).getCell(1).toString(), sheet.getRow(i).getCell(3).toString());
				}
			}
		}
		grid.close();
	}
}
