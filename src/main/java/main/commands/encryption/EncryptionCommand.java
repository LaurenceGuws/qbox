package main.commands.encryption;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ArgGroup;

@Command(
    name = "encryption",
    mixinStandardHelpOptions = true,
    description = "Encrypt or decrypt a given string or file.",
    usageHelpAutoWidth = true
)
public class EncryptionCommand implements Runnable {

    static class EncryptionAction {
        @Option(names = {"-g", "--generate-key"}, description = "Generate a new AES key.")
        boolean generateKey;

        @Option(names = {"-e", "--encrypt"}, description = "Encrypt the input string.")
        String encryptInput;

        @Option(names = {"-d", "--decrypt"}, description = "Decrypt the input string.")
        String decryptInput;
    }

    @ArgGroup(exclusive = true, multiplicity = "1", heading = "Actions:\n")
    EncryptionAction action;

    @Option(
        names = {"-k", "--key"},
        description = "The AES key for encryption or decryption (Base64-encoded)."
    )
    String key;

    @Option(
        names = {"-f", "--file"},
        description = "Specify file input and output. Default is string input/output."
    )
    boolean fileMode;

    @Option(
        names = {"-i", "--input-file"},
        description = "Path to the input file (required in file mode)."
    )
    String inputFile;

    @Option(
        names = {"-o", "--output-file"},
        description = "Path to the output file (optional, used in file mode)."
    )
    String outputFile;

    @Option(
        names = {"-c", "--clean"},
        description = "Output only the value without additional text."
    )
    boolean cleanOutput;

    @Override
    public void run() {
        try {
            if (action.generateKey) {
                handleGenerateKey();
            } else if (fileMode) {
                handleFileMode();
            } else {
                handleStringMode();
            }
        } catch (Exception e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    private void handleGenerateKey() {
        String newKey = EncryptionLogic.generateKey();
        if (cleanOutput) {
            System.out.println(newKey);
        } else {
            System.out.printf("Generated AES Key: %s%n", newKey);
        }
    }

    private void handleFileMode() throws Exception {
        if (inputFile == null) {
            throw new IllegalArgumentException("File mode requires an input file. Use --input-file to specify the file.");
        }

        if (action.encryptInput != null) {
            if (key == null) {
                throw new IllegalArgumentException("Encryption requires a key. Use --key to provide one.");
            }
            EncryptionLogic.encryptFile(inputFile, key, outputFile, cleanOutput);
        } else if (action.decryptInput != null) {
            if (key == null) {
                throw new IllegalArgumentException("Decryption requires a key. Use --key to provide one.");
            }
            EncryptionLogic.decryptFile(inputFile, key, outputFile, cleanOutput);
        } else {
            throw new IllegalArgumentException("Please specify an action: --encrypt or --decrypt.");
        }
    }

    private void handleStringMode() throws Exception {
        if (action.encryptInput != null) {
            if (key == null) {
                throw new IllegalArgumentException("Encryption requires a key. Use --key to provide one.");
            }
            String encrypted = EncryptionLogic.encrypt(action.encryptInput, key);
            if (cleanOutput) {
                System.out.println(encrypted);
            } else {
                System.out.printf("Encrypted: %s%n", encrypted);
            }
        } else if (action.decryptInput != null) {
            if (key == null) {
                throw new IllegalArgumentException("Decryption requires a key. Use --key to provide one.");
            }
            String decrypted = EncryptionLogic.decrypt(action.decryptInput, key);
            if (cleanOutput) {
                System.out.println(decrypted);
            } else {
                System.out.printf("Decrypted: %s%n", decrypted);
            }
        } else {
            throw new IllegalArgumentException("Please specify an action: --encrypt or --decrypt.");
        }
    }
}
