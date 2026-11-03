package utils;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wait_Helper {
	
	WebDriver driver;
	
	public Wait_Helper(WebDriver driver) {
		
		this.driver=driver;
		}
	
	public void call_implicitWait(int wait_sec) {
		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(wait_sec));
	}
	
	public void frameWait(WebElement waitLocator) {
		
		WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(waitLocator));
		
	}
	
	public void alertAlertIfPresent() {
		WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(5));
		Alert alert=wait.until(ExpectedConditions.alertIsPresent());
		alert.accept();
	}
	
	public void visibilityOfWait(WebElement WaitFacility) {
		
		WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(15));
		 wait.until(ExpectedConditions.visibilityOf(WaitFacility));
		 
	}
	 

}

