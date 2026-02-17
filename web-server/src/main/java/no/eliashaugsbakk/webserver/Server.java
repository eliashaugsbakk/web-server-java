package no.eliashaugsbakk.webserver;

import com.sun.net.httpserver.HttpServer;
import no.eliashaugsbakk.webserver.api.HomeHandler;
import no.eliashaugsbakk.webserver.api.WikiHandler;
import no.eliashaugsbakk.webserver.db.Jdbc.DatabaseManager;
import no.eliashaugsbakk.webserver.db.Jdbc.JdbcPageRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    private final int port = 8000;

    public void start(JdbcPageRepository pageRepo) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/wiki", new WikiHandler(pageRepo));
        server.createContext("/", new HomeHandler());

        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        System.out.println("Server is live on port " + port);
    }

}
