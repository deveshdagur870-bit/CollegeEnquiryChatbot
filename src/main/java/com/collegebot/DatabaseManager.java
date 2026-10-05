package com.collegebot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the SQLite database: creates the file on first run, loads schema.sql
 * (table structure + sample FAQ/student data), and hands out connections.
 */
public class DatabaseManager {

    private static final String DB_FILE = "college_chatbot.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    public static void initialize() {
        boolean freshDb = !Files.exists(Paths.get(DB_FILE));
        try {
            Class.forName("org.sqlite.JDBC");
            if (freshDb) {
                runSchema();
                System.out.println("New database created and seeded with sample data: " + DB_FILE);
            } else {
                System.out.println("Using existing database: " + DB_FILE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void runSchema() throws IOException, SQLException {
        InputStream in = DatabaseManager.class.getResourceAsStream("/db/schema.sql");
        if (in == null) {
            throw new FileNotFoundException("schema.sql not found in resources/db/");
        }
        String sql = new String(in.readAllBytes());

        try (Connection con = getConnection();
             Statement st = con.createStatement()) {
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    st.execute(trimmed);
                }
            }
        }
    }

    public static void close() {
        // Each call opens/closes its own connection, so nothing persistent to close here.
        // Kept as a hook for future connection-pool style cleanup.
    }
}
