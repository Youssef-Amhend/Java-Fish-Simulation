package com.dtp5.model;

import java.awt.Color;
import java.util.Random;

/**
 * Represents a sea turtle - a rare, majestic creature that moves gracefully.
 * Sea turtles eat jellyfish and algae, and are much larger than fish.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class SeaTurtle extends Entity implements Updatable {

    /** Velocity components */
    private double vx, vy;

    /** Current heading angle */
    private double heading;

    /** Flipper animation phase */
    private double flipperPhase;

    /** Base movement speed */
    private final double speed;

    /** Shell size */
    private final double shellSize;

    /** Shell color */
    private final Color shellColor;

    /** Skin color */
    private final Color skinColor;

    /** Target position for wandering */
    private double targetX, targetY;

    /** Time until next target change */
    private int wanderTimer;

    /** Random for behavior */
    private static final Random random = new Random();

    /** Shell color palette */
    private static final Color[] SHELL_COLORS = {
            new Color(85, 107, 47), // Olive green
            new Color(139, 90, 43), // Brown
            new Color(107, 142, 35), // Olive drab
            new Color(160, 82, 45), // Sienna
    };

    /**
     * Creates a new sea turtle at the specified position.
     */
    public SeaTurtle(double x, double y) {
        super(x, y);

        // Size and speed
        this.shellSize = 40 + random.nextDouble() * 20;
        this.speed = 1.0 + random.nextDouble() * 0.5;

        // Colors
        this.shellColor = SHELL_COLORS[random.nextInt(SHELL_COLORS.length)];
        this.skinColor = new Color(
                70 + random.nextInt(30),
                120 + random.nextInt(30),
                70 + random.nextInt(30));

        // Initial movement
        this.heading = random.nextDouble() * Math.PI * 2;
        this.vx = Math.cos(heading) * speed;
        this.vy = Math.sin(heading) * speed;

        // Animation
        this.flipperPhase = random.nextDouble() * Math.PI * 2;

        // Wandering
        this.targetX = x;
        this.targetY = y;
        this.wanderTimer = 0;
    }

    @Override
    public void update(SimulationContext context) {
        // Update flipper animation
        flipperPhase += 0.08;

        // Wandering behavior
        wanderTimer--;
        if (wanderTimer <= 0 || distanceSquaredTo(targetX, targetY) < 2500) {
            // Pick new target
            targetX = 100 + random.nextDouble() * (context.width() - 200);
            targetY = 100 + random.nextDouble() * (context.height() - 200);
            wanderTimer = 200 + random.nextInt(300);
        }

        // Steer towards target
        double dx = targetX - posX;
        double dy = targetY - posY;
        double targetAngle = Math.atan2(dy, dx);

        // Smooth turning
        double angleDiff = targetAngle - heading;
        while (angleDiff > Math.PI)
            angleDiff -= 2 * Math.PI;
        while (angleDiff < -Math.PI)
            angleDiff += 2 * Math.PI;
        heading += angleDiff * 0.02;

        // Apply velocity
        vx = Math.cos(heading) * speed;
        vy = Math.sin(heading) * speed;

        // Apply environmental current (turtles fight it somewhat)
        if (context.environmentalField() != null) {
            var current = context.environmentalField().sampleVector(posX, posY);
            vx += current.x * 0.1;
            vy += current.y * 0.1;
        }

        // Update position
        posX += vx;
        posY += vy;

        // Boundary avoidance
        double margin = shellSize * 2;
        if (posX < margin) {
            posX = margin;
            targetX = context.width() / 2;
        }
        if (posX > context.width() - margin) {
            posX = context.width() - margin;
            targetX = context.width() / 2;
        }
        if (posY < margin) {
            posY = margin;
            targetY = context.height() / 2;
        }
        if (posY > context.height() - margin) {
            posY = context.height() - margin;
            targetY = context.height() / 2;
        }
    }

    /**
     * Gets the shell size.
     */
    public double getShellSize() {
        return shellSize;
    }

    /**
     * Gets the shell color.
     */
    public Color getShellColor() {
        return shellColor;
    }

    /**
     * Gets the skin color.
     */
    public Color getSkinColor() {
        return skinColor;
    }

    /**
     * Gets the current heading angle.
     */
    public double getHeading() {
        return heading;
    }

    /**
     * Gets the flipper animation value.
     * 
     * @return Value between -1 and 1
     */
    public double getFlipperValue() {
        return Math.sin(flipperPhase);
    }

    @Override
    public EntityType getType() {
        return EntityType.SEA_TURTLE;
    }
}
