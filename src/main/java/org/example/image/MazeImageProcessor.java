package org.example.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MazeImageProcessor {
    public boolean[][] extractMazeGrid(BufferedImage image, int targetWidth, int targetHeight) {
        boolean[][] mazeMap = new boolean[targetHeight][targetWidth];

        int cellWidth = image.getWidth() / targetWidth;
        int cellHeight = image.getHeight() / targetHeight;

        for (int row = 0; row < targetHeight; row++) {
            for (int col = 0; col < targetWidth; col++) {
                int sampleX = (col * cellWidth) + (cellWidth / 2);
                int sampleY = (row * cellHeight) + (cellHeight / 2);

                boolean isWhite = Color.WHITE.equals(new Color(image.getRGB(sampleX, sampleY)));
                mazeMap[row][col] = isWhite;
            }
        }
        return mazeMap;
    }
}
