package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

//TODO split this class to multiple files
//  MazeRepository, for http and downloading
//  MazeImageProcessor, converts the BufferedImage to boolean[][]
//  MazeSolver, takes the boolean[][] and returns List<Index>
public class MazeSolver {
    private int mazeWidth;
    private int mazeHeight;
    private RenderConfig renderConfig;

    private BufferedImage image;

    boolean[][] mazeMap;
    private boolean[][] path;

    private boolean solutionFound;
    private List<Coordinate> pathIndexes; //todo change "Index" to "Coordinate"

    public void initialize() {
        this.renderConfig = AppConfig.getRenderConfig();
        this.mazeWidth = AppConfig.getMazeWidth();
        this.mazeHeight = AppConfig.getMazeHeight();

        fetchImage();
        processImage();
        findPath();
    }

    public record Coordinate(int x, int y) {}

    private void fetchImage() {
        String url = AppConfig.BASE_IMAGE_URL + "?width=" + mazeWidth + "&height=" + mazeHeight;
        try {
            image = ImageIO.read(URI.create(url).toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processImage() {
        mazeMap = new boolean[mazeHeight][mazeWidth];
        int cellWidth = image.getWidth() / AppConfig.getMazeWidth();
        int cellHeight = image.getHeight() / AppConfig.getMazeHeight();

        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.decode(renderConfig.getWallCellColor()));
        for (int row = 0; row < image.getHeight(); row += cellHeight) {
            for (int col = 0; col < image.getWidth(); col += cellWidth) {
                Color currentColor = new Color(image.getRGB(col, row));
                boolean isWhite = currentColor.equals(Color.WHITE);

                mazeMap[row / cellHeight][col / cellWidth] = isWhite;
                if (!isWhite) g2d.fillRect(col, row, cellWidth, cellHeight);
            }
        }

        if (renderConfig.isDrawGrid()) {
            g2d.setColor(Color.decode(renderConfig.getGridColor()));
            for (int row = 0; row < image.getHeight(); row += cellHeight) {
                g2d.drawLine(0, row, image.getWidth(), row);
            }
            for (int col = 0; col < image.getWidth(); col += cellWidth) {
                g2d.drawLine(col, 0, col, image.getHeight());
            }
        }
    }

    //todo switch to BFS algorithm
    private boolean pathFinderHelper(int row, int col, boolean[][] visited) {
        if (row < 0 || row >= mazeMap.length || col < 0 || col >= mazeMap[0].length) return false;
        if (!mazeMap[row][col] || visited[row][col]) return false;

        visited[row][col] = true;

        if (row == mazeMap.length - 1 && col == mazeMap[0].length - 1) {
            path[row][col] = true;
            pathIndexes.add(new Coordinate(col, row));
            return true;
        }

        if (pathFinderHelper(row, col + 1, visited) ||
                pathFinderHelper(row, col - 1, visited) ||
                pathFinderHelper(row + 1, col, visited) ||
                pathFinderHelper(row - 1, col, visited)) {

            path[row][col] = true;
            pathIndexes.add(new Coordinate(col, row));
            return true;
        }

        return false;
    }

    private void findPath() {
        path = new boolean[mazeMap.length][mazeMap[0].length];
        pathIndexes = new ArrayList<>();

        solutionFound = pathFinderHelper(0, 0, new boolean[mazeMap.length][mazeMap[0].length]);
        if (solutionFound) pathIndexes = pathIndexes.reversed();
    }

    public boolean hasSolution() {
        return path != null && solutionFound;
    }

    public List<Coordinate> getPathIndexes() {
        return pathIndexes;
    }
}
