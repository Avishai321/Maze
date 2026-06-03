package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MazeSolver {
    private int mazeWidth;
    private int mazeHeight;
    private RenderConfig renderConfig;

    BufferedImage image;
    boolean[][] mazeMap;

    public void initialize() {
        this.renderConfig = AppConfig.getRenderConfig();
        this.mazeWidth = AppConfig.getMazeWidth();
        this.mazeHeight = AppConfig.getMazeHeight();

        fetchImage();
        processImage();
        saveImageToFile("maze_image", "jpg");
    }

    public void fetchImage() {
        String url = AppConfig.BASE_IMAGE_URL + "?width=" + mazeWidth + "&height=" + mazeHeight;
        try {
            image = ImageIO.read(URI.create(url).toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processImage() {
        mazeMap = new boolean[mazeHeight][mazeWidth];
        int cellWidth = image.getWidth() / AppConfig.getMazeWidth();
        int cellHeight = image.getHeight() / AppConfig.getMazeHeight();

        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.decode(renderConfig.getWallCellColor()));
        for (int row = 0; row < image.getHeight(); row += cellHeight) {
            for (int col = 0; col < image.getWidth(); col += cellWidth) {
                Color currentColor = new Color(image.getRGB(col, row));
                boolean isWhite = currentColor.equals(Color.WHITE);

                mazeMap[row / cellHeight][col / cellWidth] = isWhite;
                if (!isWhite) g2d.fillRect(col, row, cellWidth, cellHeight);
            }
        }

        if (renderConfig.isDrawGrid()) {
            g2d.setColor(Color.decode(renderConfig.getGridColor()));
            for (int row = 0; row < image.getHeight(); row += cellHeight) {
                g2d.drawLine(0, row, image.getWidth(), row);
            }
            for (int col = 0; col < image.getWidth(); col += cellWidth) {
                g2d.drawLine(col, 0, col, image.getHeight());
            }
        }
    }

    public void saveImageToFile(String fileName, String extension) {
        File outputFile = new File(fileName + "." + extension);
        try {
            ImageIO.write(image, extension, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
