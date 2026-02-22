package no.eliashaugsbakk.uploader.controller;

import no.eliashaugsbakk.uploader.config.ConfigManager;
import no.eliashaugsbakk.uploader.exception.UploaderException;
import no.eliashaugsbakk.uploader.model.CliInput;
import no.eliashaugsbakk.uploader.model.NetworkConfig;
import no.eliashaugsbakk.uploader.service.DataNormalizerService;
import no.eliashaugsbakk.uploader.service.NetworkService;
import no.eliashaugsbakk.uploader.utils.AuthUtils;
import no.eliashaugsbakk.utils.HashUtils;
import no.eliashaugsbakk.utils.JsonUtils;
import no.eliashaugsbakk.utils.Post;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CliController {

  ConfigManager configManager;
  public CliController() throws IOException {
    configManager = new ConfigManager();
  }

  public void execute(String[] args) throws Exception {
    CliInput input = new ArgParser().parse(args);

    if (input.helpRequested()) {
      printHelp();
      return;
    }

    updatePersistentConfig(input);

    if (input.networkTest() || !input.filePaths().isEmpty()) {
      NetworkConfig cfg = getValidatedConfig();
      NetworkService service = new NetworkService(cfg.url(), cfg.token(), cfg.port());

      if (input.networkTest()) {
        service.testConnectivity();
      }

      if (!input.filePaths().isEmpty()) {
        performUpload(service, input.filePaths());
      }

    }
  }

  private NetworkConfig getValidatedConfig() {
    String url = configManager.readUrl();
    String token = configManager.readToken();
    int port = configManager.readPort();

    if (url == null || url.equals("-")) {
      throw new UploaderException("Incomplete url configuration. Use --setUrl <url> to set the url.");
    }
    if (token == null || token.equals("-")) {
      throw new UploaderException("Incomplete token configuration. Use --setToken to generate a new token.");
    }
    if (port == 0) {
      throw new UploaderException("Incomplete configuration. Use --setPort to specify the port of your local tor daemon");
    }

    return new NetworkConfig(url, token, port);
  }

  private void updatePersistentConfig(CliInput input) throws IOException {
    if (input.url() != null) {
      configManager.setUrl(input.url());
      System.out.println("Url has been set: " + input.url());
    }
    if (input.port() != null) {
      configManager.setPort(input.port());
      if (input.port() == 9150) {
        System.out.println("Port has been set: " + input.port() + " (Tor Browser Daemon)");
      } else {
        System.out.println("Port has been set: " + input.port());
      }
    }
    if (input.generateToken()) {
      String token = new AuthUtils().generateAuthKey(32);
      configManager.setToken(token);
      System.out.println("Token has been set: " + token);
    }
  }

  private void performUpload(NetworkService networkService, List<String> filePaths) throws Exception {

    DataNormalizerService dataNormalizer = new DataNormalizerService(filePaths);

    Post post = new Post(
            dataNormalizer.getTextFile().title(),
            dataNormalizer.getTextFile().body(),
            dataNormalizer.getImagesFiles()
    );

    String json = new JsonUtils().getJson(post);

    System.out.println("Uploading file(s)...");
    networkService.uploadBundle(
            json.getBytes(StandardCharsets.UTF_8),
            "Upload_JSON_" + System.currentTimeMillis(),
            new HashUtils().calculateSHA256(json.getBytes()));

    System.out.println("File(s) have been uploaded.");
  }

  private void printHelp() {
    System.out.println("""
        Help:
        uploadClient [-h | --help]
        
        General use:
        Pass in relative or absolute file paths for files to upload.
          (Only supports one .md file at a time. Supported images: png, jpg, jpeg, bmp, webp)
        
        Options:
        uploadClient [option] [choice]
        
        Options:
        [-t | --setToken]               - generates a new Authentication Token.
        [-u | --setUrl]   <url>         - sets the host url.
        [-p | --setPort]  <port>/defult - sets the port to the local Tor Daemon you have running.
        [-p | --setPort]                - sets the port to 9150 default
        [-n | --networkTest]            - tests the network connection.
                                          (Port default argument: Tor Browser Daemon = 9150)
                                          (The default port of the Tor Daemon is 9050)
        """);
  }
}
