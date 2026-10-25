package com.l2track.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        String configPath = System.getProperty("configFile");
        File file = null;

        if (configPath != null && !configPath.trim().isEmpty()) {
            file = new File(configPath);
            if (!file.isAbsolute()) {
                file = Paths.get(System.getProperty("user.dir"), configPath).toFile();
            }
        }

        if (file == null || !file.exists()) {
            file = resolveDefaultConfigPath().toFile();
        }

        try (InputStream input = new FileInputStream(file)) {
            props.clear();
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config properties from " + file.getAbsolutePath(), e);
        }
    }

    private static Path resolveDefaultConfigPath() {
        return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "config.properties");
    }

    public static String get(String key) {
        String val = props.getProperty(key);
        if (val == null) return null;
        return val.replace("${user.home}", System.getProperty("user.home"));
    }

    public static Path getPath(String key) {
        String value = get(key);
        if (value == null || value.trim().isEmpty()) {
            return Paths.get(System.getProperty("user.dir"));
        }

        Path path = Paths.get(value);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir"), value);
        }
        return path;
    }
}
