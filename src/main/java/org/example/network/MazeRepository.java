package org.example.network;

import org.example.config.AppConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

public class MazeRepository {
    public BufferedImage fetchMazeImage(int width, int height) throws IOException {
        String url = AppConfig.BASE_IMAGE_URL + "?width=" + width + "&height=" + height;
        return ImageIO.read(URI.create(url).toURL());
    }
}
