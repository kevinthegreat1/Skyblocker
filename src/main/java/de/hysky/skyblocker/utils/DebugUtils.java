package de.hysky.skyblocker.utils;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class DebugUtils {
    public static void init() {
        if (SkyblockerConfigManager.get().general.debug) {
            setLogLevel(Level.DEBUG);
        }
    }

    public static void setLogLevel(Level level) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("de.hysky.skyblocker");
        if (!loggerConfig.getName().equals("de.hysky.skyblocker")) {
            LoggerConfig skyblockerLoggerConfig = new LoggerConfig("de.hysky.skyblocker", level, true);
            config.addLogger("de.hysky.skyblocker", skyblockerLoggerConfig);
            loggerConfig = skyblockerLoggerConfig;
        }
        loggerConfig.setLevel(level);
        context.updateLoggers();
    }
}
