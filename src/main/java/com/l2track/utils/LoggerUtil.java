package com.l2track.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerUtil {
    private static final Logger ROOT_LOGGER = Logger.getLogger("L2TrackAssignment");

    private LoggerUtil() {
    }

    static {
        try {
            Path logDir = Paths.get("target", "logs");
            Files.createDirectories(logDir);

            Path logFile = logDir.resolve("l2track.log");
            FileHandler fileHandler = new FileHandler(logFile.toString(), true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.INFO);

            ROOT_LOGGER.setUseParentHandlers(false);
            ROOT_LOGGER.setLevel(Level.INFO);
            ROOT_LOGGER.addHandler(fileHandler);
            ROOT_LOGGER.addHandler(consoleHandler);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize L2Track logging", e);
        }
    }

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setUseParentHandlers(false);
        logger.setLevel(ROOT_LOGGER.getLevel());
        for (var handler : ROOT_LOGGER.getHandlers()) {
            logger.addHandler(handler);
        }
        return logger;
    }

    public static void info(String message) {
        ROOT_LOGGER.log(Level.INFO, message);
    }

    public static void warn(String message) {
        ROOT_LOGGER.log(Level.WARNING, message);
    }

    public static void error(String message, Throwable throwable) {
        ROOT_LOGGER.log(Level.SEVERE, message, throwable);
    }
}
