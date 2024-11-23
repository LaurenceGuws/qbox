package main.commands.formatter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
// import java.util.logging.Logger;

@Command(
    name = "formatter",
    mixinStandardHelpOptions = true,
    description = "Format and transform raw text data between various formats.",
    subcommands = {
        FormatterCommand.FormatCommand.class
    }
)
public class FormatterCommand implements Runnable {

    // private static final Logger LOG = Logger.getLogger(FormatterCommand.class.getName());

    @Override
    public void run() {
        System.out.println("Formatter client. Use --help to view available commands.");
    }

    @Command(name = "format", description = "Formats raw text data between different formats.")
    static class FormatCommand implements Callable<Integer> {

        @Option(names = {"-i", "--input"}, description = "Input file or raw data string.", required = true)
        private String input;

        @Option(names = {"-o", "--output"}, description = "Output file or 'stdout' for console output.", required = true)
        private String output;

        @Option(names = {"--input-format"}, description = "Input format: JSON, YAML, XML, CSV, PLAIN_TEXT.", required = true)
        private Formatter.FormatType inputFormat;

        @Option(names = {"--output-format"}, description = "Output format: JSON, YAML, XML, CSV, PLAIN_TEXT, TABLE.", required = true)
        private Formatter.FormatType outputFormat;

        @Option(names = {"--file"}, description = "Treat input and output as files.")
        private boolean isFile = false;

        @Override
        public Integer call() {
            try {
                String rawData;

                // Handle input
                if (isFile) {
                    Path inputPath = Path.of(input);
                    if (!Files.exists(inputPath)) {
                        throw new IllegalArgumentException(String.format("Input file not found at path: %s", input));
                    }
                    rawData = Files.readString(inputPath);
                    System.out.println("File content loaded successfully.");
                } else {
                    rawData = input;
                }

                // Parse input data
                String normalizedData = Formatter.normalize(rawData, inputFormat);

                // Format output
                String formattedOutput = Formatter.transform(normalizedData, outputFormat);

                // Handle output
                if (isFile && !output.equalsIgnoreCase("stdout")) {
                    Path outputPath = Path.of(output);
                    Files.writeString(outputPath, formattedOutput);
                    System.out.printf("Formatted data written to: %s%n", output);
                } else {
                    System.out.println(formattedOutput);
                }

                return 0;
            } catch (Exception e) {
                System.err.printf("Error processing data: %s%n", e.getMessage());
                return 1;
            }
        }
    }
}
