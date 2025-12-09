## Ocean Ecosystem Simulator

High-performance Swing simulation that visualizes a living ocean with schooling fish, sharks, regenerating plankton fields, environmental currents, and interactive controls. The project demonstrates multithreaded updates, spatial partitioning, and rich rendering tuned for Java 21.

### Features
- Real-time boids-style schooling with species-specific shapes, speeds, and colors.
- Predator–prey loop with sharks chasing fish and fishermen hooks that can catch fish.
- Regenerating plankton food patches; fish feed to regain energy and can reproduce when thriving.
- Procedural current and temperature field that pushes fish and changes the ecosystem mood.
- Spatial grid to keep neighbor queries efficient even with thousands of fish.
- Interactive UI: pause/resume, speed control, add fish/sharks/plankton, toggle currents overlay, trigger fishing.
- Cinematic rendering: gradients, caustics, trails, shadows, obstacle ripples, and double buffering for smooth frames.
- Live stats: FPS, population, obstacles, births/deaths, and average fish energy.

### Getting Started
1. Ensure Java 21+ and Maven are installed.
2. From the project root, run:
   - `mvn clean package`
   - `mvn exec:java -Dexec.mainClass="com.dtp5.Main"` (or run `Main` from your IDE)
3. Use F11 or Esc to toggle fullscreen. Window resizes are supported.

### Controls
- `Pause/Resume`: stop or resume the simulation loop.
- `Speed`: slider adjusts timer delay (higher = faster sim).
- `🐟 Add Fish` / `🦈 Shark`: inject new agents (capped to sensible limits).
- `🟢 Plankton`: add a new food patch.
- `🌊 Currents`: toggle the current/temperature vector overlay.
- `🎣 Fish`: drop the hook; it retracts automatically after a catch or reaching depth.
- Mouse click anywhere in the ocean to drop a temporary obstacle that fish avoid.

### Architecture Highlights
- **Multithreaded updates**: fish updates split across CPU cores; shared state synchronized via coarse barriers.
- **Spatial partitioning**: `SpatialGrid` prunes neighbor searches to 3×3 cells.
- **Environmental systems**:
  - `EnvironmentalField` procedurally animates current vectors and a temperature gradient.
  - `PlanktonPatch` regenerates biomass; fish feed within a radius to restore energy.
- **Ecosystem lifecycle**: fish lose energy each tick, feed to survive, reproduce when above a threshold, and die when depleted; `SimulationStats` tracks births/deaths and mean energy.
- **Rendering**: double-buffered `OceanJPanel` draws layered effects (background gradient, caustics, plankton glow, current vectors, obstacles, fish/sharks, fisherman).
- **Configuration**: tune constants in `SimulationConfig` (counts, physics, energy economy, visuals, UI).

### Extending
- Add new species by updating `FishSpecies` with size/speed/palette parameters.
- Adjust ecosystem difficulty by changing energy costs/gains in `SimulationConfig`.
- Implement new predators or agents by following the `Poisson`/`Shark` pattern and adding a renderer.

### Known Limits
- No persistent saves; stats reset on restart.
- Simple collision detection for fishing hook and feeding; adequate for visualization.

### License
MIT (default for coursework/demo). Update this section if you need a different license.

