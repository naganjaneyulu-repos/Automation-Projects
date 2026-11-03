package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class utils_pages {
	
	WebDriver driver;
	
	public utils_pages(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//a[@id='btn-make-appointment']")
	public WebElement make_appointment;
	
	@FindBy(xpath="//input[@id='txt-username']")
	public WebElement username;
	
	@FindBy(xpath="//input[@id='txt-password']")
	public WebElement password;
	
	@FindBy(xpath="//button[@id='btn-login']")
	public WebElement login_button;
	
	@FindBy(xpath="//select[@id='combo_facility']")
	public WebElement Facility_city;
	
	@FindBy(xpath="//input[@id='chk_hospotal_readmission']")
	public WebElement  hospital_readmission;
	
	@FindBy(xpath="//input[@id='radio_program_medicare']")
	public WebElement Medicare_button;
	
	@FindBy(xpath="//input[@id='radio_program_medicaid']")
	public WebElement Medicaid_button;
	
	@FindBy(xpath="//input[@id='txt_visit_date']")
	public WebElement Visit_Data;
	
	@FindBy(xpath="//textarea[@id='txt_comment']")
	public WebElement comments;
	
	@FindBy(xpath="//button[@id='btn-book-appointment']")
	public WebElement book_button;
	
	@FindBy(xpath="//h2")
	public WebElement confirmation;
	
	@FindBy(xpath="//p[@id='facility']")
	public WebElement booked_facility;
	
	@FindBy(xpath="//h2[text()='Login']/following-sibling::p[2]")
	public WebElement failed_msg;
	

}

