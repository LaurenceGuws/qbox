package main.commands.encryption;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EncryptionCommandTest {

    @Test
    public void testGenerateKeyCommand() {
        EncryptionCommand command = new EncryptionCommand();
        CommandLine cmd = new CommandLine(command);

        // Capture the output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        // Execute the generate-key command
        cmd.execute("--generate-key");

        // Validate the output contains the generated key
        String result = output.toString();
        assertTrue(result.contains("Generated AES Key: "), "Output should contain the generated key.");
    }

    @Test
    public void testEncryptCommand() {
        String key = EncryptionLogic.generateKey();
        String plainText = "Hello World!";
        EncryptionCommand command = new EncryptionCommand();
        CommandLine cmd = new CommandLine(command);

        // Capture the output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        // Execute the encryption command
        cmd.execute("--encrypt", plainText, "--key", key);

        // Validate the output contains an encrypted string
        String result = output.toString().trim();
        assertTrue(result.startsWith("Encrypted: "), "Output should contain the encrypted text.");
    }

    @Test
    public void testDecryptCommand() {
        String key = EncryptionLogic.generateKey();
        String plainText = "Hello World!";
        String encryptedText = EncryptionLogic.encrypt(plainText, key);
        EncryptionCommand command = new EncryptionCommand();
        CommandLine cmd = new CommandLine(command);

        // Capture the output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        // Execute the decryption command
        cmd.execute("--decrypt", encryptedText, "--key", key);

        // Validate the output contains the decrypted text
        String result = output.toString().trim();
        assertTrue(result.contains("Decrypted: Hello World!"), "Output should contain the decrypted text.");
    }
}
