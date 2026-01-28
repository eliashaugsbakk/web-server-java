package no.eliashaugsbakk.uploader.service;

import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp og Tor-logic
 */
public class NetworkService {
  private final OkHttpClient client;
  private final String baseUrl;
  private final String authToken;

  public NetworkService(String baseUrl, String authToken, int torPort) {
    this.baseUrl = baseUrl;
    this.authToken = authToken;

    Proxy torProxy = new Proxy(Proxy.Type.SOCKS,
        InetSocketAddress.createUnresolved("127.0.0.1", torPort));

    this.client = new OkHttpClient.Builder()
        .proxy(torProxy)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build();
  }

  public void uploadBundle(byte[] zipData, String fileName, String hash) throws IOException {
    RequestBody fileBody = RequestBody.create(
        zipData,
        MediaType.parse("application/zip")
    );

    RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", fileName, fileBody)
        .addFormDataPart("sha256", hash)
        .build();

    Request request = new Request.Builder()
        .url(baseUrl + "/upload")
        .header("Authorization", "Bearer " + authToken)
        .post(requestBody)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Server error: " + response.code() + " - " + response.message());
      }
      assert response.body() != null;
      System.out.println("Upload successful. Server response: " + response.body().string());
    }
  }


  public void testConnectivity() {
    Request request = new Request.Builder()
        .url(baseUrl)
        .get()
        .build();

    System.out.println("Pinging server: " + baseUrl + " over Tor...");

    try (Response response = client.newCall(request).execute()) {
      System.out.println("Server is hit");
      System.out.println("Status code: " + response.code());
    } catch (IOException e) {
      System.err.println("Could not reach server: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
