package mee.prosject.webpage.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.springframework.stereotype.Service;

@Service
public class PageRenderer {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public PageRenderer() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    public String render(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}
