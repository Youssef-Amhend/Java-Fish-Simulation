package com.dtp5.renderer;

import com.dtp5.config.SimulationConfig;
import com.dtp5.model.ZoneAEviter;

import java.awt.*;
import java.awt.geom.*;

/**
 * Handles all obstacle rendering with ripple and pulsing effects.
 */
public class ObstacleRenderer {

    private static final BasicStroke RIPPLE_STROKE = new BasicStroke(2.0f);
    private static final BasicStroke OBSTACLE_STROKE = new BasicStroke(3.0f);

    /**
     * Renders an obstacle with ripple effects and pulsing animation.
     */
    public static void render(ZoneAEviter obstacle, Graphics2D g2d) {
        double x = obstacle.posX;
        double y = obstacle.posY;
        double radius = obstacle.rayon;

        // Calculate fade based on remaining lifetime
        float lifetimeRatio = obstacle.tempsRestant / (float) SimulationConfig.OBSTACLE_LIFETIME;
        float alpha = SimulationConfig.OBSTACLE_ALPHA_START * lifetimeRatio;

        // Pulsing effect
        double pulse = 1.0 + 0.1 * Math.sin(obstacle.tempsRestant / 10.0);
        double pulsedRadius = radius * pulse;

        // Draw ripples
        drawRipples(x, y, radius, lifetimeRatio, g2d);

        // Draw main obstacle circle with gradient
        drawObstacleCircle(x, y, pulsedRadius, alpha, g2d);
    }

    /**
     * Draws expanding ripple circles around the obstacle.
     */
    private static void drawRipples(double x, double y, double radius, float lifetimeRatio, Graphics2D g2d) {
        g2d.setStroke(RIPPLE_STROKE);

        for (int i = 0; i < SimulationConfig.RIPPLE_COUNT; i++) {
            // Calculate ripple expansion
            double expansionPhase = (System.currentTimeMillis() / 1000.0 + i * 0.3) % 1.0;
            double rippleRadius = radius + expansionPhase * radius * 2;

            // Fade ripple based on expansion
            float rippleAlpha = (1.0f - (float) expansionPhase) * 0.3f * lifetimeRatio;

            Color rippleColor = new Color(
                    SimulationConfig.OBSTACLE_COLOR.getRed(),
                    SimulationConfig.OBSTACLE_COLOR.getGreen(),
                    SimulationConfig.OBSTACLE_COLOR.getBlue(),
                    (int) (rippleAlpha * 255));

            g2d.setColor(rippleColor);
            Ellipse2D.Double ripple = new Ellipse2D.Double(
                    x - rippleRadius,
                    y - rippleRadius,
                    rippleRadius * 2,
                    rippleRadius * 2);
            g2d.draw(ripple);
        }
    }

    /**
     * Draws the main obstacle circle with radial gradient.
     */
    private static void drawObstacleCircle(double x, double y, double radius, float alpha, Graphics2D g2d) {
        // Create radial gradient from center
        Point2D center = new Point2D.Double(x, y);
        float[] fractions = { 0.0f, 0.7f, 1.0f };

        Color centerColor = new Color(
                255, 255, 255,
                (int) (alpha * 200));
        Color midColor = new Color(
                SimulationConfig.OBSTACLE_COLOR.getRed(),
                SimulationConfig.OBSTACLE_COLOR.getGreen(),
                SimulationConfig.OBSTACLE_COLOR.getBlue(),
                (int) (alpha * 255));
        Color edgeColor = new Color(
                SimulationConfig.OBSTACLE_COLOR.getRed(),
                SimulationConfig.OBSTACLE_COLOR.getGreen(),
                SimulationConfig.OBSTACLE_COLOR.getBlue(),
                (int) (alpha * 150));

        RadialGradientPaint gradient = new RadialGradientPaint(
                center,
                (float) radius,
                fractions,
                new Color[] { centerColor, midColor, edgeColor });

        // Fill obstacle
        g2d.setPaint(gradient);
        Ellipse2D.Double obstacle = new Ellipse2D.Double(
                x - radius,
                y - radius,
                radius * 2,
                radius * 2);
        g2d.fill(obstacle);

        // Draw outline
        g2d.setStroke(OBSTACLE_STROKE);
        Color outlineColor = new Color(
                SimulationConfig.OBSTACLE_COLOR.getRed(),
                SimulationConfig.OBSTACLE_COLOR.getGreen(),
                SimulationConfig.OBSTACLE_COLOR.getBlue(),
                (int) (alpha * 255));
        g2d.setColor(outlineColor);
        g2d.draw(obstacle);
    }
}
