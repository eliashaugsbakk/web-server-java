package no.eliashaugsbakk.webserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

public class MyHttpServer {

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/wiki", new WikiHandler());
        server.createContext("/", new HomeHandler());

        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
    }

}
