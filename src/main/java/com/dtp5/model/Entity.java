package com.dtp5.model;

import java.util.UUID;

/**
 * Base class for all entities in the simulation.
 * Provides common functionality for position, identification, and lifecycle
 * management.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public abstract class Entity {

    /** Unique identifier for this entity */
    private final UUID id;

    /** Position in world coordinates */
    protected double posX;
    protected double posY;

    /** Whether this entity is alive and should be updated/rendered */
    protected boolean alive = true;

    /**
     * Creates a new entity at the origin.
     */
    protected Entity() {
        this.id = UUID.randomUUID();
        this.posX = 0;
        this.posY = 0;
    }

    /**
     * Creates a new entity at the specified position.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    protected Entity(double x, double y) {
        this.id = UUID.randomUUID();
        this.posX = x;
        this.posY = y;
    }

    /**
     * Gets the unique identifier for this entity.
     * 
     * @return UUID of this entity
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the X position of this entity.
     * 
     * @return X coordinate
     */
    public double getX() {
        return posX;
    }

    /**
     * Gets the Y position of this entity.
     * 
     * @return Y coordinate
     */
    public double getY() {
        return posY;
    }

    /**
     * Sets the position of this entity.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setPosition(double x, double y) {
        this.posX = x;
        this.posY = y;
    }

    /**
     * Checks if this entity is alive.
     * 
     * @return true if alive, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Marks this entity as dead (will be removed on next update).
     */
    public void kill() {
        this.alive = false;
    }

    /**
     * Calculates the distance to another entity.
     * 
     * @param other The other entity
     * @return Distance in pixels
     */
    public double distanceTo(Entity other) {
        return Math.sqrt(distanceSquaredTo(other));
    }

    /**
     * Calculates the squared distance to another entity (faster, no sqrt).
     * 
     * @param other The other entity
     * @return Squared distance
     */
    public double distanceSquaredTo(Entity other) {
        double dx = other.posX - this.posX;
        double dy = other.posY - this.posY;
        return dx * dx + dy * dy;
    }

    /**
     * Calculates the squared distance to a point.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @return Squared distance
     */
    public double distanceSquaredTo(double x, double y) {
        double dx = x - this.posX;
        double dy = y - this.posY;
        return dx * dx + dy * dy;
    }

    /**
     * Gets the entity type for identification.
     * 
     * @return EntityType enum value
     */
    public abstract EntityType getType();

    /**
     * Enum defining all entity types in the simulation.
     */
    public enum EntityType {
        FISH,
        SHARK,
        JELLYFISH,
        SEA_TURTLE,
        PLANKTON,
        ALGAE,
        ROCK,
        CORAL,
        OBSTACLE,
        FISHERMAN,
        PARTICLE
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Entity entity = (Entity) obj;
        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
