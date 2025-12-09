package com.dtp5.renderer;

import com.dtp5.model.Coral;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Renders colorful coral formations with various shapes and patterns.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class CoralRenderer {

    private static final Random renderRandom = new Random();

    /**
     * Renders a coral formation.
     * 
     * @param coral      The coral to render
     * @param g2d        Graphics context
     * @param frameCount Current frame for animation
     */
    public static void render(Coral coral, Graphics2D g2d, long frameCount) {
        double x = coral.posX;
        double y = coral.posY;
        double width = coral.getBaseWidth();
        double height = coral.getBaseHeight();
        double sway = coral.getSwayValue();

        // Save transform
        AffineTransform originalTransform = g2d.getTransform();

        // Apply position and sway
        g2d.translate(x, y);
        g2d.rotate(coral.rotation + sway);

        // Render based on type
        switch (coral.type) {
            case BRAIN -> renderBrainCoral(g2d, width, height, coral);
            case BRANCHING -> renderBranchingCoral(g2d, width, height, coral);
            case TABLE -> renderTableCoral(g2d, width, height, coral);
            case TUBE -> renderTubeCoral(g2d, width, height, coral);
            case FAN -> renderFanCoral(g2d, width, height, coral);
            case STAGHORN -> renderStaghornCoral(g2d, width, height, coral);
            case MUSHROOM -> renderMushroomCoral(g2d, width, height, coral);
        }

        // Restore transform
        g2d.setTransform(originalTransform);
    }

    private static void renderBrainCoral(Graphics2D g2d, double width, double height, Coral coral) {
        // Round, bumpy shape
        Color primary = coral.primaryColor;
        Color secondary = coral.secondaryColor;

        // Base dome
        Ellipse2D.Double dome = new Ellipse2D.Double(-width / 2, -height, width, height);

        GradientPaint gradient = new GradientPaint(
                0, (float) -height, primary.brighter(),
                0, 0, primary.darker());
        g2d.setPaint(gradient);
        g2d.fill(dome);

        // Brain-like grooves
        g2d.setColor(secondary.darker());
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 0; i < 5; i++) {
            double offsetY = -height * 0.8 + i * height * 0.15;
            double waveWidth = width * (0.3 + i * 0.1);
            GeneralPath groove = new GeneralPath();
            groove.moveTo(-waveWidth / 2, offsetY);
            for (double t = 0; t <= 1; t += 0.1) {
                double gx = -waveWidth / 2 + t * waveWidth;
                double gy = offsetY + Math.sin(t * Math.PI * 3) * 3;
                groove.lineTo(gx, gy);
            }
            g2d.draw(groove);
        }
    }

    private static void renderBranchingCoral(Graphics2D g2d, double width, double height, Coral coral) {
        Color primary = coral.primaryColor;

        g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Draw branches recursively
        drawBranch(g2d, 0, 0, -Math.PI / 2, height * 0.6, 3, primary, 3);
    }

    private static void drawBranch(Graphics2D g2d, double x, double y, double angle,
            double length, double thickness, Color color, int depth) {
        if (depth <= 0 || length < 5)
            return;

        double endX = x + Math.cos(angle) * length;
        double endY = y + Math.sin(angle) * length;

        // Gradient along branch
        g2d.setColor(depth > 2 ? color : color.brighter());
        g2d.setStroke(new BasicStroke((float) thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int) x, (int) y, (int) endX, (int) endY);

        // Sub-branches
        double spread = 0.4 + renderRandom.nextDouble() * 0.3;
        drawBranch(g2d, endX, endY, angle - spread, length * 0.7, thickness * 0.7, color, depth - 1);
        drawBranch(g2d, endX, endY, angle + spread, length * 0.7, thickness * 0.7, color, depth - 1);
    }

    private static void renderTableCoral(Graphics2D g2d, double width, double height, Coral coral) {
        Color primary = coral.primaryColor;

        // Stem
        g2d.setColor(primary.darker());
        g2d.fillRect((int) (-width * 0.1), (int) (-height * 0.3), (int) (width * 0.2), (int) (height * 0.3));

        // Table top (ellipse)
        GradientPaint gradient = new GradientPaint(
                0f, (float) -height, primary.brighter(),
                0f, (float) (-height * 0.5), primary);
        g2d.setPaint(gradient);
        g2d.fill(new Ellipse2D.Double(-width / 2, -height, width, height * 0.4));

        // Edge detail
        g2d.setColor(primary.darker());
        g2d.setStroke(new BasicStroke(2f));
        g2d.draw(new Ellipse2D.Double(-width / 2, -height, width, height * 0.4));
    }

    private static void renderTubeCoral(Graphics2D g2d, double width, double height, Coral coral) {
        Color primary = coral.primaryColor;

        // Multiple tubes
        int tubeCount = 3 + renderRandom.nextInt(3);
        for (int i = 0; i < tubeCount; i++) {
            double offsetX = (i - tubeCount / 2.0) * width * 0.3;
            double tubeHeight = height * (0.6 + renderRandom.nextDouble() * 0.4);
            double tubeWidth = width * 0.25;

            // Tube body
            GradientPaint gradient = new GradientPaint(
                    (float) offsetX - (float) tubeWidth / 2, 0, primary,
                    (float) offsetX + (float) tubeWidth / 2, 0, primary.darker());
            g2d.setPaint(gradient);
            g2d.fillRect((int) (offsetX - tubeWidth / 2), (int) (-tubeHeight),
                    (int) tubeWidth, (int) tubeHeight);

            // Tube opening
            g2d.setColor(primary.darker().darker());
            g2d.fill(new Ellipse2D.Double(offsetX - tubeWidth / 2, -tubeHeight - tubeWidth * 0.3,
                    tubeWidth, tubeWidth * 0.6));
        }
    }

    private static void renderFanCoral(Graphics2D g2d, double width, double height, Coral coral) {
        Color primary = coral.primaryColor;
        Color secondary = coral.secondaryColor;

        // Fan shape
        GeneralPath fan = new GeneralPath();
        fan.moveTo(0, 0);
        fan.curveTo(-width * 0.3, -height * 0.5, -width * 0.5, -height, 0, -height);
        fan.curveTo(width * 0.5, -height, width * 0.3, -height * 0.5, 0, 0);
        fan.closePath();

        // Gradient fill
        GradientPaint gradient = new GradientPaint(
                0, 0, primary,
                0, (float) -height, secondary);
        g2d.setPaint(gradient);
        g2d.fill(fan);

        // Veins
        g2d.setColor(secondary.darker());
        g2d.setStroke(new BasicStroke(0.5f));
        for (int i = 0; i < 7; i++) {
            double angle = -Math.PI / 2 + (i - 3) * 0.2;
            double len = height * 0.85;
            g2d.drawLine(0, 0, (int) (Math.cos(angle) * len * 0.5), (int) (Math.sin(angle) * len));
        }
    }

    private static void renderStaghornCoral(Graphics2D g2d, double width, double height, Coral coral) {
        Color primary = coral.primaryColor;

        g2d.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Multiple antler-like branches
        int branchCount = 3 + renderRandom.nextInt(2);
        for (int i = 0; i < branchCount; i++) {
            double angle = -Math.PI / 2 + (i - branchCount / 2.0) * 0.4;
            double len = height * (0.8 + renderRandom.nextDouble() * 0.2);

            g2d.setColor(primary);
            double x1 = 0;
            double y1 = 0;
            double x2 = Math.cos(angle) * len;
            double y2 = Math.sin(angle) * len;
            g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

            // Sub-branches
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(primary.brighter());
            double subLen = len * 0.4;
            g2d.drawLine((int) x2, (int) y2,
                    (int) (x2 + Math.cos(angle - 0.4) * subLen),
                    (int) (y2 + Math.sin(angle - 0.4) * subLen));
            g2d.drawLine((int) x2, (int) y2,
                    (int) (x2 + Math.cos(angle + 0.4) * subLen),
                    (int) (y2 + Math.sin(angle + 0.4) * subLen));

            g2d.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
    }

    private static void renderMushroomCoral(Graphics2D g2d, double width, double height, Coral coral) {
        Color primary = coral.primaryColor;

        // Stem
        g2d.setColor(primary.darker());
        g2d.fillRect((int) (-width * 0.15), (int) (-height * 0.4), (int) (width * 0.3), (int) (height * 0.4));

        // Cap
        GeneralPath cap = new GeneralPath();
        cap.moveTo(-width / 2, -height * 0.4);
        cap.curveTo(-width / 2, -height, width / 2, -height, width / 2, -height * 0.4);
        cap.curveTo(width * 0.3, -height * 0.3, -width * 0.3, -height * 0.3, -width / 2, -height * 0.4);
        cap.closePath();

        GradientPaint gradient = new GradientPaint(
                0f, (float) -height, primary.brighter(),
                0f, (float) (-height * 0.4), primary);
        g2d.setPaint(gradient);
        g2d.fill(cap);

        // Gills
        g2d.setColor(primary.darker());
        g2d.setStroke(new BasicStroke(1f));
        for (int i = -4; i <= 4; i++) {
            double x = i * width * 0.08;
            g2d.drawLine((int) x, (int) (-height * 0.35), (int) x, (int) (-height * 0.45));
        }
    }
}
