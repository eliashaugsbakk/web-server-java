package no.eliashaugsbakk.webserver.model;

import java.time.Instant;

public record Page(
        String title,
        String slug,
        Instant created,
        String content
) {}
