package no.eliashaugsbakk.webserver.service.media;

import jakarta.annotation.PostConstruct;
import no.eliashaugsbakk.webserver.model.ImageMetaData;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImageRegistry {
    private final ImageRepository imageRepository;
    private final Map<Long, ImageMetaData> idIndex = new ConcurrentHashMap<>();

    public ImageRegistry(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void init() throws SQLException {
        refreshFromDb();
    }

    public void refreshFromDb() throws SQLException {
        Collection<ImageMetaData> allImages = imageRepository.getAllImagesMetaData();

        Map<Long, ImageMetaData> newIdIndex = new HashMap<>();

        for (ImageMetaData imageMeta : allImages) {
            newIdIndex.put(imageMeta.id(), imageMeta);
        }

        idIndex.clear();
        idIndex.putAll(newIdIndex);
    }
    public ImageMetaData getById(long id) {
        return idIndex.get(id);
    }

    public Collection<ImageMetaData> getAllImages() {
        return new ArrayList<>(idIndex.values());
    }

    public long getNumberOfImages() {
        return idIndex.keySet().stream().max(Long::compare).orElse(0L);
    }

    public void addImage(ImageMetaData meta)  {
        idIndex.put(meta.id(), meta);
    }

    public void removeImage(ImageMetaData meta) {
        idIndex.remove(meta.id());
    }
}
