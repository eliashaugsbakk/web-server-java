package no.eliashaugsbakk.webserver.controller;

import no.eliashaugsbakk.webserver.model.Image;
import no.eliashaugsbakk.webserver.service.media.ImageRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ImageController {
    private final ImageRepository imageRepository;

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/db/images/{id}")
    public ResponseEntity<byte[]> serveImage(@PathVariable long id) {
      System.out.println("Serving image id=" + id);
        Image image = imageRepository.getImageById(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getData());
    }
}
