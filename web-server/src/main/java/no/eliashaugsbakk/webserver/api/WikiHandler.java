package no.eliashaugsbakk.webserver.api;

import com.sun.net.httpserver.HttpExchange;
import no.eliashaugsbakk.webserver.db.PageRepository;
import no.eliashaugsbakk.webserver.model.Page;

public class WikiHandler extends BaseHandler {

    public WikiHandler(PageRepository pageRepo) {
        super(pageRepo);
    }

    @Override
    protected String getTitle(HttpExchange exchange) throws Exception {
        String slug = getSlugFromPath(exchange);
        assert pageRepo != null;
        Page page = pageRepo.getPageBySlug(slug);
        if (page == null) throw new Exception();
        return page.title();
    }

    private String getSlugFromPath(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        return (parts.length > 2) ? parts[2] : "";
    }

    @Override
    protected String getContent(HttpExchange exchange) throws Exception {
        String slug = getSlugFromPath(exchange);
        assert pageRepo != null;
        Page page = pageRepo.getPageBySlug(slug);
        return page.html();
    }
}
