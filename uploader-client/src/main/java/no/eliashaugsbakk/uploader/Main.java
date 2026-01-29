package no.eliashaugsbakk.uploader;

import no.eliashaugsbakk.uploader.config.ConfigManager;
import no.eliashaugsbakk.uploader.security.AuthService;
import no.eliashaugsbakk.uploader.service.BundleService;
import no.eliashaugsbakk.uploader.service.NetworkService;
import no.eliashaugsbakk.uploader.util.HashUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

  ConfigManager configManager;
  static List<String> filePaths = new ArrayList<>();

  public Main() throws Exception {
    configManager = new ConfigManager();
  }

  static void main(String[] args) throws Exception {

    Main main = new Main();

    if (args.length == 0) {
      System.out.println("Arguments are required.\n");
      main.printHelpMessage();
    }


    boolean markdownPassed = false;

    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        switch (args[i]) {
          case "--setToken", "-t" -> {
            main.setToken();
          }
          case "--setUrl", "-u" -> {
            main.setUrl(args[i + 1]);
            i++;
          }
          case "--setPort", "-p" -> {
            main.setPort(args[i + 1]);
          }
          case "--help", "-h" -> {
            main.printHelpMessage();
          }
          default -> {
            System.out.println("Unknown argument: " + args[i]);
            main.printHelpMessage();
          }
        }
      } else {
        if (args[i].endsWith(".md")) {
          if (!markdownPassed) {
            markdownPassed = true;
          } else {
            System.out.println("Only one markdown file allowed at a time.");
            System.exit(0);
          }
        }
        filePaths.add(args[i]);
      }
    }

    if (!filePaths.isEmpty()) {
      byte[] zippedFiles = new BundleService(filePaths).bundleToZip();

      if (main.configManager.readUrl().equals("-") || main.configManager.readToken()
          .equals("-") || main.configManager.readPort() == 0) {


        NetworkService networkService =
            new NetworkService(main.configManager.readUrl(), main.configManager.readToken(),
                main.configManager.readPort());

        networkService.uploadBundle(zippedFiles, "PlaceholderName",
            new HashUtils().calculateSHA256(zippedFiles));
      } else {
        System.out.println("Program is not configured correctly.");
        System.out.println("Current url: " + main.configManager.readUrl());
        System.out.println("Current port: " + main.configManager.readPort());
        System.out.println("Current token: " + main.configManager.readToken());

        main.printHelpMessage();
      }
    }
  }







  private void setToken() throws IOException {
    String token = new AuthService().generateAuthKey(32);
    configManager.setToken(token);
    System.out.println("Token has been sat to: " + "'" + token + "'");
  }
  private void setUrl(String url) throws IOException {
    configManager.setUrl(url);
    System.out.println("Url has been sat to: " + "'" + url + "'");
  }
  private void setPort(String port) throws IOException {
    configManager.setPort(Integer.parseInt(port));
    System.out.println("Port has been sat to: " + port);
  }
  private void printHelpMessage() {
    System.out.println("""
        Help:
        uploadClient [-h | --help]
        
        General use:
        Pass in relative or absolute file paths for files to upload.
          (Only supports one .md file at a time. Supported images: png, jpg, jpeg, bmp, webp)
        
        Options:
        uploadClient [option] [choice]
        
        Options:
        [-t | --setToken]             - generates a new Authentication Token.
        [-u | --setUrl]   <url>       - sets the host url.
        [-p | --setPort]  <port>      - sets the port to the local Tor Daemon you have running.
                                        (Defaults: Browser=9150, System=9050)
        """);
  }
}
