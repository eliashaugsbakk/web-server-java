package no.eliashaugsbakk.webserver.model;

import java.time.Instant;

public record Page(
        String slug,
        String title,
        Instant created,
        String html
) {}
