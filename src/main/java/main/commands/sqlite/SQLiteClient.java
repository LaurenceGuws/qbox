package main.commands.sqlite;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Callable;

@Command(name = "sqlite", mixinStandardHelpOptions = true, description = "SQLite client to manage and query databases.", subcommands = {
        SQLiteClient.CreateDbCommand.class,
        SQLiteClient.QueryCommand.class
})
public class SQLiteClient implements Runnable {

    @Override
    public void run() {
        System.out.println("SQLite client. Use --help to view available commands.");
    }

    @Command(name = "create-db", description = "Creates a SQLite database from a .sql schema file.")
    static class CreateDbCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "The path to the .sql schema file.")
        private String schemaFile;

        @Option(names = { "-o", "--output-db" }, description = "Path to the output .db file.", required = true)
        private String outputDb;

        @Override
        public Integer call() throws Exception {
            Path schemaPath = Path.of(schemaFile);
            if (!Files.exists(schemaPath)) {
                System.err.printf("Error: Schema file %s not found.%n", schemaFile);
                return 1;
            }

            String schema = Files.readString(schemaPath);

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + outputDb)) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(schema);
                }
                System.out.printf("Database created successfully: %s%n", outputDb);
            } catch (Exception e) {
                System.err.printf("Error creating database: %s%n", e.getMessage());
                return 1;
            }

            return 0;
        }
    }

    @Command(name = "query", description = "Executes a SQL query on the specified SQLite database.")
    static class QueryCommand implements Callable<Integer> {
        @Option(names = { "-d", "--db" }, description = "Path to the SQLite database file.", required = true)
        private String dbFile;

        @Option(names = { "-q", "--query" }, description = "SQL query to execute.", required = true)
        private String query;

        @Option(names = { "-c", "--clean" }, description = "Output only the query result.")
        private boolean cleanOutput;

        @Override
        public Integer call() throws Exception {
            Path dbPath = Path.of(dbFile);
            if (!Files.exists(dbPath)) {
                System.err.printf("Error: Database file %s not found.%n", dbFile);
                return 1;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
                    Statement stmt = conn.createStatement()) {

                boolean isResultSet = stmt.execute(query);

                if (isResultSet) {
                    try (ResultSet rs = stmt.getResultSet()) {
                        int columnCount = rs.getMetaData().getColumnCount();

                        if (!cleanOutput) {
                            System.out.printf("Query executed successfully on database: %s%n", dbFile);
                        }

                        while (rs.next()) {
                            for (int i = 1; i <= columnCount; i++) {
                                if (!cleanOutput) {
                                    System.out.printf("%s: ", rs.getMetaData().getColumnName(i));
                                }
                                System.out.printf("%s\t", rs.getString(i));
                            }
                            System.out.println();
                        }
                    }
                } else {
                    int updateCount = stmt.getUpdateCount();
                    if (!cleanOutput) {
                        System.out.printf("Query executed successfully on database: %s%n", dbFile);
                    }
                    System.out.printf("%d rows affected.%n", updateCount);
                }
            } catch (Exception e) {
                System.err.printf("Error executing query: %s%n", e.getMessage());
                return 1;
            }

            return 0;
        }
    }

}
