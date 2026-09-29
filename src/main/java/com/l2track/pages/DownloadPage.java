package com.l2track.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadPage extends BasePage {

    public DownloadPage(WebDriver driver) {
        super(driver);
    }

    public void downloadFile(String fileName) {
        // Try exact link text match first
        try {
            WebElement link = driver.findElement(By.linkText(fileName));
            link.click();
            return;
        } catch (Exception e) {
            // Fall through to partial match
        }
        // Try partial match if exact link text fails
        WebElement link = driver.findElement(By.partialLinkText(fileName));
        link.click();
        // caller will verify file presence in download folder
    }

    public List<WebElement> getAvailableDownloadLinks() {
        return driver.findElements(By.xpath("//a[@href]"));
    }

    public String readTextFileHeader(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        if (lines.isEmpty()) return "";
        return lines.get(0);
    }

    public List<String> listFilesInZip(File zipFile) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            java.util.ArrayList<String> names = new java.util.ArrayList<>();
            while ((entry = zis.getNextEntry()) != null) {
                names.add(entry.getName());
            }
            return names;
        }
    }
}
