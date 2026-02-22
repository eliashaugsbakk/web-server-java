package no.eliashaugsbakk.webserver.db;

import no.eliashaugsbakk.webserver.model.Page;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface PageRepository {
    Page getPageBySlug(String slug) throws SQLException;
    List<Page> getAllPages() throws SQLException;
    boolean addPage(Page page) throws SQLException;
    List<Page> searchInTitle(String query) throws SQLException;
    Set<String> getAllSlugs() throws SQLException;
}
