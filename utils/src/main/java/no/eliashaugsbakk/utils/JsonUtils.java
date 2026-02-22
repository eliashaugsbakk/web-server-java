package no.eliashaugsbakk.utils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * This class will produce the JSON to send as the HTTP body when uploading a new site.
 * {
 *   "title": "Page Title",
 *   "html": "<h1>Hello World</h1><p>This is a test.</p>",
 *   "images": [
 *     {
 *       "filename": "image1.webp",
 *       "data": "encoded_base64_string_of_image_1"
 *     },
 *     {
 *       "filename": "image2.webp",
 *       "data": "encoded_base64_string_of_image_2"
 *     }
 *   ]
 * }
 * </p>
 * */
public class JsonUtils {


    public String getJson(Post post) {
        JsonObject root = new JsonObject();

        root.add("title", post.title());
        root.add("html", post.html());

        JsonArray imageArray = new JsonArray();

        Base64.Encoder encoder = Base64.getEncoder();

        for (Image image : post.images()) {
            JsonObject imageEntry = new JsonObject();
            imageEntry.add("title", image.title());
            imageEntry.add("data", encoder.encodeToString(image.data()));

            imageArray.add(imageEntry);
        }

        root.add("images", imageArray);

        return root.toString();
    }

    public Post getPost(String jsonString) {

        JsonObject root = Json.parse(jsonString).asObject();

        String title = root.get("title").asString();
        String html = root.get("html").asString();

        List<Image> images = new ArrayList<>();
        JsonArray imageArray = root.get("images").asArray();
        Base64.Decoder decoder = Base64.getDecoder();

        for (JsonValue value : imageArray) {
            JsonObject imageEntry = value.asObject();

            String imageTitle = imageEntry.getString("title", "");
            byte[] imageData = decoder.decode(imageEntry.getString("data", ""));

            images.add(new Image(imageTitle, imageData));
        }
        return new Post(title, html, images);
    }
}
