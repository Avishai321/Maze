package org.example.ui;

import org.example.algorithm.Coordinate;
import org.example.config.AppConfig;
import org.example.config.RenderConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class MazeCanvas extends JPanel {

    private boolean[][] mazeMap;
    private List<Coordinate> pathIndexes;
    private int currentFrameIndex = 0;

    private Color cachedWallColor;
    private Color cachedPathColor;
    private Color cachedGridColor;

    private BufferedImage staticBackground;

    public MazeCanvas() {
        setOpaque(false);
    }

    public void setMazeData(boolean[][] map, List<Coordinate> path) {
        this.mazeMap = map;
        this.pathIndexes = path;

        RenderConfig config = AppConfig.getRenderConfig();
        cachedWallColor = Color.decode(config.getWallCellColor());
        cachedPathColor = Color.decode(config.getPathColor());
        cachedGridColor = Color.decode(config.getGridColor());
        staticBackground = null;

        resetAnimation();
        repaint();
    }

    public void resetAnimation() {
        currentFrameIndex = 0;
    }

    public boolean nextFrame() {
        if (pathIndexes != null && currentFrameIndex < pathIndexes.size()) {
            currentFrameIndex++;
            return true;
        }
        return false;
    }

    private void preRenderBackground() {
        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0 || mazeMap == null) return;

        staticBackground = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = staticBackground.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int mHeight = mazeMap.length;
        int mWidth = mazeMap[0].length;

        int cellSize = Math.min(width / mWidth, height / mHeight);
        int totalMazePixelWidth = mWidth * cellSize;
        int totalMazePixelHeight = mHeight * cellSize;

        int startX = (width - totalMazePixelWidth) / 2;
        int startY = (height - totalMazePixelHeight) / 2;

        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                int rectX = startX + (col * cellSize);
                int rectY = startY + (row * cellSize);

                g2d.setColor(mazeMap[row][col] ? Color.WHITE : cachedWallColor);
                g2d.fillRect(rectX, rectY, cellSize, cellSize);
            }
        }

        g2d.dispose();
    }

    private boolean shouldPaintBackground() {
        if (staticBackground == null) return true;
        return (staticBackground.getWidth() != getWidth() || staticBackground.getHeight() != getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        long startTime = System.nanoTime();

        super.paintComponent(g);

        if (mazeMap == null || mazeMap.length == 0) return;

        if (shouldPaintBackground()) preRenderBackground();
        if (staticBackground != null) g.drawImage(staticBackground, 0, 0, null);

        if (pathIndexes != null && !pathIndexes.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int mHeight = mazeMap.length;
            int mWidth = mazeMap[0].length;
            int cellSize = Math.min(getWidth() / mWidth, getHeight() / mHeight);
            int totalMazePixelWidth = mWidth * cellSize;
            int totalMazePixelHeight = mHeight * cellSize;
            int startX = (getWidth() - totalMazePixelWidth) / 2;
            int startY = (getHeight() - totalMazePixelHeight) / 2;

            int framesToDraw = Math.min(currentFrameIndex, pathIndexes.size());
            g2d.setColor(cachedPathColor);

            for (int i = 0; i < framesToDraw; i++) {
                Coordinate point = pathIndexes.get(i);
                int x = startX + (point.x() * cellSize);
                int y = startY + (point.y() * cellSize);
                g2d.fillRect(x, y, cellSize, cellSize);
            }

            // drawing the grid in preRenderBackground() will significantly boost render time.
            // the problem is that the path is overriding the grid.
            // in a 100x100 maze, where the grid is being painted once, the render time is around 1ms.
            // when I paint the grid here, the render time goes up to around 40ms (!!!!)
            if (AppConfig.getRenderConfig().isDrawGrid()) {
                g2d.setColor(cachedGridColor);
                for (int row = 0; row <= mHeight; row++) {
                    int y = startY + (row * cellSize);
                    g2d.drawLine(startX, y, startX + totalMazePixelWidth, y);
                }
                for (int col = 0; col <= mWidth; col++) {
                    int x = startX + (col * cellSize);
                    g2d.drawLine(x, startY, x, startY + totalMazePixelHeight);
                }
            }

            g2d.dispose();
        }

        long renderTimeNs = System.nanoTime() - startTime;
        double renderTimeMs = renderTimeNs / 1_000_000.0;
        System.out.printf("Frame rendered in: %.3f ms%n", renderTimeMs);
    }
}
