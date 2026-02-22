package no.eliashaugsbakk.webserver.service;

import no.eliashaugsbakk.utils.Image;
import no.eliashaugsbakk.webserver.db.PageRepository;
import no.eliashaugsbakk.webserver.model.Page;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

public class PostStorageService {
    private final String imageDir = "/website/images/";
    private final PageRepository pageRepo;

    public PostStorageService(PageRepository pageRepo) {
        this.pageRepo = pageRepo;
    }

    public boolean createPage(String title, String html, List<Image> images) {
        try {
            String slug = generateSlug(title);



            try {
                html = storeImages(images, slug, html);
            } catch (IOException e) {
                System.err.println("Error storing image: " + e.getMessage());
            }

            Instant createdAt = Instant.now();
            return pageRepo.addPage(new Page(
                    slug,
                    title,
                    createdAt,
                    html
            ));

        } catch (SQLException e) {
            System.err.println("Error while trying to save page: " + e.getMessage());
            return false;
        }
    }

    private String storeImages(List<Image> images, String slug, String html) throws IOException {
        try {
            String newHtml = html;
            for (Image image : images) {
                String safeName = slug + image.title();

                newHtml = newHtml.replace(image.title(), safeName);

                Path destination = Paths.get(imageDir,  safeName);
                Files.write(destination, image.data());
            }
            return newHtml;
        } catch (Exception e) {
            System.err.println("Error while trying to save image: " + e.getMessage());
            return null;
        }
    }


    private String generateSlug(String title) throws SQLException {
        String base = title.toLowerCase()
                .replaceAll("æ", "ae")
                .replaceAll("ø", "oe")
                .replaceAll("å", "aa")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String slug = base;
        int counter = 2;
        while (pageRepo.getAllSlugs().contains(slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }

}
