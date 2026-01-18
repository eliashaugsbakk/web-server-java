package mee.prosject.webpage.model;

import java.time.Instant;

public record Page(
        long id,
        String title,
        String slug, // a unique url safe version of the page title
        Instant created_at,
        String markdown,
        String html
) {}
