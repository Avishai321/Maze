package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame(AppConfig.APP_TITLE);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            window.add(new SettingsPanel());
            window.pack();
            window.setLocationRelativeTo(null);

            window.setVisible(true);
        });
    }
}
