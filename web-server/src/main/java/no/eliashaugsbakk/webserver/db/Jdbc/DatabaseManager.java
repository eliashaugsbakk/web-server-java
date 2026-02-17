package no.eliashaugsbakk.webserver.db.Jdbc;

import java.sql.*;

public class DatabaseManager {

    private final String url;
    private Connection connection;

    public DatabaseManager(String dbFilePath) {
        this.url = "jdbc:sqlite:" + dbFilePath;
    }

    public void initialize() {
        String sql = """
        CREATE TABLE IF NOT EXISTS page (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            slug TEXT NOT NULL UNIQUE,
            title TEXT NOT NULL,
            created LONG NOT NULL,
            content TEXT
        );
    """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Could not initialize database: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
        }
        return this.connection;
    }

    public void stopConnection() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
