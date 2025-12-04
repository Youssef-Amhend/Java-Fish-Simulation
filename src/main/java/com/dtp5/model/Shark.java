package com.dtp5.model;

import java.awt.Color;
import java.util.List;

/**
 * Represents a predator shark that chases fish.
 */
public class Shark extends Poisson {

    public Shark(double _x, double _y, double _dir) {
        super(_x, _y, _dir);
        // Sharks are faster and larger
        this.color = Color.GRAY;
        // Override speed and size in rendering, but here we set base properties
    }

    /**
     * Updates shark behavior - chases nearest fish.
     */
    public void MiseAJourShark(List<Poisson> nearbyFish, double largeur, double hauteur) {
        Poisson target = null;
        double minDistance = Double.MAX_VALUE;

        // Find nearest prey
        for (Poisson p : nearbyFish) {
            if (!(p instanceof Shark)) { // Don't eat other sharks
                double d = DistanceCarre(p);
                if (d < minDistance) {
                    minDistance = d;
                    target = p;
                }
            }
        }

        // Chase behavior
        if (target != null && minDistance < 40000) { // Detection range 200px
            double dist = Math.sqrt(minDistance);
            double dx = (target.posX - posX) / dist;
            double dy = (target.posY - posY) / dist;

            // Steer towards target
            vitesseX += dx * 0.2;
            vitesseY += dy * 0.2;
        } else {
            // Wander if no target
            vitesseX += (Math.random() - 0.5) * 0.1;
            vitesseY += (Math.random() - 0.5) * 0.1;
        }

        // Normalize speed (sharks are faster)
        double speed = Math.sqrt(vitesseX * vitesseX + vitesseY * vitesseY);
        double maxSpeed = 4.5; // Faster than most fish
        if (speed > 0) {
            vitesseX = (vitesseX / speed) * maxSpeed;
            vitesseY = (vitesseY / speed) * maxSpeed;
        }

        // Keep within bounds
        if (posX < 0) {
            posX = 0;
            vitesseX *= -1;
        }
        if (posX > largeur) {
            posX = largeur;
            vitesseX *= -1;
        }
        if (posY < 0) {
            posY = 0;
            vitesseY *= -1;
        }
        if (posY > hauteur) {
            posY = hauteur;
            vitesseY *= -1;
        }

        // Update position
        MiseAJourPosition();
    }
}
