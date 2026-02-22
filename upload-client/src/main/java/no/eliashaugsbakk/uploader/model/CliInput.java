package no.eliashaugsbakk.uploader.model;

import java.util.List;

public record CliInput(
    List<String> filePaths,
    String url,
    Integer port,
    boolean generateToken,
    boolean networkTest,
    boolean helpRequested
) {}
