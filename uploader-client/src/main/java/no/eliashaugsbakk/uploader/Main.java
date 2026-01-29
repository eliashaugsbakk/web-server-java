package no.eliashaugsbakk.uploader;

import no.eliashaugsbakk.uploader.config.ConfigManager;
import no.eliashaugsbakk.uploader.security.AuthService;
import no.eliashaugsbakk.uploader.service.NetworkService;
import no.eliashaugsbakk.uploader.util.HashUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
  static void main(String[] args) throws Exception {
    new ConfigManager();
  }
}
