package main.commands.encryption;

import org.junit.jupiter.api.Test;
import org.jboss.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionLogicTest {

    private static final Logger LOGGER = Logger.getLogger(EncryptionLogicTest.class);

    @Test
    public void testEncryptAndDecrypt() {
        LOGGER.info("Starting test: testEncryptAndDecrypt");
        
        String originalText = "Hello QBox!";
        LOGGER.debug("Original text: " + originalText);
        
        String key = EncryptionLogic.generateKey();
        LOGGER.debug("Generated encryption key: " + key);
        
        // Encrypt the original text
        String encryptedText = EncryptionLogic.encrypt(originalText, key);
        LOGGER.debug("Encrypted text: " + encryptedText);
        
        // Decrypt the encrypted text
        String decryptedText = EncryptionLogic.decrypt(encryptedText, key);
        LOGGER.debug("Decrypted text: " + decryptedText);
        
        // Verify that the decrypted text matches the original
        assertEquals(originalText, decryptedText, "Decrypted text should match the original text.");
        LOGGER.info("testEncryptAndDecrypt completed successfully.");
    }

    @Test
    public void testInvalidDecryption() {
        LOGGER.info("Starting test: testInvalidDecryption");
        
        String invalidCipherText = "InvalidCipherText";
        LOGGER.debug("Invalid ciphertext: " + invalidCipherText);
        
        String key = EncryptionLogic.generateKey();
        LOGGER.debug("Generated encryption key: " + key);
        
        // Attempt to decrypt invalid ciphertext
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            EncryptionLogic.decrypt(invalidCipherText, key);
        }, "Decryption of invalid ciphertext should throw an exception.");
        
        LOGGER.debug("Caught exception as expected: " + exception.getMessage());
        LOGGER.info("testInvalidDecryption completed successfully.");
    }

    @Test
    public void testKeyGeneration() {
        LOGGER.info("Starting test: testKeyGeneration");
        
        // Generate two keys
        String key1 = EncryptionLogic.generateKey();
        String key2 = EncryptionLogic.generateKey();
        
        LOGGER.debug("Generated key 1: " + key1);
        LOGGER.debug("Generated key 2: " + key2);
        
        // Ensure the keys are not null
        assertNotNull(key1, "Generated key should not be null.");
        assertNotNull(key2, "Generated key should not be null.");
        
        // Ensure the keys have the correct length
        assertEquals(44, key1.length(), "Generated key should have 44 characters (Base64 of 256-bit key).");
        assertEquals(44, key2.length(), "Generated key should have 44 characters (Base64 of 256-bit key).");
        
        // Ensure the keys are unique
        assertNotEquals(key1, key2, "Generated keys should not match.");
        
        LOGGER.info("testKeyGeneration completed successfully.");
    }
}
