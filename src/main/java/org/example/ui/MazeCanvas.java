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
    private List<Coordinate> coordinates;
    private int currentFrameIndex = 0;

    private RenderConfig renderConfig;

    private Color wallColor;
    private Color gridColor;
    private Color pathColor;

    private int cellSize;
    private int startX;
    private int startY;

    private BufferedImage backgroundLayer;
    private BufferedImage pathLayer;
    private BufferedImage gridLayerOverlay;

    public MazeCanvas() {
        setOpaque(false);
    }

    public void setMazeData(boolean[][] map, List<Coordinate> path) {
        this.mazeMap = map;
        this.coordinates = path;

        renderConfig = AppConfig.getRenderConfig();

        wallColor = Color.decode(renderConfig.getWallCellColor());
        gridColor = Color.decode(renderConfig.getGridColor());
        pathColor = Color.decode(renderConfig.getPathColor());

        backgroundLayer = null;
        pathLayer = null;
        gridLayerOverlay = null;

        resetAnimation();
    }

    public void resetAnimation() {
        currentFrameIndex = 0;

        if (pathLayer != null) {
            Graphics2D g = pathLayer.createGraphics();
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, pathLayer.getWidth(), pathLayer.getHeight());
            g.dispose();
        }
        repaint();
    }

    public boolean nextFrame() {
        if (coordinates != null && currentFrameIndex < coordinates.size()) {
            Coordinate point = coordinates.get(currentFrameIndex);

            int x = startX + (point.x() * cellSize);
            int y = startY + (point.y() * cellSize);

            if (pathLayer != null) {
                Graphics2D gPath = pathLayer.createGraphics();
                gPath.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gPath.setColor(pathColor);

                gPath.fillRect(x, y, cellSize, cellSize);
                gPath.dispose();
            }

            currentFrameIndex++;
            repaint(x, y, cellSize, cellSize);
            return true;
        }
        return false;
    }

    private void preRenderLayers() {
        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0 || mazeMap == null) return;

        if (backgroundLayer != null) backgroundLayer.flush();
        if (pathLayer != null) pathLayer.flush();
        if (gridLayerOverlay != null) gridLayerOverlay.flush();

        backgroundLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        pathLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        gridLayerOverlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D gWall = backgroundLayer.createGraphics();
        Graphics2D gPath = pathLayer.createGraphics();
        Graphics2D gGrid = gridLayerOverlay.createGraphics();

        gWall.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gPath.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gGrid.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int mHeight = mazeMap.length;
        int mWidth = mazeMap[0].length;

        cellSize = Math.min(width / mWidth, height / mHeight);
        int totalMazePixelWidth = mWidth * cellSize;
        int totalMazePixelHeight = mHeight * cellSize;

        startX = (width - totalMazePixelWidth) / 2;
        startY = (height - totalMazePixelHeight) / 2;

        // background
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                int rectX = startX + (col * cellSize);
                int rectY = startY + (row * cellSize);

                gWall.setColor(mazeMap[row][col] ? Color.WHITE : wallColor);
                gWall.fillRect(rectX, rectY, cellSize, cellSize);
            }
        }

        // path
        if (coordinates != null && !coordinates.isEmpty()) {
            gPath.setColor(pathColor);
            int framesToDraw = Math.min(currentFrameIndex, coordinates.size());
            for (int i = 0; i < framesToDraw; i++) {
                Coordinate point = coordinates.get(i);
                int x = startX + (point.x() * cellSize);
                int y = startY + (point.y() * cellSize);
                gPath.fillRect(x, y, cellSize, cellSize);
            }
        }

        // grid
        if (renderConfig.isDrawGrid()) {
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
        gPath.dispose();
        gGrid.dispose();
    }

    private boolean shouldPaintBackground() {
        return backgroundLayer == null || pathLayer == null || gridLayerOverlay == null ||
                backgroundLayer.getWidth() != getWidth() || backgroundLayer.getHeight() != getHeight();
    }

    @Override
    protected void paintComponent(Graphics g) {
        long startTime = System.nanoTime();

        super.paintComponent(g);

        if (mazeMap == null || mazeMap.length == 0) return;

        if (shouldPaintBackground()) preRenderLayers();

        if (backgroundLayer != null) g.drawImage(backgroundLayer, 0, 0, null);
        if (pathLayer != null) g.drawImage(pathLayer, 0, 0, null);
        if (gridLayerOverlay != null) g.drawImage(gridLayerOverlay, 0, 0, null);

        long renderTimeNs = System.nanoTime() - startTime;
        double renderTimeMs = renderTimeNs / 1_000_000.0;
        System.out.printf("Frame rendered in: %.3f ms%n", renderTimeMs);
    }
}
