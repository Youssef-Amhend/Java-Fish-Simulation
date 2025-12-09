package com.dtp5.model;

import java.awt.Color;
import java.util.Random;

/**
 * Represents a coral formation on the ocean floor.
 * Coral provides shelter for fish and adds visual beauty.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class Coral {

    /** Base position */
    public final double posX;
    public final double posY;

    /** Coral type */
    public final CoralType type;

    /** Size multiplier */
    public final double size;

    /** Primary color */
    public final Color primaryColor;

    /** Secondary color */
    public final Color secondaryColor;

    /** Rotation for variety */
    public final double rotation;

    /** Sway phase for animation */
    private double swayPhase;

    /** Sway speed */
    private final double swaySpeed;

    /**
     * Types of coral with different shapes.
     */
    public enum CoralType {
        BRAIN, // Round, brain-like texture
        BRANCHING, // Tree-like branches
        TABLE, // Flat, table-shaped
        TUBE, // Tube or pipe coral
        FAN, // Sea fan shape
        STAGHORN, // Antler-like branches
        MUSHROOM // Mushroom-shaped
    }

    /** Color palettes for different coral types */
    private static final Color[][] CORAL_PALETTES = {
            // BRAIN
            { new Color(255, 127, 80), new Color(255, 99, 71) },
            // BRANCHING
            { new Color(255, 182, 193), new Color(255, 105, 180) },
            // TABLE
            { new Color(64, 224, 208), new Color(0, 206, 209) },
            // TUBE
            { new Color(255, 215, 0), new Color(255, 165, 0) },
            // FAN
            { new Color(238, 130, 238), new Color(186, 85, 211) },
            // STAGHORN
            { new Color(255, 127, 80), new Color(255, 69, 0) },
            // MUSHROOM
            { new Color(147, 112, 219), new Color(138, 43, 226) }
    };

    /**
     * Creates a new coral at the specified position.
     */
    public Coral(double x, double y, CoralType type, Random random) {
        this.posX = x;
        this.posY = y;
        this.type = type;
        this.size = 0.7 + random.nextDouble() * 0.6;
        this.rotation = (random.nextDouble() - 0.5) * 0.3;
        this.swayPhase = random.nextDouble() * Math.PI * 2;
        this.swaySpeed = 0.01 + random.nextDouble() * 0.015;

        // Get colors from palette with slight variation
        Color[] palette = CORAL_PALETTES[type.ordinal()];
        this.primaryColor = varyColor(palette[0], random, 20);
        this.secondaryColor = varyColor(palette[1], random, 20);
    }

    /**
     * Creates a random coral at the specified position.
     */
    public static Coral createRandom(double x, double y, Random random) {
        CoralType[] types = CoralType.values();
        CoralType type = types[random.nextInt(types.length)];
        return new Coral(x, y, type, random);
    }

    private Color varyColor(Color base, Random random, int variance) {
        int r = clamp(base.getRed() + random.nextInt(variance * 2) - variance, 0, 255);
        int g = clamp(base.getGreen() + random.nextInt(variance * 2) - variance, 0, 255);
        int b = clamp(base.getBlue() + random.nextInt(variance * 2) - variance, 0, 255);
        return new Color(r, g, b);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Updates the coral's sway animation.
     */
    public void update(double currentVx, double currentVy) {
        swayPhase += swaySpeed;
        // Slightly influenced by current
        swayPhase += (currentVx + currentVy) * 0.0005;
    }

    /**
     * Gets the current sway offset for rendering.
     * 
     * @return Sway value between -1 and 1
     */
    public double getSwayValue() {
        return Math.sin(swayPhase) * 0.1;
    }

    /**
     * Gets the base width for this coral type.
     */
    public double getBaseWidth() {
        return switch (type) {
            case BRAIN -> 50 * size;
            case BRANCHING -> 40 * size;
            case TABLE -> 70 * size;
            case TUBE -> 25 * size;
            case FAN -> 45 * size;
            case STAGHORN -> 55 * size;
            case MUSHROOM -> 35 * size;
        };
    }

    /**
     * Gets the base height for this coral type.
     */
    public double getBaseHeight() {
        return switch (type) {
            case BRAIN -> 35 * size;
            case BRANCHING -> 60 * size;
            case TABLE -> 25 * size;
            case TUBE -> 45 * size;
            case FAN -> 50 * size;
            case STAGHORN -> 50 * size;
            case MUSHROOM -> 40 * size;
        };
    }
}
