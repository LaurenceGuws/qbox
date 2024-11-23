// package main.commands.formatter;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.Test;
// import picocli.CommandLine;
// import org.jboss.logging.Logger;

// import java.io.ByteArrayOutputStream;
// import java.io.PrintStream;
// import java.nio.file.Files;
// import java.nio.file.Path;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// public class FormatterCommandTest {

//     private static final Logger LOGGER = Logger.getLogger(FormatterCommandTest.class);

//     // Temporary files used in tests
//     private Path inputFile;
//     private Path outputFile;

//     @AfterEach
//     public void cleanUp() throws Exception {
//         LOGGER.info("Cleaning up files...");
//         if (inputFile != null && Files.exists(inputFile)) {
//             Files.deleteIfExists(inputFile);
//         }
//         if (outputFile != null && Files.exists(outputFile)) {
//             Files.deleteIfExists(outputFile);
//         }
//     }

//     @Test
//     public void testJsonToYamlConversion() throws Exception {
//         performConversionTest(
//                 "test-input.json",
//                 "{ \"name\": \"Alice\", \"age\": 30 }",
//                 Formatter.FormatType.JSON,
//                 "test-output.yaml",
//                 Formatter.FormatType.YAML,
//                 "name: \"Alice\"",
//                 "age: 30"
//         );
//     }

//     @Test
//     public void testYamlToJsonConversion() throws Exception {
//         performConversionTest(
//                 "test-input.yaml",
//                 "name: Alice\nage: 30",
//                 Formatter.FormatType.YAML,
//                 "test-output.json",
//                 Formatter.FormatType.JSON,
//                 "\"name\" : \"Alice\"",
//                 "\"age\" : 30"
//         );
//     }

//     @Test
//     public void testJsonToXmlConversion() throws Exception {
//         performConversionTest(
//                 "test-input.json",
//                 "{ \"person\": { \"name\": \"Alice\", \"age\": 30 } }",
//                 Formatter.FormatType.JSON,
//                 "test-output.xml",
//                 Formatter.FormatType.XML,
//                 "<person>",
//                 "<name>Alice</name>",
//                 "<age>30</age>"
//         );
//     }

//     @Test
//     public void testXmlToJsonConversion() throws Exception {
//         performConversionTest(
//                 "test-input.xml",
//                 "<person><name>Alice</name><age>30</age></person>",
//                 Formatter.FormatType.XML,
//                 "test-output.json",
//                 Formatter.FormatType.JSON,
//                 "\"name\" : \"Alice\"",
//                 "\"age\" : 30"
//         );
//     }

//     @Test
//     public void testJsonToCsvConversion() throws Exception {
//         performConversionTest(
//                 "test-input.json",
//                 "[ { \"name\": \"Alice\", \"age\": 30 }, { \"name\": \"Bob\", \"age\": 25 } ]",
//                 Formatter.FormatType.JSON,
//                 "test-output.csv",
//                 Formatter.FormatType.CSV,
//                 "name,age",
//                 "Alice,30",
//                 "Bob,25"
//         );
//     }

//     @Test
//     public void testCsvToJsonConversion() throws Exception {
//         performConversionTest(
//                 "test-input.csv",
//                 "name,age\nAlice,30\nBob,25",
//                 Formatter.FormatType.CSV,
//                 "test-output.json",
//                 Formatter.FormatType.JSON,
//                 "\"name\" : \"Alice\"",
//                 "\"age\" : \"30\"",
//                 "\"name\" : \"Bob\"",
//                 "\"age\" : \"25\""
//         );
//     }

//     @Test
//     public void testPlainTextToJsonConversion() throws Exception {
//         performConversionTest(
//                 "test-input.txt",
//                 "{ \"name\": \"Alice\", \"age\": 30 }",
//                 Formatter.FormatType.PLAIN_TEXT,
//                 "test-output.json",
//                 Formatter.FormatType.JSON,
//                 "\"name\" : \"Alice\"",
//                 "\"age\" : 30"
//         );
//     }

//     @Test
//     public void testJsonToTableConversion() throws Exception {
//         performConversionTest(
//                 "test-input.json",
//                 "{ \"name\": \"Alice\", \"age\": 30 }",
//                 Formatter.FormatType.JSON,
//                 "test-output.txt",
//                 Formatter.FormatType.TABLE,
//                 "name                 : \"Alice\"",
//                 "age                  : 30"
//         );
//     }

//     @Test
//     public void testYamlToPlainTextConversion() throws Exception {
//         performConversionTest(
//                 "test-input.yaml",
//                 "message: Hello, World!",
//                 Formatter.FormatType.YAML,
//                 "test-output.txt",
//                 Formatter.FormatType.PLAIN_TEXT,
//                 "message: Hello, World!"
//         );
//     }

//     @Test
//     public void testInvalidFormatConversion() throws Exception {
//         inputFile = Path.of("test-invalid-input.xyz");
//         outputFile = Path.of("test-output.json");

//         LOGGER.info("Starting test: testInvalidFormatConversion");

//         // Step 1: Create an input file with unsupported format
//         String content = "Unsupported content format";
//         Files.writeString(inputFile, content);
//         assertTrue(Files.exists(inputFile), "Input file should exist.");

//         // Step 2: Execute the formatter command and capture output
//         LOGGER.info("Executing formatter command with unsupported input format");
//         FormatterCommand.FormatCommand formatCommand = new FormatterCommand.FormatCommand();
//         CommandLine cmd = new CommandLine(formatCommand);

//         ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
//         System.setErr(new PrintStream(errorOutput));

//         int exitCode = cmd.execute(
//                 "--input", inputFile.toString(),
//                 "--output", outputFile.toString(),
//                 "--input-format", "UNSUPPORTED",
//                 "--output-format", "JSON",
//                 "--file"
//         );

//         // Step 3: Verify that an error occurred
//         String errorMsg = errorOutput.toString().trim();
//         LOGGER.info("Error output: " + errorMsg);
//         assertTrue(exitCode != 0, "Exit code should be non-zero for invalid input format.");
//         assertTrue(errorMsg.contains("Unsupported input format"), "Error message should indicate unsupported input format.");
//     }

//     // Helper method to perform conversion tests
//     private void performConversionTest(
//             String inputFileName,
//             String inputContent,
//             Formatter.FormatType inputFormat,
//             String outputFileName,
//             Formatter.FormatType outputFormat,
//             String... expectedOutputs
//     ) throws Exception {
//         inputFile = Path.of(inputFileName);
//         outputFile = Path.of(outputFileName);

//         LOGGER.info(String.format("Starting test: %s to %s conversion", inputFormat, outputFormat));

//         // Step 1: Create an input file
//         Files.writeString(inputFile, inputContent);
//         assertTrue(Files.exists(inputFile), "Input file should exist.");

//         // Step 2: Execute the formatter command
//         LOGGER.info(String.format("Executing formatter command to convert %s to %s", inputFormat, outputFormat));
//         FormatterCommand.FormatCommand formatCommand = new FormatterCommand.FormatCommand();
//         CommandLine cmd = new CommandLine(formatCommand);

//         cmd.execute(
//                 "--input", inputFile.toString(),
//                 "--output", outputFile.toString(),
//                 "--input-format", inputFormat.name(),
//                 "--output-format", outputFormat.name(),
//                 "--file"
//         );

//         // Step 3: Verify the output file
//         assertTrue(Files.exists(outputFile), "Output file should exist.");
//         String outputContent = Files.readString(outputFile);
//         LOGGER.info(String.format("%s output content:\n%s", outputFormat, outputContent));

//         for (String expectedOutput : expectedOutputs) {
//             assertTrue(outputContent.contains(expectedOutput), String.format("Output should contain '%s'.", expectedOutput));
//         }

//         LOGGER.info(String.format("Test completed: %s to %s conversion", inputFormat, outputFormat));
//     }
// }
