package no.eliashaugsbakk.uploader.service;

import no.eliashaugsbakk.uploader.model.Image;
import no.eliashaugsbakk.uploader.model.Post;
import no.eliashaugsbakk.uploader.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonMakerTest {
    @Test
    void getJson_returns_the_expected_json_string() {
        JsonUtils jsonMaker = new JsonUtils();
        Post jsonPost = new Post("Title", "<p> html string </p>", List.of(
                new Image("image1", "data1".getBytes()),
                new Image("image2", "data2".getBytes())
        ));
        String json = jsonMaker.getJson(jsonPost);

        assertTrue(json.contains("Title"));
        assertTrue(json.contains("html string </p>"));
        assertTrue(json.contains("image1"));
        assertTrue(json.contains(Base64.getEncoder().encodeToString("data1".getBytes())));
    }
}
