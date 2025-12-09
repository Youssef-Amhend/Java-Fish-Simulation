package com.dtp5.particle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 * Lightweight particle for visual effects.
 * Designed for high-performance rendering of many particles.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class Particle {

    /** Position */
    public double x, y;

    /** Velocity */
    public double vx, vy;

    /** Size of the particle */
    public double size;

    /** Remaining lifetime in ticks */
    public int lifetime;

    /** Maximum lifetime (for alpha calculation) */
    public int maxLifetime;

    /** Particle color */
    public Color color;

    /** Particle type */
    public ParticleType type;

    /** Whether this particle is active */
    public boolean active = false;

    /** Phase for wobble effects */
    public double phase;

    /**
     * Particle types for different visual effects.
     */
    public enum ParticleType {
        BUBBLE,
        DEBRIS,
        SPLASH,
        GLOW,
        SPARKLE
    }

    /**
     * Creates an inactive particle (for pooling).
     */
    public Particle() {
        this.active = false;
    }

    /**
     * Initializes this particle with the given parameters.
     */
    public void init(double x, double y, double vx, double vy,
            double size, int lifetime, Color color, ParticleType type) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.color = color;
        this.type = type;
        this.phase = Math.random() * Math.PI * 2;
        this.active = true;
    }

    /**
     * Updates this particle.
     */
    public void update() {
        if (!active)
            return;

        // Update position
        x += vx;
        y += vy;

        // Type-specific behavior
        switch (type) {
            case BUBBLE:
                // Bubbles wobble side to side and slow down as they rise
                phase += 0.15;
                vx = Math.sin(phase) * 0.3;
                vy *= 0.998; // Slight drag
                size *= 1.002; // Grow slightly
                break;

            case DEBRIS:
                // Debris drifts slowly
                vy *= 0.97;
                vx *= 0.97;
                break;

            case SPLASH:
                // Splash particles fall with gravity
                vy += 0.1;
                break;

            case GLOW:
                // Glow particles pulse
                phase += 0.1;
                break;

            case SPARKLE:
                // Sparkles fade quickly
                break;
        }

        // Decrease lifetime
        lifetime--;
        if (lifetime <= 0) {
            active = false;
        }
    }

    /**
     * Renders this particle.
     */
    public void render(Graphics2D g2d) {
        if (!active)
            return;

        // Calculate alpha based on remaining lifetime
        float lifeRatio = (float) lifetime / maxLifetime;
        int alpha = (int) (lifeRatio * color.getAlpha());

        Color renderColor = new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                Math.max(0, Math.min(255, alpha)));

        switch (type) {
            case BUBBLE:
                renderBubble(g2d, renderColor);
                break;

            case DEBRIS:
                renderDebris(g2d, renderColor);
                break;

            case SPLASH:
                renderSplash(g2d, renderColor);
                break;

            case GLOW:
                renderGlow(g2d, renderColor, lifeRatio);
                break;

            case SPARKLE:
                renderSparkle(g2d, renderColor, lifeRatio);
                break;
        }
    }

    private void renderBubble(Graphics2D g2d, Color color) {
        double s = size;

        // Bubble body
        g2d.setColor(color);
        g2d.draw(new Ellipse2D.Double(x - s / 2, y - s / 2, s, s));

        // Highlight
        Color highlight = new Color(255, 255, 255, color.getAlpha() / 2);
        g2d.setColor(highlight);
        g2d.fill(new Ellipse2D.Double(x - s / 4, y - s / 3, s / 3, s / 4));
    }

    private void renderDebris(Graphics2D g2d, Color color) {
        g2d.setColor(color);
        g2d.fillRect((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
    }

    private void renderSplash(Graphics2D g2d, Color color) {
        g2d.setColor(color);
        g2d.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
    }

    private void renderGlow(Graphics2D g2d, Color color, float lifeRatio) {
        // Multiple layers for glow effect
        for (int i = 3; i > 0; i--) {
            double s = size * (1 + i * 0.5);
            int a = color.getAlpha() / (i + 1);
            Color glowColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
            g2d.setColor(glowColor);
            g2d.fill(new Ellipse2D.Double(x - s / 2, y - s / 2, s, s));
        }
    }

    private void renderSparkle(Graphics2D g2d, Color color, float lifeRatio) {
        double pulse = 0.5 + 0.5 * Math.sin(phase * 5);
        double s = size * pulse;

        g2d.setColor(color);
        // Draw cross shape
        g2d.drawLine((int) (x - s), (int) y, (int) (x + s), (int) y);
        g2d.drawLine((int) x, (int) (y - s), (int) x, (int) (y + s));
    }
}
