package helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class contains the method to read the config property file.
 * */

public class PropReader {
	static Properties prop = new Properties();
	static File file = null;

	public static String readWebdriverConfig(String key) {
		String property = "";
		try {
			property = loadPropFiles().getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return property;
	}

	public static Properties loadPropFiles() throws IOException {
		Properties mavenProps = new Properties();
		InputStream in = PropReader.class.getResourceAsStream("/Config.properties");
		mavenProps.load(in);
		return mavenProps;
	}
	
}
