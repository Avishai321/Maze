package org.example.ui;

import org.example.config.AppConfig;

import javax.swing.*;
import java.awt.*;

public class Window {
    public  enum Panel {
        SETTINGS_PANEL("SETTINGS_PANEL"),
        MAZE_PANEL("MAZE_PANEL");

        private final String value;

        Panel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static JPanel panels;
    private static MazePanel mazePanel;

    public Window() {
        JFrame window = new JFrame(AppConfig.APP_TITLE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(AppConfig.WINDOW_MIN_SIZE);
        window.setBackground(AppConfig.COLOR_BACKGROUND);

        SettingsPanel settingsPanel = new SettingsPanel();
        mazePanel = new MazePanel();

        panels = new JPanel(new CardLayout());
        panels.add(settingsPanel, Panel.SETTINGS_PANEL.getValue());
        panels.add(mazePanel, Panel.MAZE_PANEL.getValue());

        window.add(panels);
        window.pack();
        window.setLocationRelativeTo(null);

        window.setVisible(true);
    }

    protected static void changeScene(Panel panel) {
        CardLayout cardLayout = (CardLayout) panels.getLayout();
        cardLayout.show(panels, panel.getValue());

        if (panel.equals(Panel.MAZE_PANEL)) {
            mazePanel.initialize();
        }
    }
}
