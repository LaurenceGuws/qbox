package main.commands.formatter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
    name = "format",
    mixinStandardHelpOptions = true,
    description = "Format and transform raw text data between various formats.",
    subcommands = {
        FormatterCommand.ExamplesCommand.class
    }
)
public class FormatterCommand implements Callable<Integer> {

    @Option(names = {"-i", "--input"}, description = "Input file or raw data string.", required = true)
    private String input;

    @Option(names = {"-o", "--output"}, description = "Output file or 'stdout' for console output.", required = true)
    private String output;

    @Option(names = {"-if", "--input-format"}, description = "Input format: JSON, YAML, XML, CSV, PLAIN_TEXT.", required = true)
    private Formatter.FormatType inputFormat;

    @Option(names = {"-of", "--output-format"}, description = "Output format: JSON, YAML, XML, CSV, PLAIN_TEXT, TABLE.", required = true)
    private Formatter.FormatType outputFormat;

    @Option(names = {"--file"}, description = "Treat input and output as files.")
    private boolean isFile = false;

    @Option(names = {"-c", "--clean"}, description = "Output only the transformed value without additional text.")
    private boolean cleanOutput;

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
            } else {
                rawData = input;
            }

            // Parse input data
            String normalizedData = Formatter.normalize(rawData, inputFormat);

            // Transform output data
            String formattedOutput = Formatter.transform(normalizedData, outputFormat);

            // Handle output
            if (isFile && !output.equalsIgnoreCase("stdout")) {
                Path outputPath = Path.of(output);
                Files.writeString(outputPath, formattedOutput);
                if (!cleanOutput) {
                    System.out.printf("Formatted data written to: %s%n", output);
                }
            } else {
                System.out.println(cleanOutput ? formattedOutput : "Formatted Output: \n" + formattedOutput);
            }

            return 0;
        } catch (Exception e) {
            System.err.printf("Error processing data: %s%n", e.getMessage());
            return 1;
        }
    }

    // Subcommand for examples
    @Command(name = "examples", description = "Show usage examples for the format tool.")
    static class ExamplesCommand implements Runnable {

        @Override
        public void run() {
            System.out.println("\n=== Format Tool Usage Examples ===\n");

            // Formatting examples
            System.out.println("1. Format a JSON file to YAML:");
            System.out.println("   $ qbox format -i input.json -o output.yaml -if JSON -of YAML --file");
            System.out.println();

            System.out.println("2. Convert plain text to JSON and output to console:");
            System.out.println("   $ qbox format -i 'raw text' -o stdout -if PLAIN_TEXT -of JSON");
            System.out.println();

            System.out.println("3. Reformat an XML file to a human-readable table:");
            System.out.println("   $ qbox format -i input.xml -o output.txt -if XML -of TABLE --file");
            System.out.println();

            System.out.println("4. Use clean output mode for automation:");
            System.out.println("   $ qbox format -i input.csv -o stdout -if CSV -of JSON -c");
            System.out.println();
        }
    }
}
