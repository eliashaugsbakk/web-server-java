package no.eliashaugsbakk.uploader.service;

import no.eliashaugsbakk.uploader.model.TextFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class DataNormalizerServiceTest {

  @TempDir
  Path tempDir;

  @Test
  void getTextFile_returns_the_text_file() throws IOException {
    Path mdPath = tempDir.resolve("markdown.md");
    Files.writeString(mdPath, "markdown text");

    DataNormalizerService normalizer = new DataNormalizerService(List.of(mdPath.toString()));

    assertEquals(new TextFile("markdown.md", "markdown text"), normalizer.getTextFile());
  }

  @Test
  void getImageFiles_returns_the_image_files() throws IOException {
    Path filesDir = Files.createDirectory( tempDir.resolve("files"));

    Path markdown = filesDir.resolve("markdown.md");
    Path image = filesDir.resolve("image.png");

    Files.writeString(markdown, "markdown text ![alt text](image.png)");

    byte[] imageBytes = createTestImage();
    Files.write(image, imageBytes);


    DataNormalizerService normalizer =
            new DataNormalizerService(
                    List.of(markdown.toString(), image.toString())
            );

      assertEquals(1, normalizer.getImagesFiles().size());
    assertTrue(normalizer.getImagesFiles().getFirst().title().contains("image.webp"));
  }


  private byte[] createTestImage() throws IOException {
    BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = img.createGraphics();
    g2d.setColor(Color.RED);
    g2d.fillRect(0, 0, 10, 10);
    g2d.dispose();

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      ImageIO.write(img, "png", baos);
      return baos.toByteArray();
    }
  }
}
