package hooks;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.aventstack.extentreports.ExtentTest;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import utils.ExtentReportManager;
import utils.ExcelUtils;
import utils.ScreenshotUtils;
import utils.WebDriverManager;


public class Hooks {
	
	private static final ThreadLocal<ExtentTest> scenarioTest = new ThreadLocal<>();
	private static final Object workbookWriteLock = new Object();
	private static final Set<String> failedScenarioGroups = ConcurrentHashMap.newKeySet();
	private static String runEvidenceSheetName;
	private final List<Path> stepScreenshots = new ArrayList<>();
	private int stepIndex;

	@BeforeAll
	public static void startReport(){
		failedScenarioGroups.clear();
		runEvidenceSheetName = "StepEvidence_" + new SimpleDateFormat("yyMMdd_HHmmss_SSS").format(new Date());
		ExtentReportManager.initializeReports();
		
	}
	
	@Before
	public void startScenario(Scenario scenario) {
		stepScreenshots.clear();
		stepIndex = 0;
		scenarioTest.set(ExtentReportManager.extentReports.createTest(scenario.getName()));
				
				
	}
	
	@After
	public void firstScenario(Scenario scenario) {
		String executionStatus = scenario.isFailed() ? "Failed" : "Passed";
		if(scenario.isFailed()) {
			failedScenarioGroups.add(scenario.getName());
			scenarioTest.get().fail("Scenario failed: "+scenario.getName());
		}
		else {
			scenarioTest.get().pass("Scenario passed: "+scenario.getName());
		}
		// Scenario Outline examples share one Excel test-case title; keep its result failed
		// if any example failed during this run.
		Path workbookPath = Paths.get("src", "test", "resources", "TestSuite", "Test Suite.xlsx");
		try {
			synchronized (workbookWriteLock) {
				String status = failedScenarioGroups.contains(scenario.getName()) ? "Failed" : "Passed";
				ExcelUtils.updateScenarioResult(workbookPath, scenario.getName(), status,
						actualResultsFor(scenario.getName()), stepScreenshots, scenario.getLine(), executionStatus,
						runEvidenceSheetName);
			}
		} catch (IOException e) {
			System.err.println("Could not update Excel result for '" + scenario.getName()
					+ "' in " + workbookPath.toAbsolutePath() + ": " + e.getMessage());
		} finally {
			scenarioTest.remove();
			WebDriverManager.closeActiveBrowser();
		}
	}

	@AfterStep
	public void captureScreenshotAfterStep(Scenario scenario) {
		Path screenshotPath = null;
		WebDriver driver = WebDriverManager.getActiveDriver();
		if (driver != null) {
			String safeScenarioName = scenario.getName().replaceAll("[^A-Za-z0-9._-]", "_");
			Path candidate = Paths.get("target", "screenshots", safeScenarioName + "-"
					+ scenario.getLine() + "-step-" + (stepIndex + 1) + "-" + System.nanoTime() + ".png");
			try {
				new ScreenshotUtils(driver).screenshot(candidate.toString());
				screenshotPath = candidate;
			} catch (IOException e) {
				System.err.println("Could not capture screenshot after step " + (stepIndex + 1)
						+ " of '" + scenario.getName() + "': " + e.getMessage());
			}
		}
		stepScreenshots.add(screenshotPath);
		stepIndex++;
	}

	private List<String> actualResultsFor(String scenarioName) {
		switch (scenarioName.trim()) {
		case "Verify active member details":
			return Arrays.asList(
					"User is on CURA Healthcare Service",
					"User clicked on Make appointment",
					"User entered username and password and clicked on Login",
					"User is on Make Appoinment application",
					"User selected apprioprate facility and HealthProgram and clicked on Book appoinment",
					"Screen states appointment confirmation");
		case "Verify with incorrect username":
			return Arrays.asList(
					"User is on CURA Healthcare Service",
					"User clicked on Make appointment",
					"User entered username and password and clicked on Login",
					"Screen states Login failed confirmation");
		case "Verify with incorrect password":
			return Arrays.asList(
					"User is on CURA Healthcare Service",
					"User clicked on Make appointment",
					"User entered correct username and wrong password and clicked on Login",
					"Screen states Login failed confirmation");
		default:
			return Collections.emptyList();
		}
	}

	
	@AfterAll
	public static void closeReport() {
		ExtentReportManager.flushReport();

		File workbook = new File("src/test/resources/TestSuite/Test Suite.xlsx");
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(workbook);
			} catch (IOException e) {
				System.err.println("Could not open Excel test results: " + e.getMessage());
			}
		} else {
			System.out.println("Open the Excel test results manually: " + workbook.getAbsolutePath());
		}

		File report = new File("target/ExtentReport.html");
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(report.toURI());
			} catch (IOException e) {
				System.err.println("Could not open Extent report: " + e.getMessage());
			}
		} else {
			System.out.println("Open the Extent report manually: " + report.getAbsolutePath());
		}
	}

}

