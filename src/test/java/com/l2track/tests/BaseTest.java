package com.l2track.tests;

import com.l2track.core.ConfigReader;
import com.l2track.core.DriverFactory;
import com.l2track.listeners.TestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.l2track.utils.ZipUtils;
import com.l2track.utils.ExcelUtils;
import java.io.IOException;

@Listeners({TestListener.class})
public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String downloadDir = ConfigReader.get("download.dir");
        DriverFactory.initDriver(downloadDir);
        driver = DriverFactory.getDriver();
        driver.manage().window().maximize();
        driver.get(ConfigReader.get("base.url"));

        // ensure download dir exists
        try {
            Path p = Paths.get(downloadDir);
            if (!Files.exists(p)) Files.createDirectories(p);
            // ensure sample zip exists in testdata (create if placeholder)
            Path testDataDir = Paths.get("src/test/resources/testdata");
            Path zipPath = testDataDir.resolve("sample.zip");
            ZipUtils.ensureZipContainsTestFiles(zipPath, testDataDir);
            // ensure sample excel exists for data-driven tests
            Path excel = testDataDir.resolve("testdata.xlsx");
            ExcelUtils.ensureSampleExcel(excel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
