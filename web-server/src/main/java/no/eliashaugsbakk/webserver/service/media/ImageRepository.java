package no.eliashaugsbakk.webserver.service.media;

import no.eliashaugsbakk.webserver.model.Image;
import no.eliashaugsbakk.webserver.model.ImageMetaData;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class ImageRepository {

    private final DataSource dataSource;

    public ImageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Return all image metadata (id, filename, contentType, uploadedAt)
     * without loading the actual BLOB data.
     */
    public Collection<ImageMetaData> getAllImagesMetaData() throws SQLException {
        String sql = "SELECT id, filename, contentType, uploadedAt FROM page_image";
        List<ImageMetaData> images = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ImageMetaData image = new ImageMetaData(
                        rs.getLong("id"),
                        rs.getString("filename"),
                        rs.getString("contentType"),
                        rs.getTimestamp("uploadedAt").toInstant()
                );
                images.add(image);
            }
        }
        return images;
    }

    /**
     * Insert a new image into the database.
     * Returns true if the insert was successful.
     */
    public boolean addImage(Image image) {
        String insertSql = """
        INSERT INTO page_image (id, filename, contentType, uploadedAt, data)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, image.getId());
                ps.setString(2, image.getFilename());
                ps.setString(3, image.getContentType());
                ps.setTimestamp(4, Timestamp.from(image.getUploadedAt()));
                ps.setBytes(5, image.getData());

                ps.executeUpdate();
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

    /**
     * Delete an image by its ID.
     * Returns true if a row was actually deleted.
     */
    public boolean deleteImage(long imageId) {
        String deleteSql = "DELETE FROM page_image WHERE id = ?";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, imageId);
                int affectedRows = ps.executeUpdate();

                conn.commit();
                return affectedRows > 0;
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

    /**
     * Retrieve a full Image by its ID, including the BLOB data.
     * Returns null if no image was found.
     */
    public Image getImageById(long imageId) {
        String sql = "SELECT id, filename, contentType, uploadedAt, data FROM page_image WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, imageId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Image(
                            rs.getLong("id"),
                            rs.getString("filename"),
                            rs.getString("contentType"),
                            rs.getTimestamp("uploadedAt").toInstant(),
                            rs.getBytes("data")
                    );
                } else {
                    return null;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
