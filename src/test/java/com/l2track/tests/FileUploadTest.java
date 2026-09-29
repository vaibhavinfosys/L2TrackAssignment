package com.l2track.tests;

import com.l2track.pages.UploadPage;
import com.l2track.core.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;

public class FileUploadTest extends BaseTest {

    @org.testng.annotations.Test(dataProvider = "uploadFiles", dataProviderClass = com.l2track.dataproviders.ExcelDataProvider.class,
            description = "Upload files driven by Excel testdata")
    public void testFileUpload(String relativePath, String expectedHeader) {
        driver.get(ConfigReader.get("base.url") + "/upload");
        UploadPage uploadPage = new UploadPage(driver);

        String path = Paths.get("src/test/resources", relativePath).toAbsolutePath().toString();
        uploadPage.uploadFile(path);
        String uploaded = uploadPage.getUploadedFileName();
        Assert.assertTrue(uploaded != null && uploaded.contains(new File(path).getName()), "Uploaded file name should be displayed");
    }
}
