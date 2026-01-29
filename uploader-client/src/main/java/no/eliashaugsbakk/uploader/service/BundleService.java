package no.eliashaugsbakk.uploader.service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class BundleService {

  record FileData(String name, byte[] body) {}

  String textFileTitle;
  String textFileBody;
  private final List<FileData> imageFiles = new ArrayList<>();

  public BundleService(List<String> filePaths) throws IOException {
    for (String s : filePaths) {
      Path path = Path.of(s);

      byte[] fileData = Files.readAllBytes(path);

      String fileName = path.getFileName().toString();


      if (fileName.endsWith(".md")) {
        textFileTitle = fileName;
        textFileBody = Files.readString(path);
      } else {
        imageFiles.add(new FileData(fileName, fileData));
      }
    }
  }

  public byte[] bundleToZip() throws IOException {

    List<FileData> normalizedImages = new ArrayList<>();

    // 1. Normalize images
    for (FileData image : imageFiles) {
      byte[] imageData = image.body();
      String imageName = image.name();

      byte[] newImageData = normalizeImage(imageData);
      String newImageName = imageName.replaceAll("(?i)\\.(png|jpg|jpeg|bmp)$", ".webp");

      normalizedImages.add(new FileData(newImageName, newImageData));

      // 2. Update Markdown references
      textFileBody = updateMarkdownLinks(textFileBody, image.name(), newImageName);
    }

    // 3. Verify Markdown references
    verifyImageNaming(normalizedImages, textFileBody);

    // 4. Add data to map
    Map<String, byte[]> files = new HashMap<>();
    files.put(textFileTitle, textFileBody.getBytes(StandardCharsets.UTF_8));
    for (FileData image : normalizedImages) {
      files.put(image.name(), image.body());
    }

    return zipFiles(files);
  }

  public byte[] zipFiles(Map<String, byte[]> filesToZip) throws IOException {
    // 1. ByteArrayOutputStream holds the resulting ZIP data in RAM
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos)) {

      for (Map.Entry<String, byte[]> entry : filesToZip.entrySet()) {
        // 2. Define the file inside the zip
        ZipEntry zipEntry = new ZipEntry(entry.getKey());
        zos.putNextEntry(zipEntry);

        // 3. Write the actual file data
        zos.write(entry.getValue());
        zos.closeEntry();
      }

      // 4. Important: Finish and flush the Zip stream before getting bytes
      zos.finish();
      return baos.toByteArray();
    }
  }

  public void verifyImageNaming(List<FileData> images, String markdownFile) {
    for (FileData image : images) {

      String name = image.name();

      String regex = "\\b" + Pattern.quote(name) + "\\b";
      boolean found = Pattern.compile(regex).matcher(markdownFile).find();

      if (!found) {
        throw new RuntimeException("Image '" + name + "' is not referenced in the Markdown file.");
      }
    }
  }

  public byte[] normalizeImage(byte[] imageToNormalize) throws IOException {
    BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageToNormalize));
    if (originalImage == null) throw new IOException("Invalid image data");

    int targetWidth = Math.min(originalImage.getWidth(), 1200); // limit image width ro 1200px
    int targetHeight = (int) (originalImage.getHeight() * ((double) targetWidth / originalImage.getWidth()));

    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

    Graphics2D g2d = outputImage.createGraphics();
    // Enable high-quality scaling
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
    g2d.dispose();

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      ImageIO.write(outputImage, "webp", baos);
      return baos.toByteArray();
    }
  }

  // Update Markdown links
  public String updateMarkdownLinks(String markdownContent, String oldName, String newName) {
    String escapedOldName = Pattern.quote(oldName);
    return markdownContent.replaceAll(escapedOldName, newName);
  }
}
