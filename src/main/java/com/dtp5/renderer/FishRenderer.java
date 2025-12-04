package com.dtp5.renderer;

import com.dtp5.model.Poisson;

import java.awt.*;
import java.awt.geom.*;

/**
 * Handles all fish rendering with beautiful visual effects.
 * Now supports different species with unique appearances!
 */
public class FishRenderer {

    private static final BasicStroke FISH_STROKE = new BasicStroke(1.5f);
    private static final BasicStroke TRAIL_STROKE = new BasicStroke(1.0f);

    /**
     * Renders a fish with all visual effects based on its species.
     */
    public static void render(Poisson fish, Graphics2D g2d, long frameCount) {
        double x = fish.posX;
        double y = fish.posY;
        double angle = Math.atan2(fish.vitesseY, fish.vitesseX);

        // Save original transform
        AffineTransform originalTransform = g2d.getTransform();

        // Draw trail effect first (behind fish)
        drawTrail(fish, g2d);

        // Translate and rotate for fish
        g2d.translate(x, y);
        g2d.rotate(angle);

        // Draw shadow
        drawShadow(g2d, fish);

        // Draw fish body (species-specific size)
        drawFishBody(fish, g2d, frameCount);

        // Restore transform
        g2d.setTransform(originalTransform);
    }

    /**
     * Draws the trail effect behind the fish.
     */
    private static void drawTrail(Poisson fish, Graphics2D g2d) {
        if (fish.trail.isEmpty())
            return;

        g2d.setStroke(TRAIL_STROKE);

        for (int i = 0; i < fish.trail.size() - 1; i++) {
            Point2D.Double p1 = fish.trail.get(i);
            Point2D.Double p2 = fish.trail.get(i + 1);

            // Fade trail based on position
            float alpha = 0.3f * (1.0f - (float) i / fish.trail.size());
            Color trailColor = new Color(
                    fish.color.getRed(),
                    fish.color.getGreen(),
                    fish.color.getBlue(),
                    (int) (alpha * 255));

            g2d.setColor(trailColor);
            g2d.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
        }
    }

    /**
     * Draws a subtle shadow beneath the fish.
     */
    private static void drawShadow(Graphics2D g2d, Poisson fish) {
        int bodyLength = fish.species.bodyLength;
        int bodyWidth = fish.species.bodyWidth;

        g2d.setColor(new Color(0, 0, 0, 30));
        Ellipse2D.Double shadow = new Ellipse2D.Double(
                -bodyLength / 2.0 + 2,
                bodyWidth / 2.0 + 1,
                bodyLength,
                bodyWidth / 2.0);
        g2d.fill(shadow);
    }

    /**
     * Draws the detailed fish body with gradient and animated tail.
     * Size is based on species.
     */
    private static void drawFishBody(Poisson fish, Graphics2D g2d, long frameCount) {
        int bodyLength = fish.species.bodyLength;
        int bodyWidth = fish.species.bodyWidth;
        int tailLength = fish.species.tailLength;
        int tailWidth = bodyWidth;

        // Calculate tail animation - faster for faster fish
        double tailWave = Math.sin(frameCount / (10.0 / fish.species.speed * 3)) * 3;

        // Create fish body shape (pointed front, wider middle)
        GeneralPath body = new GeneralPath();
        body.moveTo(bodyLength / 2.0, 0); // Nose
        body.curveTo(
                bodyLength / 4.0, -bodyWidth / 2.0,
                -bodyLength / 4.0, -bodyWidth / 2.0,
                -bodyLength / 2.0, 0);
        body.curveTo(
                -bodyLength / 4.0, bodyWidth / 2.0,
                bodyLength / 4.0, bodyWidth / 2.0,
                bodyLength / 2.0, 0);
        body.closePath();

        // Create gradient for body
        Point2D start = new Point2D.Double(-bodyLength / 2.0, 0);
        Point2D end = new Point2D.Double(bodyLength / 2.0, 0);

        Color darkerColor = darkenColor(fish.color, 0.6f);
        LinearGradientPaint gradient = new LinearGradientPaint(
                start, end,
                new float[] { 0.0f, 1.0f },
                new Color[] { darkerColor, fish.color });

        // Fill body with gradient
        g2d.setPaint(gradient);
        g2d.fill(body);

        // Draw body outline
        g2d.setColor(darkerColor);
        g2d.setStroke(FISH_STROKE);
        g2d.draw(body);

        // Draw animated tail
        GeneralPath tail = new GeneralPath();
        tail.moveTo(-bodyLength / 2.0, -2);
        tail.lineTo(-bodyLength / 2.0 - tailLength,
                -tailWidth + tailWave);
        tail.lineTo(-bodyLength / 2.0, 0);
        tail.lineTo(-bodyLength / 2.0 - tailLength,
                tailWidth + tailWave);
        tail.lineTo(-bodyLength / 2.0, 2);
        tail.closePath();

        g2d.setPaint(gradient);
        g2d.fill(tail);
        g2d.setColor(darkerColor);
        g2d.draw(tail);

        // Add eye
        g2d.setColor(Color.BLACK);
        int eyeSize = Math.max(2, bodyLength / 6);
        g2d.fillOval(
                bodyLength / 4 - eyeSize / 2,
                -eyeSize / 2,
                eyeSize, eyeSize);
    }

    /**
     * Darkens a color by a factor.
     */
    private static Color darkenColor(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }
}
