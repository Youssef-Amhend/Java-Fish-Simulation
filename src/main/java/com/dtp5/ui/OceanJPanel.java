package com.dtp5.ui;

import com.dtp5.config.SimulationConfig;
import com.dtp5.model.Ocean;
import com.dtp5.model.Poisson;
import com.dtp5.model.ZoneAEviter;
import com.dtp5.model.PlanktonPatch;
import com.dtp5.model.EnvironmentalField;
import com.dtp5.renderer.FishRenderer;
import com.dtp5.renderer.ObstacleRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.util.Random;

/**
 * Main panel for rendering the ocean simulation with beautiful graphics and
 * double buffering.
 */
public class OceanJPanel extends JPanel implements PropertyChangeListener, MouseListener {
    protected Ocean ocean;
    protected Timer timer;
    private ControlPanel controlPanel;
    private int baseDelay;

    // Double buffering for smooth rendering
    private BufferedImage backBuffer;
    private Graphics2D backGraphics;

    // Caustic light effect
    private Caustic[] caustics;
    private Random random = new Random();

    public OceanJPanel() {
        this.setBackground(SimulationConfig.OCEAN_BOTTOM_COLOR);
        this.addMouseListener(this);
        this.baseDelay = SimulationConfig.TIMER_DELAY_MS;

        // Initialize caustics
        caustics = new Caustic[SimulationConfig.CAUSTIC_COUNT];
        for (int i = 0; i < caustics.length; i++) {
            caustics[i] = new Caustic();
        }
    }

    public void Lancer() {
        ocean = new Ocean(
                SimulationConfig.INITIAL_FISH_COUNT,
                this.getWidth(),
                this.getHeight());
        ocean.addPropertyChangeListener(this);

        // Create and add control panel
        controlPanel = new ControlPanel(ocean);

        // Setup pause button
        controlPanel.getPauseButton().addActionListener(e -> {
            if (controlPanel.isPaused()) {
                timer.start();
                controlPanel.setPaused(false);
            } else {
                timer.stop();
                controlPanel.setPaused(true);
            }
        });

        // Setup speed slider
        controlPanel.getSpeedSlider().addChangeListener(e -> {
            int value = controlPanel.getSpeedSlider().getValue();
            int newDelay = (int) (baseDelay * 100.0 / value);
            timer.setDelay(Math.max(1, newDelay));
        });

        // Setup add fish button
        controlPanel.getAddFishButton().addActionListener(e -> {
            ocean.addFish();
        });

        // Start timer
        timer = new javax.swing.Timer(baseDelay, e -> {
            ocean.MiseAJourOcean();
            controlPanel.updateStats();
        });
        timer.start();

        // Initialize back buffer
        initBackBuffer();
    }

    private void initBackBuffer() {
        int w = getWidth();
        int h = getHeight();
        if (w > 0 && h > 0) {
            backBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            backGraphics = backBuffer.createGraphics();
            backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            backGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Reinitialize buffer if size changed
        if (backBuffer == null || backBuffer.getWidth() != getWidth() || backBuffer.getHeight() != getHeight()) {
            initBackBuffer();
        }

        if (backBuffer == null || ocean == null)
            return;

        // Render to back buffer
        renderScene(backGraphics);

        // Draw back buffer to screen (double buffering)
        g.drawImage(backBuffer, 0, 0, null);
    }

    /**
     * Renders the entire scene to the provided graphics context.
     */
    private void renderScene(Graphics2D g2d) {
        // Draw ocean background with depth
        drawEnhancedOceanBackground(g2d);

        // Draw caustic light effects
        drawCaustics(g2d);

        // Draw rocks at the bottom (behind everything)
        drawRocks(g2d);
        
        // Draw algae (behind fish but above rocks)
        drawAlgae(g2d);

        // Draw plankton fields beneath fish
        if (ocean.showPlankton) {
            drawPlankton(g2d);
        }

        if (ocean.showCurrents) {
            drawCurrents(g2d);
        }

        // Draw obstacles first (behind fish)
        for (ZoneAEviter o : ocean.obstacles) {
            ObstacleRenderer.render(o, g2d);
        }

        // Draw fish on top
        for (Poisson p : ocean.poissons) {
            FishRenderer.render(p, g2d, ocean.getFrameCount());
        }

        // Draw Sharks
        for (com.dtp5.model.Shark s : ocean.sharks) {
            com.dtp5.renderer.SharkRenderer.render(s, g2d);
        }

        // Draw Fisherman
        com.dtp5.renderer.FishermanRenderer.render(ocean.fisherman, g2d);

        drawGlassVignette(g2d);

        // Overlay control panel is now a Swing component at the top
    }

    private void drawRocks(Graphics2D g2d) {
        for (com.dtp5.model.Rock rock : ocean.rocks) {
            com.dtp5.renderer.RockRenderer.render(rock, g2d);
        }
    }
    
    private void drawAlgae(Graphics2D g2d) {
        long frameCount = ocean != null ? ocean.getFrameCount() : 0;
        EnvironmentalField.VectorCell[][] cells = ocean.environmentalField.getCells();
        double cellW = ocean.environmentalField.getCellWidth();
        double cellH = ocean.environmentalField.getCellHeight();
        
        for (com.dtp5.model.Algae a : ocean.algae) {
            // Get current at algae base
            int cellX = (int) (a.baseX / cellW);
            int cellY = (int) (a.baseY / cellH);
            double vx = 0, vy = 0;
            if (cellX >= 0 && cellX < cells.length && cellY >= 0 && cellY < cells[0].length) {
                vx = cells[cellX][cellY].vx;
                vy = cells[cellX][cellY].vy;
            }
            com.dtp5.renderer.AlgaeRenderer.render(a, g2d, frameCount, vx, vy);
        }
    }
    
    private void drawPlankton(Graphics2D g2d) {
        for (PlanktonPatch patch : ocean.planktons) {
            float alpha = (float) Math.min(0.45, 0.2 + patch.getBiomass() / SimulationConfig.PLANKTON_MAX_BIOMASS);
            g2d.setColor(new Color(80, 200, 120, (int) (alpha * 255)));
            double r = patch.getRadius();
            g2d.fillOval((int) (patch.posX - r), (int) (patch.posY - r), (int) (2 * r), (int) (2 * r));
        }
    }

    private void drawCurrents(Graphics2D g2d) {
        EnvironmentalField.VectorCell[][] cells = ocean.environmentalField.getCells();
        int width = getWidth();
        int height = getHeight();
        double cellW = width / (double) cells.length;
        double cellH = height / (double) cells[0].length;

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                double centerX = x * cellW + cellW / 2;
                double centerY = y * cellH + cellH / 2;
                double vx = cells[x][y].vx;
                double vy = cells[x][y].vy;
                double len = Math.sqrt(vx * vx + vy * vy);
                double scale = 18 * Math.tanh(len); // smooth arrow lengths
                double endX = centerX + vx * scale;
                double endY = centerY + vy * scale;

                // Color encodes temperature (warmer = more red, colder = blue)
                double temp = cells[x][y].temperature;
                float tNorm = (float) Math.max(0, Math.min(1, (temp - 14) / 12.0));
                Color c = new Color(
                        (int) (80 + 120 * tNorm),
                        (int) (150 + 70 * (1 - tNorm)),
                        (int) (220 - 80 * tNorm),
                        140);
                g2d.setColor(c);
                g2d.drawLine((int) centerX, (int) centerY, (int) endX, (int) endY);
                // Arrow head
                if (len > 0.01) {
                    double angle = Math.atan2(vy, vx);
                    int ah = 4;
                    int ax1 = (int) (endX - Math.cos(angle - 0.5) * ah);
                    int ay1 = (int) (endY - Math.sin(angle - 0.5) * ah);
                    int ax2 = (int) (endX - Math.cos(angle + 0.5) * ah);
                    int ay2 = (int) (endY - Math.sin(angle + 0.5) * ah);
                    g2d.drawLine((int) endX, (int) endY, ax1, ay1);
                    g2d.drawLine((int) endX, (int) endY, ax2, ay2);
                }
            }
        }
    }

    /**
     * Draws a multi-layer gradient ocean background with depth.
     */
    private void drawEnhancedOceanBackground(Graphics2D g2d) {
        int height = getHeight();
        int width = getWidth();

        // Multi-stop gradient for realistic depth
        GradientPaint gradient = new GradientPaint(
                0, 0, SimulationConfig.OCEAN_TOP_COLOR,
                0, height, SimulationConfig.OCEAN_BOTTOM_COLOR);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Add subtle wave patterns at surface
        drawSurfaceWaves(g2d, width, height);
    }

    /**
     * Draws animated caustic light patterns.
     */
    private void drawCaustics(Graphics2D g2d) {
        long frame = ocean != null ? ocean.getFrameCount() : 0;
        int width = getWidth();
        int height = getHeight();

        for (Caustic c : caustics) {
            c.update(frame, width, height);
            c.draw(g2d, width, height);
        }
    }

    /**
     * Draws subtle wave patterns near the surface.
     */
    private void drawSurfaceWaves(Graphics2D g2d, int width, int height) {
        long frame = ocean != null ? ocean.getFrameCount() : 0;

        g2d.setColor(new Color(255, 255, 255, 20));
        for (int y = 50; y < 200; y += 50) {
            for (int x = 0; x < width; x += 40) {
                double waveY = y + Math.sin((x + frame * 0.5) / 30.0) * 10;
                g2d.fillOval((int) (x - 2), (int) (waveY - 1), 4, 2);
            }
        }
    }

    /**
     * Glass vignette to keep the scene framed like an aquarium.
     */
    private void drawGlassVignette(Graphics2D g2d) {
        int w = getWidth();
        int h = getHeight();
        Color edge = new Color(0, 0, 0, 60);
        // Darken edges
        g2d.setPaint(new GradientPaint(0, 0, edge, w / 2f, 0, new Color(0, 0, 0, 0), true));
        g2d.fillRect(0, 0, w, h);
        g2d.setPaint(new GradientPaint(w, 0, edge, w / 2f, 0, new Color(0, 0, 0, 0), true));
        g2d.fillRect(0, 0, w, h);
        g2d.setPaint(new GradientPaint(0, 0, edge, 0, h / 2f, new Color(0, 0, 0, 0), true));
        g2d.fillRect(0, 0, w, h);
        g2d.setPaint(new GradientPaint(0, h, edge, 0, h / 2f, new Color(0, 0, 0, 0), true));
        g2d.fillRect(0, 0, w, h);

        // Soft highlight along the top like water surface reflection
        g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 60), 0, 80, new Color(255, 255, 255, 0)));
        g2d.fillRect(0, 0, w, 120);
    }

    /**
     * Draws control panel as overlay.
     */
    /**
     * Draws control panel as overlay.
     * (Deprecated: Control panel is now a Swing component at the top)
     */
    private void drawOverlayControlPanel(Graphics2D g2d) {
        // No-op
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Add obstacles anywhere in ocean
        if (ocean != null) {
            ocean.AjouterObstacle(
                    e.getX(),
                    e.getY(),
                    SimulationConfig.DEFAULT_OBSTACLE_RADIUS);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Inner class for caustic light effects.
     */
    private class Caustic {
        double x, y;
        double size;
        double phase;
        double speed;

        Caustic() {
            reset();
        }

        void reset() {
            x = random.nextDouble();
            y = random.nextDouble();
            size = 30 + random.nextDouble() * 70;
            phase = random.nextDouble() * Math.PI * 2;
            speed = 0.02 + random.nextDouble() * 0.03;
        }

        void update(long frame, int width, int height) {
            phase += speed;

            // Slowly drift
            x += Math.sin(phase) * 0.0003;
            y += Math.cos(phase * 1.3) * 0.0002;

            // Wrap around
            if (x < 0)
                x = 1;
            if (x > 1)
                x = 0;
            if (y < 0)
                y = 1;
            if (y > 1)
                y = 0;
        }

        void draw(Graphics2D g2d, int width, int height) {
            double intensity = (Math.sin(phase) + 1) / 2;
            int alpha = (int) (SimulationConfig.CAUSTIC_ALPHA * 255 * intensity);

            g2d.setColor(new Color(255, 255, 255, alpha));

            int screenX = (int) (x * width);
            int screenY = (int) (y * height);

            Ellipse2D.Double caustic = new Ellipse2D.Double(
                    screenX - size / 2,
                    screenY - size / 2,
                    size,
                    size);

            g2d.fill(caustic);
        }
    }
}
