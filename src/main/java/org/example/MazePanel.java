package org.example;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    MazeSolver mazeSolver;

    public MazePanel() {
        mazeSolver = new MazeSolver();

        setBackground(AppConfig.COLOR_BACKGROUND);

        JButton backButton = new JButton("Back");
        backButton.setFocusable(false);
        backButton.addActionListener(e -> Main.changeScene(Main.SETTINGS_PANEL));
        add(backButton);
    }

    public void initialize() {
        mazeSolver.initialize();
    }
}
