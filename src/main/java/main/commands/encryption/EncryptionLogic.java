package main.commands.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class EncryptionLogic {

    private static final String AES = "AES";

    /**
     * Generate a new AES key.
     *
     * @return the AES key as a Base64-encoded string.
     */
    public static String generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES);
            keyGen.init(256); // Use 128 or 192 if 256 is not supported
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }

    /**
     * Encrypt a plain text string using the provided AES key.
     *
     * @param plainText the text to encrypt.
     * @param base64Key the Base64-encoded AES key.
     * @return the encrypted text as a Base64-encoded string.
     */
    public static String encrypt(String plainText, String base64Key) {
        try {
            SecretKey secretKey = decodeKey(base64Key);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Decrypt an encrypted string using the provided AES key.
     *
     * @param cipherText the encrypted text (Base64-encoded).
     * @param base64Key  the Base64-encoded AES key.
     * @return the decrypted plain text.
     */
    public static String decrypt(String cipherText, String base64Key) {
        try {
            SecretKey secretKey = decodeKey(base64Key);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Encrypt a file.
     *
     * @param inputFilePath the path to the input file.
     * @param base64Key     the Base64-encoded AES key.
     * @param outputFilePath the path to the output file.
     * @param cleanOutput   if true, suppress additional messages.
     * @throws Exception if an error occurs.
     */
    public static void encryptFile(String inputFilePath, String base64Key, String outputFilePath, boolean cleanOutput) throws Exception {
        Path inputPath = Path.of(inputFilePath);
        Path outputPath = outputFilePath != null ? Path.of(outputFilePath) : null;

        String content = Files.readString(inputPath);
        String encrypted = encrypt(content, base64Key);

        if (outputPath != null) {
            Files.writeString(outputPath, encrypted, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            if (!cleanOutput) {
                System.out.printf("File encrypted successfully: %s%n", outputPath);
            }
        } else {
            System.out.println(encrypted);
        }
    }

    /**
     * Decrypt a file.
     *
     * @param inputFilePath the path to the input file.
     * @param base64Key     the Base64-encoded AES key.
     * @param outputFilePath the path to the output file.
     * @param cleanOutput   if true, suppress additional messages.
     * @throws Exception if an error occurs.
     */
    public static void decryptFile(String inputFilePath, String base64Key, String outputFilePath, boolean cleanOutput) throws Exception {
        Path inputPath = Path.of(inputFilePath);
        Path outputPath = outputFilePath != null ? Path.of(outputFilePath) : null;

        String content = Files.readString(inputPath);
        String decrypted = decrypt(content, base64Key);

        if (outputPath != null) {
            Files.writeString(outputPath, decrypted, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            if (!cleanOutput) {
                System.out.printf("File decrypted successfully: %s%n", outputPath);
            }
        } else {
            System.out.println(decrypted);
        }
    }

    /**
     * Convert a Base64-encoded key string to a SecretKey.
     *
     * @param base64Key the Base64-encoded AES key.
     * @return the SecretKey.
     */
    private static SecretKey decodeKey(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, AES);
    }
}

