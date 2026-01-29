package no.eliashaugsbakk.uploader.config;

import no.eliashaugsbakk.uploader.security.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

  Path path;
  ConfigManager configManager;

  @BeforeEach
  void setUp() throws IOException {
    this.path = Path.of("tmp", "uploadClientTest");
    this.configManager = new ConfigManager(this.path);
  }

  @AfterEach
  void cleanUp() throws IOException {
    Files.deleteIfExists(this.path);
  }


  @Test
  void defaultConfigFileIsGeneratedWhenMissing() {

    assertEquals("-", configManager.readUrl());
    assertEquals(9050, configManager.readPort());
    assertEquals(32, configManager.readToken().length());
  }

  @Test
  void setAndReadConfigWorksAsExpected() throws IOException {

    configManager.setUrl("test-url");
    configManager.setPort(100);
    String previousToken = configManager.readToken();

    configManager.setToken(new AuthService().generateAuthKey(32));

    assertNotEquals(previousToken, configManager.readToken());
    assertEquals("test-url", configManager.readUrl());
    assertEquals(100, configManager.readPort());

  }
}
