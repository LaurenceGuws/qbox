# QBox Logger Command

The **QBox Logger Command** provides a simple yet highly configurable CLI-based logging utility. It allows you to log messages to different outputs (`stdout`, `stderr`, or files) and supports configurable log levels, formats, and timestamps.

---

## Features
- **Log Levels**: Supports common log levels like `INFO`, `WARN`, `ERROR`, `DEBUG`, and `TRACE`.
- **Configurable Formats**: Logs can be formatted as plain text or JSON.
- **Output Options**: Logs can be sent to the console (`stdout`, `stderr`) or written to a specified file.
- **Timestamps**: Optionally include timestamps in log entries.

---

## Usage

### Basic Command Structure
```bash
qbox logger -m <message> -l <level> [options]
```

### Options
| Option               | Description                                           | Default        |
|----------------------|-------------------------------------------------------|----------------|
| `-m, --message`      | The log message to output. **(Required)**             | N/A            |
| `-l, --level`        | Log level: `INFO`, `WARN`, `ERROR`, `DEBUG`, `TRACE`. **(Required)** | N/A |
| `-o, --output`       | Output destination: `stdout`, `stderr`, or a file path. | `stdout`       |
| `-f, --format`       | Log format: `plain`, `json`.                          | `plain`        |
| `-t, --timestamp`    | Include timestamps in logs (`true` or `false`).       | `true`         |
| `-h, --help`         | Show help message and usage instructions.             |                |
| `-V, --version`      | Print the version of QBox.                            |                |

---

## Examples

### Log to `stdout` with Timestamp (default behavior)
```bash
qbox logger -m "Application started" -l INFO
```
**Output:**
```
[2024-11-23T22:55:15] [INFO] Application started
```

### Log Without Timestamp
```bash
qbox logger -m "Application started" -l INFO -t=false
```
**Output:**
```
[INFO] Application started
```

### Log in JSON Format
```bash
qbox logger -m "Application started" -l INFO -f json
```
**Output:**
```json
{"message": "Application started", "level": "INFO"}
```

### Log to a File
```bash
qbox logger -m "An error occurred" -l ERROR -o app.log
```
**File Content (`app.log`):**
```
[2024-11-23T22:56:00] [ERROR] An error occurred
```

### Log to `stderr`
```bash
qbox logger -m "Debugging application" -l DEBUG -o stderr
```
**Output (to `stderr`):**
```
[2024-11-23T22:56:30] [DEBUG] Debugging application
```

---

## Advanced Usage

### Combining Multiple Options
Log an error message in JSON format without a timestamp and write to a file:
```bash
qbox logger -m "Fatal error occurred" -l ERROR -f json -t=false -o error.log
```
**File Content (`error.log`):**
```json
{"message": "Fatal error occurred", "level": "ERROR"}
```

---

## Notes
- Ensure the specified file path for `-o` is writable.
- Use quotes for messages with spaces, e.g., `-m "Multi-word message"`.

---

The **QBox Logger Command** is a powerful and flexible tool designed to integrate seamlessly into shell scripts, automation workflows, or any environment requiring robust logging capabilities.