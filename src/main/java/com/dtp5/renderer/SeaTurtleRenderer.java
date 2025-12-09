package com.dtp5.renderer;

import com.dtp5.model.SeaTurtle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * Renders sea turtles with detailed shell patterns and flipper animation.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class SeaTurtleRenderer {

    private static final BasicStroke SHELL_STROKE = new BasicStroke(1.5f);

    /**
     * Renders a sea turtle.
     * 
     * @param turtle     The turtle to render
     * @param g2d        Graphics context
     * @param frameCount Current frame for animation
     */
    public static void render(SeaTurtle turtle, Graphics2D g2d, long frameCount) {
        double x = turtle.getX();
        double y = turtle.getY();
        double heading = turtle.getHeading();
        double shellSize = turtle.getShellSize();
        double flipperValue = turtle.getFlipperValue();

        // Save transform
        AffineTransform originalTransform = g2d.getTransform();

        // Translate and rotate to turtle position
        g2d.translate(x, y);
        g2d.rotate(heading);

        // Draw shadow
        drawShadow(g2d, shellSize);

        // Draw flippers (behind body)
        drawFlippersBack(g2d, turtle, flipperValue, shellSize);

        // Draw shell
        drawShell(g2d, turtle, shellSize);

        // Draw head
        drawHead(g2d, turtle, shellSize);

        // Draw flippers (front)
        drawFlipper(g2d, turtle, shellSize, flipperValue, true);
        drawFlipper(g2d, turtle, shellSize, flipperValue, false);

        // Restore transform
        g2d.setTransform(originalTransform);
    }

    private static void drawShadow(Graphics2D g2d, double size) {
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fill(new Ellipse2D.Double(-size * 0.4 + 3, -size * 0.3 + 3, size * 0.8, size * 0.6));
    }

    private static void drawFlippersBack(Graphics2D g2d, SeaTurtle turtle,
            double flipperValue, double size) {
        Color skinColor = turtle.getSkinColor();
        g2d.setColor(skinColor.darker());

        // Back flippers (smaller, more static)
        double backFlipperAngle = flipperValue * 0.2;

        // Left back flipper
        AffineTransform t = g2d.getTransform();
        g2d.translate(-size * 0.25, size * 0.2);
        g2d.rotate(Math.PI * 0.7 + backFlipperAngle);
        drawFlipperShape(g2d, size * 0.3, skinColor.darker());
        g2d.setTransform(t);

        // Right back flipper
        g2d.translate(-size * 0.25, -size * 0.2);
        g2d.rotate(-Math.PI * 0.7 - backFlipperAngle);
        drawFlipperShape(g2d, size * 0.3, skinColor.darker());
        g2d.setTransform(t);
    }

    private static void drawFlipper(Graphics2D g2d, SeaTurtle turtle, double size,
            double flipperValue, boolean top) {
        Color skinColor = turtle.getSkinColor();
        double flipperAngle = flipperValue * 0.5;

        AffineTransform t = g2d.getTransform();

        if (top) {
            g2d.translate(size * 0.2, -size * 0.25);
            g2d.rotate(-Math.PI / 4 - flipperAngle);
        } else {
            g2d.translate(size * 0.2, size * 0.25);
            g2d.rotate(Math.PI / 4 + flipperAngle);
        }

        drawFlipperShape(g2d, size * 0.5, skinColor);
        g2d.setTransform(t);
    }

    private static void drawFlipperShape(Graphics2D g2d, double length, Color color) {
        GeneralPath flipper = new GeneralPath();
        flipper.moveTo(0, 0);
        flipper.curveTo(
                length * 0.3, -length * 0.2,
                length * 0.7, -length * 0.15,
                length, 0);
        flipper.curveTo(
                length * 0.7, length * 0.1,
                length * 0.3, length * 0.15,
                0, 0);
        flipper.closePath();

        g2d.setColor(color);
        g2d.fill(flipper);
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.draw(flipper);
    }

    private static void drawShell(Graphics2D g2d, SeaTurtle turtle, double size) {
        Color shellColor = turtle.getShellColor();

        // Shell base (oval)
        double shellWidth = size * 0.8;
        double shellHeight = size * 0.6;

        // Gradient for 3D effect
        GradientPaint shellGradient = new GradientPaint(
                0, (float) -shellHeight / 2, shellColor.brighter(),
                0, (float) shellHeight / 2, shellColor.darker());

        Ellipse2D.Double shell = new Ellipse2D.Double(
                -shellWidth / 2, -shellHeight / 2, shellWidth, shellHeight);

        g2d.setPaint(shellGradient);
        g2d.fill(shell);

        // Shell pattern (scutes)
        drawShellPattern(g2d, shellWidth, shellHeight, shellColor);

        // Shell outline
        g2d.setColor(shellColor.darker().darker());
        g2d.setStroke(SHELL_STROKE);
        g2d.draw(shell);
    }

    private static void drawShellPattern(Graphics2D g2d, double width, double height, Color shellColor) {
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.setStroke(new BasicStroke(1.0f));

        // Central line
        g2d.drawLine(0, (int) (-height * 0.35), 0, (int) (height * 0.35));

        // Lateral lines
        g2d.drawLine((int) (-width * 0.25), (int) (-height * 0.3),
                (int) (-width * 0.25), (int) (height * 0.3));
        g2d.drawLine((int) (width * 0.25), (int) (-height * 0.3),
                (int) (width * 0.25), (int) (height * 0.3));

        // Cross lines
        for (int i = -2; i <= 2; i++) {
            double y = i * height * 0.12;
            g2d.drawLine((int) (-width * 0.35), (int) y, (int) (width * 0.35), (int) y);
        }
    }

    private static void drawHead(Graphics2D g2d, SeaTurtle turtle, double size) {
        Color skinColor = turtle.getSkinColor();

        // Head (oval extending from shell)
        double headLength = size * 0.35;
        double headWidth = size * 0.2;

        GeneralPath head = new GeneralPath();
        head.moveTo(size * 0.35, 0);
        head.curveTo(
                size * 0.35 + headLength * 0.5, -headWidth * 0.5,
                size * 0.35 + headLength, -headWidth * 0.3,
                size * 0.35 + headLength, 0);
        head.curveTo(
                size * 0.35 + headLength, headWidth * 0.3,
                size * 0.35 + headLength * 0.5, headWidth * 0.5,
                size * 0.35, 0);
        head.closePath();

        g2d.setColor(skinColor);
        g2d.fill(head);
        g2d.setColor(skinColor.darker());
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.draw(head);

        // Eye
        double eyeX = size * 0.35 + headLength * 0.6;
        double eyeY = -headWidth * 0.15;
        double eyeSize = size * 0.05;

        g2d.setColor(Color.BLACK);
        g2d.fill(new Ellipse2D.Double(eyeX - eyeSize / 2, eyeY - eyeSize / 2, eyeSize, eyeSize));

        // Eye highlight
        g2d.setColor(Color.WHITE);
        g2d.fill(new Ellipse2D.Double(eyeX - eyeSize / 4, eyeY - eyeSize / 3, eyeSize / 3, eyeSize / 3));
    }
}
