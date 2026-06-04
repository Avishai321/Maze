package org.example.image;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class MazeImageProcessor {
    public boolean[][] extractMazeGrid(BufferedImage image, int targetWidth, int targetHeight) {
        boolean[][] mazeMap = new boolean[targetHeight][targetWidth];
        int cellWidth = image.getWidth() / targetWidth;
        int cellHeight = image.getHeight() / targetHeight;

        for (int row = 0; row < image.getHeight(); row += cellHeight) {
            for (int col = 0; col < image.getWidth(); col += cellWidth) {
                Color currentColor = new Color(image.getRGB(col, row));
                boolean isWhite = currentColor.equals(Color.WHITE);

                mazeMap[row / cellHeight][col / cellWidth] = isWhite;
            }
        }
        return mazeMap;
    }
}
