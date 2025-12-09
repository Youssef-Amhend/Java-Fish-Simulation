package com.dtp5.model;

import java.util.Random;

/**
 * Represents animated algae/seaweed that sways with currents.
 */
public class Algae {
    public double baseX;
    public double baseY;
    public double height;
    public double width;
    public double phase;
    public double swaySpeed;
    public AlgaeType type;
    
    public enum AlgaeType {
        SHORT_ALGAE,
        MEDIUM_ALGAE,
        TALL_ALGAE,
        KELP
    }
    
    public Algae(double x, double y, AlgaeType type, Random random) {
        this.baseX = x;
        this.baseY = y;
        this.type = type;
        this.phase = random.nextDouble() * Math.PI * 2;
        this.swaySpeed = 0.02 + random.nextDouble() * 0.03;
        
        switch (type) {
            case SHORT_ALGAE:
                this.height = 40 + random.nextDouble() * 30;
                this.width = 8 + random.nextDouble() * 6;
                break;
            case MEDIUM_ALGAE:
                this.height = 80 + random.nextDouble() * 50;
                this.width = 12 + random.nextDouble() * 8;
                break;
            case TALL_ALGAE:
                this.height = 150 + random.nextDouble() * 80;
                this.width = 15 + random.nextDouble() * 10;
                break;
            case KELP:
                this.height = 200 + random.nextDouble() * 100;
                this.width = 20 + random.nextDouble() * 15;
                break;
        }
    }
    
    public void update(long frameCount, double currentVx, double currentVy) {
        phase += swaySpeed;
        // Sway influenced by current
        phase += (currentVx + currentVy) * 0.0001;
    }
}

