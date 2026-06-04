package org.example.util;

import java.awt.Color;

public final class ColorUtils {
    private ColorUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Color getIntermediateColor(Color start, Color target, int currentStep, int totalSteps) {
        float ratio = (float) currentStep / totalSteps;

        int r = (int) (start.getRed() + (target.getRed() - start.getRed()) * ratio);
        int g = (int) (start.getGreen() + (target.getGreen() - start.getGreen()) * ratio);
        int b = (int) (start.getBlue() + (target.getBlue() - start.getBlue()) * ratio);

        return new Color(r, g, b);
    }

    public static Color getInvertedColor(Color color) {
        int r = 255 - color.getRed();
        int g = 255 - color.getGreen();
        int b = 255 - color.getBlue();

        return new Color(r, g, b);
    }
}
