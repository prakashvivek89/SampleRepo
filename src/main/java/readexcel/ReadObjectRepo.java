package readexcel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import exceptions.FrameworkExceptions;

/**
 * This class reads the object repository file.
 */

public class ReadObjectRepo extends ExcelReusables {
	public static Map<String, 	Map<String, String>> objectrepo = new HashedMap<>();

	private static ReadObjectRepo readOR;

	private ReadObjectRepo() {
	}

	public static ReadObjectRepo getInstance() {
		if (readOR == null) {
			readOR = new ReadObjectRepo();
		}
		return readOR;
	}
	
	public void getORData() throws FrameworkExceptions, InvalidFormatException, IOException {
		Workbook workbook = readFile("ObjectRepository", "ObjectRepository");
		Sheet sheet = workbook.getSheetAt(0);
		int totalnumberrow = sheet.getLastRowNum();
		for (int i = 1; i <= totalnumberrow; i++) {
			if (!(isRowEmpty(sheet.getRow(i)))) {
				
				String pageName = getCellValueString(sheet.getRow(i).getCell(0)).toLowerCase();
				String objectName = getCellValueString(sheet.getRow(i).getCell(1)).toLowerCase();
				String type = getCellValueString(sheet.getRow(i).getCell(2)).toLowerCase();
				String value = getCellValueString(sheet.getRow(i).getCell(3));
				
				Map<String, String> typeWithValue = new HashMap<>();
				typeWithValue.put("type", type);
				typeWithValue.put("value", value);
				
				if (!(objectrepo.containsKey(pageName + "|" + objectName))) {
						objectrepo.put(pageName + "|" + objectName, typeWithValue);
				} else {
					throw new FrameworkExceptions("In Object Repository, duplicate combination of parent '<b>" + pageName + "</b>' and object <b>'" + objectName + "</b>' is defined");
				}
			}
		}
		workbook.close();
	}	
}


