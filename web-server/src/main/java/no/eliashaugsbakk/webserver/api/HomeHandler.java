package no.eliashaugsbakk.webserver.api;

import com.sun.net.httpserver.HttpExchange;
import no.eliashaugsbakk.webserver.db.PageRepository;

public class HomeHandler extends BaseHandler {

    public HomeHandler(PageRepository pageRepo) {
        super(pageRepo);
    }

    @Override
    protected String getTitle(HttpExchange exchange) {
        return "Home Page";
    }

    @Override
    protected String getContent(HttpExchange exchange) {
        return "<h1>This is the Home Page</h1>";
    }
}
