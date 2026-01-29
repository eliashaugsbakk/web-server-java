package no.eliashaugsbakk.uploader.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.HashSet;
import java.util.Set;

class BundleServiceTest {

  @TempDir
  Path tempDir;

  @Test
  void testBundleToZip_ProcessesImagesAndUpdatesMarkdown() throws IOException {
    // 1. Setup
    Path mdPath = tempDir.resolve("post.md");
    Files.writeString(mdPath, "![alt](test-image.png)");

    Path imgPath = tempDir.resolve("test-image.png");
    // Generate REAL valid PNG bytes
    Files.write(imgPath, createTestImage());

    // 2. Execute
    BundleService service = new BundleService(List.of(mdPath.toString(), imgPath.toString()));
    byte[] zipResult = service.bundleToZip();

    // 3. Verify (logic remains the same)
    assertNotNull(zipResult);

    // 3. Verify ZIP contents
    assertNotNull(zipResult);

    Set<String> entryNames = new HashSet<>();
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipResult))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        entryNames.add(entry.getName());

        // If it's the markdown file, check if the link was updated to .webp
        if (entry.getName().equals("post.md")) {
          String content = new String(zis.readAllBytes());
          assertTrue(content.contains("test-image.webp"), "Markdown should reference .webp");
          assertFalse(content.contains("test-image.png"), "Markdown should NOT reference .png");
        }
      }
    }

    assertTrue(entryNames.contains("test-image.webp"), "Zip should contain the webp image");
    assertTrue(entryNames.contains("post.md"), "Zip should contain the markdown file");
  }

  @Test
  void testVerifyImageNaming_ThrowsExceptionWhenUnreferenced() throws IOException {
    Path mdPath = tempDir.resolve("broken.md");
    Files.writeString(mdPath, "No images here!");

    Path imgPath = tempDir.resolve("ghost.png");
    // Use valid image bytes so it passes the 'normalizeImage' step
    // but fails the 'verifyImageNaming' step.
    Files.write(imgPath, createTestImage());

    BundleService service = new BundleService(List.of(mdPath.toString(), imgPath.toString()));

    // NOW it will reach your validation logic and throw the RuntimeException
    assertThrows(RuntimeException.class, service::bundleToZip);
  }

  @Test
  void verifyIsActualZip(@TempDir Path tempDir) throws IOException {
    // 1. Create a dummy file so the service has something to zip
    Path dummyMd = tempDir.resolve("test.md");
    Files.writeString(dummyMd, "Sample content");

    // 2. Initialize the service
    BundleService service = new BundleService(List.of(dummyMd.toString()));
    byte[] result = service.bundleToZip();

    // 3. The "Magic Number" assert (Fast check)
    assertEquals(0x50, result[0], "First byte should be 'P'");
    assertEquals(0x4B, result[1], "Second byte should be 'K'");

    // 4. The "Structural" assert (Deep check)
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(result))) {
      ZipEntry entry = zis.getNextEntry();
      assertNotNull(entry, "The ZIP should contain at least one entry");
      assertEquals("test.md", entry.getName());
    }
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
