package com.dtp5.renderer;

import com.dtp5.model.Jellyfish;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;

/**
 * Renders jellyfish with translucent bell and flowing tentacles.
 * Includes bioluminescent glow effect for nighttime.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class JellyfishRenderer {

        private static final BasicStroke THIN_STROKE = new BasicStroke(1.0f);

        /**
         * Renders a jellyfish.
         * 
         * @param jellyfish  The jellyfish to render
         * @param g2d        Graphics context
         * @param frameCount Current frame for animation
         * @param isNight    Whether it's nighttime (for glow effect)
         */
        public static void render(Jellyfish jellyfish, Graphics2D g2d, long frameCount, boolean isNight) {
                double x = jellyfish.getX();
                double y = jellyfish.getY();
                double bellRadius = jellyfish.getBellRadius();
                double pulse = jellyfish.getPulseValue();
                Color baseColor = jellyfish.getBaseColor();

                // Calculate bell shape based on pulse
                double bellHeight = bellRadius * (0.6 + pulse * 0.15);
                double bellWidth = bellRadius * (1.0 - pulse * 0.1);

                // Glow effect for night
                if (isNight) {
                        renderGlow(g2d, x, y, bellRadius * 2, jellyfish.getGlowColor());
                }

                // Save transform
                AffineTransform originalTransform = g2d.getTransform();
                g2d.translate(x, y);

                // Draw tentacles first (behind body)
                renderTentacles(g2d, jellyfish, frameCount, bellWidth, bellHeight);

                // Draw bell (body)
                renderBell(g2d, bellWidth, bellHeight, baseColor);

                // Draw internal organs (subtle)
                renderOrgans(g2d, bellWidth * 0.3, bellHeight * 0.3, baseColor);

                // Restore transform
                g2d.setTransform(originalTransform);
        }

        private static void renderGlow(Graphics2D g2d, double x, double y, double radius, Color glowColor) {
                // Multiple layers for soft glow
                for (int i = 4; i > 0; i--) {
                        double r = radius * (1 + i * 0.3);
                        int alpha = glowColor.getAlpha() / (i * 2);
                        Color layerColor = new Color(
                                        glowColor.getRed(),
                                        glowColor.getGreen(),
                                        glowColor.getBlue(),
                                        Math.max(10, alpha));
                        g2d.setColor(layerColor);
                        g2d.fill(new Ellipse2D.Double(x - r / 2, y - r / 2, r, r));
                }
        }

        private static void renderTentacles(Graphics2D g2d, Jellyfish jellyfish, long frameCount,
                        double bellWidth, double bellHeight) {
                int tentacleCount = jellyfish.getTentacleCount();
                double tentacleLength = jellyfish.getTentacleLength();
                Color baseColor = jellyfish.getBaseColor();

                // Semi-transparent tentacle color
                Color tentacleColor = new Color(
                                baseColor.getRed(),
                                baseColor.getGreen(),
                                baseColor.getBlue(),
                                80);

                g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                for (int i = 0; i < tentacleCount; i++) {
                        // Distribute across bottom of bell
                        double xOffset = (i - (tentacleCount - 1) / 2.0) * (bellWidth * 1.5 / tentacleCount);
                        double startY = bellHeight * 0.5;

                        // Animate tentacle with waves
                        double phase = frameCount * 0.05 + i * 0.7;
                        double wave1 = Math.sin(phase) * 15;
                        double wave2 = Math.sin(phase * 1.3 + 1) * 10;
                        double wave3 = Math.sin(phase * 0.7 + 2) * 8;

                        // Draw tentacle as curve
                        GeneralPath tentacle = new GeneralPath();
                        tentacle.moveTo(xOffset, startY);

                        double segment = tentacleLength / 3;
                        tentacle.quadTo(
                                        xOffset + wave1, startY + segment,
                                        xOffset + wave1 + wave2, startY + segment * 2);
                        tentacle.quadTo(
                                        xOffset + wave1 + wave2, startY + segment * 2,
                                        xOffset + wave2 + wave3, startY + tentacleLength);

                        // Gradient alpha along length
                        g2d.setColor(tentacleColor);
                        g2d.draw(tentacle);
                }
        }

        private static void renderBell(Graphics2D g2d, double width, double height, Color baseColor) {
                // Bell shape (dome)
                GeneralPath bell = new GeneralPath();
                bell.moveTo(-width, height * 0.3);

                // Curve to top
                bell.curveTo(
                                -width, -height * 0.5,
                                0, -height,
                                0, -height);
                bell.curveTo(
                                0, -height,
                                width, -height * 0.5,
                                width, height * 0.3);

                // Bottom edge (wavy)
                bell.curveTo(
                                width * 0.7, height * 0.5,
                                0, height * 0.3,
                                0, height * 0.5);
                bell.curveTo(
                                0, height * 0.3,
                                -width * 0.7, height * 0.5,
                                -width, height * 0.3);

                bell.closePath();

                // Gradient fill
                GradientPaint gradient = new GradientPaint(
                                0, (float) -height, brighter(baseColor, 1.3f),
                                0, (float) height, baseColor);
                g2d.setPaint(gradient);
                g2d.fill(bell);

                // Subtle outline
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.setStroke(THIN_STROKE);
                g2d.draw(bell);
        }

        private static void renderOrgans(Graphics2D g2d, double width, double height, Color baseColor) {
                // Central organs (slightly darker)
                Color organColor = new Color(
                                Math.max(0, baseColor.getRed() - 40),
                                Math.max(0, baseColor.getGreen() - 40),
                                Math.max(0, baseColor.getBlue() - 40),
                                120);

                g2d.setColor(organColor);
                g2d.fill(new Ellipse2D.Double(-width / 2, -height / 2, width, height));
        }

        private static Color brighter(Color c, float factor) {
                return new Color(
                                Math.min(255, (int) (c.getRed() * factor)),
                                Math.min(255, (int) (c.getGreen() * factor)),
                                Math.min(255, (int) (c.getBlue() * factor)),
                                c.getAlpha());
        }
}
