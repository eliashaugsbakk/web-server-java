package no.eliashaugsbakk.webserver.model;

import java.time.Instant;

public record Page(
        String title,
        String slug,
        Instant createdAt,
        String markdown,
        String html
) {}
