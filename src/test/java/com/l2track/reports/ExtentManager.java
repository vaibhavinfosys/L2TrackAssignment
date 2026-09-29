package com.l2track.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

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
                spark.config().setReportName("L2Track Assignment Report");
                extent = new ExtentReports();
                extent.attachReporter(spark);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return extent;
    }
}
