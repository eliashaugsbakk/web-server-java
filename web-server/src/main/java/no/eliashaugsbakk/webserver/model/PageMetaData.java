package no.eliashaugsbakk.webserver.model;

import java.time.Instant;

public record PageMetaData(
        long id,
        String title,
        String slug, // a unique url safe version of the page title
        Instant createdAt
) {}
