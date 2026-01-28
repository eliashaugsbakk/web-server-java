package no.eliashaugsbakk.webserver;

import no.eliashaugsbakk.webserver.model.Page;
import no.eliashaugsbakk.webserver.model.PageMetaData;
import no.eliashaugsbakk.webserver.service.page.PageContentService;
import no.eliashaugsbakk.webserver.service.page.PageCreator;
import no.eliashaugsbakk.webserver.service.page.PageRegistry;
import no.eliashaugsbakk.webserver.service.page.PageRepository;
import org.h2.jdbcx.JdbcDataSource;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {


    private DataSource createTestDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    private void initSchema(@NonNull DataSource ds) throws SQLException {
        try (Connection c = ds.getConnection();
             Statement s = c.createStatement()) {

            s.execute("""
            CREATE TABLE wiki_pages (
                id BIGINT PRIMARY KEY,
                title VARCHAR(255),
                slug VARCHAR(255) NOT NULL UNIQUE,
                createdAt TIMESTAMP
            )
        """);

            s.execute("""
            CREATE TABLE wiki_pages_content (
                page_id BIGINT PRIMARY KEY,
                markdown TEXT NOT NULL,
                html TEXT NOT NULL,
                FOREIGN KEY (page_id)
                    REFERENCES wiki_pages(id)
                    ON DELETE CASCADE
            )
        """);
        }
    }


    @Test
    void addPageToDb() throws SQLException {
        DataSource ds = createTestDataSource();
        initSchema(ds);

        PageRepository pageRepository = new PageRepository(ds);
        PageRegistry pageRegistry = new PageRegistry(pageRepository);
        PageContentService pageContentService = new PageContentService(pageRegistry);
        PageCreator pageCreator = new PageCreator(pageRegistry, pageContentService);

        Page page = pageCreator.createPage("TestPage", "This is a test page.");

        pageRepository.addPage(page);

        pageRegistry.refreshFromDb();

        Page retrievedPage = pageRepository.getPageBySlug(page.slug());

        Assertions.assertNotNull(retrievedPage);
        Assertions.assertEquals(page.title(), retrievedPage.title());
    }

    @Test
    void getPageById() throws SQLException {
        DataSource ds = createTestDataSource();
        initSchema(ds);

        PageRepository pageRepository = new PageRepository(ds);
        PageRegistry pageRegistry = new PageRegistry(pageRepository);
        PageContentService pageContentService = new PageContentService(pageRegistry);
        PageCreator pageCreator = new PageCreator(pageRegistry, pageContentService);

        Page page = pageCreator.createPage("TestPage", "This is a test page.");
        pageRepository.addPage(page);
        pageRegistry.refreshFromDb();


        Page retrievedPageById = pageRepository.getPageById(page.id());
        Assertions.assertEquals(page.title(), retrievedPageById.title());
    }

    @Test
    void getPageBySlug() throws SQLException {
        DataSource ds = createTestDataSource();
        initSchema(ds);

        PageRepository pageRepository = new PageRepository(ds);
        PageRegistry pageRegistry = new PageRegistry(pageRepository);
        PageContentService pageContentService = new PageContentService(pageRegistry);
        PageCreator pageCreator = new PageCreator(pageRegistry, pageContentService);

        Page page = pageCreator.createPage("TestPage", "This is a test page.");
        pageRepository.addPage(page);
        pageRegistry.refreshFromDb();


        Page retrievedPageBySlug = pageRepository.getPageBySlug(page.slug());
        Assertions.assertEquals(page.title(), retrievedPageBySlug.title());
    }

    @Test
    void deletePageFromDb() throws SQLException {
        DataSource ds = createTestDataSource();
        initSchema(ds);

        PageRepository pageRepository = new PageRepository(ds);
        PageRegistry pageRegistry = new PageRegistry(pageRepository);
        PageContentService pageContentService = new PageContentService(pageRegistry);
        PageCreator pageCreator = new PageCreator(pageRegistry, pageContentService);

        Page page = pageCreator.createPage("TestPage", "This is a test page.");
        pageRepository.addPage(page);
        pageRegistry.refreshFromDb();
        Page retrievedPage = pageRepository.getPageBySlug("testpage");

        Assertions.assertNotNull(retrievedPage);
        Assertions.assertEquals("TestPage", retrievedPage.title());

        PageMetaData pageMetaData = pageCreator.getMetaDataOfPage(retrievedPage);
        pageRepository.deletePage(pageMetaData.id());
        pageRegistry.removePage(pageMetaData);
        Page nowDeletedretrievedPage = pageRepository.getPageBySlug("testpage");

        Assertions.assertNull(pageRegistry.getById(pageMetaData.id()));
        Assertions.assertNull(nowDeletedretrievedPage);
    }
}
