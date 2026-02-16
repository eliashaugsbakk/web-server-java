package no.eliashaugsbakk.webserver.model;

import java.time.Instant;

public record PageMetaData(
        String title,
        String slug,
        Instant createdAt
) {}
