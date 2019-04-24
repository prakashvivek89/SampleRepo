package helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.FileUtils;

import exceptions.FrameworkExceptions;

/**
 * This class copies the all the excel files before accessing them.
 * @author vivek.prakash
 */

public class CopyFiles {

	public static void copyFileForRegression() throws Exception {
		copyProjectReusable();
		copyObjectRepo();
		copyTestData();
		copyTestsuiteFolder();
	}

	public static void copySingleFile(String suiteName) throws Exception {
		copyProjectReusable();
		copyObjectRepo();
		copyTestData();
		copySinlgeTestcase(suiteName);
	}

	public static void deleteFlder() throws IOException {
		FileUtils.cleanDirectory(new File(Constants.COPIEDFILESDIR.toString()));
		FileUtils.cleanDirectory(new File(Constants.DYNAMICXMLDIR.toString()));
		FileUtils.cleanDirectory(new File(Constants.DYNAMICCLASSDIR.toString()));
		FileUtils.deleteQuietly(new File(System.getProperty("user.dir") + "\\src\\main\\resources\\dynamicXML\\regression.xml"));
	}

	private static void copyProjectReusable() throws Exception {
		File srcFolder = new File(Constants.EXCELREUSABLEPATH.toString());
		File destFolder = new File(Constants.COPIEDFILESDIR.toString()+"/ProjectResuables");
		copyFolder(srcFolder, destFolder);
	}

	private static void copyObjectRepo() throws Exception {
		File srcFolder = new File(Constants.EXCELOBJECTREPOPATH.toString());
		File destFolder = new File(Constants.COPIEDFILESDIR.toString() + "/ObjectRepository");
		copyFolder(srcFolder, destFolder);
	}

	private static void copyTestData() throws Exception {
		File srcFolder = new File(Constants.EXCELTESTDATAPATH.toString());
		File destFolder = new File(Constants.COPIEDFILESDIR.toString() + "/TestData");
		copyFolder(srcFolder, destFolder);
	}

	private static void copyTestsuiteFolder() throws Exception {
		File srcFolder = new File(Constants.EXCELTESTCASEPATH.toString());
		File destFolder = new File(Constants.COPIEDFILESDIR.toString() + "/TestCases");
		copyFolder(srcFolder, destFolder);
	}

	private static void copySinlgeTestcase(String suiteName) throws Exception {
		if(suiteName.contains(",")) {
			for(String suite:suiteName.split(",")) {
				suite = suite.trim();
				File srcFolder = new File(Constants.EXCELTESTCASEPATH.toString());
				File destFolder = new File(Constants.COPIEDFILESDIR.toString() + "/TestCases");
				copyFolder(srcFolder, destFolder, suite);
			}
		}
		else {
			File srcFolder = new File(Constants.EXCELTESTCASEPATH.toString());
			File destFolder = new File(Constants.COPIEDFILESDIR.toString() + "/TestCases");
			copyFolder(srcFolder, destFolder, suiteName);
		}
	}

	static void copyFolder(File src, File dest, String suiteName) throws Exception {
		if (!dest.exists()) {
			dest.mkdir();
		}
		if ((new File(src, suiteName.trim() + ".xlsx")).exists()&&!(suiteName.contains("~$"))) {
			File srcf = new File(src, suiteName + ".xlsx");
			FileUtils.copyFile(srcf, new File(dest + File.separator + suiteName + ".xlsx"));
		} else if (new File(src, suiteName.trim() + ".xls").exists()&&!(suiteName.contains("~$"))) {
			File srcf = new File(src, suiteName + ".xls");
			FileUtils.copyFile(srcf, new File(dest + File.separator + suiteName + ".xls"));
		}
		else if (!(new File(src, suiteName.trim() + ".xls").exists())) {
			throw new FrameworkExceptions(" Invalid suiteName '<b>"+suiteName+"'</b> is defined in the config file ");
		}
	}
	
	

	static void copyFolder(File src, File dest) throws Exception {

		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				if (!(file.toString().contains("~"))) {
					File srcFile = new File(src, file);
					File destFile = new File(dest, file);
					copyFolder(srcFile, destFile);
				}
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}
	

}