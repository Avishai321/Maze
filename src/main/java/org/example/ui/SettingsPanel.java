package org.example.ui;

import org.example.config.AppConfig;
import org.example.config.RenderConfig;
import org.example.network.ConfigService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.ParseException;

public class SettingsPanel extends JPanel {
    private JLabel wallCellColorValue;
    private JLabel pathColorValue;
    private JLabel drawGridValue;
    private JLabel gridColorValue;
    private JLabel animationDelayValue;

    private JSpinner widthSpinner;
    private JSpinner heightSpinner;

    private JButton getMazeButton;
    private JButton refreshButton;

    private static final Color COLOR_PANEL_BG = new Color(45, 45, 45);
    private static final Color COLOR_LOADING = new Color(220, 180, 50);
    private static final Color COLOR_SUCCESS = new Color(100, 220, 100);
    private static final Color COLOR_ERROR = new Color(255, 100, 100);
    private static final Color COLOR_TEXT_KEY = new Color(180, 180, 180);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_KEY = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font FONT_VALUE = new Font("Segoe UI", Font.BOLD, 18);

    public SettingsPanel() {
        setPreferredSize(AppConfig.BOARD_SIZE);
        setBackground(AppConfig.COLOR_BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        refetchConfigs();
    }

    private void initializeUI() {
        JPanel unifiedGroup = new JPanel(new BorderLayout());
        unifiedGroup.setBackground(COLOR_PANEL_BG);
        unifiedGroup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(80, 80, 80), 2, true),
                        " Configurations & Map Settings ",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        FONT_TITLE,
                        Color.WHITE
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel contentSplitter = new JPanel(new GridLayout(1, 2, 40, 0));
        contentSplitter.setOpaque(false);

        contentSplitter.add(createServerColumn());
        contentSplitter.add(createLocalColumn());
        unifiedGroup.add(contentSplitter, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        GridBagConstraints mainConstraints = new GridBagConstraints();
        mainConstraints.fill = GridBagConstraints.BOTH;
        mainConstraints.weightx = 1.0;
        mainConstraints.weighty = 1.0;
        mainConstraints.insets = new Insets(40, 40, 40, 40);

        centerWrapper.add(unifiedGroup, mainConstraints);
        add(centerWrapper, BorderLayout.CENTER);

        add(createActionButtons(), BorderLayout.SOUTH);
    }

    private JPanel createServerColumn() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints cKey = new GridBagConstraints();
        cKey.gridx = 0;
        cKey.anchor = GridBagConstraints.WEST;
        cKey.weightx = 1.0;
        cKey.insets = new Insets(15, 10, 15, 0);

        GridBagConstraints cVal = new GridBagConstraints();
        cVal.gridx = 1;
        cVal.anchor = GridBagConstraints.EAST;
        cVal.weightx = 0.0;
        cVal.insets = new Insets(15, 0, 15, 10);

        // --- Row 0: Wall Cell Color ---
        cKey.gridy = 0; cVal.gridy = 0;
        panel.add(createKeyLabel("Wall Cell Color:"), cKey);
        wallCellColorValue = createValueLabel();
        panel.add(wallCellColorValue, cVal);

        // --- Row 1: Path Color ---
        cKey.gridy = 1; cVal.gridy = 1;
        panel.add(createKeyLabel("Path Color:"), cKey);
        pathColorValue = createValueLabel();
        panel.add(pathColorValue, cVal);

        // --- Row 2: Draw Grid ---
        cKey.gridy = 2; cVal.gridy = 2;
        panel.add(createKeyLabel("Draw Grid:"), cKey);
        drawGridValue = createValueLabel();
        panel.add(drawGridValue, cVal);

        // --- Row 3: Grid Color ---
        cKey.gridy = 3; cVal.gridy = 3;
        panel.add(createKeyLabel("Grid Color:"), cKey);
        gridColorValue = createValueLabel();
        panel.add(gridColorValue, cVal);

        // --- Row 4: Animation Delay ---
        cKey.gridy = 4; cVal.gridy = 4;
        panel.add(createKeyLabel("Animation Delay:"), cKey);
        animationDelayValue = createValueLabel();
        panel.add(animationDelayValue, cVal);

        GridBagConstraints fillerC = new GridBagConstraints();
        fillerC.gridx = 0; fillerC.gridy = 5;
        fillerC.gridwidth = 2; fillerC.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), fillerC);

        return panel;
    }

    private JPanel createLocalColumn() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints cLabel = new GridBagConstraints();
        cLabel.gridx = 0;
        cLabel.anchor = GridBagConstraints.EAST;
        cLabel.insets = new Insets(15, 10, 15, 10);

        GridBagConstraints cSpinner = new GridBagConstraints();
        cSpinner.gridx = 1;
        cSpinner.anchor = GridBagConstraints.WEST;
        cSpinner.insets = new Insets(15, 10, 15, 10);

        // --- Row 0: Width ---
        cLabel.gridy = 0; cSpinner.gridy = 0;
        panel.add(createKeyLabel("Maze Width:"), cLabel);
        widthSpinner = createFlexibleSpinner();
        panel.add(widthSpinner, cSpinner);

        // --- Row 1: Height ---
        cLabel.gridy = 1; cSpinner.gridy = 1;
        panel.add(createKeyLabel("Maze Height:"), cLabel);
        heightSpinner = createFlexibleSpinner();
        panel.add(heightSpinner, cSpinner);

        GridBagConstraints fillerC = new GridBagConstraints();
        fillerC.gridx = 0; fillerC.gridy = 2;
        fillerC.gridwidth = 2; fillerC.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), fillerC);

        return panel;
    }

    private JPanel createActionButtons() {
        // FIX 4: Exactly match the spacing and borders of MazePanel's button container
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        refreshButton = new StyledButton("Refresh Config", false);
        refreshButton.addActionListener(e -> refetchConfigs());

        getMazeButton = new StyledButton("Get Maze", true);
        getMazeButton.addActionListener(e -> {
            commitSpinnerEdit(widthSpinner);
            commitSpinnerEdit(heightSpinner);

            int selectedWidth = validateSpinner(widthSpinner);
            int selectedHeight = validateSpinner(heightSpinner);

            widthSpinner.setValue(selectedWidth);
            heightSpinner.setValue(selectedHeight);

            RenderConfig currentConfig = AppConfig.getRenderConfig();
            if (currentConfig == null) {
                JOptionPane.showMessageDialog(this,
                        "Please wait for configurations to load before generating a maze.",
                        "Configuration Pending",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            AppConfig.setMazeDimensions(selectedWidth, selectedHeight);
            Main.changeScene(Main.MAZE_PANEL);
        });
        getMazeButton.setEnabled(false);

        buttonPanel.add(refreshButton);
        buttonPanel.add(getMazeButton);

        return buttonPanel;
    }

    private void setActionButtonsEnable(boolean enable) {
        refreshButton.setEnabled(enable);
        getMazeButton.setEnabled(enable);
    }

    private JLabel createKeyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_KEY);
        label.setForeground(COLOR_TEXT_KEY);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("Loading...");
        label.setFont(FONT_VALUE);
        label.setForeground(COLOR_LOADING);
        return label;
    }

    private JSpinner createFlexibleSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(
                AppConfig.DEFAULT_MAZE_WIDTH,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                1
        );
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(FONT_VALUE);
        spinner.setPreferredSize(new Dimension(100, 35));

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        JFormattedTextField textField = editor.getTextField();
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBackground(new Color(60, 60, 60));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);

        return spinner;
    }

    private int validateSpinner(JSpinner spinner) {
        try {
            spinner.commitEdit();
            int val = (Integer) spinner.getValue();

            //todo according to the instructions, an invalid value should return AppConfig.DEFAULT_MAZE_WIDTH
            return Math.clamp(val, AppConfig.MIN_MAZE_WIDTH, AppConfig.MAX_MAZE_WIDTH);
        } catch (ParseException e) {
            return AppConfig.DEFAULT_MAZE_WIDTH;
        }
    }

    private void commitSpinnerEdit(JSpinner spinner) {
        try {
            spinner.commitEdit();
        } catch (ParseException ex) {
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) editor).getTextField().setValue(spinner.getValue());
            }
        }
    }

    public void refetchConfigs() {
        setAllLabels("Loading...", COLOR_LOADING);
        setActionButtonsEnable(false);

        ConfigService.fetchRenderConfig(new ConfigService.ConfigCallback() {
            @Override
            public void onSuccess(RenderConfig config) {
                AppConfig.setRenderConfig(config);
                SwingUtilities.invokeLater(() -> {
                    setActionButtonsEnable(true);
                    updateUIWithData(config);
                });
            }

            @Override
            public void onError(Exception e) {
                refreshButton.setEnabled(true);
                SwingUtilities.invokeLater(() -> setAllLabels("Connection Error", COLOR_ERROR));
            }
        });
    }

    private void updateUIWithData(RenderConfig config) {
        if (config == null) {
            setAllLabels("Connection Error", COLOR_ERROR);
            return;
        }

        updateColorLabel(wallCellColorValue, config.getWallCellColor());
        updateColorLabel(pathColorValue, config.getPathColor());
        updateColorLabel(gridColorValue, config.getGridColor());

        updateSingleLabel(drawGridValue, String.valueOf(config.isDrawGrid()));
        updateSingleLabel(animationDelayValue, config.getAnimationDelayMs() + " ms");
    }

    private void updateSingleLabel(JLabel label, String text) {
        label.setText(text != null ? text : "N/A");
        label.setForeground(COLOR_SUCCESS);
        label.setIcon(null); // Ensure no icon is drawn for plain text
    }

    private void updateColorLabel(JLabel label, String hexCode) {
        label.setText(hexCode != null ? hexCode : "N/A");
        label.setForeground(COLOR_SUCCESS);

        Color parsedColor = safeParseColor(hexCode);
        if (parsedColor != null) {
            label.setIcon(new ColorIcon(16, 16, parsedColor));
            label.setIconTextGap(8);
        } else label.setIcon(null);
    }

    private void setAllLabels(String text, Color color) {
        JLabel[] allLabels = {wallCellColorValue, pathColorValue, drawGridValue, gridColorValue, animationDelayValue};
        for (JLabel label : allLabels) {
            label.setText(text);
            label.setForeground(color);
            label.setIcon(null);
        }
    }

    private Color safeParseColor(String colorStr) {
        if (colorStr == null || colorStr.trim().isEmpty()) return null;
        try {
            return Color.decode(colorStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private record ColorIcon(int width, int height, Color color) implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, width, height);

            g.setColor(new Color(150, 150, 150));
            g.drawRect(x, y, width - 1, height - 1);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
