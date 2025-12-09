package com.dtp5.model;

import com.dtp5.config.SimulationConfig;

/**
 * Regenerating food source for fish. Plankton have biomass that can be consumed
 * and slowly regrow.
 */
public class PlanktonPatch extends Objet {
    private double biomass;
    private final double radius;

    public PlanktonPatch(double x, double y, double initialBiomass) {
        super(x, y);
        this.biomass = initialBiomass;
        this.radius = SimulationConfig.PLANKTON_FEED_RADIUS;
    }

    public double getRadius() {
        return radius;
    }

    public double getBiomass() {
        return biomass;
    }

    public boolean isDepleted() {
        return biomass <= 2.0;
    }

    /**
     * Regrows biomass gradually up to a cap.
     */
    public void regenerate() {
        biomass = Math.min(
                SimulationConfig.PLANKTON_MAX_BIOMASS,
                biomass + SimulationConfig.PLANKTON_REGEN_RATE);
    }

    /**
     * Consume biomass and return energy awarded to the fish.
     */
    public double consume(double requested) {
        double taken = Math.min(requested, biomass);
        biomass -= taken;
        return taken;
    }
}

