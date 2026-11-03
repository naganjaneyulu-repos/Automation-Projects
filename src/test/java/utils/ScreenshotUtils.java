package utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtils {
	
	WebDriver driver;
	
	public ScreenshotUtils(WebDriver driver){
		
		this.driver=driver;	
		}
	
	public void screenshot(String path) throws IOException {
		
		TakesScreenshot ts=(TakesScreenshot)driver;
		File sourceFile=ts.getScreenshotAs(OutputType.FILE);
		File destFile=new File(path);
		File parent=destFile.getParentFile();
		if(parent != null && !parent.exists() && !parent.mkdirs()) {
			throw new IOException("Could not create screenshot directory: " + parent.getAbsolutePath());
		}
		FileUtils.copyFile(sourceFile, destFile);
		
	}
	
	

}

