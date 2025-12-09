package com.dtp5.model;

import java.util.Random;

/**
 * Represents a rock or reef structure at the bottom of the aquarium.
 */
public class Rock {
    public double posX;
    public double posY;
    public double width;
    public double height;
    public RockType type;
    public double rotation;
    
    public enum RockType {
        SMALL_ROCK,
        MEDIUM_ROCK,
        LARGE_ROCK,
        REEF_CLUSTER
    }
    
    public Rock(double x, double y, RockType type, Random random) {
        this.posX = x;
        this.posY = y;
        this.type = type;
        this.rotation = random.nextDouble() * Math.PI * 2;
        
        switch (type) {
            case SMALL_ROCK:
                this.width = 25 + random.nextDouble() * 20;
                this.height = 15 + random.nextDouble() * 15;
                break;
            case MEDIUM_ROCK:
                this.width = 45 + random.nextDouble() * 30;
                this.height = 30 + random.nextDouble() * 25;
                break;
            case LARGE_ROCK:
                this.width = 80 + random.nextDouble() * 50;
                this.height = 50 + random.nextDouble() * 40;
                break;
            case REEF_CLUSTER:
                this.width = 120 + random.nextDouble() * 80;
                this.height = 60 + random.nextDouble() * 50;
                break;
        }
    }
}

