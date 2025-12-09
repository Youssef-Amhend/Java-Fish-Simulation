package com.dtp5.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Poisson extends Objet {
    // Velocity components
    public double vitesseX;
    public double vitesseY;

    // Ecosystem state
    public double energy;
    public boolean alive = true;

    // Visual properties
    public Color color;
    public LinkedList<Point2D.Double> trail;

    // Species
    public FishSpecies species;

    // Static random for color selection
    private static final Random colorRandom = new Random();

    /**
     * Creates a new fish at the specified position with the given direction.
     */
    public Poisson(double _x, double _y, double _dir) {
        this(_x, _y, _dir, getRandomSpecies());
    }

    /**
     * Creates a new fish with a specific species.
     */
    public Poisson(double _x, double _y, double _dir, FishSpecies _species) {
        posX = _x;
        posY = _y;
        species = _species;

        vitesseX = Math.cos(_dir) * species.speed;
        vitesseY = Math.sin(_dir) * species.speed;

        // Assign random color from species palette
        color = species.getRandomColor(colorRandom);

        // Initialize trail
        trail = new LinkedList<>();

        // Base energy
        energy = com.dtp5.config.SimulationConfig.BASE_ENERGY;
    }

    private static FishSpecies getRandomSpecies() {
        FishSpecies[] all = FishSpecies.values();
        return all[colorRandom.nextInt(all.length)];
    }

    public double getVitesseX() {
        return vitesseX;
    }

    public double getVitesseY() {
        return vitesseY;
    }

    /**
     * Updates the fish's position and trail.
     */
    protected void MiseAJourPosition() {
        // Add current position to trail
        trail.addFirst(new Point2D.Double(posX, posY));

        // Limit trail length
        while (trail.size() > 5) { // Fixed trail length
            trail.removeLast();
        }

        // Move fish
        posX += vitesseX;
        posY += vitesseY;
    }

    /**
     * Checks if another fish is within alignment range.
     */
    protected boolean DansAlignement(Poisson p) {
        double distanceCarre = DistanceCarre(p);
        double maxDist = species.getMaxDistance();
        double minDist = species.getMinDistance();
        return (distanceCarre < maxDist * maxDist &&
                distanceCarre > minDist * minDist);
    }

    /**
     * Calculates the distance to the nearest wall.
     */
    protected double DistanceAuMur(double murXMin, double murYMin, double murXMax, double murYMax) {
        double min = Math.min(posX - murXMin, posY - murYMin);
        min = Math.min(min, murXMax - posX);
        min = Math.min(min, murYMax - posY);
        return min;
    }

    /**
     * Normalizes the velocity vector to maintain constant speed.
     */
    protected void Normaliser() {
        double longueur = Math.sqrt(vitesseX * vitesseX + vitesseY * vitesseY);
        if (longueur > 0) {
            double targetSpeed = species.speed;
            vitesseX = (vitesseX / longueur) * targetSpeed;
            vitesseY = (vitesseY / longueur) * targetSpeed;
        }
    }

    /**
     * Avoids walls by adjusting velocity when near boundaries.
     */
    protected boolean EviterMurs(double murXMin, double murYMin, double murXMax, double murYMax) {
        boolean turned = false;

        // Clamp hard bounds to keep aquarium framing
        if (posX < murXMin) {
            posX = murXMin;
            vitesseX = Math.abs(vitesseX);
            turned = true;
        } else if (posX > murXMax) {
            posX = murXMax;
            vitesseX = -Math.abs(vitesseX);
            turned = true;
        }

        if (posY < murYMin) {
            posY = murYMin;
            vitesseY = Math.abs(vitesseY);
            turned = true;
        } else if (posY > murYMax) {
            posY = murYMax;
            vitesseY = -Math.abs(vitesseY);
            turned = true;
        }

        // Soft steering when near glass to keep fish inside frame
        double padding = species.getMinDistance() * 2.5;
        double push = species.speed * 0.25;

        if (posX - murXMin < padding) {
            vitesseX += push;
            turned = true;
        } else if (murXMax - posX < padding) {
            vitesseX -= push;
            turned = true;
        }

        if (posY - murYMin < padding) {
            vitesseY += push;
            turned = true;
        } else if (murYMax - posY < padding) {
            vitesseY -= push;
            turned = true;
        }

        if (turned) {
            Normaliser();
        }
        return turned;
    }

    /**
     * Avoids obstacles by steering away from them.
     */
    protected boolean EviterObstacles(ArrayList<ZoneAEviter> obstacles) {
        if (!obstacles.isEmpty()) {
            // Find nearest obstacle
            ZoneAEviter obstacleProche = obstacles.get(0);
            double distanceCarre = DistanceCarre(obstacleProche);
            for (ZoneAEviter o : obstacles) {
                if (DistanceCarre(o) < distanceCarre) {
                    obstacleProche = o;
                    distanceCarre = DistanceCarre(o);
                }
            }

            if (distanceCarre < (obstacleProche.rayon * obstacleProche.rayon)) {
                // Collision - calculate avoidance vector
                double distance = Math.sqrt(distanceCarre);
                double diffX = (obstacleProche.posX - posX) / distance;
                double diffY = (obstacleProche.posY - posY) / distance;
                vitesseX = vitesseX - diffX * species.speed * 0.5;
                vitesseY = vitesseY - diffY * species.speed * 0.5;
                Normaliser();
                return true;
            }
        }
        return false;
    }

    /**
     * Avoids other fish to prevent crowding - now uses nearby fish list.
     */
    protected boolean EviterPoissons(List<Poisson> nearbyFish) {
        Poisson closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Poisson p : nearbyFish) {
            if (p != this) {
                double dist = DistanceCarre(p);
                if (dist < closestDist) {
                    closest = p;
                    closestDist = dist;
                }
            }
        }

        if (closest == null)
            return false;

        // Avoidance
        double minDist = species.getMinDistance();
        if (closestDist < minDist * minDist) {
            double distance = Math.sqrt(closestDist);
            double diffX = (closest.posX - posX) / distance;
            double diffY = (closest.posY - posY) / distance;
            vitesseX = vitesseX - diffX * species.speed * 0.25;
            vitesseY = vitesseY - diffY * species.speed * 0.25;
            Normaliser();
            return true;
        }
        return false;
    }

    /**
     * Aligns with nearby fish by averaging their velocity vectors - now uses nearby
     * fish list.
     */
    protected void CalculerDirectionMoyenne(List<Poisson> nearbyFish) {
        double vitesseXTotal = 0;
        double vitesseYTotal = 0;
        int nbTotal = 0;

        for (Poisson p : nearbyFish) {
            if (p != this && DansAlignement(p)) {
                vitesseXTotal += p.vitesseX;
                vitesseYTotal += p.vitesseY;
                nbTotal++;
            }
        }

        if (nbTotal >= 1) {
            double schoolingStrength = species.getSchoolingStrength();
            vitesseX = (vitesseXTotal / nbTotal) * schoolingStrength + vitesseX * (1 - schoolingStrength);
            vitesseY = (vitesseYTotal / nbTotal) * schoolingStrength + vitesseY * (1 - schoolingStrength);
            Normaliser();
        }
    }

    /**
     * Avoids sharks by fleeing.
     */
    protected boolean EviterRequins(List<Shark> sharks) {
        if (sharks.isEmpty())
            return false;

        Shark closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Shark s : sharks) {
            double dist = DistanceCarre(s);
            if (dist < closestDist) {
                closest = s;
                closestDist = dist;
            }
        }

        if (closest != null && closestDist < 22500) { // 150px detection
            double distance = Math.sqrt(closestDist);
            double diffX = (closest.posX - posX) / distance;
            double diffY = (closest.posY - posY) / distance;

            // Flee hard
            vitesseX -= diffX * species.speed * 0.8;
            vitesseY -= diffY * species.speed * 0.8;
            Normaliser();
            return true;
        }
        return false;
    }

    /**
     * Main update method - applies all behaviors in priority order.
     * Now uses spatial grid for efficient neighbor queries.
     */
    public void MiseAJour(List<Poisson> nearbyFish, ArrayList<ZoneAEviter> obstacles,
            List<Shark> sharks, List<PlanktonPatch> planktons, EnvironmentalField field,
            double largeur, double hauteur) {
        if (!EviterMurs(0, 0, largeur, hauteur)) {
            if (!EviterRequins(sharks)) {
                if (!EviterObstacles(obstacles)) {
                    if (!EviterPoissons(nearbyFish)) {
                        CalculerDirectionMoyenne(nearbyFish);
                    }
                }
            }
        }
        applyEnvironmentalField(field);
        MiseAJourPosition();
        updateEnergy(planktons);
        checkVitality();
    }

    private void applyEnvironmentalField(EnvironmentalField field) {
        Point2D.Double vec = field.sampleVector(posX, posY);
        vitesseX += vec.x * com.dtp5.config.SimulationConfig.CURRENT_INFLUENCE;
        vitesseY += vec.y * com.dtp5.config.SimulationConfig.CURRENT_INFLUENCE;
        Normaliser();
    }

    private void updateEnergy(List<PlanktonPatch> planktons) {
        energy -= com.dtp5.config.SimulationConfig.ENERGY_DECAY_PER_TICK;
        for (PlanktonPatch patch : planktons) {
            double r = patch.getRadius();
            double dx = patch.posX - posX;
            double dy = patch.posY - posY;
            double distSq = dx * dx + dy * dy;
            if (distSq < r * r) {
                double gained = patch.consume(com.dtp5.config.SimulationConfig.FEED_ENERGY_GAIN);
                energy += gained;
                break;
            }
        }
    }

    private void checkVitality() {
        if (energy <= 0) {
            alive = false;
        }
    }
}
