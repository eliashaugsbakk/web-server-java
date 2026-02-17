package no.eliashaugsbakk.webserver.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import no.eliashaugsbakk.webserver.db.Jdbc.DatabaseManager;
import no.eliashaugsbakk.webserver.db.Jdbc.JdbcPageRepository;
import no.eliashaugsbakk.webserver.model.Page;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WikiHandler implements HttpHandler {
    private final JdbcPageRepository pageRepo;

    public WikiHandler(JdbcPageRepository pageRepo) {
        this.pageRepo = pageRepo;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            // Get the path, e.g., wiki/site1
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts.length > 2 && !parts[2].trim().isEmpty()) {
                String pageSlug = parts[2];
                Page page = pageRepo.getPageBySlug(pageSlug);

                if (page == null) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                List<Page> allPages = pageRepo.getAllPages();
                StringBuilder sidebarHtml = new StringBuilder();

                for (Page p : allPages) {
                    sidebarHtml.append("<li><a href=\"/wiki/")
                            .append(p.title())
                            .append("\">")
                            .append(p.slug())
                            .append("</a></li>");
                }

                String template;
                try {
                    template = Files.readString(Path.of("src/main/resources/templates/wikiPage.html"));
                } catch (IOException e) {
                    System.err.println("Template missing: " + e.getMessage());
                    exchange.sendResponseHeaders(500, -1);
                    return;
                }
                String finalHtml = template
                        .replace("${title}", page.title())
                        .replace("${content}", page.content())
                        .replace("${sidebar_links}", sidebarHtml.toString());

                sendResponse(exchange, finalHtml);


            } else {
                exchange.sendResponseHeaders(404, -1); // Page not found
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        }
    }


    private void sendResponse(HttpExchange exchange, String content) throws IOException {
        byte[] responseBytes = content.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        }
    }
}


