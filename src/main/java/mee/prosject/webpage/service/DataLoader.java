package mee.prosject.webpage.service;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DataLoader implements CommandLineRunner {

    private final PageRegistry registry;

    public DataLoader(PageRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void run(String @NonNull ... args) {
        Path pagesDir = Paths.get("src/main/resources/pages");
        System.out.println("Loading pages from: " + pagesDir.toAbsolutePath());

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pagesDir, "*.md")) {
            for (Path file : stream) {
                System.out.println("Found file: " + file.getFileName());

                // Read all lines
                var lines = Files.readAllLines(file);
                if (lines.isEmpty()) {
                    System.out.println("  Skipping empty file: " + file.getFileName());
                    continue;
                }

                // First line as title
                String firstLine = lines.get(0).trim();
                String title = firstLine.startsWith("#") ? firstLine.substring(1).trim() : firstLine;
                System.out.println("  Page title: " + title);

                // Rest as content
                String content = String.join("\n", lines.subList(1, lines.size()));
                System.out.println("  Page content length: " + content.length() + " characters");

                // Add to registry
                registry.addPage(title, content);
                System.out.println("  Added page to registry: " + title);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Finished loading pages.");
    }

}
