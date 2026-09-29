package com.l2track.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {
    public static void ensureSampleExcel(Path path) throws IOException {
        if (Files.exists(path)) return;
        Files.createDirectories(path.getParent());
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("upload");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("fileRelativePath");
            header.createCell(1).setCellValue("expectedHeader");

            Row r1 = sheet.createRow(1);
            r1.createCell(0).setCellValue("testdata/sample.txt");
            r1.createCell(1).setCellValue("This is a sample text file for upload/download tests.");

            Row r2 = sheet.createRow(2);
            r2.createCell(0).setCellValue("testdata/sample.csv");
            r2.createCell(1).setCellValue("Name,Value");

            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                workbook.write(fos);
            }
        }
    }

    public static List<String[]> readExcel(Path path) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(path.toFile());
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> it = sheet.iterator();
            if (it.hasNext()) it.next(); // skip header
            while (it.hasNext()) {
                Row row = it.next();
                String p = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                String expected = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                data.add(new String[]{p, expected});
            }
        }
        return data;
    }
}
