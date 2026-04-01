package net.optifine.shaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SMCLog {
    private static final Logger LOGGER = LogManager.getLogger(SMCLog.class);
    private static final String PREFIX = "[shaders] ";

    public static void severe(String message) {
        LOGGER.error("[shaders] " + message);
    }

    public static void warning(String message) {
        LOGGER.warn("[shaders] " + message);
    }

    public static void info(String message) {
        LOGGER.info("[shaders] " + message);
    }

    public static void fine(String message) {
        LOGGER.debug("[shaders] " + message);
    }

    public static void severe(String format, Object... args) {
        String s = String.format(format, args);
        LOGGER.error("[shaders] " + s);
    }

    public static void warning(String format, Object... args) {
        String s = String.format(format, args);
        LOGGER.warn("[shaders] " + s);
    }

    public static void info(String format, Object... args) {
        String s = String.format(format, args);
        LOGGER.info("[shaders] " + s);
    }

    public static void fine(String format, Object... args) {
        String s = String.format(format, args);
        LOGGER.debug("[shaders] " + s);
    }
}
