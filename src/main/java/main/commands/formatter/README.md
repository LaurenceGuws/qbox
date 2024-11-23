# Formatter CLI Tool

## Overview

The Formatter CLI Tool is a Quarkus-based command-line application designed to transform raw text data between various formats. It provides a powerful yet simple interface to process structured data such as JSON, YAML, XML, CSV, PLAIN_TEXT, and TABLE.

## Features

- **Data Transformation**: Convert structured data between formats like JSON ↔ YAML, JSON ↔ XML, and more.
- **File and Raw Data Support**: Operates on both files and inline data strings.
- **Command-Line Interface**: Easy-to-use CLI with well-documented options.
- **Customizable Output**: Save output to files or display directly on the console.

---

## Supported Formats

- **Input Formats**: JSON, YAML, XML, CSV, PLAIN_TEXT
- **Output Formats**: JSON, YAML, XML, CSV, PLAIN_TEXT, TABLE

---

## Build Instructions

To build the application as a native binary using Quarkus:

### 1. Prerequisites
- **Java 17** or higher
- **Maven** installed
- **GraalVM** installed and configured for native builds

### 2. Build Command
Run the following command in your project directory:
```bash
quarkus build -Dnative --clean
```

- **`--clean`**: Ensures a fresh build by removing any existing artifacts.
- **`-Dnative`**: Specifies that a native binary should be built.

### 3. Run the Native Application
After the build completes, the native executable will be located in the `target` directory. You can run it directly:
```bash
./target/qbox-1.0.0-SNAPSHOT-runner
```

---

## Usage

### Command Syntax
```bash
qbox formatter format \
  --input=<input> \
  --output=<output> \
  --input-format=<inputFormat> \
  --output-format=<outputFormat> \
  [--file]
```

### Options
- **`-i, --input`**  
  Input file path or raw data string (required).

- **`-o, --output`**  
  Output file path or `stdout` to display the output on the console (required).

- **`--input-format`**  
  Specify the format of the input data. Supported values: `JSON`, `YAML`, `XML`, `CSV`, `PLAIN_TEXT` (required).

- **`--output-format`**  
  Specify the format of the output data. Supported values: `JSON`, `YAML`, `XML`, `CSV`, `PLAIN_TEXT`, `TABLE` (required).

- **`--file`**  
  Treat the input and output as files rather than raw strings (optional).

---

## Examples

### Example 1: Convert JSON to YAML
```bash
qbox formatter format \
  --input='{"name":"Alice","age":30}' \
  --output='output.yaml' \
  --input-format=JSON \
  --output-format=YAML
```

### Example 2: Convert YAML to JSON
```bash
qbox formatter format \
  --input="input.yaml" \
  --output="output.json" \
  --input-format=YAML \
  --output-format=JSON \
  --file
```

### Example 3: Convert JSON to TABLE and Display on Console
```bash
qbox formatter format \
  --input='{"name":"Alice","age":30}' \
  --output='stdout' \
  --input-format=JSON \
  --output-format=TABLE
```