package no.eliashaugsbakk.uploader.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Handles the conversion from Markdown to HTML.
 */
public class MdToHtml {

    public String getHtml(String markdown) {
        MutableDataSet options = new MutableDataSet();

        options.setFrom(ParserEmulationProfile.GITHUB);

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        return html;
    }
}
