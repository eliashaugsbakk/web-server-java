package mee.prosject.webpage.service;

import mee.prosject.webpage.model.Page;
import mee.prosject.webpage.model.PageMetaData;
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

    public Collection<PageMetaData> getAllPages() throws SQLException {
        String sql = """
        SELECT p.id, p.title, p.slug, p.created_at
        FROM wiki_pages p
        JOIN wiki_pages_content c ON p.id = c.page_id
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
                        rs.getTimestamp("created_at").toInstant()
                );
                pages.add(page);
            }
        }

        return pages;
    }

    public boolean addPage(Page page) {
        String insertMeta = """
            INSERT INTO wiki_pages (id, title, slug, created_at)
            VALUES (?, ?, ?, ?)
        """;

        String insertContent = """
            INSERT INTO wiki_pages_content (page_id, markdown, html)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement psMeta = conn.prepareStatement(insertMeta);
                 PreparedStatement psContent = conn.prepareStatement(insertContent)) {

                // Insert metadata
                psMeta.setLong(1, page.id());
                psMeta.setString(2, page.title());
                psMeta.setString(3, page.slug());
                psMeta.setTimestamp(4, Timestamp.from(page.created_at()));
                psMeta.executeUpdate();

                // Insert content
                psContent.setLong(1, page.id());
                psContent.setString(2, page.markdown());
                psContent.setString(3, page.html());
                psContent.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePage(Long id) {
        String deleteMeta = "DELETE FROM wiki_pages WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteMeta)) {

            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }    }

    public Page getPageById(long id) {
        String sql = """
            SELECT p.id, p.title, p.slug, p.created_at, c.markdown, c.html
            FROM wiki_pages p
            JOIN wiki_pages_content c ON p.id = c.page_id
            WHERE p.id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Page(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("slug"),
                            rs.getTimestamp("created_at").toInstant(),
                            rs.getString("markdown"),
                            rs.getString("html")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;    }

    public Page getPageBySlug(String slug) {
        String sql = """
            SELECT p.id, p.title, p.slug, p.created_at, c.markdown, c.html
            FROM wiki_pages p
            JOIN wiki_pages_content c ON p.id = c.page_id
            WHERE p.slug = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Page(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("slug"),
                            rs.getTimestamp("created_at").toInstant(),
                            rs.getString("markdown"),
                            rs.getString("html")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}