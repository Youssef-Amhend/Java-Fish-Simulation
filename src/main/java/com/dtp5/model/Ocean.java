package com.dtp5.model;

import com.dtp5.config.SimulationConfig;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    public ArrayList<PlanktonPatch> planktons;
    public ArrayList<Rock> rocks;
    public ArrayList<Algae> algae;
    public Fisherman fisherman;
    public EnvironmentalField environmentalField;
    public SimulationStats stats;
    public boolean showCurrents = true;
    public boolean showPlankton = true;

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
        largeur = _largeur <= 0 ? SimulationConfig.WINDOW_WIDTH : _largeur;
        hauteur = _hauteur <= 0 ? SimulationConfig.WINDOW_HEIGHT : _hauteur;
        generateur = new Random();
        obstacles = new ArrayList<>();
        sharks = new ArrayList<>();
        planktons = new ArrayList<>();
        rocks = new ArrayList<>();
        algae = new ArrayList<>();
        fisherman = new Fisherman(largeur / 2);
        support = new PropertyChangeSupport(this);
        stats = new SimulationStats();

        // Initialize spatial grid
        spatialGrid = new SpatialGrid(largeur, hauteur, SimulationConfig.GRID_CELL_SIZE);
        environmentalField = new EnvironmentalField(largeur, hauteur);

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

        // Seed plankton patches
        for (int i = 0; i < SimulationConfig.INITIAL_PLANKTON_PATCHES; i++) {
            spawnPlanktonPatch();
        }
        
        // Create rocks at the bottom
        initializeRocks();
        
        // Create algae
        initializeAlgae();
    }
    
    private void initializeRocks() {
        // Create rocks along the bottom
        int numRocks = 8 + generateur.nextInt(5);
        for (int i = 0; i < numRocks; i++) {
            double x = generateur.nextDouble() * largeur;
            double y = hauteur - 20 - generateur.nextDouble() * 100; // Bottom area
            
            Rock.RockType type;
            double rand = generateur.nextDouble();
            if (rand < 0.3) {
                type = Rock.RockType.SMALL_ROCK;
            } else if (rand < 0.7) {
                type = Rock.RockType.MEDIUM_ROCK;
            } else if (rand < 0.9) {
                type = Rock.RockType.LARGE_ROCK;
            } else {
                type = Rock.RockType.REEF_CLUSTER;
            }
            
            rocks.add(new Rock(x, y, type, generateur));
        }
    }
    
    private void initializeAlgae() {
        // Create algae growing from rocks and bottom
        int numAlgae = 25 + generateur.nextInt(15);
        for (int i = 0; i < numAlgae; i++) {
            double x = generateur.nextDouble() * largeur;
            double y = hauteur - 10 - generateur.nextDouble() * 50; // Bottom area
            
            Algae.AlgaeType type;
            double rand = generateur.nextDouble();
            if (rand < 0.3) {
                type = Algae.AlgaeType.SHORT_ALGAE;
            } else if (rand < 0.6) {
                type = Algae.AlgaeType.MEDIUM_ALGAE;
            } else if (rand < 0.85) {
                type = Algae.AlgaeType.TALL_ALGAE;
            } else {
                type = Algae.AlgaeType.KELP;
            }
            
            algae.add(new Algae(x, y, type, generateur));
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
        if (sharks.size() >= SimulationConfig.MAX_SHARKS) {
            return;
        }
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
        addFish(1);
    }
    
    public void addFish(int count) {
        if (poissons.length + count > SimulationConfig.MAX_FISH) {
            count = SimulationConfig.MAX_FISH - poissons.length;
            if (count <= 0) return;
        }
        
        Poisson[] newPoissons = new Poisson[poissons.length + count];
        System.arraycopy(poissons, 0, newPoissons, 0, poissons.length);
        
        for (int i = 0; i < count; i++) {
            newPoissons[poissons.length + i] = new Poisson(
                    generateur.nextDouble() * largeur,
                    generateur.nextDouble() * hauteur,
                    generateur.nextDouble() * 2 * Math.PI);
        }
        poissons = newPoissons;
    }

    public void spawnPlanktonPatch() {
        planktons.add(new PlanktonPatch(
                generateur.nextDouble() * largeur,
                generateur.nextDouble() * hauteur,
                SimulationConfig.PLANKTON_MAX_BIOMASS * 0.5));
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
        ConcurrentLinkedQueue<Poisson> newborns = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, poissons.length);

            if (start < end) {
                tasks.add(() -> {
                    for (int j = start; j < end; j++) {
                        Poisson p = poissons[j];
                        // Read-only access to grid and obstacles
                        List<Poisson> nearby = spatialGrid.getNearbyFish(p);
                        p.MiseAJour(nearby, obstacles, sharks, planktons, environmentalField, largeur, hauteur);
                        if (!p.alive) {
                            continue;
                        }
                        // Attempt reproduction if energy is high
                        if (p.energy > SimulationConfig.REPRODUCTION_THRESHOLD &&
                                poissons.length + newborns.size() < SimulationConfig.MAX_FISH) {
                            p.energy -= SimulationConfig.REPRODUCTION_COST;
                            newborns.add(new Poisson(
                                    p.posX + generateur.nextGaussian() * 4,
                                    p.posY + generateur.nextGaussian() * 4,
                                    generateur.nextDouble() * 2 * Math.PI,
                                    p.species));
                        }
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

        // 3. Collect survivors & newborns
        List<Poisson> survivors = new ArrayList<>(poissons.length);
        double totalEnergy = 0;
        for (Poisson p : poissons) {
            if (p.alive) {
                survivors.add(p);
                totalEnergy += p.energy;
            } else {
                stats.recordDeath();
            }
        }

        for (Poisson n : newborns) {
            survivors.add(n);
            stats.recordBirth();
            totalEnergy += n.energy;
        }

        if (!survivors.isEmpty()) {
            stats.setAvgEnergy(totalEnergy / survivors.size());
        }

        poissons = survivors.toArray(new Poisson[0]);
    }

    public void MiseAJourOcean() {
        // Environment first
        environmentalField.tick(1.0);
        planktons.forEach(PlanktonPatch::regenerate);
        planktons.removeIf(PlanktonPatch::isDepleted);
        // Occasionally spawn new plankton
        if (planktons.size() < SimulationConfig.INITIAL_PLANKTON_PATCHES * 2 &&
                generateur.nextDouble() < 0.02) {
            spawnPlanktonPatch();
        }
        
        // Update algae swaying
        updateAlgae();

        MiseAJourObstacles();
        MiseAJourPoissons();
        MiseAJourSharks();
        MiseAJourFisherman();
        frameCount++;
        support.firePropertyChange("oceanUpdated", null, this);
    }
    
    private void updateAlgae() {
        for (Algae a : algae) {
            // Get current at algae base position
            int cellX = (int) (a.baseX / environmentalField.getCellWidth());
            int cellY = (int) (a.baseY / environmentalField.getCellHeight());
            EnvironmentalField.VectorCell[][] cells = environmentalField.getCells();
            if (cellX >= 0 && cellX < cells.length && cellY >= 0 && cellY < cells[0].length) {
                double vx = cells[cellX][cellY].vx;
                double vy = cells[cellX][cellY].vy;
                a.update(frameCount, vx, vy);
            } else {
                a.update(frameCount, 0, 0);
            }
        }
    }

    // Cleanup
    public void shutdown() {
        executor.shutdown();
    }
}
