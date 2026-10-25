package com.l2track.tests;

import com.l2track.core.ConfigReader;
import com.l2track.dataproviders.ExcelDataProvider;
import com.l2track.pages.UploadPage;
import com.l2track.utils.LoggerUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class FileUploadTest extends BaseTest {
    private static final Logger LOGGER = LoggerUtil.getLogger(FileUploadTest.class);

    @Test(dataProvider = "uploadFiles",
            dataProviderClass = ExcelDataProvider.class,
            description = "Upload files driven by Excel test data")
    public void testFileUpload(String fileRelativePath, String expectedHeader) {
        Assert.assertNotNull(fileRelativePath, "File relative path from Excel should not be null");
        Assert.assertFalse(fileRelativePath.trim().isEmpty(), "File relative path from Excel should not be empty");
        Assert.assertNotNull(expectedHeader, "Expected header from Excel should not be null");

        String absolutePath = Paths.get("src/test/resources", fileRelativePath).toAbsolutePath().toString();
        LOGGER.info("Uploading file: " + absolutePath + " | expected header: " + expectedHeader);

        driver.get(ConfigReader.get("base.url") + "/upload");
        UploadPage uploadPage = new UploadPage(driver);
        uploadPage.uploadFile(absolutePath);

        String uploaded = uploadPage.getUploadedFileName();
        String uploadedFileName = new File(absolutePath).getName();

        LOGGER.info("Uploaded file name displayed: " + uploaded);
        Assert.assertTrue(uploaded != null && uploaded.toLowerCase().contains(uploadedFileName.toLowerCase()),
                "Uploaded file name should be displayed after upload. Actual value: " + uploaded);
    }
}
