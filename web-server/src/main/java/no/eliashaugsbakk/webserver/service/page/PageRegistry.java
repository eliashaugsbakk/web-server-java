package no.eliashaugsbakk.webserver.service.page;

import jakarta.annotation.PostConstruct;
import no.eliashaugsbakk.webserver.model.PageMetaData;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// holds metadata for pages in memory
@Component
public class PageRegistry {

    private final PageRepository pageRepository;
    private final Map<Long, PageMetaData> idIndex = new ConcurrentHashMap<>();
    private final Map<String, PageMetaData> slugIndex = new ConcurrentHashMap<>();

    public PageRegistry(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @PostConstruct
    public void init() throws SQLException {
        refreshFromDb();
    }

    public void refreshFromDb() throws SQLException {
        Collection<PageMetaData> allPages = pageRepository.getAllPagesMetaData();

        Map<Long, PageMetaData> newIdIndex = new HashMap<>();
        Map<String, PageMetaData> newSlugIndex = new HashMap<>();

        for (PageMetaData pageMeta : allPages) {
            newIdIndex.put(pageMeta.id(), pageMeta);
            newSlugIndex.put(pageMeta.slug(), pageMeta);
        }

        idIndex.clear();
        idIndex.putAll(newIdIndex);

        slugIndex.clear();
        slugIndex.putAll(newSlugIndex);
    }

    public PageMetaData getById(long id) {
        return idIndex.get(id);
    }

    PageMetaData getBySlug(String slug) {
        return slugIndex.get(slug);
    }

    public Collection<PageMetaData> getAllPages() {
        return new ArrayList<>(idIndex.values());
    }

    Map<String, PageMetaData> getSlugMap() {
        return new HashMap<>(slugIndex);
    }

    public long getNumberOfPages() {
        return idIndex.keySet().stream().max(Long::compare).orElse(0L);
    }

    public void addPage(PageMetaData meta)  {
        idIndex.put(meta.id(), meta);
        slugIndex.put(meta.slug(), meta);
    }

    public void removePage(PageMetaData meta) {
        idIndex.remove(meta.id());
        slugIndex.remove(meta.slug());
    }
}
