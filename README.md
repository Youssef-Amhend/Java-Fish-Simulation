# 🌊 Ocean Ecosystem Simulation

<div align="center">

![Java](https://img.shields.io/badge/Java-21+-orange?style=for-the-badge&logo=openjdk)
![Swing](https://img.shields.io/badge/Swing-UI-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**A stunning, high-performance ocean simulation featuring realistic fish schooling, predator-prey dynamics, day/night cycles, and beautiful visual effects.**

[Features](#-features) • [Getting Started](#-getting-started) • [Controls](#-controls) • [Architecture](#-architecture) • [Configuration](#-configuration)

</div>

---

## ✨ Features

### 🐟 Marine Life
- **Schooling Fish** - Boids-style flocking with species-specific colors, sizes, and behaviors
- **Sharks** 🦈 - Predators that hunt and chase fish with intelligent pursuit AI
- **Jellyfish** 🎐 - Bioluminescent creatures with pulsating movement and tentacle physics
- **Sea Turtles** 🐢 - Gentle wandering creatures with flipper animations
- **Dynamic Population** - Fish automatically respawn to maintain ecosystem balance

### 🌅 Environmental Systems
- **Day/Night Cycle** - Dynamic lighting with sunrise, sunset, moonrise, and stars
- **Ocean Currents** - Procedural flow fields that affect all creature movement
- **Plankton Patches** - Regenerating food sources that fish consume for energy
- **Temperature Gradients** - Visual temperature zones affecting the ecosystem

### 🎨 Visual Effects
- **Caustic Lighting** - Animated underwater light patterns
- **Particle System** - Bubbles, splash effects, and ambient particles
- **Coral Reefs** - Procedurally generated coral with swaying animations
- **Seaweed & Algae** - Current-reactive vegetation on the ocean floor
- **Glass Vignette** - Aquarium-style edge effects

### 🎮 Interactive Features
- **Fullscreen Mode** - Immersive game-like experience (launches fullscreen by default)
- **Fisherman Hook** 🎣 - Drop a fishing line to catch fish
- **Click Obstacles** - Click anywhere to create temporary obstacles
- **Real-time Statistics** - FPS, population counts, births, deaths, and energy levels

---

## 🚀 Getting Started

### Prerequisites
- **Java 21+** (with preview features enabled)
- **Maven** (for building)
- Any Java IDE (IntelliJ IDEA recommended)

### Running the Simulation

#### Option 1: From IDE
1. Open the project in IntelliJ IDEA
2. Run `Main.java` in `src/main/java/com/dtp5/`

#### Option 2: Build Executable JAR (Recommended)
```bash
# Build the project (creates executable JAR with all dependencies)
mvn clean package -DskipTests
```

Then run using one of these methods:

**Windows:** Double-click `run.bat` or run:
```batch
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

**Or directly with Java:**
```bash
java --enable-preview -jar target/ocean-ecosystem-simulator-2.0.0.jar
```

#### Option 3: Using Maven Exec
```bash
mvn exec:java -Dexec.mainClass="com.dtp5.Main"
```

### First Launch
The simulation starts in **fullscreen mode** for an immersive experience. Press `F11` or `ESC` to toggle windowed mode.

---

## 🎮 Controls

### Keyboard
| Key | Action |
|-----|--------|
| `F11` | Toggle fullscreen |
| `ESC` | Toggle fullscreen |

### Mouse
| Action | Effect |
|--------|--------|
| **Click anywhere** | Create temporary obstacle (fish avoid it) |

### Control Panel Buttons

| Button | Description |
|--------|-------------|
| ⏸ **Pause** | Pause/resume simulation |
| ❌ **Exit** | Close the application |
| 🐟 **Fish** | Add fish (click) or select quantity from dropdown |
| 🦈 **Shark** | Add a hunting shark |
| 🎐 **Jelly** | Add a jellyfish |
| 🐢 **Turtle** | Add a sea turtle |
| 🎣 **Fisher** | Drop fishing hook |
| 🟢 **Plankton** | Spawn a food patch |
| 🌊 **Currents** | Toggle current visualization |
| 🌙 **Day/Night** | Toggle day/night cycle |
| 💫 **Bubbles** | Toggle particle effects |
| 😌 **Calm** | Set calm current pattern |
| 🌀 **Swirl** | Set swirling current pattern |
| 🌪️ **Whirl** | Set whirlpool current pattern |

### Sliders
- **Current** - Adjust current strength
- **Time** - Control day/night cycle speed
- **Sim Speed** - Adjust simulation speed (10% - 200%)

---

## 🏗️ Architecture

```
src/main/java/com/dtp5/
├── Main.java                 # Application entry point
├── config/
│   └── SimulationConfig.java # All configurable constants
├── model/
│   ├── Ocean.java           # Main simulation engine
│   ├── Poisson.java         # Fish with boids behavior
│   ├── Shark.java           # Predator AI
│   ├── Jellyfish.java       # Pulsating jellyfish
│   ├── SeaTurtle.java       # Wandering turtle
│   ├── SpatialGrid.java     # O(1) neighbor lookups
│   ├── EnvironmentalField.java # Current/temperature system
│   ├── DayNightCycle.java   # Time and lighting
│   └── ...
├── renderer/
│   ├── FishRenderer.java    # Beautiful fish drawing
│   ├── SharkRenderer.java   # Shark visualization
│   ├── CoralRenderer.java   # Procedural coral
│   └── ...
├── particle/
│   ├── ParticleSystem.java  # Bubble & splash effects
│   └── Particle.java
├── ui/
│   ├── OceanJPanel.java     # Main rendering panel
│   └── ControlPanel.java    # Modern UI controls
└── event/
    └── EventBus.java        # Decoupled event system
```

### Key Design Patterns
- **Multithreaded Updates** - Fish updates distributed across CPU cores
- **Spatial Partitioning** - `SpatialGrid` for efficient neighbor queries
- **Double Buffering** - Smooth, flicker-free rendering
- **Entity-Component Pattern** - Modular creature design
- **Observer Pattern** - Event bus for decoupled communication

---

## ⚙️ Configuration

Edit `SimulationConfig.java` to customize the simulation:

```java
// Window
public static final boolean START_FULLSCREEN = true;

// Population
public static final int INITIAL_FISH_COUNT = 300;
public static final int MIN_FISH = 20;        // Auto-respawn threshold
public static final int MAX_FISH = 1800;

// Performance (for 100+ FPS)
public static final int TIMER_DELAY_MS = 8;   // Lower = faster

// Ecosystem
public static final double BASE_ENERGY = 1200.0;
public static final double REPRODUCTION_THRESHOLD = 1400.0;

// Visuals
public static final int CAUSTIC_COUNT = 15;
```

---

## 🎯 Performance Tips

To achieve **100+ FPS**:
1. ✅ Timer delay set to 8ms (already configured)
2. ✅ Rendering hints optimized for speed
3. Toggle off **Currents** display (reduces arrow rendering)
4. Toggle off **Day/Night** cycle (reduces gradient calculations)
5. Toggle off **Bubbles** (reduces particle count)

---

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Made with 💙 for ocean lovers**

*A Java Swing demonstration of ecosystem simulation, spatial algorithms, and real-time graphics*

</div>
