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

    private Color wallColor;
    private Color gridColor;
    private Color pathColor;

    private BufferedImage backgroundLayer;
    private BufferedImage gridLayerOverlay;

    public MazeCanvas() {
        setOpaque(false);
    }

    public void setMazeData(boolean[][] map, List<Coordinate> path) {
        this.mazeMap = map;
        this.pathIndexes = path;

        RenderConfig config = AppConfig.getRenderConfig();

        wallColor = Color.decode(config.getWallCellColor());
        gridColor = Color.decode(config.getGridColor());
        pathColor = Color.decode(config.getPathColor());

        backgroundLayer = null;
        gridLayerOverlay = null;

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

        if (backgroundLayer != null) backgroundLayer.flush();
        if (gridLayerOverlay != null) gridLayerOverlay.flush();

        // initialize both layers with transparency support
        backgroundLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridLayerOverlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D gWall = backgroundLayer.createGraphics();
        Graphics2D gGrid = gridLayerOverlay.createGraphics();

        gWall.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gGrid.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int mHeight = mazeMap.length;
        int mWidth = mazeMap[0].length;

        int cellSize = Math.min(width / mWidth, height / mHeight);
        int totalMazePixelWidth = mWidth * cellSize;
        int totalMazePixelHeight = mHeight * cellSize;

        int startX = (width - totalMazePixelWidth) / 2;
        int startY = (height - totalMazePixelHeight) / 2;

        RenderConfig config = AppConfig.getRenderConfig();

        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                int rectX = startX + (col * cellSize);
                int rectY = startY + (row * cellSize);

                gWall.setColor(mazeMap[row][col] ? Color.WHITE : wallColor);
                gWall.fillRect(rectX, rectY, cellSize, cellSize);
            }
        }

        if (config.isDrawGrid()) {
            gGrid.setColor(gridColor);
            for (int row = 0; row <= mHeight; row++) {
                int y = startY + (row * cellSize);
                gGrid.drawLine(startX, y, startX + totalMazePixelWidth, y);
            }
            for (int col = 0; col <= mWidth; col++) {
                int x = startX + (col * cellSize);
                gGrid.drawLine(x, startY, x, startY + totalMazePixelHeight);
            }
        }

        gWall.dispose();
        gGrid.dispose();
    }

    private boolean shouldPaintBackground() {
        if (backgroundLayer == null || gridLayerOverlay == null) return true;
        return (backgroundLayer.getWidth() != getWidth() || backgroundLayer.getHeight() != getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        long startTime = System.nanoTime();

        super.paintComponent(g);

        if (mazeMap == null || mazeMap.length == 0) return;
        if (shouldPaintBackground()) preRenderBackground();

        // layer 1: maze background
        if (backgroundLayer != null) g.drawImage(backgroundLayer, 0, 0, null);

        // layer 2: animated solution path
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

            g2d.setColor(pathColor);
            int framesToDraw = Math.min(currentFrameIndex, pathIndexes.size());
            for (int i = 0; i < framesToDraw; i++) {
                Coordinate point = pathIndexes.get(i);
                int x = startX + (point.x() * cellSize);
                int y = startY + (point.y() * cellSize);

                g2d.fillRect(x, y, cellSize, cellSize);
            }
            g2d.dispose();
        }

        // layer 3: grid, this is drawn last, ensuring the grid always sits above the animated path
        if (gridLayerOverlay != null) g.drawImage(gridLayerOverlay, 0, 0, null);

        long renderTimeNs = System.nanoTime() - startTime;
        double renderTimeMs = renderTimeNs / 1_000_000.0;
        System.out.printf("Frame rendered in: %.3f ms%n", renderTimeMs);
    }
}
