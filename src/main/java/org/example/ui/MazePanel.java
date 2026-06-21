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

public class MazePanel extends JPanel {
    private MazeCanvas mazeCanvas;
    private JButton solveButton;
    private JLabel statusLabel;

    private Timer solveAnimation;
    private SwingWorker<MazeResult, Void> worker;

    private boolean[][] currentMazeMap;
    private List<Coordinate> currentPath;

    private record MazeResult(boolean[][] map, List<Coordinate> path) {}

    public MazePanel() {
        setPreferredSize(AppConfig.WINDOW_SIZE);
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

    public void initialize() {
        solveButton.setEnabled(false);
        statusLabel.setText(" ");
        mazeCanvas.setMazeData(null, null);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        worker = new SwingWorker<>() {
            @Override
            protected MazeResult doInBackground() throws Exception {
                int mazeWidth = AppConfig.getMazeWidth();
                int mazeHeight = AppConfig.getMazeHeight();

                MazeRepository repo = new MazeRepository();
                BufferedImage img = repo.fetchMazeImage(mazeWidth, mazeHeight);

                MazeImageProcessor processor = new MazeImageProcessor();
                boolean[][] tempMap = processor.extractMazeGrid(img, mazeWidth, mazeHeight);

                BreadthFirstPathfinder pathfinder = new BreadthFirstPathfinder();
                List<Coordinate> tempPath = pathfinder.findPath(tempMap);

                return new MazeResult(tempMap, tempPath);
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                setCursor(Cursor.getDefaultCursor());
                try {
                    MazeResult result = get();

                    currentMazeMap = result.map();
                    currentPath = result.path();

                    System.out.println(currentPath != null && !currentPath.isEmpty() ? "Has Solution" : "No Solution");

                    mazeCanvas.setMazeData(currentMazeMap, currentPath);
                    solveButton.setEnabled(true);
                } catch (ExecutionException e) {
                    System.err.println("Failed pipeline");
                    statusLabel.setText("Failed to load maze from server.");
                } catch (InterruptedException e) {
                    System.err.println("Worker interrupted");
                    Thread.currentThread().interrupt();
                }
            }
        };
        worker.execute();
    }

    private JButton createBackButton() {
        JButton btn = new StyledButton("Back", false);
        btn.addActionListener(e -> {
            if (solveAnimation != null && solveAnimation.isRunning()) solveAnimation.stop();
            if (worker != null && !worker.isDone()) worker.cancel(true);
            Window.changeScene(Window.Panel.SETTINGS_PANEL);
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
            } else {
                statusLabel.setText("No path exists for this maze.");
            }
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
}
