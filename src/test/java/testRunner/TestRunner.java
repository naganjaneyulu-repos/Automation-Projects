package testRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
		
		features="src/test/resources/features/HealthCase.feature",
		glue={"hooks","stepdefinitions"},
		plugin= {"pretty","html:target/report.html"},
		monochrome=true,
		dryRun=false)
public class TestRunner extends AbstractTestNGCucumberTests{
	@Override
	@DataProvider(parallel = true)
	public Object[][] scenarios() {
		return super.scenarios();
	}

}

