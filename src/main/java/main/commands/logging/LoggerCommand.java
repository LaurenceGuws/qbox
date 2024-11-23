package main.commands.logging;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.FileWriter;
import java.nio.file.Path;

@Command(name = "logger", mixinStandardHelpOptions = true, description = "CLI-based logging utility with configurable levels and outputs.")
public class LoggerCommand implements Runnable {

    @Option(names = {"-m", "--message"}, description = "The message to log.", required = true)
    private String message;

    @Option(names = {"-l", "--level"}, description = "Log level: INFO, WARN, ERROR, DEBUG, TRACE.", required = true)
    private String level;

    @Option(names = {"-o", "--output"}, description = "Output destination: 'stdout', 'stderr', or file path.", defaultValue = "stdout")
    private String output;

    @Option(names = {"-f", "--format"}, description = "Log format: 'plain', 'json'.", defaultValue = "plain")
    private String format;

    @Option(names = {"-t", "--timestamp"}, description = "Include timestamp in logs (true/false).", defaultValue = "true")
    private Boolean timestamp;

    @Override
    public void run() {
        try {
            String logMessage = formatLogMessage();

            if ("stdout".equalsIgnoreCase(output)) {
                System.out.println(logMessage);
            } else if ("stderr".equalsIgnoreCase(output)) {
                System.err.println(logMessage);
            } else {
                try (FileWriter writer = new FileWriter(Path.of(output).toFile(), true)) {
                    writer.write(logMessage + System.lineSeparator());
                }
            }
        } catch (Exception e) {
            System.err.printf("Error while logging: %s%n", e.getMessage());
        }
    }

    private String formatLogMessage() {
        StringBuilder logBuilder = new StringBuilder();

        if (Boolean.TRUE.equals(timestamp)) {
            logBuilder.append(String.format("[%s] ", java.time.LocalDateTime.now()));
        }

        logBuilder.append(String.format("[%s] ", level.toUpperCase()));

        if ("json".equalsIgnoreCase(format)) {
            logBuilder.append(String.format("{\"message\": \"%s\"}", message));
        } else {
            logBuilder.append(message);
        }

        return logBuilder.toString();
    }
}
