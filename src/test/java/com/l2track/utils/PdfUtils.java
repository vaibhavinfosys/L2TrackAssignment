package com.l2track.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfUtils {
    public static String extractFirstLine(File pdf) throws IOException {
        try (PDDocument document = PDDocument.load(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text == null || text.isEmpty()) return "";
            String[] lines = text.split("\r?\n");
            return lines.length > 0 ? lines[0] : "";
        }
    }
}
