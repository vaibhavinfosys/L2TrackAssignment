package com.l2track.tests;

import com.l2track.core.ConfigReader;
import com.l2track.core.DriverFactory;
import com.l2track.listeners.TestListener;
import com.l2track.utils.ExcelUtils;
import com.l2track.utils.ZipUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Listeners({TestListener.class})
public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String downloadDir = ConfigReader.get("download.dir");
        if (downloadDir == null || downloadDir.trim().isEmpty()) {
            downloadDir = "target/downloads";
        }

        Path resolvedDownloadDir = ConfigReader.getPath("download.dir");
        if (resolvedDownloadDir == null || resolvedDownloadDir.toString().endsWith(System.getProperty("user.dir"))) {
            resolvedDownloadDir = Paths.get(System.getProperty("user.dir"), "target", "downloads");
        }

        DriverFactory.initDriver(resolvedDownloadDir.toString());
        driver = DriverFactory.getDriver();
        driver.manage().window().maximize();

        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            driver.get(baseUrl);
        }

        try {
            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            Path testDataDir = projectRoot.resolve("src/test/resources/testdata");

            if (Files.exists(testDataDir)) {
                Path zipPath = testDataDir.resolve("sample.zip");
                ZipUtils.ensureZipContainsTestFiles(zipPath, testDataDir);

                Path excel = testDataDir.resolve("testdata.xlsx");
                ExcelUtils.ensureSampleExcel(excel);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to prepare test data files", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            DriverFactory.quitDriver();
        }
    }
}
