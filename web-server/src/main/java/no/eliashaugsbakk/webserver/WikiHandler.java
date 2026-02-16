package no.eliashaugsbakk.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

class WikiHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            // Get the path, e.g., wiki/site1
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts.length > 2 && !parts[2].trim().isEmpty()) {
                String pageName = parts[2];

                // Logic: Fetch from DB here. If null, send 404.

                String response = "<h1> You requested the wiki: " + pageName + "</h1>";
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }
}


