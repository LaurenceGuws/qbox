package main.commands.encryption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "encryption", mixinStandardHelpOptions = true, description = "Encrypt or decrypt a given string or file.", usageHelpAutoWidth = true, subcommands = {
        EncryptionCommand.KeygenCommand.class, EncryptionCommand.StringCommand.class,
        EncryptionCommand.FileCommand.class,
        EncryptionCommand.ExamplesCommand.class })
public class EncryptionCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Use one of the subcommands: keygen, string, or file.");
    }

    // Key generation subcommand
    @Command(name = "keygen", description = "Generate a new AES key.")
    static class KeygenCommand implements Runnable {
        @Option(names = { "-c", "--clean" }, description = "Output only the value without additional text.")
        boolean cleanOutput;

        @Override
        public void run() {
            String newKey = EncryptionLogic.generateKey();
            if (cleanOutput) {
                System.out.println(newKey);
            } else {
                System.out.printf("Generated AES Key: %s%n", newKey);
            }
        }
    }

    // String encryption/decryption subcommand
    @Command(name = "string", description = "Encrypt or decrypt a string.")
    static class StringCommand implements Runnable {
        @Option(names = { "-e", "--encrypt" }, description = "The string to encrypt.")
        String encryptInput;

        @Option(names = { "-d", "--decrypt" }, description = "The string to decrypt.")
        String decryptInput;

        @Option(names = { "-k",
                "--key" }, required = true, description = "The AES key for encryption or decryption (Base64-encoded).")
        String key;

        @Option(names = { "-c", "--clean" }, description = "Output only the value without additional text.")
        boolean cleanOutput;

        @Override
        public void run() {
            try {
                if (encryptInput != null) {
                    String encrypted = EncryptionLogic.encrypt(encryptInput, key);
                    System.out.println(cleanOutput ? encrypted : "Encrypted: " + encrypted);
                } else if (decryptInput != null) {
                    String decrypted = EncryptionLogic.decrypt(decryptInput, key);
                    System.out.println(cleanOutput ? decrypted : "Decrypted: " + decrypted);
                } else {
                    throw new IllegalArgumentException("Specify --encrypt or --decrypt.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    @Command(name = "file", description = "Encrypt or decrypt a file or folder.")
    static class FileCommand implements Runnable {
        @Option(names = { "-e", "--encrypt" }, description = "Encrypt the file or folder.")
        boolean encrypt;

        @Option(names = { "-d", "--decrypt" }, description = "Decrypt the file or folder.")
        boolean decrypt;

        @Option(names = { "-i", "--input-folder" }, description = "Path to the input folder or file.", required = true)
        String inputFolder;

        @Option(names = { "-O",
                "--output-folder" }, description = "Path to the output folder (optional if --replace=true).")
        String outputFolder;

        @Option(names = { "-k",
                "--key" }, required = true, description = "The AES key for encryption or decryption (Base64-encoded).")
        String key;

        @Option(names = {
                "--recursive" }, defaultValue = "true", description = "Process files in subdirectories (default: true).")
        boolean recursive;

        @Option(names = {
                "--replace" }, defaultValue = "true", description = "Replace input files (default: true). If false, output-folder must be provided.")
        boolean replace;

        @Option(names = { "--filter" }, description = "Filter files by extension (e.g., '*.txt').")
        String filter;

        @Override
        public void run() {
            try {
                File input = new File(inputFolder);

                if (!input.exists()) {
                    throw new IllegalArgumentException("Input folder or file does not exist: " + inputFolder);
                }

                if (!replace && outputFolder == null) {
                    throw new IllegalArgumentException("Output folder must be specified if --replace is false.");
                }

                if (input.isDirectory()) {
                    String targetFolder = replace ? inputFolder : outputFolder;
                    processFolder(input, targetFolder, recursive, filter);
                } else {
                    File outputFile = replace ? input : new File(outputFolder, input.getName());
                    processFile(input, outputFile);
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        private void processFile(File inputFile, File outputFile) throws Exception {
            if (encrypt) {
                EncryptionLogic.encryptFile(inputFile.getAbsolutePath(), key, outputFile.getAbsolutePath(), false);
                System.out.printf("Encrypted: %s -> %s%n", inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
            } else if (decrypt) {
                EncryptionLogic.decryptFile(inputFile.getAbsolutePath(), key, outputFile.getAbsolutePath(), false);
                System.out.printf("Decrypted: %s -> %s%n", inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
            } else {
                throw new IllegalArgumentException("Specify --encrypt or --decrypt.");
            }
        }

        private void processFolder(File inputFolder, String targetFolderPath, boolean recursive, String filter)
                throws Exception {
            File targetFolder = new File(targetFolderPath);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }

            List<File> files = getFilesFromFolder(inputFolder, recursive, filter);

            for (File file : files) {
                String relativePath = inputFolder.toURI().relativize(file.toURI()).getPath();
                File outputFile = replace ? file : new File(targetFolder, relativePath);

                if (!replace) {
                    outputFile.getParentFile().mkdirs();
                }

                processFile(file, outputFile);
            }
        }

        private List<File> getFilesFromFolder(File folder, boolean recursive, String filter) {
            List<File> files = new ArrayList<>();
            File[] entries = folder.listFiles();
            if (entries == null) {
                return files;
            }

            for (File entry : entries) {
                if (entry.isDirectory() && recursive) {
                    files.addAll(getFilesFromFolder(entry, true, filter));
                } else if (entry.isFile() && (filter == null || entry.getName().matches(filter.replace("*", ".*")))) {
                    files.add(entry);
                }
            }
            return files;
        }
    }

    // Examples subcommand
    @Command(
        name = "examples",
        description = "Show usage examples for the encryption tool."
    )
    static class ExamplesCommand implements Runnable {

        @Override
        public void run() {
            System.out.println("\n=== Encryption Tool Usage Examples ===\n");

            // Key generation examples
            System.out.println("1. Generate a new AES key:");
            System.out.println("   $ qbox encryption keygen");
            System.out.println("   $ qbox encryption keygen --clean");
            System.out.println();

            // String encryption examples
            System.out.println("2. Encrypt and decrypt a string:");
            System.out.println("   $ qbox encryption string --encrypt \"Hello, World!\" --key YOUR_BASE64_KEY");
            System.out.println("   $ qbox encryption string --decrypt \"ENCRYPTED_TEXT\" --key YOUR_BASE64_KEY");
            System.out.println("   $ qbox encryption string --encrypt \"Sensitive Data\" --key YOUR_BASE64_KEY --clean");
            System.out.println();

            // Single file encryption examples
            System.out.println("3. Encrypt and decrypt a single file:");
            System.out.println("   $ qbox encryption file --encrypt --input-folder ./file.txt --key YOUR_BASE64_KEY");
            System.out.println("   $ qbox encryption file --decrypt --input-folder ./file.txt --key YOUR_BASE64_KEY --replace=false --output-folder ./decrypted/");
            System.out.println();

            // Folder encryption examples
            System.out.println("4. Encrypt and decrypt a folder:");
            System.out.println("   $ qbox encryption file --encrypt --input-folder ./test_folder --key YOUR_BASE64_KEY");
            System.out.println("   $ qbox encryption file --decrypt --input-folder ./test_folder --key YOUR_BASE64_KEY --replace=false --output-folder ./decrypted_folder/");
            System.out.println();

            // Recursive and filtering examples
            System.out.println("5. Use recursive mode and filter files by extension:");
            System.out.println("   $ qbox encryption file --encrypt --input-folder ./test_folder --key YOUR_BASE64_KEY --filter \"*.txt\"");
            System.out.println("   $ qbox encryption file --decrypt --input-folder ./test_folder --key YOUR_BASE64_KEY --recursive=false");
            System.out.println();

            // Replace examples
            System.out.println("6. Replace input files in-place:");
            System.out.println("   $ qbox encryption file --encrypt --input-folder ./test_folder --key YOUR_BASE64_KEY --replace");
            System.out.println();

            // Non-replace examples
            System.out.println("7. Output to a different folder without replacing input:");
            System.out.println("   $ qbox encryption file --encrypt --input-folder ./test_folder --key YOUR_BASE64_KEY --replace=false --output-folder ./encrypted_folder/");
            System.out.println();
        }
    }

}
