package com.dtp5.event;

import com.dtp5.model.Poisson;

/**
 * Event fired when a fish dies (starvation, caught, etc.).
 */
public class FishDeathEvent extends SimulationEvent {

    public enum DeathCause {
        STARVATION,
        CAUGHT_BY_FISHERMAN,
        EATEN_BY_SHARK,
        OTHER
    }

    private final Poisson fish;
    private final DeathCause cause;
    private final double lastX;
    private final double lastY;

    public FishDeathEvent(Poisson fish, DeathCause cause, long frameNumber) {
        super(null, frameNumber); // Poisson doesn't extend Entity yet
        this.fish = fish;
        this.cause = cause;
        this.lastX = fish.posX;
        this.lastY = fish.posY;
    }

    public Poisson getFish() {
        return fish;
    }

    public DeathCause getCause() {
        return cause;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    @Override
    public EventType getEventType() {
        return EventType.FISH_DIED;
    }
}
