package com.l2track.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UploadPage extends BasePage {

    @FindBy(id = "file-upload")
    private WebElement fileInput;

    @FindBy(id = "file-submit")
    private WebElement uploadButton;

    @FindBy(id = "uploaded-files")
    private WebElement uploadedFiles;

    public UploadPage(WebDriver driver) {
        super(driver);
    }

    public void uploadFile(String absoluteFilePath) {
        fileInput.sendKeys(absoluteFilePath);
        uploadButton.click();
    }

    public String getUploadedFileName() {
        try {
            return uploadedFiles.getText();
        } catch (Exception e) {
            return null;
        }
    }
}
