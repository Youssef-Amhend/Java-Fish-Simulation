package com.dtp5.config;

import java.awt.*;

/**
 * Centralized configuration for the fish simulation.
 * Contains all constants for colors, animations, physics, and UI settings.
 */
public class SimulationConfig {

    // === WINDOW SETTINGS ===
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 900;
    public static final String WINDOW_TITLE = "Ocean Ecosystem Simulation";
    public static final boolean START_FULLSCREEN = false;

    // === SIMULATION SETTINGS ===
    public static final int INITIAL_FISH_COUNT = 500;
    public static final int INITIAL_PLANKTON_PATCHES = 15;
    public static final int TIMER_DELAY_MS = 16; // ~60 FPS

    // === SPATIAL PARTITIONING ===
    public static final int GRID_CELL_SIZE = 100; // pixels

    // === FISH PHYSICS ===
    public static final double FISH_SPEED = 3.0;
    public static final double FISH_MIN_DISTANCE = 5.0;
    public static final double FISH_MIN_DISTANCE_SQ = 25.0;
    public static final double FISH_MAX_DISTANCE = 40.0;
    public static final double FISH_MAX_DISTANCE_SQ = 1600.0;

    // === FISH VISUAL SETTINGS ===
    public static final int FISH_BODY_LENGTH = 12;
    public static final int FISH_BODY_WIDTH = 6;
    public static final int FISH_TAIL_LENGTH = 8;
    public static final int FISH_TAIL_WIDTH = 6;
    public static final float FISH_ALPHA = 0.85f; // Transparency
    public static final int TRAIL_LENGTH = 5; // Number of trail points
    public static final float TRAIL_ALPHA = 0.3f;

    // === FISH COLOR PALETTES ===
    public static final Color[] FISH_COLORS = {
            new Color(255, 140, 0), // Orange
            new Color(255, 215, 0), // Gold
            new Color(0, 191, 255), // Deep Sky Blue
            new Color(138, 43, 226), // Blue Violet
            new Color(255, 20, 147), // Deep Pink
            new Color(50, 205, 50), // Lime Green
            new Color(255, 99, 71), // Tomato
            new Color(64, 224, 208), // Turquoise
    };

    // === OCEAN BACKGROUND ===
    public static final Color OCEAN_TOP_COLOR = new Color(0, 105, 148); // Deep ocean blue
    public static final Color OCEAN_MIDDLE_COLOR = new Color(0, 119, 190); // Medium blue
    public static final Color OCEAN_BOTTOM_COLOR = new Color(13, 27, 42); // Very dark blue
    public static final int CAUSTIC_COUNT = 30;
    public static final float CAUSTIC_ALPHA = 0.15f;

    // === OBSTACLE SETTINGS ===
    public static final double DEFAULT_OBSTACLE_RADIUS = 30.0;
    public static final int OBSTACLE_LIFETIME = 500; // Frames
    public static final int RIPPLE_COUNT = 3;
    public static final float OBSTACLE_ALPHA_START = 0.7f;
    public static final Color OBSTACLE_COLOR = new Color(255, 69, 0); // Red-Orange

    // === ANIMATION SETTINGS ===
    public static final double ROTATION_SMOOTHING = 0.15; // Lower = smoother but slower
    public static final int TAIL_ANIMATION_SPEED = 10; // Frames per tail wave cycle

    // === ECOSYSTEM SETTINGS ===
    public static final double BASE_ENERGY = 1200.0;
    public static final double ENERGY_DECAY_PER_TICK = 0.35;
    public static final double FEED_ENERGY_GAIN = 180.0;
    public static final double REPRODUCTION_THRESHOLD = 1400.0;
    public static final double REPRODUCTION_COST = 600.0;
    public static final double CURRENT_INFLUENCE = 0.35;
    public static final int ENV_FIELD_CELLS = 32;
    public static final double ENV_FIELD_TIME_SCALE = 0.0015;
    public static final double PLANKTON_MAX_BIOMASS = 400.0;
    public static final double PLANKTON_REGEN_RATE = 0.8;
    public static final double PLANKTON_FEED_RADIUS = 28.0;
    public static final int MAX_FISH = 1800;
    public static final int MAX_SHARKS = 35;

    // === UI CONTROL PANEL ===
    public static final int CONTROL_PANEL_HEIGHT = 60;
    public static final Color CONTROL_PANEL_BG = new Color(30, 30, 30, 200);
    public static final Color CONTROL_PANEL_TEXT = Color.WHITE;
    public static final Font CONTROL_PANEL_FONT = new Font("Arial", Font.BOLD, 14);

    private SimulationConfig() {
        // Prevent instantiation
    }
}
