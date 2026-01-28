package no.eliashaugsbakk.webserver.service.page;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import no.eliashaugsbakk.webserver.model.PageMetaData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PageContentService {
    private final Parser parser;
    private final HtmlRenderer renderer;
    private final PageRegistry pageRegistry;

    public PageContentService(PageRegistry pageRegistry) {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
        this.pageRegistry = pageRegistry;
    }
    // From mark-down to HTML
    public String renderHtml(String markdown) {
        return renderer.render(parser.parse(markdown));
    }

    public List<PageMetaData> searchFor(String searchTerm) {
        String normalizedSearchTerm = normalize(searchTerm);
        List<PageMetaData> searchResults = new ArrayList<>();

        for (PageMetaData pageMetaData : pageRegistry.getAllPages()) {
            String normalizedTitle = normalize(pageMetaData.title());
            if (normalizedTitle.contains(normalizedSearchTerm)) {
                searchResults.add(pageMetaData);
            }
        }
        return searchResults;
    }

    private static String normalize(String s) {
        return s.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
    }
}
