L2TrackAssignment - File Upload & Download Automation

Framework: Java, Selenium WebDriver, TestNG, POM, simple DDF, ExtentReports

Setup

1. JDK 11 installed
2. Maven installed
3. Chrome browser installed (tests use Chrome)

How to run

From project root run:

```powershell
mvn clean test
```

Reports

Extent report will be generated at the path configured in `src/test/resources/config.properties` (default: `target/extent-report/extent-report.html`). Screenshots for failed tests will be in `target/screenshots`.

Notes

- Add any binary sample files (e.g., .pdf, .jpg, .zip) into `src/test/resources/testdata` if you want to execute tests for those types.
- CI workflow included in `.github/workflows/ci.yml` to run `mvn test` and archive reports.

Install Maven (Windows helper)

If you don't have Maven installed on Windows, there's a helper script `run-setup.ps1` in the project root that will install Chocolatey (if missing) and then install Maven. Run PowerShell as Administrator and execute:

```powershell
.\run-setup.ps1
```

After the script finishes, open a new PowerShell window and run `mvn -v` to verify.
