package com.dtp5.model;

import com.dtp5.config.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Ocean class.
 * Tests initialization, entity management, and simulation updates.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
class OceanTest {

    private Ocean ocean;
    private static final int TEST_FISH_COUNT = 50;
    private static final double TEST_WIDTH = 800;
    private static final double TEST_HEIGHT = 600;

    @BeforeEach
    void setUp() {
        ocean = new Ocean(TEST_FISH_COUNT, TEST_WIDTH, TEST_HEIGHT);
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Ocean should initialize with correct dimensions")
        void shouldInitializeWithCorrectDimensions() {
            assertEquals(TEST_WIDTH, ocean.getWidth());
            assertEquals(TEST_HEIGHT, ocean.getHeight());
        }

        @Test
        @DisplayName("Ocean should initialize with correct fish count")
        void shouldInitializeWithCorrectFishCount() {
            assertEquals(TEST_FISH_COUNT, ocean.poissons.length);
        }

        @Test
        @DisplayName("All fish should be alive initially")
        void allFishShouldBeAliveInitially() {
            for (Poisson fish : ocean.poissons) {
                assertTrue(fish.alive, "Fish should be alive initially");
            }
        }

        @Test
        @DisplayName("Ocean should have environmental field")
        void shouldHaveEnvironmentalField() {
            assertNotNull(ocean.environmentalField);
        }

        @Test
        @DisplayName("Ocean should have day/night cycle")
        void shouldHaveDayNightCycle() {
            assertNotNull(ocean.dayNightCycle);
        }

        @Test
        @DisplayName("Ocean should have particle system")
        void shouldHaveParticleSystem() {
            assertNotNull(ocean.particleSystem);
        }

        @Test
        @DisplayName("Ocean should initialize with rocks")
        void shouldHaveRocks() {
            assertFalse(ocean.rocks.isEmpty(), "Ocean should have rocks");
        }

        @Test
        @DisplayName("Ocean should initialize with algae")
        void shouldHaveAlgae() {
            assertFalse(ocean.algae.isEmpty(), "Ocean should have algae");
        }

        @Test
        @DisplayName("Ocean should initialize with coral")
        void shouldHaveCoral() {
            assertFalse(ocean.corals.isEmpty(), "Ocean should have coral");
        }

        @Test
        @DisplayName("Ocean should initialize with jellyfish")
        void shouldHaveJellyfish() {
            assertFalse(ocean.jellyfish.isEmpty(), "Ocean should have jellyfish");
        }
    }

    @Nested
    @DisplayName("Entity Management Tests")
    class EntityManagementTests {

        @Test
        @DisplayName("Should add fish correctly")
        void shouldAddFish() {
            int initialCount = ocean.poissons.length;
            ocean.addFish(10);
            assertEquals(initialCount + 10, ocean.poissons.length);
        }

        @Test
        @DisplayName("Should not exceed max fish limit")
        void shouldNotExceedMaxFish() {
            ocean.addFish(SimulationConfig.MAX_FISH * 2);
            assertTrue(ocean.poissons.length <= SimulationConfig.MAX_FISH);
        }

        @Test
        @DisplayName("Should add shark correctly")
        void shouldAddShark() {
            int initialCount = ocean.sharks.size();
            ocean.addShark();
            assertEquals(initialCount + 1, ocean.sharks.size());
        }

        @Test
        @DisplayName("Should not exceed max shark limit")
        void shouldNotExceedMaxSharks() {
            for (int i = 0; i < SimulationConfig.MAX_SHARKS + 10; i++) {
                ocean.addShark();
            }
            assertTrue(ocean.sharks.size() <= SimulationConfig.MAX_SHARKS);
        }

        @Test
        @DisplayName("Should add jellyfish")
        void shouldAddJellyfish() {
            int initialCount = ocean.jellyfish.size();
            ocean.addJellyfish();
            assertEquals(initialCount + 1, ocean.jellyfish.size());
        }

        @Test
        @DisplayName("Should add sea turtle")
        void shouldAddSeaTurtle() {
            int initialCount = ocean.seaTurtles.size();
            ocean.addSeaTurtle();
            assertEquals(initialCount + 1, ocean.seaTurtles.size());
        }

        @Test
        @DisplayName("Should add obstacle")
        void shouldAddObstacle() {
            int initialCount = ocean.obstacles.size();
            ocean.addObstacle(100, 100, 30);
            assertEquals(initialCount + 1, ocean.obstacles.size());
        }

        @Test
        @DisplayName("Should spawn plankton patch")
        void shouldSpawnPlankton() {
            int initialCount = ocean.planktons.size();
            ocean.spawnPlanktonPatch();
            assertEquals(initialCount + 1, ocean.planktons.size());
        }
    }

    @Nested
    @DisplayName("Simulation Update Tests")
    class SimulationUpdateTests {

        @Test
        @DisplayName("Frame count should increment on update")
        void frameCountShouldIncrement() {
            long initialFrame = ocean.getFrameCount();
            ocean.updateOcean();
            assertEquals(initialFrame + 1, ocean.getFrameCount());
        }

        @Test
        @DisplayName("Multiple updates should work without error")
        void multipleUpdatesShouldWork() {
            assertDoesNotThrow(() -> {
                for (int i = 0; i < 100; i++) {
                    ocean.updateOcean();
                }
            });
        }

        @Test
        @DisplayName("Fish positions should change after update")
        void fishPositionsShouldChange() {
            double initialX = ocean.poissons[0].posX;
            double initialY = ocean.poissons[0].posY;

            for (int i = 0; i < 10; i++) {
                ocean.updateOcean();
            }

            // At least one coordinate should have changed
            boolean positionChanged = ocean.poissons[0].posX != initialX ||
                    ocean.poissons[0].posY != initialY;

            assertTrue(positionChanged, "Fish position should change after updates");
        }

        @Test
        @DisplayName("Day/night cycle should progress")
        void dayNightShouldProgress() {
            double initialPhase = ocean.dayNightCycle.getPhase();

            for (int i = 0; i < 100; i++) {
                ocean.updateOcean();
            }

            assertNotEquals(initialPhase, ocean.dayNightCycle.getPhase());
        }
    }

    @Nested
    @DisplayName("Fisherman Tests")
    class FishermanTests {

        @Test
        @DisplayName("Should toggle fisherman")
        void shouldToggleFisherman() {
            boolean initialState = ocean.fisherman.isFishing;
            ocean.toggleFisherman();
            assertNotEquals(initialState, ocean.fisherman.isFishing);
        }
    }

    @Nested
    @DisplayName("Display Flag Tests")
    class DisplayFlagTests {

        @Test
        @DisplayName("Should toggle currents display")
        void shouldToggleCurrents() {
            boolean initial = ocean.showCurrents;
            ocean.showCurrents = !ocean.showCurrents;
            assertNotEquals(initial, ocean.showCurrents);
        }

        @Test
        @DisplayName("Should toggle day/night")
        void shouldToggleDayNight() {
            boolean initial = ocean.dayNightEnabled;
            ocean.dayNightEnabled = !ocean.dayNightEnabled;
            assertNotEquals(initial, ocean.dayNightEnabled);
        }

        @Test
        @DisplayName("Should toggle particles")
        void shouldToggleParticles() {
            boolean initial = ocean.particlesEnabled;
            ocean.particlesEnabled = !ocean.particlesEnabled;
            assertNotEquals(initial, ocean.particlesEnabled);
        }
    }

    @Test
    @DisplayName("Should shutdown cleanly")
    void shouldShutdownCleanly() {
        assertDoesNotThrow(() -> ocean.shutdown());
    }
}
