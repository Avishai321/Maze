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
    private RenderConfig serverConfigs;

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

        this.serverConfigs = config;
        this.mazeWidth = AppConfig.getMazeWidth();
        this.mazeHeight = AppConfig.getMazeHeight();

        String url = AppConfig.BASE_IMAGE_URL + "?width=" + mazeWidth + "&height=" + mazeHeight;
        BufferedImage image;

        try {
            image = ImageIO.read(URI.create(url).toURL()); //todo make it async or something, it must be faster
            File outputFile = new File("saved_image.jpg");
            ImageIO.write(image, "jpg", outputFile);

            boolean[][] map = new boolean[mazeHeight][mazeWidth];
            int cellWidth = image.getWidth() / AppConfig.getMazeWidth();
            int cellHeight = image.getHeight() / AppConfig.getMazeHeight();

            for (int y = 0; y < AppConfig.getMazeHeight(); y++) {
                for (int x = 0; x < AppConfig.getMazeWidth(); x++) {
                    Color color = new Color(image.getRGB(x * cellWidth, y * cellHeight));
                    map[y][x] = color.equals(Color.WHITE);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
