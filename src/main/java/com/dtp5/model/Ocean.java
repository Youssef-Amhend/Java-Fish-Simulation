package com.dtp5.model;

import com.dtp5.config.SimulationConfig;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executors;

/**
 * Represents the ocean environment containing fish, sharks, obstacles, and a
 * fisherman.
 * Uses Multithreading for high performance updates.
 */
public class Ocean {
    // Attributes
    public Poisson[] poissons;
    public ArrayList<Shark> sharks;
    public ArrayList<ZoneAEviter> obstacles;
    public Fisherman fisherman;

    protected Random generateur;
    protected double largeur;
    protected double hauteur;

    // Spatial partitioning
    private SpatialGrid spatialGrid;

    // Multithreading
    private ExecutorService executor;
    private int numThreads;

    // PropertyChangeSupport
    private final PropertyChangeSupport support;

    // Frame counter
    private long frameCount = 0;

    /**
     * Creates a new ocean with the specified dimensions and fish count.
     */
    public Ocean(int _nbPoissons, double _largeur, double _hauteur) {
        largeur = _largeur;
        hauteur = _hauteur;
        generateur = new Random();
        obstacles = new ArrayList<>();
        sharks = new ArrayList<>();
        fisherman = new Fisherman(largeur / 2);
        support = new PropertyChangeSupport(this);

        // Initialize spatial grid
        spatialGrid = new SpatialGrid(largeur, hauteur, SimulationConfig.GRID_CELL_SIZE);

        // Initialize thread pool
        numThreads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(numThreads);

        // Create fish
        poissons = new Poisson[_nbPoissons];
        for (int i = 0; i < _nbPoissons; i++) {
            poissons[i] = new Poisson(
                    generateur.nextDouble() * largeur,
                    generateur.nextDouble() * hauteur,
                    generateur.nextDouble() * 2 * Math.PI);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void AjouterObstacle(double _posX, double _posY, double rayon) {
        obstacles.add(new ZoneAEviter(_posX, _posY, rayon));
    }

    public void addShark() {
        sharks.add(new Shark(
                generateur.nextDouble() * largeur,
                generateur.nextDouble() * hauteur,
                generateur.nextDouble() * 2 * Math.PI));
    }

    public void toggleFisherman() {
        if (fisherman.isFishing) {
            fisherman.isFishing = false;
        } else {
            fisherman.startFishing(
                    generateur.nextDouble() * largeur,
                    hauteur * 0.8);
        }
    }

    public void addFish() {
        Poisson[] newPoissons = new Poisson[poissons.length + 1];
        System.arraycopy(poissons, 0, newPoissons, 0, poissons.length);
        newPoissons[poissons.length] = new Poisson(
                generateur.nextDouble() * largeur,
                generateur.nextDouble() * hauteur,
                generateur.nextDouble() * 2 * Math.PI);
        poissons = newPoissons;
    }

    public long getFrameCount() {
        return frameCount;
    }

    protected void MiseAJourObstacles() {
        for (ZoneAEviter obstacle : obstacles) {
            obstacle.MiseAJour();
        }
        obstacles.removeIf(o -> o.estMort());
    }

    protected void MiseAJourSharks() {
        // Rebuild grid for sharks to find fish efficiently
        // (We already rebuilt it in MiseAJourPoissons, but sharks move after fish)
        // Actually, sharks can use the grid from the beginning of the frame, it's fine.

        for (Shark s : sharks) {
            List<Poisson> nearby = spatialGrid.getNearbyFish(s);
            s.MiseAJourShark(nearby, largeur, hauteur);
        }
    }

    protected void MiseAJourFisherman() {
        fisherman.update();
        if (fisherman.isFishing) {
            // Check for catches
            List<Poisson> caught = new ArrayList<>();
            for (Poisson p : poissons) {
                if (fisherman.checkCatch(p)) {
                    caught.add(p);
                }
            }

            // Remove caught fish
            if (!caught.isEmpty()) {
                removeFish(caught);
                fisherman.isFishing = false; // Catch one and retract
                fisherman.movingDown = false;
            }
        }
    }

    private void removeFish(List<Poisson> toRemove) {
        if (toRemove.isEmpty())
            return;

        Poisson[] newPoissons = new Poisson[poissons.length - toRemove.size()];
        int idx = 0;
        for (Poisson p : poissons) {
            if (!toRemove.contains(p)) {
                if (idx < newPoissons.length) {
                    newPoissons[idx++] = p;
                }
            }
        }
        poissons = newPoissons;
    }

    /**
     * Updates all fish using multithreading.
     */
    protected void MiseAJourPoissons() {
        // 1. Rebuild Spatial Grid (Sequential - fast)
        spatialGrid.clear();
        for (Poisson p : poissons) {
            spatialGrid.addFish(p);
        }

        // 2. Parallel Update of Fish Behaviors
        // We split the array into chunks for each thread
        int chunkSize = (int) Math.ceil((double) poissons.length / numThreads);
        List<java.util.concurrent.Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, poissons.length);

            if (start < end) {
                tasks.add(() -> {
                    for (int j = start; j < end; j++) {
                        Poisson p = poissons[j];
                        // Read-only access to grid and obstacles
                        List<Poisson> nearby = spatialGrid.getNearbyFish(p);
                        p.MiseAJour(nearby, obstacles, sharks, largeur, hauteur);
                    }
                    return null;
                });
            }
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void MiseAJourOcean() {
        MiseAJourObstacles();
        MiseAJourPoissons();
        MiseAJourSharks();
        MiseAJourFisherman();
        frameCount++;
        support.firePropertyChange("oceanUpdated", null, this);
    }

    // Cleanup
    public void shutdown() {
        executor.shutdown();
    }
}
