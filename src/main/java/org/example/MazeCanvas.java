package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MazeCanvas extends JPanel {
    private final MazeSolver mazeSolver;

    //todo track the current animation state
    private int animationIndex = 0; //todo change this weird name

    public MazeCanvas(MazeSolver mazeSolver) {
        this.mazeSolver = mazeSolver;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("Repaint called");
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        int mWidth = AppConfig.getMazeWidth();
        int mHeight = AppConfig.getMazeHeight();

        // don't draw if the maze hasn't been initialized yet
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

                if (mazeSolver.mazeMap[row][col]) g2d.setColor(Color.WHITE);
                else g2d.setColor(wallColor);

                g2d.fillRect(rectX, rectY, cellSize, cellSize);
            }
        }

        if (mazeSolver.hasSolution()) {
            System.out.println("Has solution, painting things");
            List<Point> pathIndexes = mazeSolver.getPathIndexes();
            g2d.setColor(Color.GREEN);
            for (int i = 0; i < animationIndex; i++) {
                Point point = pathIndexes.get(i);
                int x = startX + (point.x * cellSize);
                int y = startY + (point.y * cellSize);
                g2d.fillRect(x, y, cellSize, cellSize);
            }
            animationIndex = Math.min(animationIndex + 1, pathIndexes.size());
        }

        Color gridColor = Color.decode(config.getGridColor());
        if (config.isDrawGrid()) {
            g2d.setColor(gridColor);

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
