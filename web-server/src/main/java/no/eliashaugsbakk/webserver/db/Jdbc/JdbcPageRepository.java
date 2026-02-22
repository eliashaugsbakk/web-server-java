package no.eliashaugsbakk.webserver.db.Jdbc;

import no.eliashaugsbakk.webserver.db.PageRepository;
import no.eliashaugsbakk.webserver.model.Page;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class JdbcPageRepository implements PageRepository {
    private final DatabaseManager dbManager;

    public JdbcPageRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Page getPageBySlug(String slug) throws SQLException {
        String sql = "SELECT slug, title, created, content FROM pages WHERE slug = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            try (ResultSet resultSet = stmt.executeQuery()) {

                if (resultSet.next()) {
                    return mapFullPage(resultSet);
                }
            }
            return null; // Page does not exist
        }
    }

        @Override
    public List<Page> getAllPages() throws SQLException {
        // We only select what we need for the links to keep it fast
        String sql = "SELECT slug, title FROM pages ORDER BY title ASC";
        List<Page> result = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
                Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                result.add(mapPageSummary(resultSet));
            }
        }
        return result;
    }

    @Override
    public boolean addPage(Page page) throws SQLException {
        String sql =
                """
                INSERT INTO pages (slug, title, created, content) VALUES (?, ?, ?, ?);
                """;
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, page.slug());
            stmt.setString(2, page.title());
            stmt.setLong(3, page.created().toEpochMilli());
            stmt.setString(4, page.html());

            return stmt.executeUpdate() == 1;
        }
    }

    @Override
    public List<Page> searchInTitle(String query) throws SQLException {

        String sanitized = query
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");

        String sql =
                """
                SELECT slug, title FROM pages 
                WHERE title LIKE ? ESCAPE '!' 
                ORDER BY title ASC
                """;
        List<Page> result = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + sanitized + "%");

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    result.add(mapPageSummary(resultSet));
                }
            }
        }
        return result;
    }

    @Override
    public Set<String> getAllSlugs() throws SQLException {
        String sql =
                """
                SELECT slug FROM pages
                """;
        Set <String> result = new HashSet<>();

        try (Connection conn = dbManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                result.add(resultSet.getString("slug"));
            }
        }
        return result;
    }

    private Page mapPageSummary(ResultSet resultSet) throws SQLException {
        return new Page(
                resultSet.getString("slug"),
                resultSet.getString("title"),
                null,
                null
        );
    }
    private Page mapFullPage(ResultSet resultSet) throws SQLException {
        return new Page(
                resultSet.getString("slug"),
                resultSet.getString("title"),
                Instant.ofEpochMilli(resultSet.getLong("created")),
                resultSet.getString("content")
        );
    }
}
