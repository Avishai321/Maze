package org.example.ui;

import org.example.config.AppConfig;

import javax.swing.*;
import java.awt.*;

public class Window {
    public static final String SETTINGS_PANEL = "SETTINGS_PANEL";
    public static final String MAZE_PANEL = "MAZE_PANEL";

    private static JPanel panels;
    private static MazePanel mazePanel;

    public Window() {
        initialize();
    }

    private void initialize() {
        JFrame window = new JFrame(AppConfig.APP_TITLE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(AppConfig.WINDOW_MIN_SIZE);
        window.setBackground(AppConfig.COLOR_BACKGROUND);

        SettingsPanel settingsPanel = new SettingsPanel();
        mazePanel = new MazePanel();

        panels = new JPanel(new CardLayout());
        panels.add(settingsPanel, SETTINGS_PANEL);
        panels.add(mazePanel, MAZE_PANEL);

        window.add(panels);
        window.pack();
        window.setLocationRelativeTo(null);

        window.setVisible(true);
    }

    protected static void changeScene(String scene) {
        CardLayout cardLayout = (CardLayout) panels.getLayout();
        cardLayout.show(panels, scene);

        if (scene.equals(MAZE_PANEL)) {
            mazePanel.initialize();
        }
    }
}
