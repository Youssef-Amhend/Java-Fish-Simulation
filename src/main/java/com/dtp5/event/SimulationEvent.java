package com.dtp5.event;

import com.dtp5.model.Entity;

/**
 * Base class for all simulation events.
 * Events are immutable and contain information about what happened.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public abstract class SimulationEvent {

    /** Timestamp when this event occurred */
    private final long timestamp;

    /** The entity that caused this event, if any */
    private final Entity source;

    /** Frame number when this event occurred */
    private final long frameNumber;

    /**
     * Creates a new event with the current timestamp.
     * 
     * @param source      The entity that caused this event (can be null)
     * @param frameNumber The current frame number
     */
    protected SimulationEvent(Entity source, long frameNumber) {
        this.timestamp = System.currentTimeMillis();
        this.source = source;
        this.frameNumber = frameNumber;
    }

    /**
     * Gets the timestamp when this event occurred.
     * 
     * @return Timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the source entity that caused this event.
     * 
     * @return Source entity, or null if not applicable
     */
    public Entity getSource() {
        return source;
    }

    /**
     * Gets the frame number when this event occurred.
     * 
     * @return Frame number
     */
    public long getFrameNumber() {
        return frameNumber;
    }

    /**
     * Gets the type of this event.
     * 
     * @return Event type
     */
    public abstract EventType getEventType();

    /**
     * Enum of all event types.
     */
    public enum EventType {
        FISH_BORN,
        FISH_DIED,
        FISH_CAUGHT,
        FISH_FED,
        SHARK_HUNTED,
        PLANKTON_DEPLETED,
        OBSTACLE_CREATED,
        SIMULATION_PAUSED,
        SIMULATION_RESUMED,
        DAY_STARTED,
        NIGHT_STARTED
    }
}
