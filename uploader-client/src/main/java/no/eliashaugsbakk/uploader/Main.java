package no.eliashaugsbakk.uploader;

import no.eliashaugsbakk.uploader.service.NetworkService;

public class Main {
  static void main() {
    int TOR_PORT = 9050;
    String onionUrl = "http://p2r7lchnztd2vs5c6uc4eh4skrxv467ya6ewwrny4rdknmozwkbcouid.onion/upload";
    String token = "";

    NetworkService network = new NetworkService(onionUrl, token, TOR_PORT);

    network.testConnectivity();
  }
}
