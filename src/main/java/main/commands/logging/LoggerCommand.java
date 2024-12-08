package main.commands.logging;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.io.FileWriter;
import java.nio.file.Path;

@Command(
    name = "logger",
    mixinStandardHelpOptions = true,
    description = "CLI-based logging utility with configurable levels and outputs.",
    subcommands = {
        LoggerCommand.ExamplesCommand.class
    }
)
public class LoggerCommand implements Runnable {

    @Parameters(index = "0", description = "The message to log. Can also be specified with -m or --message.", arity = "0..1")
    private String positionalMessage;

    @Option(names = {"-m", "--message"}, description = "The message to log. Overrides the positional argument if both are provided.")
    private String message;

    @Option(names = {"-l", "--level"}, description = "Log level: INFO, WARN, ERROR, DEBUG, TRACE.", defaultValue = "INFO")
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
            // Resolve the log message
            String logMessage = message != null ? message : positionalMessage;
            if (logMessage == null || logMessage.isEmpty()) {
                throw new IllegalArgumentException("A message is required for logging.");
            }

            String formattedLogMessage = formatLogMessage(logMessage);

            // Handle output
            if ("stdout".equalsIgnoreCase(output)) {
                System.out.println(formattedLogMessage);
            } else if ("stderr".equalsIgnoreCase(output)) {
                System.err.println(formattedLogMessage);
            } else {
                try (FileWriter writer = new FileWriter(Path.of(output).toFile(), true)) {
                    writer.write(formattedLogMessage + System.lineSeparator());
                }
            }
        } catch (Exception e) {
            System.err.printf("Error while logging: %s%n", e.getMessage());
        }
    }

    private String formatLogMessage(String logMessage) {
        StringBuilder logBuilder = new StringBuilder();

        if (Boolean.TRUE.equals(timestamp)) {
            logBuilder.append(String.format("[%s] ", java.time.LocalDateTime.now()));
        }

        logBuilder.append(String.format("[%s] ", level.toUpperCase()));

        if ("json".equalsIgnoreCase(format)) {
            logBuilder.append(String.format("{\"message\": \"%s\"}", logMessage));
        } else {
            logBuilder.append(logMessage);
        }

        return logBuilder.toString();
    }

    // Subcommand for examples
    @Command(name = "examples", description = "Show usage examples for the logger tool.")
    static class ExamplesCommand implements Runnable {

        @Override
        public void run() {
            System.out.println("\n=== Logger Tool Usage Examples ===\n");

            // Logging examples
            System.out.println("1. Log a simple INFO message to stdout:");
            System.out.println("   $ qbox logger \"This is an info log.\"");
            System.out.println();

            System.out.println("2. Log a DEBUG message to stderr:");
            System.out.println("   $ qbox logger -m \"Debugging this feature.\" -l DEBUG -o stderr");
            System.out.println();

            System.out.println("3. Log an ERROR message to a file:");
            System.out.println("   $ qbox logger -m \"An error occurred.\" -l ERROR -o error.log");
            System.out.println();

            System.out.println("4. Log a WARN message in JSON format:");
            System.out.println("   $ qbox logger \"Warning: High memory usage.\" -l WARN -f json");
            System.out.println();

            System.out.println("5. Log a TRACE message without a timestamp:");
            System.out.println("   $ qbox logger -m \"Tracing program execution.\" -l TRACE -t false");
            System.out.println();
        }
    }
}
