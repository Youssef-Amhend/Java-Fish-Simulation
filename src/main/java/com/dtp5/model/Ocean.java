package com.dtp5.model;

import com.dtp5.config.SimulationConfig;
import com.dtp5.event.EventBus;
import com.dtp5.event.FishBornEvent;
import com.dtp5.event.FishDeathEvent;
import com.dtp5.particle.ParticleSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Represents the ocean environment containing all simulation entities.
 * <p>
 * This is the main simulation class that manages:
 * <ul>
 * <li>Fish (Poisson) with schooling behavior</li>
 * <li>Predators (Sharks)</li>
 * <li>New creatures (Jellyfish, Sea Turtles)</li>
 * <li>Environmental features (rocks, algae, coral, plankton)</li>
 * <li>Environmental systems (currents, day/night cycle)</li>
 * <li>Visual effects (particle system)</li>
 * </ul>
 * 
 * Uses multithreading for high-performance updates with thousands of entities.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class Ocean {

    private static final Logger logger = LoggerFactory.getLogger(Ocean.class);

    // ==================== ENTITIES ====================

    /** Fish array (uses array for performance, resized as needed) */
    public Poisson[] poissons;

    /** Sharks (thread-safe list) */
    public final List<Shark> sharks;

    /** Jellyfish */
    public final List<Jellyfish> jellyfish;

    /** Sea turtles */
    public final List<SeaTurtle> seaTurtles;

    /** Temporary obstacles (click-created) */
    public final List<ZoneAEviter> obstacles;

    /** Plankton food patches */
    public final List<PlanktonPatch> planktons;

    /** Rocks on ocean floor */
    public final List<Rock> rocks;

    /** Algae/seaweed */
    public final List<Algae> algae;

    /** Coral formations */
    public final List<Coral> corals;

    /** Fisherman hook */
    public Fisherman fisherman;

    // ==================== SYSTEMS ====================

    /** Environmental current and temperature field */
    public EnvironmentalField environmentalField;

    /** Day/night cycle manager */
    public DayNightCycle dayNightCycle;

    /** Particle system for visual effects */
    public ParticleSystem particleSystem;

    /** Statistics tracker */
    public SimulationStats stats;

    // ==================== DISPLAY FLAGS ====================

    /** Whether to show current vectors */
    public boolean showCurrents = true;

    /** Whether to show plankton patches */
    public boolean showPlankton = true;

    /** Whether day/night cycle is enabled */
    public boolean dayNightEnabled = true;

    /** Whether particles are enabled */
    public boolean particlesEnabled = true;

    // ==================== INTERNAL STATE ====================

    /** Random number generator */
    protected final Random random;

    /** Ocean width in pixels */
    protected double width;

    /** Ocean height in pixels */
    protected double height;

    /** Spatial partitioning grid for efficient neighbor queries */
    private SpatialGrid spatialGrid;

    /** Thread pool for parallel fish updates */
    private ExecutorService executor;

    /** Number of worker threads */
    private final int numThreads;

    /** Property change support for UI updates */
    private final PropertyChangeSupport support;

    /** Current frame number */
    private long frameCount = 0;

    /** Event bus for decoupled communication */
    private final EventBus eventBus;

    /** Bubble spawn timer */
    private int bubbleTimer = 0;

    // ==================== CONSTRUCTOR ====================

    /**
     * Creates a new ocean with the specified dimensions and initial fish count.
     * 
     * @param initialFishCount Number of fish to spawn initially
     * @param width            Ocean width in pixels
     * @param height           Ocean height in pixels
     */
    public Ocean(int initialFishCount, double width, double height) {
        logger.info("Initializing ocean: {}x{} with {} fish", width, height, initialFishCount);

        this.width = width <= 0 ? SimulationConfig.WINDOW_WIDTH : width;
        this.height = height <= 0 ? SimulationConfig.WINDOW_HEIGHT : height;
        this.random = new Random();
        this.support = new PropertyChangeSupport(this);
        this.eventBus = EventBus.getInstance();
        this.stats = new SimulationStats();

        // Initialize thread-safe collections
        this.sharks = new CopyOnWriteArrayList<>();
        this.jellyfish = new CopyOnWriteArrayList<>();
        this.seaTurtles = new CopyOnWriteArrayList<>();
        this.obstacles = new CopyOnWriteArrayList<>();
        this.planktons = new CopyOnWriteArrayList<>();
        this.rocks = new ArrayList<>();
        this.algae = new ArrayList<>();
        this.corals = new ArrayList<>();

        // Initialize systems
        this.spatialGrid = new SpatialGrid(this.width, this.height, SimulationConfig.GRID_CELL_SIZE);
        this.environmentalField = new EnvironmentalField(this.width, this.height);
        this.dayNightCycle = new DayNightCycle();
        this.particleSystem = new ParticleSystem();
        this.fisherman = new Fisherman(this.width / 2);

        // Initialize thread pool
        this.numThreads = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(numThreads);
        logger.debug("Using {} worker threads", numThreads);

        // Spawn initial entities
        spawnInitialFish(initialFishCount);
        spawnInitialPlankton();
        initializeEnvironment();

        logger.info("Ocean initialization complete");
    }

    // ==================== INITIALIZATION ====================

    private void spawnInitialFish(int count) {
        poissons = new Poisson[count];
        for (int i = 0; i < count; i++) {
            poissons[i] = new Poisson(
                    random.nextDouble() * width,
                    random.nextDouble() * height,
                    random.nextDouble() * 2 * Math.PI);
        }
    }

    private void spawnInitialPlankton() {
        for (int i = 0; i < SimulationConfig.INITIAL_PLANKTON_PATCHES; i++) {
            spawnPlanktonPatch();
        }
    }

    private void initializeEnvironment() {
        initializeRocks();
        initializeAlgae();
        initializeCoral();
        initializeCreatures();
    }

    private void initializeRocks() {
        int numRocks = 8 + random.nextInt(5);
        for (int i = 0; i < numRocks; i++) {
            double x = random.nextDouble() * width;
            double y = height - 20 - random.nextDouble() * 100;

            Rock.RockType type;
            double rand = random.nextDouble();
            if (rand < 0.3) {
                type = Rock.RockType.SMALL_ROCK;
            } else if (rand < 0.7) {
                type = Rock.RockType.MEDIUM_ROCK;
            } else if (rand < 0.9) {
                type = Rock.RockType.LARGE_ROCK;
            } else {
                type = Rock.RockType.REEF_CLUSTER;
            }

            rocks.add(new Rock(x, y, type, random));
        }
    }

    private void initializeAlgae() {
        int numAlgae = 25 + random.nextInt(15);
        for (int i = 0; i < numAlgae; i++) {
            double x = random.nextDouble() * width;
            double y = height - 10 - random.nextDouble() * 50;

            Algae.AlgaeType type;
            double rand = random.nextDouble();
            if (rand < 0.3) {
                type = Algae.AlgaeType.SHORT_ALGAE;
            } else if (rand < 0.6) {
                type = Algae.AlgaeType.MEDIUM_ALGAE;
            } else if (rand < 0.85) {
                type = Algae.AlgaeType.TALL_ALGAE;
            } else {
                type = Algae.AlgaeType.KELP;
            }

            algae.add(new Algae(x, y, type, random));
        }
    }

    private void initializeCoral() {
        int numCoral = 10 + random.nextInt(8);
        for (int i = 0; i < numCoral; i++) {
            double x = random.nextDouble() * width;
            double y = height - 15 - random.nextDouble() * 80;
            corals.add(Coral.createRandom(x, y, random));
        }
    }

    private void initializeCreatures() {
        // Add a few jellyfish
        int numJellyfish = 3 + random.nextInt(4);
        for (int i = 0; i < numJellyfish; i++) {
            double x = random.nextDouble() * width;
            double y = height * 0.3 + random.nextDouble() * height * 0.5;
            jellyfish.add(new Jellyfish(x, y));
        }

        // Add 1-2 sea turtles (rare)
        if (random.nextDouble() < 0.7) {
            double x = random.nextDouble() * width;
            double y = height * 0.4 + random.nextDouble() * height * 0.3;
            seaTurtles.add(new SeaTurtle(x, y));
        }
    }

    // ==================== ENTITY MANAGEMENT ====================

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Adds a temporary obstacle at the specified position.
     */
    public void addObstacle(double x, double y, double radius) {
        obstacles.add(new ZoneAEviter(x, y, radius));

        // Spawn splash particles
        if (particlesEnabled) {
            particleSystem.spawnSplash(x, y, 10);
        }
    }

    /**
     * Legacy method name for backwards compatibility.
     */
    public void AjouterObstacle(double x, double y, double radius) {
        addObstacle(x, y, radius);
    }

    /**
     * Adds a new shark to the ocean.
     */
    public void addShark() {
        if (sharks.size() >= SimulationConfig.MAX_SHARKS) {
            logger.debug("Maximum sharks reached");
            return;
        }

        Shark shark = new Shark(
                random.nextDouble() * width,
                random.nextDouble() * height,
                random.nextDouble() * 2 * Math.PI);
        sharks.add(shark);
        logger.debug("Added shark, total: {}", sharks.size());
    }

    /**
     * Adds a new jellyfish to the ocean.
     */
    public void addJellyfish() {
        if (jellyfish.size() >= 15) {
            return;
        }

        double x = random.nextDouble() * width;
        double y = height * 0.3 + random.nextDouble() * height * 0.4;
        jellyfish.add(new Jellyfish(x, y));
    }

    /**
     * Adds a new sea turtle to the ocean.
     */
    public void addSeaTurtle() {
        if (seaTurtles.size() >= 5) {
            return;
        }

        double x = random.nextDouble() * width;
        double y = height * 0.3 + random.nextDouble() * height * 0.4;
        seaTurtles.add(new SeaTurtle(x, y));
    }

    /**
     * Toggles the fisherman fishing state.
     */
    public void toggleFisherman() {
        if (fisherman.isFishing) {
            fisherman.isFishing = false;
        } else {
            fisherman.startFishing(
                    random.nextDouble() * width,
                    height * 0.8);

            // Splash effect
            if (particlesEnabled) {
                particleSystem.spawnSplash(fisherman.posX, 10, 8);
            }
        }
    }

    /**
     * Adds a single fish to the ocean.
     */
    public void addFish() {
        addFish(1);
    }

    /**
     * Adds multiple fish to the ocean.
     */
    public void addFish(int count) {
        if (poissons.length + count > SimulationConfig.MAX_FISH) {
            count = SimulationConfig.MAX_FISH - poissons.length;
            if (count <= 0)
                return;
        }

        Poisson[] newPoissons = new Poisson[poissons.length + count];
        System.arraycopy(poissons, 0, newPoissons, 0, poissons.length);

        for (int i = 0; i < count; i++) {
            newPoissons[poissons.length + i] = new Poisson(
                    random.nextDouble() * width,
                    random.nextDouble() * height,
                    random.nextDouble() * 2 * Math.PI);
        }
        poissons = newPoissons;
    }

    /**
     * Spawns a new plankton patch.
     */
    public void spawnPlanktonPatch() {
        planktons.add(new PlanktonPatch(
                random.nextDouble() * width,
                random.nextDouble() * height,
                SimulationConfig.PLANKTON_MAX_BIOMASS * 0.5));
    }

    /**
     * Gets the current frame count.
     */
    public long getFrameCount() {
        return frameCount;
    }

    /**
     * Gets the ocean width.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the ocean height.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Updates the ocean dimensions when the window is resized.
     * This ensures fish stay within the visible boundaries.
     */
    public void setDimensions(double newWidth, double newHeight) {
        if (newWidth > 0 && newHeight > 0) {
            this.width = newWidth;
            this.height = newHeight;

            // Update spatial grid for new dimensions
            this.spatialGrid = new SpatialGrid(newWidth, newHeight, SimulationConfig.GRID_CELL_SIZE);

            // Update environmental field
            this.environmentalField = new EnvironmentalField(newWidth, newHeight);
        }
    }

    // ==================== UPDATE METHODS ====================

    /**
     * Main update method - updates all systems and entities.
     */
    public void updateOcean() {
        // Update environmental systems
        environmentalField.tick(1.0);

        if (dayNightEnabled) {
            dayNightCycle.tick();
        }

        // Update particles
        if (particlesEnabled) {
            particleSystem.update();
            spawnAmbientBubbles();
        }

        // Update plankton
        planktons.forEach(PlanktonPatch::regenerate);
        planktons.removeIf(PlanktonPatch::isDepleted);

        // Occasionally spawn new plankton
        if (planktons.size() < SimulationConfig.INITIAL_PLANKTON_PATCHES * 2 &&
                random.nextDouble() < 0.02) {
            spawnPlanktonPatch();
        }

        // Update algae sway
        updateAlgae();

        // Update coral sway
        updateCoral();

        // Update obstacles
        updateObstacles();

        // Update fish (parallel)
        updateFish();

        // Update sharks
        updateSharks();

        // Update new creatures
        updateJellyfish();
        updateSeaTurtles();

        // Update fisherman
        updateFisherman();

        // Dynamic fish population - maintain minimum count
        maintainFishPopulation();

        frameCount++;
        support.firePropertyChange("oceanUpdated", null, this);
    }

    /**
     * Legacy method name for backwards compatibility.
     */
    public void MiseAJourOcean() {
        updateOcean();
    }

    private void spawnAmbientBubbles() {
        bubbleTimer++;
        if (bubbleTimer > 30) {
            bubbleTimer = 0;

            // Bubbles from rocks
            if (!rocks.isEmpty() && random.nextDouble() < 0.3) {
                Rock rock = rocks.get(random.nextInt(rocks.size()));
                particleSystem.spawnBubble(rock.posX, rock.posY - 10);
            }

            // Bubbles from algae
            if (!algae.isEmpty() && random.nextDouble() < 0.2) {
                Algae a = algae.get(random.nextInt(algae.size()));
                particleSystem.spawnBubble(a.baseX, a.baseY - a.height);
            }
        }
    }

    private void updateObstacles() {
        for (ZoneAEviter obstacle : obstacles) {
            obstacle.MiseAJour();
        }
        obstacles.removeIf(ZoneAEviter::estMort);
    }

    private void updateAlgae() {
        EnvironmentalField.VectorCell[][] cells = environmentalField.getCells();
        double cellW = environmentalField.getCellWidth();
        double cellH = environmentalField.getCellHeight();

        for (Algae a : algae) {
            int cellX = (int) (a.baseX / cellW);
            int cellY = (int) (a.baseY / cellH);

            if (cellX >= 0 && cellX < cells.length && cellY >= 0 && cellY < cells[0].length) {
                double vx = cells[cellX][cellY].vx;
                double vy = cells[cellX][cellY].vy;
                a.update(frameCount, vx, vy);
            } else {
                a.update(frameCount, 0, 0);
            }
        }
    }

    private void updateCoral() {
        EnvironmentalField.VectorCell[][] cells = environmentalField.getCells();
        double cellW = environmentalField.getCellWidth();
        double cellH = environmentalField.getCellHeight();

        for (Coral c : corals) {
            int cellX = (int) (c.posX / cellW);
            int cellY = (int) (c.posY / cellH);

            double vx = 0, vy = 0;
            if (cellX >= 0 && cellX < cells.length && cellY >= 0 && cellY < cells[0].length) {
                vx = cells[cellX][cellY].vx;
                vy = cells[cellX][cellY].vy;
            }
            c.update(vx, vy);
        }
    }

    private void updateSharks() {
        for (Shark s : sharks) {
            List<Poisson> nearby = spatialGrid.getNearbyFish(s);
            s.MiseAJourShark(nearby, width, height);
        }
    }

    private void updateJellyfish() {
        SimulationContext context = createContext();
        for (Jellyfish j : jellyfish) {
            j.update(context);
        }
    }

    private void updateSeaTurtles() {
        SimulationContext context = createContext();
        for (SeaTurtle t : seaTurtles) {
            t.update(context);
        }
    }

    private void updateFisherman() {
        fisherman.update();

        if (fisherman.isFishing) {
            List<Poisson> caught = new ArrayList<>();
            for (Poisson p : poissons) {
                if (fisherman.checkCatch(p)) {
                    caught.add(p);
                }
            }

            if (!caught.isEmpty()) {
                removeFish(caught);
                fisherman.isFishing = false;
                fisherman.movingDown = false;

                // Splash effect
                if (particlesEnabled) {
                    particleSystem.spawnSplash(fisherman.posX, fisherman.posY, 5);
                }
            }
        }
    }

    /**
     * Maintains minimum fish population by spawning new fish when count drops too
     * low.
     */
    private void maintainFishPopulation() {
        int currentCount = poissons.length;
        int minCount = SimulationConfig.MIN_FISH;

        if (currentCount < minCount) {
            // Spawn fish gradually (up to 5 per frame to avoid sudden visual jumps)
            int toSpawn = Math.min(5, minCount - currentCount);

            Poisson[] newPoissons = new Poisson[currentCount + toSpawn];
            System.arraycopy(poissons, 0, newPoissons, 0, currentCount);

            for (int i = 0; i < toSpawn; i++) {
                // Spawn at random edge of screen for natural entry
                double x, y;
                if (random.nextBoolean()) {
                    // Spawn from sides
                    x = random.nextBoolean() ? 10 : width - 10;
                    y = random.nextDouble() * height;
                } else {
                    // Spawn from top/bottom
                    x = random.nextDouble() * width;
                    y = random.nextBoolean() ? 10 : height - 10;
                }

                newPoissons[currentCount + i] = new Poisson(
                        x, y,
                        random.nextDouble() * 2 * Math.PI);

                stats.recordBirth();
            }

            poissons = newPoissons;
            logger.debug("Spawned {} fish to maintain minimum population", toSpawn);
        }
    }

    private void removeFish(List<Poisson> toRemove) {
        if (toRemove.isEmpty())
            return;

        // Fire death events
        for (Poisson p : toRemove) {
            eventBus.publish(new FishDeathEvent(p, FishDeathEvent.DeathCause.CAUGHT_BY_FISHERMAN, frameCount));
        }

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
    private void updateFish() {
        // 1. Rebuild Spatial Grid
        spatialGrid.clear();
        for (Poisson p : poissons) {
            spatialGrid.addFish(p);
        }

        // 2. Parallel Update
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
                        List<Poisson> nearby = spatialGrid.getNearbyFish(p);
                        p.MiseAJour(nearby, new ArrayList<>(obstacles), sharks,
                                planktons, environmentalField, width, height);

                        if (!p.alive)
                            continue;

                        // Reproduction
                        if (p.energy > SimulationConfig.REPRODUCTION_THRESHOLD &&
                                poissons.length + newborns.size() < SimulationConfig.MAX_FISH) {

                            p.energy -= SimulationConfig.REPRODUCTION_COST;
                            Poisson child = new Poisson(
                                    p.posX + random.nextGaussian() * 4,
                                    p.posY + random.nextGaussian() * 4,
                                    random.nextDouble() * 2 * Math.PI,
                                    p.species);
                            newborns.add(child);
                        }
                    }
                    return null;
                });
            }
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            logger.error("Fish update interrupted", e);
            Thread.currentThread().interrupt();
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
                eventBus.publish(new FishDeathEvent(p, FishDeathEvent.DeathCause.STARVATION, frameCount));
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

    private SimulationContext createContext() {
        return new SimulationContext(
                width, height, 1.0, frameCount,
                spatialGrid, environmentalField,
                planktons, sharks, new ArrayList<>(obstacles),
                dayNightCycle);
    }

    // ==================== CLEANUP ====================

    /**
     * Shuts down the ocean and releases resources.
     */
    public void shutdown() {
        logger.info("Shutting down ocean");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
