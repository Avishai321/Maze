package org.example;

import javax.swing.*;
import java.awt.*;

public class StyledButton extends JButton {
    public StyledButton(String text, boolean isPrimary) {
        setText(text);
        setFont(AppConfig.FONT_BUTTON);
        setFocusable(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(AppConfig.SIZE_BUTTON);
        if (isPrimary) {
            setBackground(AppConfig.COLOR_PRIMARY_BUTTON_BACKGROUND);
            setForeground(Color.WHITE);
        }
    }
}
