package no.eliashaugsbakk.uploader.service;

import no.eliashaugsbakk.uploader.exception.UploaderException;
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

  public void uploadBundle(byte[] data, String fileName, String hash) throws IOException {
    RequestBody fileBody = RequestBody.create(
        data,
        MediaType.parse("application/json; charset=utf-8")
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
    IO.println(request);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new UploaderException("Server returned " + response.code() + ": " + response.message());
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
      if (response.code() == 200) {
        System.out.println("Status code: " + response.code() + " - OK");
      } else {
        System.out.println("Status code: " + response.code());
      }
    } catch (java.net.ConnectException e) {
      // This catches "Connection Refused" (Tor is off)
      System.err.println("\n[!] CONNECTION REFUSED");
      System.err.println("    Could not connect to the Tor proxy.");
      System.err.println("    Verify that the local Tor Browser or Daemon is running.");
      System.err.println("    Is the Tor Browser or Daemon running?");
    } catch (java.net.SocketException e) {
      System.err.println("\n[!] NETWORK ERROR: " + e.getMessage());
      System.err.println("    Verify that the port is pointing to the local Tor daemon.");
      System.err.println("    Verify that the local Tor Browser or Daemon is running.");
      System.err.println("    Is the server running correctly?");
    } catch (IOException e) {
      System.err.println("\n[X] I/O ERROR: " + e.getMessage());
    }
  }
}
