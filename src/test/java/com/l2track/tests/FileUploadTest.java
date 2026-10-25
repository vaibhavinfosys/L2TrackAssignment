package com.l2track.tests;

import com.l2track.core.ConfigReader;
import com.l2track.pages.UploadPage;
import com.l2track.utils.LoggerUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class FileUploadTest extends BaseTest {
    private static final Logger LOGGER = LoggerUtil.getLogger(FileUploadTest.class);
    private static final List<String> SUPPORTED_EXTENSIONS = List.of("txt", "csv", "pdf", "jpg", "jpeg", "png", "xlsx", "xls");

    @DataProvider(name = "uploadFiles")
    public static Object[][] uploadFiles() throws IOException {
        Path testDataDir = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "testdata");
        ensureRequiredFiles(testDataDir);

        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testDataDir)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    String ext = getExtension(path.getFileName().toString());
                    if (SUPPORTED_EXTENSIONS.contains(ext.toLowerCase(Locale.ENGLISH))) {
                        files.add(path);
                    }
                }
            }
        }

        files.sort(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ENGLISH)));

        Object[][] data = new Object[files.size()][];
        for (int i = 0; i < files.size(); i++) {
            Path file = files.get(i);
            String relativePath = Paths.get("testdata").resolve(file.getFileName().toString()).toString().replace('\\', '/');
            data[i] = new Object[] { relativePath, file.getFileName().toString() };
        }
        return data;
    }

    private static void ensureRequiredFiles(Path testDataDir) throws IOException {
        Files.createDirectories(testDataDir);

        createTextFile(testDataDir.resolve("sample.txt"), "This is a sample text file for upload validation.\nLine 2\nLine 3\n");
        createTextFile(testDataDir.resolve("sample.csv"), "Name,Value\nalpha,100\nbeta,200\n");
        createPdfPlaceholder(testDataDir.resolve("sample.pdf"));
        createImagePlaceholder(testDataDir.resolve("sample.jpg"), "fake-jpg-content");
        createImagePlaceholder(testDataDir.resolve("sample.png"), "fake-png-content");
        createXlsxFile(testDataDir.resolve("sample.xlsx"));
    }

    private static void createTextFile(Path file, String content) throws IOException {
        if (!Files.exists(file)) {
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void createPdfPlaceholder(Path file) throws IOException {
        if (!Files.exists(file)) {
            String pdfContent = "%PDF-1.4\n1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 200 200] /Contents 4 0 R >>\nendobj\n4 0 obj\n<< /Length 30 >>\nstream\nBT /F1 20 Tf 50 50 Td (Sample PDF) Tj ET\nendstream\nendobj\ntrailer\n<< /Root 1 0 R >>\n%%EOF";
            Files.write(file, pdfContent.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void createImagePlaceholder(Path file, String content) throws IOException {
        if (!Files.exists(file)) {
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void createXlsxFile(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file.toFile())) {
            Sheet sheet = workbook.createSheet("upload");
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("FileName");
            row.createCell(1).setCellValue("UploadStatus");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("sample.xlsx");
            dataRow.createCell(1).setCellValue("Uploaded");

            workbook.write(fos);
        }
    }

    private static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex >= 0 ? filename.substring(dotIndex + 1) : "";
    }

    @Test(dataProvider = "uploadFiles", description = "Upload supported file types from testdata and validate confirmation")
    public void testFileUpload(String relativePath, String fileName) {
        Assert.assertNotNull(relativePath, "Relative path should not be null");
        Assert.assertNotNull(fileName, "File name should not be null");

        String absolutePath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", relativePath)
                .normalize()
                .toString();

        LOGGER.info("Uploading file: " + absolutePath);
        Assert.assertTrue(Files.exists(Paths.get(absolutePath)), "File does not exist: " + absolutePath);

        driver.get(ConfigReader.get("base.url") + "/upload");
        UploadPage uploadPage = new UploadPage(driver);
        uploadPage.uploadFile(absolutePath);

        String uploaded = uploadPage.getUploadedFileName();
        LOGGER.info("Uploaded file name displayed: " + uploaded);

        Assert.assertTrue(uploaded != null && uploaded.toLowerCase(Locale.ENGLISH).contains(fileName.toLowerCase(Locale.ENGLISH)),
                "Uploaded file name should be displayed. Actual value: " + uploaded);
        Assert.assertTrue(driver.getPageSource().contains("File Uploaded!"),
                "Confirmation message 'File Uploaded!' was not displayed after upload.");
    }
}
