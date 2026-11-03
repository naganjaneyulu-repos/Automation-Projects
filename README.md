# CURA Healthcare UI Automation

A Java UI automation project for the public [CURA Healthcare Service demo](https://katalon-demo-cura.herokuapp.com/). It uses Selenium WebDriver, Cucumber, and TestNG to exercise appointment booking and login validation scenarios.

## Project purpose

I built this project to demonstrate my skills in test automation as I prepare for my next opportunity in software testing. It shows how I organize readable BDD scenarios, automate browser workflows, manage test data, capture evidence, and report results. I plan to continue extending it as I build my automation experience.

## Tech stack

- Java 17+
- Selenium WebDriver 4
- Cucumber 7 with Gherkin
- TestNG 7
- Maven
- ExtentReports and Apache POI

## Scenarios

- Book appointments with multiple facility and health-program combinations using a Scenario Outline.
- Verify invalid username and password handling.
- Capture screenshots after steps and write results to the Excel test suite.
- Run Cucumber scenarios concurrently through TestNG's parallel DataProvider (configured for up to three threads).

## Run locally

1. Install Java 17 or later, Maven, and Chrome.
2. From the project directory, run:

   ```bash
   mvn test -Dsurefire.suiteXmlFiles=testNG.xml
   ```

Copy `src/test/resources/Testdata/config.properties.example` to `config.properties` for local settings, then set `CURA_USERNAME`, `CURA_PASSWORD`, `CURA_WRONG_USERNAME`, and `CURA_WRONG_PASSWORD` in your environment. Credentials are intentionally excluded from version control. Update `browserName` in your local config to use `chrome`, `edge`, or `firefox` when the matching browser is installed.

Test results and screenshots are written under `target/`. Scenario status and evidence are also written to `src/test/resources/TestSuite/Test Suite.xlsx`.

## Project structure

```text
src/test/java/pages          Page objects
src/test/java/stepdefinitions Cucumber step definitions
src/test/java/hooks           Scenario lifecycle, reporting, and evidence
src/test/java/testRunner      Cucumber + TestNG runner
src/test/java/utils           Browser, wait, screenshot, and Excel utilities
src/test/resources/features   Gherkin scenarios
src/test/resources/Testdata   Browser and demo application settings
```

This project uses the CURA public demo site for learning and portfolio purposes.

