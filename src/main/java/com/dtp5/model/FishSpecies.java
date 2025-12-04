package com.dtp5.model;

import java.awt.Color;

/**
 * Defines different fish species with unique characteristics.
 */
public enum FishSpecies {
    SMALL_FISH(
            "Small Fish",
            8, // body length
            4, // body width
            6, // tail length
            4.5, // speed
            new Color[] {
                    new Color(192, 192, 192), // Silver
                    new Color(169, 169, 169), // Dark Gray
                    new Color(211, 211, 211)// Light Gray
            }),

    MEDIUM_FISH(
            "Medium Fish",
            12, // body length
            6, // body width
            8, // tail length
            3.0, // speed
            new Color[] {
                    new Color(255, 140, 0), // Orange
                    new Color(255, 215, 0), // Gold
                    new Color(64, 224, 208)// Turquoise
            }),

    LARGE_FISH(
            "Large Fish",
            18, // body length
            9, // body width
            12, // tail length
            2.0, // speed
            new Color[] {
                    new Color(70, 130, 180), // Steel Blue
                    new Color(100, 149, 237), // Cornflower Blue
                    new Color(65, 105, 225)// Royal Blue
            }),

    TROPICAL_FISH(
            "Tropical Fish",
            10, // body length
            5, // body width
            7, // tail length
            3.5, // speed
            new Color[] {
                    new Color(255, 20, 147), // Deep Pink
                    new Color(255, 69, 0), // Red-Orange
                    new Color(138, 43, 226), // Blue Violet
                    new Color(255, 215, 0)// Gold
            }),

    FAST_FISH(
            "Fast Fish",
            14, // body length
            5, // body width
            10, // tail length
            5.0, // speed
            new Color[] {
                    new Color(0, 191, 255), // Deep Sky Blue
                    new Color(135, 206, 250), // Light Sky Blue
                    new Color(30, 144, 255)// Dodger Blue
            });

    public final String name;
    public final int bodyLength;
    public final int bodyWidth;
    public final int tailLength;
    public final double speed;
    public final Color[] colors;

    FishSpecies(String name, int bodyLength, int bodyWidth, int tailLength,
            double speed, Color[] colors) {
        this.name = name;
        this.bodyLength = bodyLength;
        this.bodyWidth = bodyWidth;
        this.tailLength = tailLength;
        this.speed = speed;
        this.colors = colors;
    }

    /**
     * Get a random color for this species.
     */
    public Color getRandomColor(java.util.Random random) {
        return colors[random.nextInt(colors.length)];
    }

    /**
     * Get schooling behavior strength (0-1).
     */
    public double getSchoolingStrength() {
        return switch (this) {
            case SMALL_FISH -> 0.9; // Very strong schooling
            case MEDIUM_FISH -> 0.7; // Strong schooling
            case LARGE_FISH -> 0.3; // Weak schooling (more solitary)
            case TROPICAL_FISH -> 0.6; // Moderate schooling
            case FAST_FISH -> 0.5; // Moderate schooling
        };
    }

    /**
     * Get minimum distance kept from other fish.
     */
    public double getMinDistance() {
        return bodyLength * 0.8;
    }

    /**
     * Get detection range for other fish.
     */
    public double getMaxDistance() {
        return bodyLength * 5.0;
    }
}
