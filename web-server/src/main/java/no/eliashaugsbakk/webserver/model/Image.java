package no.eliashaugsbakk.webserver.model;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String contentType;

    private Instant uploadedAt;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    // Required by JPA
    protected Image() {}

    // Constructor for convenience
    public Image(long id, String filename, String contentType, Instant uploadedAt, byte[] data) {
        this.filename = filename;
        this.contentType = contentType;
        this.uploadedAt = uploadedAt;
        this.data = data;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
}
