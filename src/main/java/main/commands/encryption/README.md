Here’s a comprehensive `README.md` for the **encryption module** of the `qbox` application:

---

# QBox Encryption Module

QBox is a native Java toolbox, and the **encryption module** provides a secure and flexible way to encrypt and decrypt strings or files using AES encryption.

## Features

- **String Encryption/Decryption**: Quickly encrypt or decrypt text data.
- **File Encryption/Decryption**: Securely encrypt or decrypt entire files.
- **Key Generation**: Generate a secure AES encryption key in Base64 format.
- **Clean Output**: Suppress additional messages for scripting and pipelines.

---

## Usage

The `encryption` command supports three main actions:

1. **Generate a Key**
2. **Encrypt Data**
3. **Decrypt Data**

### Command Structure

```bash
qbox encryption [OPTIONS] ACTION
```

- **OPTIONS**: Flags like `--key`, `--file`, `--input-file`, and `--output-file`.
- **ACTION**: The operation you want to perform (`--generate-key`, `--encrypt`, or `--decrypt`).

---

### Actions and Examples

#### 1. Generate an AES Key
Create a new AES encryption key (Base64-encoded):

```bash
qbox encryption --generate-key
```

For clean output (only the key):

```bash
qbox encryption --generate-key --clean
```

---

#### 2. Encrypt Data

**Encrypt a String**:

```bash
qbox encryption --encrypt "YourPlainText" --key "YourBase64Key"
```

Clean output:

```bash
qbox encryption --encrypt "YourPlainText" --key "YourBase64Key" --clean
```

**Encrypt a File**:

```bash
qbox encryption --file --input-file ./file.txt --key "YourBase64Key" --encrypt "dummy"
```

Overwrite the original file:

```bash
qbox encryption --file --input-file ./file.txt --key "YourBase64Key" --encrypt "dummy" --output-file ./file.txt
```

---

#### 3. Decrypt Data

**Decrypt a String**:

```bash
qbox encryption --decrypt "YourEncryptedText" --key "YourBase64Key"
```

Clean output:

```bash
qbox encryption --decrypt "YourEncryptedText" --key "YourBase64Key" --clean
```

**Decrypt a File**:

```bash
qbox encryption --file --input-file ./file.txt --key "YourBase64Key" --decrypt "dummy"
```

Overwrite the original file:

```bash
qbox encryption --file --input-file ./file.txt --key "YourBase64Key" --decrypt "dummy" --output-file ./file.txt
```

---

### Available Flags and Options

| Flag                   | Description                                                                                   |
|------------------------|-----------------------------------------------------------------------------------------------|
| `-h, --help`           | Show help message and exit.                                                                   |
| `-V, --version`        | Print version information and exit.                                                           |
| `-f, --file`           | Specify file input and output mode.                                                           |
| `-i, --input-file`     | Path to the input file (required in file mode).                                               |
| `-o, --output-file`    | Path to the output file (optional; if omitted, overwrites the input file).                    |
| `-k, --key`            | The AES encryption/decryption key in Base64 format.                                           |
| `-c, --clean`          | Output only the value without additional text for use in scripting.                           |
| `-g, --generate-key`   | Generate a new AES key in Base64 format.                                                      |
| `-e, --encrypt`        | Encrypt the input string or file (file mode requires `--file` and `--input-file`).            |
| `-d, --decrypt`        | Decrypt the input string or file (file mode requires `--file` and `--input-file`).            |

---

### Common Use Cases

- **Encrypt a File and Overwrite It**:
  ```bash
  qbox encryption --file --input-file ./example.txt --key "YourBase64Key" --encrypt "dummy" --output-file ./example.txt
  ```

- **Decrypt a String**:
  ```bash
  qbox encryption --decrypt "EncryptedTextHere" --key "YourBase64Key"
  ```

- **Generate and Pipe a Key**:
  ```bash
  qbox encryption --generate-key --clean | xclip -selection clipboard
  ```

---

### Notes

- **Key Management**: Always store your AES key securely. Losing the key will render your encrypted data unrecoverable.
- **File Overwrites**: Use the `--output-file` flag with care when specifying the same file as input and output.

---

## About QBox

QBox is a native Java toolbox powered by **Quarkus 3.16.4**, offering fast and lightweight utilities for encryption, metadata management, and more.