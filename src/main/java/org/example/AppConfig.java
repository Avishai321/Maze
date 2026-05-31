package org.example;

public final class AppConfig {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static final String APP_TITLE = "Maze";
    public static final String SERVER_URL = "https://backend-qcf9.onrender.com/fm1/get-render-config";

    private AppConfig() {
        throw new UnsupportedOperationException("Utility class");
    }
}
