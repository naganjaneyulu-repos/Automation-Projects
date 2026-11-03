package stepdefinitions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.utils_pages;
import utils.Wait_Helper;
import utils.WebDriverManager;
import utils.configReader;

public class stepdefinitions {
	
	WebDriver driver;
	
	configReader cr=new configReader();
	
	WebDriverManager wdm=new WebDriverManager();
	
	Wait_Helper wh;
	
	utils_pages pages;
	
	 @Given("user in on CURA Healthcare Service application")
	    public void launch_Healthcare_Application() throws Exception {
		 
		 driver=wdm.invokeBrowser();
		 
		 wh=new Wait_Helper(driver);
		 
		 driver.get(cr.getProperty("weblink"));
		 
		 pages=new utils_pages(driver);
		 
		 driver.manage().window().maximize();
		 
	 }
	 
	 @When("the user clicks on the Make appointment")
	 public void user_clicks_on_the_Make_appointment() {
		 
		 pages.make_appointment.click();
		 
	 }
	 
	 @And("user enter username and  password and clicks on Login")
	 public void user_enter_username_and_password_clicks_Login() {
		 
		 pages.username.sendKeys(cr.getProperty("username"));
		 pages.password.sendKeys(cr.getProperty("password"));
		 
		 pages.login_button.click();
		 
	 }
	 
	 @Then("user is on Make Appoinment application")
	 public void user_is_on_Make_Appoinment_application() {
		 
	 }
	 
	 
	 @When("user selects apprioprate {string} and {string} and click on Book appoinment")
	 public void user_selects_apprioprate_and_click_on_Book_appoinment(String facility,String HealthProgram) {
		 
		 WebElement Facility=pages.Facility_city;
		 
		 wh.visibilityOfWait(Facility);
		 
		 Select select=new Select(Facility);
		 
		 select.selectByVisibleText(facility);
		 
		 
		 pages.hospital_readmission.click();
		 
		 if(HealthProgram.equals("Medicare")) {
			 
			 pages.Medicare_button.click();
		 }
		 else {
			 
			 pages.Medicaid_button.click();
		 }
		 
		 if(pages.Medicare_button.isSelected()) {
			 
			 System.out.println("Medicare is selected");
		 }
		 else {
			 
			 System.out.println("Medicaid is selected");		
			 }
		 
		 LocalDate today=LocalDate.now();
		 
		 DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
		 
		 String formatterDate=today.format(formatter);
		 
		 pages.Visit_Data.clear();
		 pages.Visit_Data.sendKeys(formatterDate);
		 pages.Visit_Data.sendKeys(Keys.ENTER);
		 
		 
		 pages.comments.sendKeys("Need an appointment with "+facility);
		 
		 pages.book_button.click();
		 
		 
		 }
	 
	 
	 @When("screen should state appionment confirmation")
	 public void screen_should_state_appionment_confirmation() {
		 
		 wh.visibilityOfWait(pages.booked_facility);
		 
		 System.out.println(pages.confirmation.getText()+" with "+pages.booked_facility.getText());
		 
	 }
	 
	 @And("user enter wrong username and correct password and clicks on Login")
	 public void user_enter_wrong_username_and_correct_password_clicks_on_Login() {
		 
		 pages.username.sendKeys(cr.getProperty("wrong_username"));
		 pages.password.sendKeys(cr.getProperty("password"));
		 
		 pages.login_button.click();
		 
		 
	 }
	 @And("user enter correct username and wrong password and clicks on Login")
	 public void user_correct_username_and_wrong_password_clicks_on_Login() {
		 
		 pages.username.sendKeys(cr.getProperty("username"));
		 pages.password.sendKeys(cr.getProperty("wrong_password"));
		 
		 pages.login_button.click();
		 
		 
	 }
	 
	 @Then("screen should state Login failed confirmation")
	 public void screen_should_state_Login_failed_confirmation() {
		 
		 String failed_msg=pages.failed_msg.getText();
		 
		 System.out.println(failed_msg);
		 
		 
		 boolean condition=failed_msg.contains("Login failed!");
		 
		 Assert.assertTrue(condition);
			 
			 
			 
		 
	 }
	 
	 

}

