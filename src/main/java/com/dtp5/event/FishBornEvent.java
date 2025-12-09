package com.dtp5.event;

import com.dtp5.model.Entity;
import com.dtp5.model.Poisson;

/**
 * Event fired when a fish is born (reproduction).
 */
public class FishBornEvent extends SimulationEvent {

    private final Poisson parent;
    private final Poisson child;

    public FishBornEvent(Poisson parent, Poisson child, long frameNumber) {
        super(null, frameNumber); // Poisson doesn't extend Entity yet
        this.parent = parent;
        this.child = child;
    }

    public Poisson getParent() {
        return parent;
    }

    public Poisson getChild() {
        return child;
    }

    @Override
    public EventType getEventType() {
        return EventType.FISH_BORN;
    }
}
