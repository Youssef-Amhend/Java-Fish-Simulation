package com.dtp5.model;

import com.dtp5.config.SimulationConfig;

/**
 * Represents an obstacle zone that fish should avoid.
 * Obstacles have a limited lifetime and fade over time.
 */
public class ZoneAEviter extends Objet {
    public double rayon;
    public int tempsRestant;

    public ZoneAEviter(double _x, double _y, double _rayon) {
        posX = _x;
        posY = _y;
        rayon = _rayon;
        tempsRestant = SimulationConfig.OBSTACLE_LIFETIME;
    }

    public double getRayon() {
        return rayon;
    }

    public void MiseAJour() {
        tempsRestant--;
    }

    public boolean estMort() {
        return tempsRestant <= 0;
    }
}
