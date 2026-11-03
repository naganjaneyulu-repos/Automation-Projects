package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class configReader {
	
	public static Properties prop;
	
	public String getProperty(String properties) {
		try {
			FileInputStream fis=new FileInputStream("src/test/resources/Testdata/config.properties");
			prop=new Properties();
			prop.load(fis);
		}catch(IOException e) {
			e.printStackTrace();
			System.out.println("Error: could not found or load the properties file");
		}
		return prop.getProperty(properties);
	}

}

