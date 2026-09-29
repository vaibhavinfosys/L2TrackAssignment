package com.l2track.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.l2track.core.ConfigReader;
import com.l2track.core.DriverFactory;
import com.l2track.reports.ExtentManager;
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

public class TestListener implements ITestListener {
    private static ExtentReports extent = ExtentManager.getInstance(ConfigReader.get("reports.dir"));
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest t = extent.createTest(result.getMethod().getMethodName());
        test.set(t);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test passed");
        extent.flush();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, result.getThrowable());
        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            try {
                Path p = Paths.get(ConfigReader.get("screenshot.dir"));
                if (!p.toFile().exists()) p.toFile().mkdirs();
                String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File dest = p.resolve(fileName).toFile();
                FileUtils.copyFile(src, dest);
                test.get().addScreenCaptureFromPath(dest.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        extent.flush();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test skipped");
        extent.flush();
    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
