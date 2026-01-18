INSERT INTO wiki_pages (id, title, slug, created_at)
VALUES
    (1, 'Home', 'home', CURRENT_TIMESTAMP),
    (2, 'About', 'about', CURRENT_TIMESTAMP);

INSERT INTO wiki_pages_content (page_id, markdown, html)
VALUES
    (1, 'Welcome to the home page.', '<p>Welcome to the home page.</p>'),
    (2, 'This wiki is about the project.', '<p>This wiki is about the project.</p>');
