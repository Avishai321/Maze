package org.example;

import com.google.gson.annotations.SerializedName;

public class RenderConfig {
    @SerializedName("wallCellColor")
    private String wallCellColor;

    @SerializedName("pathColor")
    private String pathColor;

    @SerializedName("drawGrid")
    private boolean drawGrid;

    @SerializedName("gridColor")
    private String gridColor;

    @SerializedName("animationDelayMs")
    private int animationDelayMs;

    public String getWallCellColor() {
        return wallCellColor;
    }

    public String getPathColor() {
        return pathColor;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public String getGridColor() {
        return gridColor;
    }

    public int getAnimationDelayMs() {
        return animationDelayMs;
    }
}
