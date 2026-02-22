package no.eliashaugsbakk.uploader.service;

import no.eliashaugsbakk.uploader.exception.UploaderException;
import no.eliashaugsbakk.uploader.model.TextFile;
import no.eliashaugsbakk.utils.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DataNormalizerService {


  private TextFile textFile = null;
  private final List<Image> imageFiles;
  private final List<Image> normalizedImageFiles;

  public DataNormalizerService(List<String> filePaths) throws IOException {

    imageFiles = new ArrayList<>();
    normalizedImageFiles = new ArrayList<>();

    boolean markdownSeen = false;

    for (String s : filePaths) {
      Path path = Path.of(s);

      byte[] fileData = Files.readAllBytes(path);

      String fileName = path.getFileName().toString();


      if (fileName.endsWith(".md") && !markdownSeen) {
        markdownSeen = true;
        textFile = new TextFile(fileName, Files.readString(path));
      } else if (fileName.endsWith(".png")
              || fileName.endsWith(".jpg")
              || fileName.endsWith(".jpeg")
              || fileName.endsWith(".bmp")) {
        imageFiles.add(new Image(fileName, fileData));
      } else {
        throw new UploaderException("Unrecognized file extension: \"" + fileName + "\"");
      }
    }

    if (!markdownSeen) {
      throw new UploaderException("No file with .md extensions found");
    }

    for (Image image : imageFiles) {
      try {
        normalizedImageFiles.add(normalizeImage(image));
      } catch (IOException e) {
        throw new UploaderException("Error reading image file: " + e.getMessage());
      }
    }

    // verify image names are present in the Markdown.
    verifyImageNaming(normalizedImageFiles, textFile.body());
  }

  public TextFile getTextFile() {
    return textFile;
  }

  public List<Image> getImagesFiles() {
    return normalizedImageFiles;
  }

  private void verifyImageNaming(List<Image> images, String markdownFile) {
    for (Image image : images) {

      String name = image.title();

      String regex = "\\b" + Pattern.quote(name) + "\\b";
      boolean found = Pattern.compile(regex).matcher(markdownFile).find();

      if (!found) {
        throw new RuntimeException("Image '" + name + "' is not referenced in the Markdown file.");
      }
    }
  }

  /**
   * Compresses the image, and renames it with its new file extension.
   * @param imageToNormalize the image to normalize
   * @return the normalized image
   * @throws IOException wrong image formatting will throw an IO exception
   */
  private Image normalizeImage(Image imageToNormalize) throws IOException {
    String newImageName = getNewImageName(imageToNormalize.title());

    byte[] imageData = imageToNormalize.data();

    BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
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
      return new Image(newImageName ,baos.toByteArray());
    }
  }


  private String getNewImageName(String oldImageName) {
    String newImageName = oldImageName.replaceAll("(?i)\\.(png|jpg|jpeg|bmp)$", ".webp");

    // Update Markdown references
    updateMarkdownLinks(oldImageName, newImageName);

    return newImageName;
  }


  private void updateMarkdownLinks(String oldName, String newName) {
    textFile = new TextFile(
            textFile.title(),
            textFile.body().replace(oldName, newName));
  }
}
