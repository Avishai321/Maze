package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MazePanel extends JPanel {
    private final MazeSolver mazeSolver;
    private MazeCanvas mazeCanvas;

    private JButton solveButton;
    private JButton backButton;
    private Timer solveAnimation;

    private final Logger logger = Logger.getLogger(MazePanel.class.getName());

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

        backButton = createBackButton(); //todo make it a class field to control it from other methods
        solveButton = createSolveButton();

        controlPanel.add(backButton);
        controlPanel.add(solveButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private JButton createBackButton() {
        JButton btn = new StyledButton("Back", false);
        btn.addActionListener(e -> {
            stopTimer();
            Main.changeScene(Main.SETTINGS_PANEL);
        });
        return btn;
    }

    private JButton createSolveButton() {
        JButton btn = new StyledButton("Check Solution", true);
        btn.setEnabled(false);
        btn.addActionListener(e -> {
            btn.setEnabled(false);
            mazeCanvas.resetAnimation();
            startTimer();
        });
        return btn;
    }

    private void startTimer() {
        stopTimer();
        int animationDelay = AppConfig.getRenderConfig().getAnimationDelayMs();

        solveAnimation = new Timer(animationDelay, e -> {
            boolean hasNextMove = mazeCanvas.nextFrame();
            mazeCanvas.repaint();

            if (!hasNextMove) {
                stopTimer();
                solveButton.setEnabled(true);
            }
        });
        solveAnimation.start();
    }

    private void stopTimer() {
        if (solveAnimation != null && solveAnimation.isRunning()) solveAnimation.stop();
    }

    public void initialize() {
        solveButton.setEnabled(false);
        backButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                mazeSolver.initialize();
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                backButton.setEnabled(true);

                try {
                    get();
                    solveButton.setEnabled(true);
                    mazeCanvas.resetAnimation();
                    mazeCanvas.repaint();
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Maze background worker was interrupted", e);
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(MazePanel.this,
                            "The operation was interrupted.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ExecutionException e) {
                    logger.log(Level.SEVERE, "Failed to initialize or solve the maze", e.getCause());
                    JOptionPane.showMessageDialog(MazePanel.this,
                            "Failed to load and solve maze: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        };
        worker.execute();
    }
}
