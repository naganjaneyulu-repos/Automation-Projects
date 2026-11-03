package utils;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;

public class WebDriverManager {
	private static final ThreadLocal<WebDriver> activeDriver = new ThreadLocal<>();
	
	public WebDriver driver;
	
	configReader cr=new configReader();
	
	public WebDriver invokeBrowser() {
		
		String browser=cr.getProperty("browserName");
		
		if(browser.equalsIgnoreCase("chrome")) {
			
			ChromeOptions options=new ChromeOptions();
			
			Map<String, Object> prefs=new HashMap<>();
			
			prefs.put("credentials_enable_service", false);
			prefs.put("profile.password_manager_enabled", false);
			prefs.put("profile.password_manager_leak_detection", false);
			
			options.setExperimentalOption("prefs", prefs);
			
			driver=new ChromeDriver(options);
		}
		else if(browser.equalsIgnoreCase("edge")) {
			driver=new EdgeDriver();
		}
		else if(browser.equalsIgnoreCase("firefox")) {
			driver=new FirefoxDriver();
		}
		else {
			throw new IllegalArgumentException("unsupported browser: "+browser);
		}
		activeDriver.set(driver);

		return driver;
	}

	/** Closes the browser started on the current Cucumber scenario thread. */
	public static void closeActiveBrowser() {
		WebDriver currentDriver = activeDriver.get();
		try {
			if (currentDriver != null) {
				currentDriver.quit();
			}
		} catch (RuntimeException e) {
			System.err.println("Could not close browser cleanly: " + e.getMessage());
		} finally {
			activeDriver.remove();
		}
	}

	public static WebDriver getActiveDriver() {
		return activeDriver.get();
	}
	

}

