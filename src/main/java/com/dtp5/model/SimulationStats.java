package com.dtp5.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe statistics aggregator for the simulation.
 */
public class SimulationStats {
    private final AtomicLong births = new AtomicLong();
    private final AtomicLong deaths = new AtomicLong();
    private final AtomicLong feedEvents = new AtomicLong();

    private volatile double avgEnergy = 0.0;

    public void recordBirth() {
        births.incrementAndGet();
    }

    public void recordDeath() {
        deaths.incrementAndGet();
    }

    public void recordFeed() {
        feedEvents.incrementAndGet();
    }

    public long getBirths() {
        return births.get();
    }

    public long getDeaths() {
        return deaths.get();
    }

    public long getFeedEvents() {
        return feedEvents.get();
    }

    public void setAvgEnergy(double avgEnergy) {
        this.avgEnergy = avgEnergy;
    }

    public double getAvgEnergy() {
        return avgEnergy;
    }
}

