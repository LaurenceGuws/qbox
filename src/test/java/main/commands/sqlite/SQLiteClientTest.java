package main.commands.sqlite;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import org.jboss.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SQLiteClientTest {

    private static final Logger LOGGER = Logger.getLogger(SQLiteClientTest.class);

    @Test
    public void testCRUDOperations() throws Exception {
        Path dbFile = Path.of("test-database.db");

        try {
            LOGGER.info("Starting test: testCRUDOperations");

            // Step 1: Create and populate the database
            LOGGER.info("Creating and populating database at: " + dbFile.toAbsolutePath());
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile)) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("CREATE TABLE test_table (id INTEGER PRIMARY KEY, name TEXT);");
                    stmt.execute("INSERT INTO test_table (name) VALUES ('Alice');");
                }
            }
            assertTrue(Files.exists(dbFile), "Database file should exist.");

            // Step 2: Read data (query)
            LOGGER.info("Executing SELECT query");
            SQLiteClient.QueryCommand selectCommand = new SQLiteClient.QueryCommand();
            CommandLine selectCmd = new CommandLine(selectCommand);

            ByteArrayOutputStream selectOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(selectOutput));
            selectCmd.execute("--db", dbFile.toString(), "--query", "SELECT * FROM test_table;");

            String selectResult = selectOutput.toString().trim();
            LOGGER.info("SELECT command output: " + selectResult);
            assertTrue(selectResult.contains("Alice"), "SELECT should return the inserted record.");

            // Step 3: Update data
            LOGGER.info("Executing UPDATE query");
            SQLiteClient.QueryCommand updateCommand = new SQLiteClient.QueryCommand();
            CommandLine updateCmd = new CommandLine(updateCommand);

            ByteArrayOutputStream updateOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(updateOutput));
            updateCmd.execute("--db", dbFile.toString(), "--query", "UPDATE test_table SET name = 'Bob' WHERE id = 1;");

            String updateResult = updateOutput.toString().trim();
            LOGGER.info("UPDATE command output: " + updateResult);

            // Verify updated data
            LOGGER.info("Executing SELECT query after UPDATE");
            ByteArrayOutputStream selectAfterUpdateOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(selectAfterUpdateOutput));
            selectCmd.execute("--db", dbFile.toString(), "--query", "SELECT * FROM test_table;");

            String selectAfterUpdateResult = selectAfterUpdateOutput.toString().trim();
            LOGGER.info("SELECT after UPDATE command output: " + selectAfterUpdateResult);
            assertTrue(selectAfterUpdateResult.contains("Bob"), "SELECT should return the updated record.");

            // Step 4: Delete data
            LOGGER.info("Executing DELETE query");
            SQLiteClient.QueryCommand deleteCommand = new SQLiteClient.QueryCommand();
            CommandLine deleteCmd = new CommandLine(deleteCommand);

            ByteArrayOutputStream deleteOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(deleteOutput));
            deleteCmd.execute("--db", dbFile.toString(), "--query", "DELETE FROM test_table WHERE id = 1;");

            String deleteResult = deleteOutput.toString().trim();
            LOGGER.info("DELETE command output: " + deleteResult);

            // Verify data deletion
            LOGGER.info("Executing SELECT query after DELETE");
            ByteArrayOutputStream selectAfterDeleteOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(selectAfterDeleteOutput));
            selectCmd.execute("--db", dbFile.toString(), "--query", "SELECT * FROM test_table;");

            String selectAfterDeleteResult = selectAfterDeleteOutput.toString().trim();
            LOGGER.info("SELECT after DELETE command output: " + selectAfterDeleteResult);
            assertTrue(!selectAfterDeleteResult.contains("Bob"), "SELECT should not return deleted record.");
        } finally {
            LOGGER.info("Cleaning up files...");
            Files.deleteIfExists(dbFile);
            LOGGER.info("Test completed: testCRUDOperations");
        }
    }
}
