package no.eliashaugsbakk.webserver.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import no.eliashaugsbakk.utils.HashUtils;
import no.eliashaugsbakk.utils.JsonUtils;
import no.eliashaugsbakk.utils.Post;
import no.eliashaugsbakk.webserver.db.TokenRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class UploadHandler implements HttpHandler {

 private final TokenRepository tokenRepo;

    public UploadHandler(TokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Bad token format
            // TODO: send error to client
            return;
        }

        String rawToken = authHeader.substring(7).trim();

        try {
            if (!tokenRepo.isValid(rawToken)) {
                // Invalid token
                // TODO: send error to client
                return;
            }
        } catch (SQLException e) {
            // Database error
            // TODO: send error to client
        }


        InputStream is = exchange.getRequestBody();
        String fullBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        String clientHash = getPartValue(fullBody, "sha256");
        String jsonBundle = getPartValue(fullBody, "bundle");

        try {
            assert jsonBundle != null;
            if (!(new HashUtils().calculateSHA256(jsonBundle.getBytes())).equals(clientHash) ) {
                // Hash mismatch
                // TODO: send error to client
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Post post = new JsonUtils().getPost(jsonBundle);



        // TODO: extract the page and images as TextFile and Image objects
        // TODO: add the page to DB and images to disk


    }


    /**
     * AI generated method to extract data from the http body.
     * To avoid any heavy dependencies.
     *
     * @param body the body of the http message
     * @param partName the part of the body to extract
     * @return the extracted value
     */
    private String getPartValue(String body, String partName) {
        // Look for the header: name="partName"
        String marker = "name=\"" + partName + "\"";
        int nameIndex = body.indexOf(marker);
        if (nameIndex == -1) return null;

        // The actual content starts after the double CRLF (\r\n\r\n)
        int contentStart = body.indexOf("\r\n\r\n", nameIndex) + 4;

        // The content ends at the next boundary start (\r\n--)
        int contentEnd = body.indexOf("\r\n--", contentStart);

        if (contentStart > 3 && contentEnd > contentStart) {
            return body.substring(contentStart, contentEnd).trim();
        }
        return null;
    }
}
