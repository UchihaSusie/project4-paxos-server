package server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServerLogger provides a centralized logging utility for the server-side application.
 * The logger includes timestamps, client IP addresses, and thread identifiers for detailed logs.
 * It will log messages at different levels: INFO, WARNING, and SEVERE (for errors).
 */
public class ServerLogger {
    // Logger instance for the ServerLogger class
    private static final Logger LOGGER = Logger.getLogger(ServerLogger.class.getName());

    /**
     * Logs an informational message, including client address and thread ID.
     *
     * @param message       the message to log
     * @param clientAddress the IP address of the client
     * @param threadName    the thread ID or name
     */
    public static void logInfo(String message, String clientAddress, long threadName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.INFO, " [" + timestamp + "], IP: " + clientAddress + ", Thread ID: "
                + threadName + ") : " + message);
    }

    /**
     * Logs a warning message, including client address.
     *
     * @param message       the warning message to log
     * @param clientAddress the IP address of the client
     */
    public static void logWarning(String message, String clientAddress) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.WARNING, " [" + timestamp + "], IP:" + clientAddress + ","  + message);

    }

    /**
     * Logs an error message, including client address and exception details.
     *
     * @param message       the error message to log
     * @param clientAddress the IP address of the client
     * @param e             the exception to include in the log
     */
    public static void logError(String message,String clientAddress, Exception e) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.SEVERE, " [" + timestamp + "], IP:" + clientAddress + " ," + message, e);
    }

    /**
     * Logs a general informational message.
     *
     * @param message the message to log
     */
    public static void logInfo(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.INFO, " [" + timestamp + "] " + message);
    }

    /**
     * Logs a general warning message.
     *
     * @param message the warming message to log
     */
    public static void logWarning(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.WARNING, " [" + timestamp + "] " + message);
    }

    /**
     * Logs a general error message, including exception details.
     *
     * @param message the error message to log
     * @param e       the exception to include in the log
     */
    public static void logError(String message, Exception e) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LOGGER.log(Level.SEVERE, " [" + timestamp + "] " + message, e);
    }
}
