package mee.prosject.webpage.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.springframework.stereotype.Service;

@Service
public class PageContentService {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public PageContentService() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();

    }
    // From mark-down to HTML
    public String renderHtml(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}
