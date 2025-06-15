package client;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClientLogger log with timestamp and log messages at different levels:
 * INFO, WARNING, and SEVERE (for errors).
 */
public class ClientLogger {
    private static final Logger LOGGER = Logger.getLogger(ClientLogger.class.getName());

    public static void logInfo(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.INFO, "[" + timestamp + "] " + message);
    }

    public static void logWarning(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.WARNING, "[" + timestamp + "] " + message);
    }

    public static void logError(String message, Exception e) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.SEVERE, "[" + timestamp + "] " + message, e);
    }

}
