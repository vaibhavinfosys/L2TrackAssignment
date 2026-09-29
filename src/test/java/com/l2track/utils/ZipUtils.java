package com.l2track.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void ensureZipContainsTestFiles(Path zipPath, Path baseDir) throws IOException {
        if (Files.exists(zipPath)) {
            // basic validation: try opening
            try (FileInputStream fis = new FileInputStream(zipPath.toFile())) {
                // if readable, assume ok
                return;
            } catch (Exception e) {
                // fall through and recreate
            }
        }
        Files.createDirectories(zipPath.getParent());
        try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            // add sample.txt
            Path t1 = baseDir.resolve("sample.txt");
            if (Files.exists(t1)) addFileToZip(zos, t1, "sample.txt");
            Path t2 = baseDir.resolve("sample.csv");
            if (Files.exists(t2)) addFileToZip(zos, t2, "sample.csv");
        }
    }

    private static void addFileToZip(ZipOutputStream zos, Path file, String entryName) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        byte[] data = Files.readAllBytes(file);
        zos.write(data, 0, data.length);
        zos.closeEntry();
    }
}
