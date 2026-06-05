package org.example.network;

import org.example.config.AppConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MazeRepository {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public BufferedImage fetchMazeImage(int width, int height) throws IOException, InterruptedException {
        String url = AppConfig.BASE_IMAGE_URL + "?width=" + width + "&height=" + height;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200)
            throw new IOException("Failed to fetch image. HTTP status code: " + response.statusCode());

        try (InputStream inputStream = response.body()) {
            BufferedImage img = ImageIO.read(inputStream);
            if (img == null) throw new IOException("Server returned invalid image format or empty body");
            return img;
        }
    }
}
