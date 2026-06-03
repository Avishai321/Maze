package org.example;

import java.awt.*;

public final class AppConfig {
    public static final String APP_TITLE = "Maze";
    public static final String CONFIG_URL = "https://backend-qcf9.onrender.com/fm1/get-render-config";
    public static final String BASE_IMAGE_URL = "https://backend-qcf9.onrender.com/fm1/get-maze-image";

    public static final int BOARD_WIDTH = 800;
    public static final int BOARD_HEIGHT = 800;

    private static RenderConfig currentRenderConfig;

    public static final int DEFAULT_MAZE_WIDTH = 30;
    public static final int MIN_MAZE_WIDTH = 5;
    public static final int MAX_MAZE_WIDTH = 100;

    private static int mazeWidth = -1;
    private static int mazeHeight = -1;

    public static final Color COLOR_BACKGROUND = new Color(35, 35, 35);

    private AppConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void setRenderConfig(RenderConfig config) {
        currentRenderConfig = config;
    }

    public static RenderConfig getRenderConfig() {
        return currentRenderConfig;
    }

    public static void setMazeDimensions(int width, int height) {
        mazeWidth = width;
        mazeHeight = height;
    }

    public static int getMazeWidth() {
        return mazeWidth;
    }

    public static int getMazeHeight() {
        return mazeHeight;
    }
}
