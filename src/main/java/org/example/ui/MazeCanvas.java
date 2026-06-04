package org.example.ui;

import org.example.algorithm.Coordinate;
import org.example.config.AppConfig;
import org.example.config.RenderConfig;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MazeCanvas extends JPanel {

    private boolean[][] mazeMap;
    private List<Coordinate> pathIndexes;
    private int currentFrameIndex = 0;

    private Color cachedWallColor;
    private Color cachedPathColor;
    private Color cachedGridColor;

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (mazeMap == null || mazeMap.length == 0) {
            g2d.dispose();
            return;
        }

        int mHeight = mazeMap.length;
        int mWidth = mazeMap[0].length;

        int PADDING = 10;
        int panelWidth = getWidth() - (PADDING * 2);
        int panelHeight = getHeight() - (PADDING * 2);

        int cellSize = Math.min(panelWidth / mWidth, panelHeight / mHeight);
        int totalMazePixelWidth = mWidth * cellSize;
        int totalMazePixelHeight = mHeight * cellSize;

        int startX = (getWidth() - totalMazePixelWidth) / 2;
        int startY = (getHeight() - totalMazePixelHeight) / 2;

        RenderConfig config = AppConfig.getRenderConfig();

        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                int rectX = startX + (col * cellSize);
                int rectY = startY + (row * cellSize);

                g2d.setColor(mazeMap[row][col] ? Color.WHITE : cachedWallColor);
                g2d.fillRect(rectX, rectY, cellSize, cellSize);
            }
        }

        if (pathIndexes != null && !pathIndexes.isEmpty()) {
            int framesToDraw = Math.min(currentFrameIndex, pathIndexes.size());
            g2d.setColor(cachedPathColor);

            for (int i = 0; i < framesToDraw; i++) {
                Coordinate point = pathIndexes.get(i);
                int x = startX + (point.x() * cellSize);
                int y = startY + (point.y() * cellSize);
                g2d.fillRect(x, y, cellSize, cellSize);
            }
        }

        if (config.isDrawGrid()) {
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
}
