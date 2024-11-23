package main.commands.logging;

import org.jboss.logging.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerService {

    private static final Logger LOGGER = Logger.getLogger(LoggerService.class);

    public static void log(String message, String level, String output, String format, boolean includeTimestamp) {
        validateInputs(level, format);

        String formattedMessage = formatMessage(message, level, format, includeTimestamp);

        switch (output.toLowerCase()) {
            case "stdout":
                System.out.println(formattedMessage);
                break;
            case "stderr":
                System.err.println(formattedMessage);
                break;
            default:
                writeToFile(formattedMessage, output);
                break;
        }
    }

    private static void validateInputs(String level, String format) {
        if (!isValidLevel(level)) {
            throw new IllegalArgumentException("Invalid log level. Supported levels: INFO, WARN, ERROR, DEBUG, TRACE.");
        }
        if (!isValidFormat(format)) {
            throw new IllegalArgumentException("Invalid log format. Supported formats: plain, json.");
        }
    }

    private static boolean isValidLevel(String level) {
        return level.equalsIgnoreCase("INFO") ||
               level.equalsIgnoreCase("WARN") ||
               level.equalsIgnoreCase("ERROR") ||
               level.equalsIgnoreCase("DEBUG") ||
               level.equalsIgnoreCase("TRACE");
    }

    private static boolean isValidFormat(String format) {
        return format.equalsIgnoreCase("plain") || format.equalsIgnoreCase("json");
    }

    private static String formatMessage(String message, String level, String format, boolean includeTimestamp) {
        StringBuilder logBuilder = new StringBuilder();

        if (includeTimestamp) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            logBuilder.append("[").append(timestamp).append("] ");
        }

        logBuilder.append("[").append(level.toUpperCase()).append("] ");

        if ("json".equalsIgnoreCase(format)) {
            logBuilder.append("{ \"message\": \"").append(message)
                      .append("\", \"level\": \"").append(level.toUpperCase()).append("\" }");
        } else {
            logBuilder.append(message);
        }

        return logBuilder.toString();
    }

    private static void writeToFile(String message, String filePath) {
        Path path = Path.of(filePath);

        try {
            Files.writeString(path, message + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            LOGGER.info("Log message written to: " + path.toAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Error writing log to file: " + e.getMessage(), e);
        }
    }
}
