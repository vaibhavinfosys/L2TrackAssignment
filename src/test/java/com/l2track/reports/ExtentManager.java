package com.l2track.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtentManager {
    private static ExtentReports extent;

    public synchronized static ExtentReports getInstance(String reportsDir) {
        if (extent == null) {
            try {
                Path p = Paths.get(reportsDir);
                if (!Files.exists(p)) Files.createDirectories(p);

                String reportPath = p.resolve("extent-report.html").toString();
                ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
                spark.config().setTheme(Theme.STANDARD);
                spark.config().setDocumentTitle("L2Track Assignment Automation Report");
                spark.config().setReportName("L2Track Assignment Report");
                spark.config().setEncoding("utf-8");
                spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

                extent = new ExtentReports();
                extent.attachReporter(spark);
                extent.setSystemInfo("Project", "L2TrackAssignment");
                extent.setSystemInfo("Environment", "QA");
                extent.setSystemInfo("Framework", "Selenium + TestNG + ExtentReports");
            } catch (Exception e) {
                throw new RuntimeException("Unable to initialize Extent Reports", e);
            }
        }
        return extent;
    }
}
