-- Create a table for key-value pairs with metadata
CREATE TABLE key_value_store (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    key TEXT NOT NULL UNIQUE,
    value TEXT NOT NULL,
    tags TEXT, -- Comma-separated tags
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP -- Automatically set to the current time
);

-- Optional: Create an index for faster lookups by key
CREATE INDEX idx_key ON key_value_store (key);

-- Optional: Create an index for faster lookups by tags
CREATE INDEX idx_tags ON key_value_store (tags);
