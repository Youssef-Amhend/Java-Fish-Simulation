package com.dtp5.particle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/**
 * Manages a pool of particles for efficient allocation and rendering.
 * Uses object pooling to avoid garbage collection during gameplay.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class ParticleSystem {

    /** Maximum number of particles */
    private static final int MAX_PARTICLES = 500;

    /** Particle pool */
    private final Particle[] particles;

    /** Random for particle generation */
    private final Random random;

    /** Index for round-robin allocation */
    private int nextParticleIndex = 0;

    /**
     * Creates a new particle system.
     */
    public ParticleSystem() {
        this.particles = new Particle[MAX_PARTICLES];
        this.random = new Random();

        // Pre-allocate all particles
        for (int i = 0; i < MAX_PARTICLES; i++) {
            particles[i] = new Particle();
        }
    }

    /**
     * Updates all active particles.
     */
    public void update() {
        for (Particle p : particles) {
            if (p.active) {
                p.update();
            }
        }
    }

    /**
     * Renders all active particles.
     */
    public void render(Graphics2D g2d) {
        for (Particle p : particles) {
            if (p.active) {
                p.render(g2d);
            }
        }
    }

    /**
     * Spawns a bubble at the given position.
     */
    public void spawnBubble(double x, double y) {
        Particle p = getNextParticle();
        double size = 3 + random.nextDouble() * 5;
        double vx = (random.nextDouble() - 0.5) * 0.5;
        double vy = -0.5 - random.nextDouble() * 1.5; // Rise up
        int lifetime = 120 + random.nextInt(180);

        Color color = new Color(200, 220, 255, 150);
        p.init(x, y, vx, vy, size, lifetime, color, Particle.ParticleType.BUBBLE);
    }

    /**
     * Spawns multiple bubbles (burst effect).
     */
    public void spawnBubbleBurst(double x, double y, int count) {
        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 20;
            double offsetY = (random.nextDouble() - 0.5) * 10;
            spawnBubble(x + offsetX, y + offsetY);
        }
    }

    /**
     * Spawns debris particles (floating detritus).
     */
    public void spawnDebris(double x, double y) {
        Particle p = getNextParticle();
        double size = 1 + random.nextDouble() * 2;
        double vx = (random.nextDouble() - 0.5) * 0.3;
        double vy = (random.nextDouble() - 0.5) * 0.3;
        int lifetime = 300 + random.nextInt(300);

        // Brownish debris
        int r = 80 + random.nextInt(40);
        int g = 60 + random.nextInt(30);
        int b = 40 + random.nextInt(20);
        Color color = new Color(r, g, b, 100);

        p.init(x, y, vx, vy, size, lifetime, color, Particle.ParticleType.DEBRIS);
    }

    /**
     * Spawns splash particles.
     */
    public void spawnSplash(double x, double y, int count) {
        for (int i = 0; i < count; i++) {
            Particle p = getNextParticle();
            double size = 2 + random.nextDouble() * 3;
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 1 + random.nextDouble() * 3;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 2; // Initial upward burst
            int lifetime = 30 + random.nextInt(30);

            Color color = new Color(200, 220, 255, 200);
            p.init(x, y, vx, vy, size, lifetime, color, Particle.ParticleType.SPLASH);
        }
    }

    /**
     * Spawns a glow particle (for bioluminescence).
     */
    public void spawnGlow(double x, double y, Color glowColor) {
        Particle p = getNextParticle();
        double size = 5 + random.nextDouble() * 10;
        int lifetime = 60 + random.nextInt(60);

        Color color = new Color(glowColor.getRed(), glowColor.getGreen(),
                glowColor.getBlue(), 100);
        p.init(x, y, 0, 0, size, lifetime, color, Particle.ParticleType.GLOW);
    }

    /**
     * Spawns sparkle particles.
     */
    public void spawnSparkles(double x, double y, int count) {
        for (int i = 0; i < count; i++) {
            Particle p = getNextParticle();
            double offsetX = (random.nextDouble() - 0.5) * 30;
            double offsetY = (random.nextDouble() - 0.5) * 30;
            double size = 2 + random.nextDouble() * 4;
            int lifetime = 20 + random.nextInt(40);

            Color color = new Color(255, 255, 200, 200);
            p.init(x + offsetX, y + offsetY, 0, 0, size, lifetime, color,
                    Particle.ParticleType.SPARKLE);
        }
    }

    /**
     * Gets the next available particle (round-robin allocation).
     */
    private Particle getNextParticle() {
        // First try to find an inactive particle
        for (int i = 0; i < MAX_PARTICLES; i++) {
            int idx = (nextParticleIndex + i) % MAX_PARTICLES;
            if (!particles[idx].active) {
                nextParticleIndex = (idx + 1) % MAX_PARTICLES;
                return particles[idx];
            }
        }

        // If all active, reuse the oldest (round-robin)
        Particle p = particles[nextParticleIndex];
        nextParticleIndex = (nextParticleIndex + 1) % MAX_PARTICLES;
        return p;
    }

    /**
     * Clears all particles.
     */
    public void clear() {
        for (Particle p : particles) {
            p.active = false;
        }
    }

    /**
     * Gets the count of active particles.
     */
    public int getActiveCount() {
        int count = 0;
        for (Particle p : particles) {
            if (p.active)
                count++;
        }
        return count;
    }
}
