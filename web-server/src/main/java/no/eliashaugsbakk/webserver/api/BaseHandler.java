package no.eliashaugsbakk.webserver.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import no.eliashaugsbakk.webserver.db.PageRepository;
import no.eliashaugsbakk.webserver.model.Page;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public abstract class BaseHandler implements HttpHandler {
    protected final PageRepository pageRepo;

    public BaseHandler(PageRepository pageRepo) {
        this.pageRepo = pageRepo;
    }

    public BaseHandler() {
        this.pageRepo = null;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String sidebarHtml = buildSidebar();
            String title = getTitle(exchange);
            String content = getContent(exchange);

            String template = Files.readString(Path.of("/home/elias/Documents/projects/Website/web-server/src/main/resources/templates/wikiPage.html"));
            String finalHtml = template
                    .replace("${title}", title)
                    .replace("${content}", content)
                    .replace("${sidebar_links}", sidebarHtml);


            sendResponse(exchange, 200, finalHtml);

        } catch (Exception e) {
            System.err.println("Error handling request: " + e.getMessage());
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.close();
        }
    }

    // Abstract methods that children MUST implement
    protected abstract String getTitle(HttpExchange exchange) throws Exception;
    protected abstract String getContent(HttpExchange exchange) throws Exception;

    // Helper: Shared sidebar logic
    private String buildSidebar() throws SQLException {
        // If there is no repo, we can't show pages!
        if (pageRepo == null) {
            return "<li>No pages yes</li>";
        }

        StringBuilder sb = new StringBuilder();

        for (Page p : pageRepo.getAllPages()) {
            sb.append("<li><a href=\"/wiki/").append(p.slug()).append("\">")
                    .append(p.title()).append("</a></li>");
        }
        return sb.toString();
    }

    // Helper: Shared response logic
    protected void sendResponse(HttpExchange exchange, int statusCode, String content) throws IOException {

        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}