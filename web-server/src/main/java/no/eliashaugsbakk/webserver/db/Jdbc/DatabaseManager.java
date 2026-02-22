package no.eliashaugsbakk.webserver.db.Jdbc;

import java.sql.*;

public class DatabaseManager {

    private final String url;

    public DatabaseManager(String dbFilePath) {
        this.url = "jdbc:sqlite:" + dbFilePath + "?busy_timeout=5000";
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }


    public void initialize() throws SQLException {
        String sqlPages = """
        CREATE TABLE IF NOT EXISTS pages (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            slug TEXT NOT NULL UNIQUE,
            title TEXT NOT NULL,
            created LONG NOT NULL,
            content TEXT
        );
    """;

        String sqlTokens = """
        CREATE TABLE IF NOT EXISTS tokens (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            tokenValue TEXT NOT NULL UNIQUE,
            createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPages);
            stmt.execute(sqlTokens);
            System.out.println("Database initialized successfully.");
        }
    }

    // New DB connection for every request to handle multiple concurrent requests
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
