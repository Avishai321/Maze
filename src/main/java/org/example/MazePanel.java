package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MazePanel extends JPanel {
    MazeSolver mazeSolver;

    private JButton solveButton;
    private MazeCanvas mazeCanvas;

    public MazePanel() {
        mazeSolver = new MazeSolver();

        setPreferredSize(AppConfig.BOARD_SIZE);
        setBackground(AppConfig.COLOR_BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
    }

    private void initializeUI() {
        mazeCanvas = new MazeCanvas(mazeSolver);
        add(mazeCanvas, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(10, 0, 30, 0));

        JButton backButton = createBackButton(); //todo make it a class field to control it from other methods
        solveButton = createSolveButton();

        controlPanel.add(backButton);
        controlPanel.add(solveButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private JButton createBackButton() {
        JButton backButton = new StyledButton("Back", false);

        //todo make it stop the timer
        backButton.addActionListener(e -> Main.changeScene(Main.SETTINGS_PANEL));

        return backButton;
    }

    private JButton createSolveButton() {
        JButton solveButton = new StyledButton("Check Solution", true);
        solveButton.setEnabled(false); // disable until maze is ready
        solveButton.addActionListener(e -> {
            startTimer();
            solveButton.setEnabled(false);
        });

        return solveButton;
    }

    //TODO IT'S JUST A PLACEHOLDER, CHANGE IT ASAP!
    private void startTimer() {
        Timer timer = new Timer(AppConfig.getRenderConfig().getAnimationDelayMs(),
                e -> mazeCanvas.repaint());
        timer.start();
    }

    public void initialize() {
        mazeSolver.initialize();

        solveButton.setEnabled(true);
        mazeCanvas.repaint();
    }
}
