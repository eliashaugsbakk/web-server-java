package no.eliashaugsbakk.webserver.service.page;

import no.eliashaugsbakk.webserver.model.Page;
import no.eliashaugsbakk.webserver.model.PageMetaData;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class PageRepository {

    private final DataSource dataSource;

    public PageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Collection<PageMetaData> getAllPagesMetaData() throws SQLException {
        String sql = """
        SELECT p.id, p.title, p.slug, p.createdAt
        FROM pages p
        """;

        List<PageMetaData> pages = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PageMetaData page = new PageMetaData(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("slug"),
                        rs.getTimestamp("createdAt").toInstant()
                );
                pages.add(page);
            }
        }
        return pages;
    }

    public boolean addPage(Page page) {
        String insertData = """
        INSERT INTO pages (id, title, slug, createdAt, markdown, html)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement psData = conn.prepareStatement(insertData)) {
                psData.setLong(1, page.id());
                psData.setString(2, page.title());
                psData.setString(3, page.slug());
                psData.setTimestamp(4, Timestamp.from(page.createdAt()));
                psData.setString(5, page.markdown());
                psData.setString(6, page.html());

                psData.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deletePage(long pageId) {
        String deleteSql = "DELETE FROM pages WHERE id = ?";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, pageId);
                int affectedRows = ps.executeUpdate();

                conn.commit();
                return affectedRows > 0; // true if a row was deleted
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public Page getPageById(long id) {
        String sql = "SELECT id, title, slug, createdAt, markdown, html FROM pages WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Page(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("slug"),
                            rs.getTimestamp("createdAt").toInstant(),
                            rs.getString("markdown"),
                            rs.getString("html")
                    );
                } else {
                    return null; // no page found
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public Page getPageBySlug(String slug) {
        String sql = "SELECT id, title, slug, createdAt, markdown, html FROM pages WHERE slug = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, slug);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Page(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("slug"),
                            rs.getTimestamp("createdAt").toInstant(),
                            rs.getString("markdown"),
                            rs.getString("html")
                    );
                } else {
                    return null; // no page found
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
