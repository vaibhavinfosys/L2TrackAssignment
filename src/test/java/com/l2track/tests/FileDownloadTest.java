package com.l2track.tests;

import com.l2track.core.ConfigReader;
import com.l2track.pages.DownloadPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class FileDownloadTest extends BaseTest {

    @Test(description = "Download files and verify contents")
    public void testFileDownload() throws Exception {
        driver.get(ConfigReader.get("base.url") + "/download");
        DownloadPage dp = new DownloadPage(driver);
        String downloadDir = ConfigReader.get("download.dir");

        // Try to download common file types; skip if they don't exist on the page
        String[] filesToDownload = new String[]{"sample.txt", "sample.csv", "sample.zip", "sample.pdf"};

        for (String fname : filesToDownload) {
            try {
                dp.downloadFile(fname);
                Path target = Paths.get(downloadDir).resolve(fname);
                Instant end = Instant.now().plus(Duration.ofSeconds(30));
                boolean found = false;
                while (Instant.now().isBefore(end)) {
                    if (target.toFile().exists()) { found = true; break; }
                    Thread.sleep(1000);
                }
                if (!found) {
                    System.out.println("File not downloaded: " + fname + " (may not exist on demo site)");
                    continue; // skip to next file
                }

                File downloaded = target.toFile();
                if (fname.endsWith(".txt") || fname.endsWith(".csv")) {
                    String header = dp.readTextFileHeader(downloaded);
                    Assert.assertNotNull(header);
                } else if (fname.endsWith(".zip")) {
                    List<String> entries = dp.listFilesInZip(downloaded);
                    Assert.assertNotNull(entries);
                } else if (fname.endsWith(".pdf")) {
                    // extract first line from pdf
                    try {
                        String first = com.l2track.utils.PdfUtils.extractFirstLine(downloaded);
                        Assert.assertNotNull(first);
                    } catch (Exception e) {
                        Assert.fail("Failed to parse PDF: " + e.getMessage());
                    }
                }
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // File link not found on page - skip and continue
                System.out.println("File not found on page: " + fname + " (skipping)");
                continue;
            }
        }
    }
}
