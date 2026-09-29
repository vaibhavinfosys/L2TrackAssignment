package com.l2track.dataproviders;

import com.l2track.utils.ExcelUtils;
import org.testng.annotations.DataProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExcelDataProvider {
    @DataProvider(name = "uploadFiles")
    public static Object[][] uploadFiles() throws Exception {
        Path excel = Paths.get("src/test/resources/testdata/testdata.xlsx");
        // Ensure the Excel file exists before reading (DataProvider runs before setUp)
        ExcelUtils.ensureSampleExcel(excel);
        List<String[]> rows = ExcelUtils.readExcel(excel);
        Object[][] out = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            out[i] = new Object[]{rows.get(i)[0], rows.get(i)[1]};
        }
        return out;
    }
}
