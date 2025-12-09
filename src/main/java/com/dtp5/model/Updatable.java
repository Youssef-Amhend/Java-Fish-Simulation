package com.dtp5.model;

/**
 * Interface for entities that can be updated each simulation tick.
 * Implementing this interface allows an entity to participate in the simulation
 * loop.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
@FunctionalInterface
public interface Updatable {

    /**
     * Updates this entity for the current simulation tick.
     * 
     * @param context The simulation context containing all necessary state
     */
    void update(SimulationContext context);
}
