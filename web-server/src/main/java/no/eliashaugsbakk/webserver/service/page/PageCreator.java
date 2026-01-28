package no.eliashaugsbakk.webserver.service.page;

import no.eliashaugsbakk.webserver.model.Page;
import no.eliashaugsbakk.webserver.model.PageMetaData;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PageCreator {
    private final PageRegistry pageRegistry;
    private final PageContentService pageContentService;


    public PageCreator(PageRegistry pageRegistry, PageContentService pageContentService) {
        this.pageRegistry = pageRegistry;

        this.pageContentService = pageContentService;
    }

    public Page createPage(String title, String markdown) {
        long id = generateId();
        String slug = generateSlug(title);
        Instant createdAt = Instant.now();
        String html = pageContentService.renderHtml(markdown);
        return new Page(id, title, slug, createdAt, markdown, html);
    }

    public PageMetaData getMetaDataOfPage(Page page) {
        long id = page.id();
        String title = page.title();
        String slug = page.slug();
        Instant createdAt = page.createdAt();
        return new PageMetaData(id, title, slug, createdAt);
    }



    private long generateId() {
        return pageRegistry.getNumberOfPages() + 1;
    }


    private String generateSlug(String title) {
        String base = title.toLowerCase()
                .replaceAll("æ", "ae")
                .replaceAll("ø", "oe")
                .replaceAll("å", "aa")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String slug = base;
        int counter = 2;
        while (pageRegistry.getSlugMap().containsKey(slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }
}
