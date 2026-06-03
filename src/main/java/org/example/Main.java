package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static final String SETTINGS_PANEL = "SETTINGS_PANEL";
    public static final String MAZE_PANEL = "MAZE_PANEL";

    private static JPanel panels;
    private static SettingsPanel settingsPanel;
    private static MazePanel mazePanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame(AppConfig.APP_TITLE);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            window.setBackground(AppConfig.COLOR_BACKGROUND);

            settingsPanel = new SettingsPanel();
            mazePanel = new MazePanel();

            panels = new JPanel(new CardLayout());
            panels.add(settingsPanel, SETTINGS_PANEL);
            panels.add(mazePanel, MAZE_PANEL);

            window.add(panels);
            window.pack();
            window.setLocationRelativeTo(null);

            window.setVisible(true);
        });
    }

    public static void changeScene(String scene) {
        CardLayout cardLayout = (CardLayout) panels.getLayout();
        cardLayout.show(panels, scene);

        if (scene.equals(MAZE_PANEL)) {
            // not the most beautiful thing in the world, but it works
            mazePanel.initialize();
        }
    }
}
