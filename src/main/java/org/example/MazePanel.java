package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MazePanel extends JPanel {
    private int mazeWidth;
    private int mazeHeight;
    private RenderConfig renderConfig;

    public MazePanel() {
        setBackground(AppConfig.COLOR_BACKGROUND);

        JButton backButton = new JButton("Back");
        backButton.setFocusable(false);
        backButton.addActionListener(e -> {
            Main.changeScene(Main.SETTINGS_PANEL);
        });

        add(backButton);
    }

    public void loadConfigs() {
        RenderConfig config = AppConfig.getRenderConfig();
        if (config == null) {
            System.err.println("Error loading server configurations");
            return;
        }

        this.renderConfig = config;
        this.mazeWidth = AppConfig.getMazeWidth();
        this.mazeHeight = AppConfig.getMazeHeight();

        String url = AppConfig.BASE_IMAGE_URL + "?width=" + mazeWidth + "&height=" + mazeHeight;
        BufferedImage image;

        try {
            image = ImageIO.read(URI.create(url).toURL());

            boolean[][] map = new boolean[mazeHeight][mazeWidth];
            int cellWidth = image.getWidth() / AppConfig.getMazeWidth();
            int cellHeight = image.getHeight() / AppConfig.getMazeHeight();

            Graphics2D g2d = image.createGraphics();

            g2d.setColor(Color.decode(renderConfig.getWallCellColor()));
            for (int row = 0; row < image.getHeight(); row += cellHeight) {
                for (int col = 0; col < image.getWidth(); col += cellWidth) {
                    Color currentColor = new Color(image.getRGB(col, row));
                    boolean isWhite = currentColor.equals(Color.WHITE);

                    map[row / cellHeight][col / cellWidth] = isWhite;
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

            File outputFile = new File("saved_image.jpg");
            ImageIO.write(image, "jpg", outputFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
