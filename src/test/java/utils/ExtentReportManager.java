package utils;

import java.io.File;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import com.aventstack.extentreports.ExtentReports;


public class ExtentReportManager {
	
	public static ExtentReports extentReports;
	
	
	
	public static void initializeReports() {
		File file=new File("target/ExtentReport.html");
		ExtentSparkReporter sparkReporter=new ExtentSparkReporter(file);
		extentReports=new ExtentReports();
		extentReports.attachReporter(sparkReporter);
		
		
		extentReports.setSystemInfo("Project","CURA Health Servie");
		extentReports.setSystemInfo("Framework","Cucumber+TestNG");
		
		}
		
	public static void flushReport() {
		if(extentReports!=null) {
			extentReports.flush();
		}
		
		
	}

}


