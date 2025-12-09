package com.dtp5.model;

import java.util.List;

/**
 * Immutable context object passed to entities during updates.
 * Contains all the state an entity might need without tight coupling.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public record SimulationContext(
        double width,
        double height,
        double deltaTime,
        long frameCount,
        SpatialGrid spatialGrid,
        EnvironmentalField environmentalField,
        List<PlanktonPatch> planktonPatches,
        List<Shark> sharks,
        List<ZoneAEviter> obstacles,
        DayNightCycle dayNightCycle) {

    /**
     * Creates a context without day/night cycle (for backwards compatibility).
     */
    public SimulationContext(
            double width,
            double height,
            double deltaTime,
            long frameCount,
            SpatialGrid spatialGrid,
            EnvironmentalField environmentalField,
            List<PlanktonPatch> planktonPatches,
            List<Shark> sharks,
            List<ZoneAEviter> obstacles) {
        this(width, height, deltaTime, frameCount, spatialGrid,
                environmentalField, planktonPatches, sharks, obstacles, null);
    }

    /**
     * Gets whether it's currently daytime.
     * 
     * @return true if day, false if night
     */
    public boolean isDaytime() {
        return dayNightCycle == null || dayNightCycle.isDaytime();
    }

    /**
     * Gets the current light level (0.0 = dark, 1.0 = bright).
     * 
     * @return Light level
     */
    public float getLightLevel() {
        return dayNightCycle == null ? 1.0f : dayNightCycle.getLightLevel();
    }
}
