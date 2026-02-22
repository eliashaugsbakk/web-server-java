package no.eliashaugsbakk.utils;

import java.util.List;

/**
 * Contains the data of a new post to upload to the server.
 * <p>The data in this object is formatted correctly, and only need to be parsed into a way of uploading the data.</p>
 * @param title
 * @param html
 * @param images
 */
public record Post(
        String title,
        String html,
        List<Image> images
) {}
