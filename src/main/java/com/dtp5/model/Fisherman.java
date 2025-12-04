package com.dtp5.model;

/**
 * Represents a fisherman's hook dropping into the ocean.
 */
public class Fisherman {
    public double posX;
    public double posY;
    public boolean isFishing;
    public boolean movingDown;
    public double speed = 3.0;
    public double maxY;

    public Fisherman(double x) {
        this.posX = x;
        this.posY = -50; // Start above screen
        this.isFishing = false;
        this.movingDown = true;
    }

    public void startFishing(double x, double depth) {
        this.posX = x;
        this.posY = 0;
        this.maxY = depth;
        this.isFishing = true;
        this.movingDown = true;
    }

    public void update() {
        if (!isFishing)
            return;

        if (movingDown) {
            posY += speed;
            if (posY >= maxY) {
                movingDown = false; // Start pulling up
            }
        } else {
            posY -= speed;
            if (posY <= 0) {
                isFishing = false; // Finished fishing trip
            }
        }
    }

    public boolean checkCatch(Poisson p) {
        if (!isFishing)
            return false;
        // Simple circle collision for hook
        double dx = p.posX - posX;
        double dy = p.posY - posY;
        return (dx * dx + dy * dy) < 400; // 20px radius catch zone
    }
}
