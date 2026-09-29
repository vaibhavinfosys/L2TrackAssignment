package com.l2track.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties props = new Properties();

    static {
        String configPath = System.getProperty("configFile", "src/test/resources/config.properties");
        try (InputStream input = new FileInputStream(configPath)) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config properties from " + configPath, e);
        }
    }

    public static String get(String key) {
        String val = props.getProperty(key);
        if (val == null) return null;
        // handle ${user.home}
        return val.replace("${user.home}", System.getProperty("user.home"));
    }
}
