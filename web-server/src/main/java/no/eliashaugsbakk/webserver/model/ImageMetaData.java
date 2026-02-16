package no.eliashaugsbakk.webserver.model;

import java.time.Instant;

public record ImageMetaData(
        String filePath,
        Instant uploadedAt
) {}
