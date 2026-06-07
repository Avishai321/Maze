package org.example.config;

import java.awt.*;

public final class AppConfig {
    public static final String APP_TITLE = "Maze";
    public static final Dimension WINDOW_SIZE = new Dimension(700, 800);
    public static final Dimension WINDOW_MIN_SIZE = new Dimension(600, 600);

    public static final String CONFIG_URL = "https://backend-qcf9.onrender.com/fm1/get-render-config";
    public static final String BASE_IMAGE_URL = "https://backend-qcf9.onrender.com/fm1/get-maze-image";

    private static RenderConfig currentRenderConfig;

    public static final int DEFAULT_MAZE_WIDTH = 30;
    public static final int MIN_MAZE_WIDTH = 5;
    public static final int MAX_MAZE_WIDTH = 100;

    private static int mazeWidth = -1;
    private static int mazeHeight = -1;

    public static final Color COLOR_BACKGROUND = new Color(35, 35, 35);
    public static final Color COLOR_PRIMARY_BUTTON_BACKGROUND = new Color(60, 120, 200);
    public static final Dimension SIZE_BUTTON = new Dimension(180, 45);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 18);

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
