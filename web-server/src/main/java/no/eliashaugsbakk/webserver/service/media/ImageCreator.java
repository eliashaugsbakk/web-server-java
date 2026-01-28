package no.eliashaugsbakk.webserver.service.media;

import no.eliashaugsbakk.webserver.model.Image;
import no.eliashaugsbakk.webserver.model.ImageMetaData;
import no.eliashaugsbakk.webserver.model.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ImageCreator {
    ImageRegistry imageRegistry;
    public ImageCreator(ImageRegistry imageRegistry) {
        this.imageRegistry = imageRegistry;
    }

    public Image createPage(String filename, String contentType, byte[] data) {
        long id = generateId();
        Instant uploadedAt = Instant.now();
        return new Image(id, filename, contentType, uploadedAt, data);
    }

    public ImageMetaData getMetaDataOfPage(Page image) {
        long id = image.id();
        String title = image.title();
        String slug = image.slug();
        Instant createdAt = image.createdAt();
        return new ImageMetaData(id, title, slug, createdAt);
    }

    private long generateId() {
        return imageRegistry.getNumberOfImages() + 1;
    }
}
