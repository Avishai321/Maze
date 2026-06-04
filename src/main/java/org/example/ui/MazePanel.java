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
    private JLabel statusLabel; // New Label

    private Timer solveAnimation;
    private SwingWorker<Void, Void> worker;

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
        controlPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JButton backButton = createBackButton();
        solveButton = createSolveButton();

        controlPanel.add(backButton);
        controlPanel.add(solveButton);
        add(controlPanel, BorderLayout.SOUTH);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setForeground(new Color(255, 100, 100));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        add(statusLabel, BorderLayout.NORTH);
    }

    private JButton createBackButton() {
        JButton btn = new StyledButton("Back", false);
        btn.addActionListener(e -> {
            if (solveAnimation != null && solveAnimation.isRunning()) solveAnimation.stop();
            if (worker != null && !worker.isDone()) worker.cancel(true);
            Main.changeScene(Main.SETTINGS_PANEL);
        });
        return btn;
    }

    private JButton createSolveButton() {
        JButton btn = new StyledButton("Check Solution", true);
        btn.setEnabled(false);
        btn.addActionListener(e -> {
            if (currentPath != null && !currentPath.isEmpty()) {
                statusLabel.setText(" ");
                btn.setEnabled(false);
                mazeCanvas.resetAnimation();
                startTimer();
            } else statusLabel.setText("No path exists for this maze.");
        });
        return btn;
    }

    private void setupTimer() {
        if (solveAnimation != null) return;

        solveAnimation = new Timer(0, e -> {
            mazeCanvas.repaint();
            if (!mazeCanvas.nextFrame()) {
                solveAnimation.stop();
                solveButton.setEnabled(true);
            }
        });
    }

    private void startTimer() {
        setupTimer();
        int animationDelay = AppConfig.getRenderConfig().getAnimationDelayMs();
        solveAnimation.setDelay(animationDelay);
        solveAnimation.restart();
    }

    public void initialize() {
        solveButton.setEnabled(false);
        statusLabel.setText(" ");
        mazeCanvas.setMazeData(null, null);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        worker = new SwingWorker<>() {
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
                if (isCancelled()) return;
                setCursor(Cursor.getDefaultCursor());
                try {
                    get();
                    mazeCanvas.setMazeData(currentMazeMap, currentPath);
                    solveButton.setEnabled(true);

                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Worker interrupted", e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    logger.log(Level.SEVERE, "Failed pipeline", e.getCause());
                    statusLabel.setText("Failed to load maze from server.");
                }
            }
        };
        worker.execute();
    }
}
