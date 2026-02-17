package no.eliashaugsbakk.webserver.db.Jdbc;

import no.eliashaugsbakk.webserver.db.PageRepository;
import no.eliashaugsbakk.webserver.model.Page;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JdbcPageRepository implements PageRepository {
    private final Connection connection;

    public JdbcPageRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Page getPageBySlug(String slug) throws SQLException {
        String sql = "SELECT slug, title, created, content FROM page WHERE slug = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT slug, title FROM page ORDER BY title ASC";
        List<Page> result = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                result.add(mapPageSummary(resultSet));
            }
        }
        return result;
    }

    @Override
    public void addPage(Page page) throws SQLException {
        String sql =
                """
                INSERT INTO page (slug, title, created, content) VALUES (?, ?, ?, ?);
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, page.slug());
            stmt.setString(2, page.title());
            stmt.setLong(3, page.created().toEpochMilli());
            stmt.setString(4, page.content());

            stmt.executeUpdate();
        }
    }

    @Override
    public List<Page> searchByTitle(String query) throws SQLException {
        String sql =
                """
                SELECT slug, title FROM page WHERE title LIKE ? ORDER BY title ASC
                """;
        List<Page> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    results.add(mapPageSummary(resultSet));
                }
            }
        }
        return results;
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
