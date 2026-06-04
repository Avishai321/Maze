package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MazeCanvas extends JPanel {
    private final MazeSolver mazeSolver;

    private int currentFrameIndex = 0;

    public MazeCanvas(MazeSolver mazeSolver) {
        this.mazeSolver = mazeSolver;
        setOpaque(false);
    }

    public void resetAnimation() {
        currentFrameIndex = 0;
    }

    public boolean nextFrame() {
        if (mazeSolver.hasSolution() && currentFrameIndex < mazeSolver.getPathIndexes().size()) {
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

        int mWidth = AppConfig.getMazeWidth();
        int mHeight = AppConfig.getMazeHeight();

        if (mWidth <= 0 || mHeight <= 0 || mazeSolver.mazeMap == null) {
            g2d.dispose();
            return;
        }

        int PADDING = 10;
        int panelWidth = getWidth() - (PADDING * 2);
        int panelHeight = getHeight() - (PADDING * 2);

        int cellSize = Math.min(panelWidth / mWidth, panelHeight / mHeight);
        int totalMazePixelWidth = mWidth * cellSize;
        int totalMazePixelHeight = mHeight * cellSize;

        int startX = (getWidth() - totalMazePixelWidth) / 2;
        int startY = (getHeight() - totalMazePixelHeight) / 2;

        RenderConfig config = AppConfig.getRenderConfig();
        Color wallColor = Color.decode(config.getWallCellColor());

        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                int rectX = startX + (col * cellSize);
                int rectY = startY + (row * cellSize);

                g2d.setColor(mazeSolver.mazeMap[row][col] ? Color.WHITE : wallColor);
                g2d.fillRect(rectX, rectY, cellSize, cellSize);
            }
        }

        if (mazeSolver.hasSolution()) {
            List<Index> pathIndexes = mazeSolver.getPathIndexes();
            int framesToDraw = Math.min(currentFrameIndex, pathIndexes.size());

            g2d.setColor(Color.decode(AppConfig.getRenderConfig().getPathColor()));
            for (int i = 0; i < framesToDraw; i++) {
                Index point = pathIndexes.get(i);
                int x = startX + (point.x * cellSize);
                int y = startY + (point.y * cellSize);
                g2d.fillRect(x, y, cellSize, cellSize);
            }
        }

        if (config.isDrawGrid()) {
            g2d.setColor(Color.decode(config.getGridColor()));
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
