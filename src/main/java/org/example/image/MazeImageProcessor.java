package org.example.image;

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

                int rgb = image.getRGB(sampleX, sampleY);
                boolean isWhite = (rgb & 0x00FFFFFF) == 0x00FFFFFF;

                mazeMap[row][col] = isWhite;
            }
        }
        return mazeMap;
    }
}
