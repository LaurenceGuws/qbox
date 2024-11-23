package main.commands.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Formatter {

    private static final Logger LOG = Logger.getLogger(Formatter.class.getName());

    public enum FormatType {
        JSON, YAML, XML, CSV, PLAIN_TEXT, TABLE
    }

    /**
     * Normalizes input based on the provided format.
     *
     * @param input       The raw input string.
     * @param inputFormat The input format.
     * @return The normalized input string.
     * @throws Exception if parsing fails.
     */
    public static String normalize(String input, FormatType inputFormat) throws Exception {
        input = sanitizeInput(input); // Ensure input is clean
        LOG.log(Level.INFO, "Normalizing input for format: {0}", inputFormat);
        switch (inputFormat) {
            case JSON:
                return validateJson(input);
            case YAML:
                return validateYaml(input);
            case XML:
                return validateXml(input);
            case CSV:
                return validateCsv(input);
            case PLAIN_TEXT:
                return input.trim();
            default:
                throw new IllegalArgumentException("Unsupported input format: " + inputFormat);
        }
    }

    /**
     * Transforms input data into the desired output format.
     *
     * @param input        The normalized input string.
     * @param outputFormat The desired output format.
     * @return The transformed output string.
     */
    public static String transform(String input, FormatType outputFormat) {
        LOG.log(Level.INFO, "Transforming data to format: {0}", outputFormat);

        try {
            switch (outputFormat) {
                case JSON:
                    return toJson(input);
                case YAML:
                    return toYaml(input);
                case XML:
                    return toXml(input);
                case CSV:
                    return toCsv(input);
                case PLAIN_TEXT:
                    return toPlainText(input);
                case TABLE:
                    return toTable(input);
                default:
                    throw new IllegalArgumentException("Unsupported output format: " + outputFormat);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error during transformation", e);
            return "Error transforming data: " + e.getMessage();
        }
    }

    // --- Private Helpers for Input Sanitization ---

    private static String sanitizeInput(String input) {
        // Remove BOM if present and trim leading/trailing whitespace
        return input.replaceAll("^\uFEFF", "").trim();
    }

    // --- Private Helpers for Validation ---

    private static String validateJson(String input) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readTree(input); // Validate JSON
        return input;
    }

    private static String validateYaml(String input) throws Exception {
        ObjectMapper mapper = new YAMLMapper();
        mapper.readTree(input); // Validate YAML
        return input;
    }

    private static String validateXml(String input) throws Exception {
        XmlMapper mapper = new XmlMapper();
        mapper.readTree(input); // Validate XML
        return input;
    }

    private static String validateCsv(String input) throws Exception {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        mapper.readerFor(JsonNode.class).with(schema).readValues(input).readAll(); // Validate CSV
        return input;
    }

    // --- Private Helpers for Serialization ---

    private static String toJson(String input) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        JsonNode data = readInputAsJsonNode(input);
        return mapper.writeValueAsString(data);
    }

    private static String toYaml(String input) throws Exception {
        YAMLMapper mapper = new YAMLMapper();
        JsonNode data = readInputAsJsonNode(input);
        return mapper.writeValueAsString(data);
    }
    

    private static String toXml(String input) throws Exception {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        JsonNode data = readInputAsJsonNode(input);
        return mapper.writeValueAsString(data);
    }

    private static String toCsv(String input) throws Exception {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.builder().setUseHeader(true).build();
        JsonNode data = readInputAsJsonNode(input);

        // Handle array and object cases
        if (data.isArray()) {
            return mapper.writer(schema).writeValueAsString(data);
        } else if (data.isObject()) {
            // Wrap single object in array
            ArrayNode arrayNode = new ObjectMapper().createArrayNode();
            arrayNode.add(data);
            return mapper.writer(schema).writeValueAsString(arrayNode);
        } else {
            throw new IllegalArgumentException("Unsupported data format for CSV conversion.");
        }
    }

    private static String toPlainText(String input) {
        // For simplicity, return the input as plain text
        return input;
    }

    private static String toTable(String input) throws Exception {
        StringBuilder tableBuilder = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(input);

        if (rootNode.isObject()) {
            rootNode.fields().forEachRemaining(entry -> {
                String value = entry.getValue().isTextual() ? entry.getValue().asText() : entry.getValue().toString();
                tableBuilder.append(String.format("%-20s: %s%n", entry.getKey(), value));
            });
        } else if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                node.fields().forEachRemaining(entry -> {
                    String value = entry.getValue().isTextual() ? entry.getValue().asText() : entry.getValue().toString();
                    tableBuilder.append(String.format("%-20s: %s%n", entry.getKey(), value));
                });
                tableBuilder.append("\n");
            }
        } else {
            tableBuilder.append(rootNode.toString());
        }

        return tableBuilder.toString();
    }

    private static JsonNode readInputAsJsonNode(String input) throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            return jsonMapper.readTree(input);
        } catch (Exception e) {
            // Try YAML
            ObjectMapper yamlMapper = new YAMLMapper();
            try {
                return yamlMapper.readTree(input);
            } catch (Exception ex) {
                // Try XML
                XmlMapper xmlMapper = new XmlMapper();
                try {
                    return xmlMapper.readTree(input);
                } catch (Exception exc) {
                    // Try CSV
                    CsvMapper csvMapper = new CsvMapper();
                    CsvSchema schema = CsvSchema.emptySchema().withHeader();
                    MappingIterator<JsonNode> it = csvMapper.readerFor(JsonNode.class).with(schema).readValues(input);
                    ArrayNode arrayNode = jsonMapper.createArrayNode();
                    while (it.hasNext()) {
                        arrayNode.add(it.next());
                    }
                    return arrayNode;
                }
            }
        }
    }
}
