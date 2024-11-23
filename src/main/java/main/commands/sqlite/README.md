# SQLite Client CLI Tool

## Overview

The **SQLite Client CLI Tool** is a Quarkus-based command-line application designed for managing SQLite databases. It enables users to create databases from schema files and execute SQL queries seamlessly.

---

## Features

- **Database Creation**: Create SQLite databases from `.sql` schema files.
- **SQL Query Execution**: Execute SQL queries on SQLite databases and display results.
- **Customizable Output**: Option to display clean query results.

---

## Build Instructions

### Prerequisites
- **Java 17** or higher
- **Maven** installed

### Build Command
Run the following command to build the application:
```bash
quarkus build -Dnative --clean
```

After building, the native binary will be available in the `target` directory.

### Run the Application
Execute the binary directly:
```bash
./target/qbox-1.0.0-SNAPSHOT-runner
```

---

## Usage

### Command Syntax
```bash
qbox sqlite <command> [options]
```

### Available Commands
1. **`create-db`**: Create a new SQLite database from a schema file.
2. **`query`**: Execute SQL queries on an existing SQLite database.

---

## Commands

### 1. Create a Database
#### Syntax
```bash
qbox sqlite create-db <schemaFile> --output-db=<outputDb>
```
#### Options
- **`<schemaFile>`**: Path to the `.sql` schema file (required).
- **`--output-db, -o`**: Path to the output SQLite `.db` file (required).

#### Example
```bash
qbox sqlite create-db schema.sql --output-db=test.db
```
Creates a database `test.db` using the schema defined in `schema.sql`.

---

### 2. Execute a SQL Query
#### Syntax
```bash
qbox sqlite query --db=<dbFile> --query=<query> [--clean]
```
#### Options
- **`--db, -d`**: Path to the SQLite database file (required).
- **`--query, -q`**: SQL query to execute (required).
- **`--clean, -c`**: Display only the query result (optional).

#### Examples

##### Execute a SELECT Query
```bash
qbox sqlite query --db=test.db --query="SELECT * FROM key_value_store;"
```

##### Execute a Query with Clean Output
```bash
qbox sqlite query --db=test.db --query="SELECT * FROM key_value_store;" --clean
```

##### Execute an UPDATE Query
```bash
qbox sqlite query --db=test.db --query="UPDATE key_value_store SET value='Bob' WHERE key='name';"
```

---

## Examples

### Example Workflow
1. **Create a Database**
```bash
qbox sqlite create-db schema.sql --output-db=test.db
```

2. **Insert Data**
```bash
qbox sqlite query --db=test.db --query="INSERT INTO key_value_store (key, value, tags) VALUES ('name', 'Alice', 'developer');"
```

3. **Query Data**
```bash
qbox sqlite query --db=test.db --query="SELECT * FROM key_value_store;"
```

4. **Update Data**
```bash
qbox sqlite query --db=test.db --query="UPDATE key_value_store SET value='Bob' WHERE key='name';"
```

5. **Delete Data**
```bash
qbox sqlite query --db=test.db --query="DELETE FROM key_value_store WHERE key='name';"
```

---

## Schema Example

Here is an example `.sql` schema file for testing:
```sql
CREATE TABLE key_value_store (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    key TEXT NOT NULL,
    value TEXT NOT NULL,
    tags TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## Notes

- Ensure the SQLite database file and schema file paths are correct.
- Use the `--clean` option for simplified query outputs.
- Use valid SQL syntax for queries. 

This tool is ideal for lightweight database management and quick queries on SQLite databases.