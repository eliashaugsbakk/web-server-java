package no.eliashaugsbakk.uploader.config;

import no.eliashaugsbakk.uploader.utils.AuthUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing to and from config files in ~/.config/
 */
public class ConfigManager {

  private final Path configPath;
  private List<String> configLines;

  public ConfigManager(Path path) throws IOException {
    this.configPath = path;
    setUp();
  }

  public ConfigManager() throws IOException {
    this.configPath = Paths.get(System.getProperty("user.home"), ".config", "webServerUploader", "config");
    setUp();
  }

  private void setUp() throws IOException {
    if (Files.notExists(configPath)) {
      Files.createDirectories(configPath.getParent());
      Files.write(configPath, List.of());
    }

    this.configLines = new ArrayList<>(Files.readAllLines(configPath));

    boolean modified = false;

    if (hasKey("[auth-token]")) {
      configLines.add("[auth-token]");
      String authKey = new AuthUtils().generateAuthKey(32);
      configLines.add(authKey);
      modified = true;
      IO.println("Authentication Key has been generated: " + authKey);
    }
    if (hasKey("[url]")) {
      configLines.add("[url]");
      configLines.add("-");
      modified = true;
      IO.println("Missing url. Run: --setUrl <onion-address.onion>");
    }
    if (hasKey("[port]")) {
      configLines.add("[port]");
      configLines.add("9150");
      modified = true;
      IO.println("Default port: 9150(Tor Browser Daemon) has been set.");
    }

    if (modified) {
      Files.write(configPath, configLines);
      IO.println("Missing configuration keys were added to: " + configPath);
    }
  }

  private boolean hasKey(String key) {
    return configLines.stream().noneMatch(line -> line.trim().equals(key));
  }

  public void setToken(String authToken) throws IOException {
    boolean success = false;
    for (int i = 0; i < configLines.size(); i++) {
      if (configLines.get(i).equals("[auth-token]")) {
        configLines.set(i + 1, authToken);
        success = true;
      }
    }
    Files.write(configPath, configLines);
    if (!success) {
      throw new IOException("Token is not stored");
    }
  }

  public void setUrl(String url) throws IOException {
    boolean success = false;
    for (int i = 0; i < configLines.size(); i++) {
      if (configLines.get(i).equals("[url]")) {
        configLines.set(i + 1, url);
        success = true;
      }
    }
    Files.write(configPath, configLines);
    if (!success) {
      throw new IOException("Url is not stored");
    }
  }

  public void setPort(int port) throws IOException {
    boolean success = false;
    for (int i = 0; i < configLines.size(); i++) {
      if (configLines.get(i).equals("[port]")) {
        configLines.set(i + 1, String.valueOf(port));
        success = true;
      }
    }
    Files.write(configPath, configLines);
    if (!success) {
      throw new IOException("Port is not stored");
    }
  }

  public String readToken() {
    for (int i = 0; i < configLines.size(); i++) {
      if (configLines.get(i).equals("[auth-token]")) {
        return configLines.get(i + 1);
      }
    }
    return "-";
  }

  public String readUrl() {
    for (int i = 0; i < configLines.size(); i++) {
      if (configLines.get(i).equals("[url]")) {
        return configLines.get(i + 1);
      }
    }
    return "-";
  }

  public int readPort() {
      for (int i = 0; i < configLines.size(); i++) {
        if (configLines.get(i).equals("[port]")) {
          return Integer.parseInt(configLines.get(i + 1));
        }
      }
      return 0;
    }
}
