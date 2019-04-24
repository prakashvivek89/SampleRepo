package readexcel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import exceptions.FrameworkExceptions;
import helper.Constants;


/**
 * This class reads the Test data excel files. 
 */


public class TestDataReader extends ExcelReusables{
	
	private static TestDataReader tdreader;
	public static Map<String, Map<String, String>> testdataMap = new HashedMap<>();
	
	
	private TestDataReader(){
	}
	
	public static TestDataReader getInstance() {
		if(tdreader==null) {
			tdreader = new TestDataReader();
		}
		return tdreader;
	}
	
	public void getTData(String excelName) throws FrameworkExceptions, InvalidFormatException, IOException {
		Workbook workbook = readFile("TestData", excelName);
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			try {
				Sheet sheet = workbook.getSheetAt(i);
				String sheetName = sheet.getSheetName();
				Row keyRow = sheet.getRow(0);
				List<String> key = new ArrayList<>();
				
				if (keyRow != null) {
					for (int j = 2; j < keyRow.getLastCellNum(); j++) {
						key.add(getCellValueString(keyRow.getCell(j)).trim());
					}
					Row valueRow;
					for (int k = 1; k <= sheet.getLastRowNum(); k++) {
						List<String> value = new ArrayList<>();
						valueRow = sheet.getRow(k);
						if (valueRow != null) {
							for (int j = 2; j <= keyRow.getLastCellNum(); j++) {
								value.add(getCellValueString(valueRow.getCell(j)).trim());
							}

							Map<String, String> testdata = new HashedMap<>();
							for (int l = 0; l < key.size(); l++) {
								testdata.put(key.get(l), value.get(l));
							}
							testdataMap.put(excelName.toLowerCase() + "|" + sheetName.toLowerCase() + "|" + k,testdata);
						}
					}
				}
		}
			catch (Exception e) {
				throw new FrameworkExceptions(workbook.getSheetAt(i).getSheetName()+"    failure     " + e.getMessage());
			}
		}
		
		
	}
	
	
	public void readAllFiles() throws InvalidFormatException, FrameworkExceptions, IOException  {
		File excelFileToRead = new File(Constants.COPIEDFILESDIR.toString()+File.separator+"TestData");
		File[] files = excelFileToRead.listFiles();
		for (File f : files) {
			if(!(f.getName().contains("~$"))) {
				getTData(FilenameUtils.removeExtension(f.getName()));
			}
		}
	}
	
}
