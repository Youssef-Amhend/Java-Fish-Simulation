package com.dtp5.model;

import com.dtp5.config.SimulationConfig;

import java.awt.geom.Point2D;

/**
 * Lightweight procedural field that simulates underwater currents and a
 * temperature gradient.
 * Values are cached per cell and evolve smoothly over time.
 */
public class EnvironmentalField {
    private final int cellsX;
    private final int cellsY;
    private final double cellSizeX;
    private final double cellSizeY;
    private final VectorCell[][] field;
    private double time = 0.0;
    private final java.util.Random random = new java.util.Random();
    private final double gyreOffsetX = random.nextDouble();
    private final double gyreOffsetY = random.nextDouble();
    
    // Adjustable parameters
    private double currentStrength = 1.0; // Multiplier for current strength (0.0 to 2.0)
    private double animationSpeed = 1.0; // Multiplier for animation speed (0.0 to 3.0)
    private CurrentPattern pattern = CurrentPattern.SWIRL;
    
    public enum CurrentPattern {
        CALM,      // Very gentle currents
        SWIRL,     // Default swirling gyres
        STRONG,    // Strong currents
        WHIRLPOOL, // Intense circular motion
        HORIZONTAL,// Left-right flow
        VERTICAL   // Up-down flow
    }

    public EnvironmentalField(double width, double height) {
        this.cellsX = SimulationConfig.ENV_FIELD_CELLS;
        this.cellsY = SimulationConfig.ENV_FIELD_CELLS;
        this.cellSizeX = width / cellsX;
        this.cellSizeY = height / cellsY;
        this.field = new VectorCell[cellsX][cellsY];
        for (int x = 0; x < cellsX; x++) {
            for (int y = 0; y < cellsY; y++) {
                field[x][y] = new VectorCell();
            }
        }
    }

    public void tick(double deltaTime) {
        time += deltaTime * SimulationConfig.ENV_FIELD_TIME_SCALE * animationSpeed;
        for (int x = 0; x < cellsX; x++) {
            for (int y = 0; y < cellsY; y++) {
                double nx = (double) x / cellsX;
                double ny = (double) y / cellsY;
                
                double vx, vy;
                
                // Generate current based on pattern
                switch (pattern) {
                    case CALM:
                        vx = 0.1 * Math.sin(time * 0.2 + nx * 3);
                        vy = 0.1 * Math.cos(time * 0.2 + ny * 3);
                        break;
                    case SWIRL:
                        // Swirling gyres for aquarium feel (curl-like)
                        double cx = nx - 0.5 + Math.sin(time * 0.4 + gyreOffsetX) * 0.15;
                        double cy = ny - 0.5 + Math.cos(time * 0.35 + gyreOffsetY) * 0.15;
                        double dist = Math.sqrt(cx * cx + cy * cy) + 1e-4;
                        double swirl = 0.35 + 0.25 * Math.sin(time * 0.6 + nx * 5 + ny * 4);
                        vx = (-cy / dist) * swirl;
                        vy = (cx / dist) * swirl;
                        double drift = 0.25 * Math.sin(time * 0.9 + ny * 6.0);
                        vx += drift;
                        break;
                    case STRONG:
                        cx = nx - 0.5 + Math.sin(time * 0.6 + gyreOffsetX) * 0.2;
                        cy = ny - 0.5 + Math.cos(time * 0.55 + gyreOffsetY) * 0.2;
                        dist = Math.sqrt(cx * cx + cy * cy) + 1e-4;
                        swirl = 0.6 + 0.4 * Math.sin(time * 0.8 + nx * 6 + ny * 5);
                        vx = (-cy / dist) * swirl;
                        vy = (cx / dist) * swirl;
                        drift = 0.5 * Math.sin(time * 1.2 + ny * 8.0);
                        vx += drift;
                        break;
                    case WHIRLPOOL:
                        cx = nx - 0.5;
                        cy = ny - 0.5;
                        dist = Math.sqrt(cx * cx + cy * cy) + 1e-4;
                        swirl = 0.8 + 0.3 * Math.sin(time * 1.0);
                        vx = (-cy / dist) * swirl;
                        vy = (cx / dist) * swirl;
                        break;
                    case HORIZONTAL:
                        vx = 0.5 * Math.sin(time * 0.5 + ny * 4);
                        vy = 0.1 * Math.cos(time * 0.3 + nx * 2);
                        break;
                    case VERTICAL:
                        vx = 0.1 * Math.sin(time * 0.3 + nx * 2);
                        vy = 0.5 * Math.cos(time * 0.5 + ny * 4);
                        break;
                    default:
                        vx = 0;
                        vy = 0;
                }

                // Soften toward the glass so edges feel calmer
                double edgeFalloff = Math.min(Math.min(nx, 1 - nx), Math.min(ny, 1 - ny)) * 2.0;
                double strength = Math.max(0.15, edgeFalloff) * currentStrength;

                field[x][y].vx = vx * strength;
                field[x][y].vy = vy * strength;

                // Temperature is higher near the surface (top of panel)
                field[x][y].temperature = 18 + 8 * (1.0 - ny) + 2 * Math.sin(time + nx * 1.2);
            }
        }
    }
    
    // Getters and setters for current control
    public void setCurrentStrength(double strength) {
        this.currentStrength = Math.max(0.0, Math.min(2.0, strength));
    }
    
    public double getCurrentStrength() {
        return currentStrength;
    }
    
    public void setAnimationSpeed(double speed) {
        this.animationSpeed = Math.max(0.0, Math.min(3.0, speed));
    }
    
    public double getAnimationSpeed() {
        return animationSpeed;
    }
    
    public void setPattern(CurrentPattern pattern) {
        this.pattern = pattern;
    }
    
    public CurrentPattern getPattern() {
        return pattern;
    }

    /**
     * Samples the current vector for a world position.
     */
    public Point2D.Double sampleVector(double worldX, double worldY) {
        int cx = clamp((int) (worldX / cellSizeX), 0, cellsX - 1);
        int cy = clamp((int) (worldY / cellSizeY), 0, cellsY - 1);
        VectorCell c = field[cx][cy];
        return new Point2D.Double(c.vx, c.vy);
    }

    public double sampleTemperature(double worldX, double worldY) {
        int cx = clamp((int) (worldX / cellSizeX), 0, cellsX - 1);
        int cy = clamp((int) (worldY / cellSizeY), 0, cellsY - 1);
        return field[cx][cy].temperature;
    }

    public VectorCell[][] getCells() {
        return field;
    }
    
    public double getCellWidth() {
        return cellSizeX;
    }
    
    public double getCellHeight() {
        return cellSizeY;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static class VectorCell {
        public double vx;
        public double vy;
        public double temperature;
    }
}

