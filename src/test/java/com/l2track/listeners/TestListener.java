package com.l2track.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.l2track.core.ConfigReader;
import com.l2track.core.DriverFactory;
import com.l2track.reports.ExtentManager;
import com.l2track.utils.LoggerUtil;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

public class TestListener implements ITestListener {
    private static final Logger LOGGER = LoggerUtil.getLogger(TestListener.class);
    private static final ExtentReports extent = ExtentManager.getInstance(ConfigReader.get("reports.dir"));
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    private ExtentTest getOrCreateTest(ITestResult result) {
        ExtentTest current = test.get();
        if (current != null) {
            return current;
        }

        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        current = extent.createTest(methodName, description == null ? "No description provided" : description);
        test.set(current);
        return current;
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extTest = getOrCreateTest(result);
        extTest.assignCategory("UI Automation");
        extTest.info("Test started at: " + new Date());
        extTest.info("Browser: " + ConfigReader.get("browser"));
        extTest.info("Base URL: " + ConfigReader.get("base.url"));
        LOGGER.info("Starting test: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest extTest = getOrCreateTest(result);
        extTest.log(Status.PASS, "Test passed successfully");
        extTest.info("Completed at: " + new Date());
        LOGGER.info("Test passed: " + result.getMethod().getMethodName());
        extent.flush();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest extTest = getOrCreateTest(result);
        Throwable throwable = result.getThrowable();

        extTest.log(Status.FAIL, "Test failed");
        if (throwable != null) {
            extTest.log(Status.FAIL, throwable);
            extTest.fail("Failure reason: " + throwable.getMessage());
        }
        extTest.info("Failed at: " + new Date());

        LOGGER.severe("Test failed: " + result.getMethod().getMethodName() + " - " + throwable);

        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            try {
                Path p = Paths.get(ConfigReader.get("screenshot.dir"));
                if (!p.toFile().exists()) {
                    p.toFile().mkdirs();
                }

                String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File dest = p.resolve(fileName).toFile();
                FileUtils.copyFile(src, dest);

                extTest.addScreenCaptureFromPath(dest.getAbsolutePath());
                extTest.info("Screenshot captured: " + dest.getAbsolutePath());
                LOGGER.info("Saved screenshot at: " + dest.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.severe("Unable to capture screenshot: " + e.getMessage());
                extTest.log(Status.WARNING, "Failed to save screenshot: " + e.getMessage());
            }
        }

        extent.flush();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest extTest = getOrCreateTest(result);
        extTest.log(Status.SKIP, "Test skipped");
        extTest.info("Skipped at: " + new Date());
        LOGGER.warning("Test skipped: " + result.getMethod().getMethodName());
        extent.flush();
    }

    @Override
    public void onStart(ITestContext context) {
        extent.setSystemInfo("Suite Name", context.getSuite().getName());
        extent.setSystemInfo("Browser", ConfigReader.get("browser"));
        extent.setSystemInfo("Base URL", ConfigReader.get("base.url"));
        extent.setSystemInfo("Download Folder", ConfigReader.get("download.dir"));
        LOGGER.info("Test suite started: " + context.getSuite().getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("Test suite finished: " + context.getSuite().getName());
        extent.flush();
    }
}
