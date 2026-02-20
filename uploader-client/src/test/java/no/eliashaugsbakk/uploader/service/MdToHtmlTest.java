package no.eliashaugsbakk.uploader.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MdToHtmlTest {
    @Test
    void getHtml_returns_the_expected_html() {
        MdToHtml mdToHtml = new MdToHtml();
        String md =
                """
                # Hello
                
                Is this **formatted** properly?
                - one
                - two
                """;

        String expectedHtml =
                """
                <h1>Hello</h1>
                <p>Is this <strong>formatted</strong> properly?</p>
                <ul>
                <li>one</li>
                <li>two</li>
                </ul>
                """;
        String html = mdToHtml.getHtml(md);

        assertTrue(expectedHtml.contains(html));
    }
}
