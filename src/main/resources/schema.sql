CREATE TABLE wiki_pages (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255),
    slug VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP
);
CREATE TABLE wiki_pages_content (
    page_id BIGINT PRIMARY KEY,
    markdown TEXT NOT NULL,
    html TEXT NOT NULL,
    FOREIGN KEY (page_id)
        REFERENCES wiki_pages(id)
        ON DELETE CASCADE
);