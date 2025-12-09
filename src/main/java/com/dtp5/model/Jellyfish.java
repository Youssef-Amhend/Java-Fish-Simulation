package com.dtp5.model;

import java.awt.Color;
import java.util.Random;

/**
 * Represents a jellyfish that drifts with currents and glows at night.
 * Jellyfish move with a pulsating motion and are translucent.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class Jellyfish extends Entity implements Updatable {

    /** Velocity components */
    private double vx, vy;

    /** Pulsation phase for movement */
    private double pulsePhase;

    /** Pulse speed (how fast the jellyfish pulsates) */
    private double pulseSpeed;

    /** Base color of this jellyfish */
    private final Color baseColor;

    /** Glow color for bioluminescence */
    private final Color glowColor;

    /** Size of the jellyfish bell */
    private final double bellRadius;

    /** Length of tentacles */
    private final double tentacleLength;

    /** Number of tentacles */
    private final int tentacleCount;

    /** Random for visual variations */
    private static final Random random = new Random();

    /** Color palette for jellyfish */
    private static final Color[] JELLYFISH_COLORS = {
            new Color(255, 100, 150, 180), // Pink
            new Color(150, 100, 255, 180), // Purple
            new Color(100, 200, 255, 180), // Cyan
            new Color(255, 200, 100, 180), // Orange
            new Color(100, 255, 200, 180), // Teal
    };

    /**
     * Creates a new jellyfish at the specified position.
     */
    public Jellyfish(double x, double y) {
        super(x, y);

        // Random visual properties
        this.bellRadius = 15 + random.nextDouble() * 25;
        this.tentacleLength = bellRadius * (2 + random.nextDouble());
        this.tentacleCount = 5 + random.nextInt(8);

        // Color selection
        this.baseColor = JELLYFISH_COLORS[random.nextInt(JELLYFISH_COLORS.length)];
        this.glowColor = new Color(
                Math.min(255, baseColor.getRed() + 50),
                Math.min(255, baseColor.getGreen() + 50),
                Math.min(255, baseColor.getBlue() + 50),
                200);

        // Movement
        this.pulsePhase = random.nextDouble() * Math.PI * 2;
        this.pulseSpeed = 0.03 + random.nextDouble() * 0.02;
        this.vx = (random.nextDouble() - 0.5) * 0.5;
        this.vy = (random.nextDouble() - 0.5) * 0.3;
    }

    @Override
    public void update(SimulationContext context) {
        // Update pulse animation
        pulsePhase += pulseSpeed;

        // Pulsating movement - jellyfish push down, then float up
        double pulsePower = Math.sin(pulsePhase);
        if (pulsePower > 0) {
            // Pulse - move down slightly
            vy += pulsePower * 0.02;
        } else {
            // Recovery - float up
            vy -= 0.01;
        }

        // Apply environmental current
        if (context.environmentalField() != null) {
            var current = context.environmentalField().sampleVector(posX, posY);
            vx += current.x * 0.15;
            vy += current.y * 0.15;
        }

        // Apply drag
        vx *= 0.98;
        vy *= 0.98;

        // Update position
        posX += vx;
        posY += vy;

        // Boundary wrapping (jellyfish can wrap around)
        if (posX < -bellRadius)
            posX = context.width() + bellRadius;
        if (posX > context.width() + bellRadius)
            posX = -bellRadius;
        if (posY < -bellRadius)
            posY = context.height() * 0.1; // Don't go above water
        if (posY > context.height() - bellRadius)
            vy = -Math.abs(vy) - 0.5; // Bounce off bottom
    }

    /**
     * Gets the current pulse value for rendering.
     * 
     * @return Pulse value between -1 and 1
     */
    public double getPulseValue() {
        return Math.sin(pulsePhase);
    }

    /**
     * Gets the bell radius.
     */
    public double getBellRadius() {
        return bellRadius;
    }

    /**
     * Gets the tentacle length.
     */
    public double getTentacleLength() {
        return tentacleLength;
    }

    /**
     * Gets the tentacle count.
     */
    public int getTentacleCount() {
        return tentacleCount;
    }

    /**
     * Gets the base color.
     */
    public Color getBaseColor() {
        return baseColor;
    }

    /**
     * Gets the glow color for night rendering.
     */
    public Color getGlowColor() {
        return glowColor;
    }

    /**
     * Gets the current velocity X.
     */
    public double getVelocityX() {
        return vx;
    }

    /**
     * Gets the current velocity Y.
     */
    public double getVelocityY() {
        return vy;
    }

    @Override
    public EntityType getType() {
        return EntityType.JELLYFISH;
    }
}
