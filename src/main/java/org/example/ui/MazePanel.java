package org.example.ui;

import org.example.algorithm.BreadthFirstPathfinder;
import org.example.algorithm.Coordinate;
import org.example.config.AppConfig;
import org.example.image.MazeImageProcessor;
import org.example.network.MazeRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MazePanel extends JPanel {
    private MazeCanvas mazeCanvas;
    private JButton solveButton;
    private JButton backButton;
    private Timer solveAnimation;

    private boolean[][] currentMazeMap;
    private List<Coordinate> currentPath;

    private final Logger logger = Logger.getLogger(MazePanel.class.getName());

    public MazePanel() {
        setPreferredSize(AppConfig.BOARD_SIZE);
        setBackground(AppConfig.COLOR_BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
    }

    private void initializeUI() {
        mazeCanvas = new MazeCanvas();
        add(mazeCanvas, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(10, 0, 30, 0));

        backButton = createBackButton();
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
            if (currentPath != null && !currentPath.isEmpty()) {
                btn.setEnabled(false);
                mazeCanvas.resetAnimation();
                startTimer();
            } else {
                JOptionPane.showMessageDialog(MazePanel.this,
                        "There is no solution to this maze.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return btn;
    }

    private void stopTimer() {
        if (solveAnimation != null && solveAnimation.isRunning()) solveAnimation.stop();
    }

    private void startTimer() {
        stopTimer();
        int animationDelay = AppConfig.getRenderConfig().getAnimationDelayMs();

        solveAnimation = new Timer(animationDelay, e -> {
            mazeCanvas.repaint();

            if (!mazeCanvas.nextFrame()) {
                stopTimer();
                solveButton.setEnabled(true);
            }
        });
        solveAnimation.start();
    }

    public void initialize() {
        solveButton.setEnabled(false);
        backButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                MazeRepository repo = new MazeRepository();
                BufferedImage img = repo.fetchMazeImage(AppConfig.getMazeWidth(), AppConfig.getMazeHeight());

                MazeImageProcessor processor = new MazeImageProcessor();
                currentMazeMap = processor.extractMazeGrid(img, AppConfig.getMazeWidth(), AppConfig.getMazeHeight());

                BreadthFirstPathfinder pathfinder = new BreadthFirstPathfinder();
                currentPath = pathfinder.findPath(currentMazeMap);

                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                backButton.setEnabled(true);

                try {
                    get();

                    mazeCanvas.setMazeData(currentMazeMap, currentPath);
                    solveButton.setEnabled(true);

                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Worker interrupted", e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    logger.log(Level.SEVERE, "Failed pipeline", e.getCause());
                    JOptionPane.showMessageDialog(MazePanel.this,
                            "Failed to load maze: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
