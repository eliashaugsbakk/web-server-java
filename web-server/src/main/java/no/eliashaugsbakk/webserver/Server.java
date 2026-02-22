package no.eliashaugsbakk.webserver;

import com.sun.net.httpserver.HttpServer;
import no.eliashaugsbakk.webserver.api.HomeHandler;
import no.eliashaugsbakk.webserver.api.SearchHandler;
import no.eliashaugsbakk.webserver.api.UploadHandler;
import no.eliashaugsbakk.webserver.api.WikiHandler;
import no.eliashaugsbakk.webserver.db.PageRepository;
import no.eliashaugsbakk.webserver.db.TokenRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    private final int port = 8000;

    public void start(PageRepository pageRepo, TokenRepository tokenRepo) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        HomeHandler home = new HomeHandler(pageRepo);
        server.createContext("/", home);
        server.createContext("/home", home);

        WikiHandler wikiHandler = new WikiHandler(pageRepo);
        server.createContext("/wiki", wikiHandler);

        SearchHandler searchHandler = new SearchHandler(pageRepo);
        server.createContext("/search", searchHandler);

        UploadHandler uploadHandler = new UploadHandler(tokenRepo);
        server.createContext("/upload", uploadHandler);

        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        System.out.println("Server is live on port " + port);
    }
}
