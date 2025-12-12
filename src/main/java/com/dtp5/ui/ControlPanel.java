package com.dtp5.ui;

import com.dtp5.config.SimulationConfig;
import com.dtp5.model.Ocean;
import com.dtp5.model.EnvironmentalField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

/**
 * Control panel with statistics display and simulation controls.
 * Features modern button styling and comprehensive creature management.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class ControlPanel extends JPanel {

    private final Ocean ocean;

    // Stats labels
    private JLabel fpsLabel;
    private JLabel fishCountLabel;
    private JLabel obstacleCountLabel;
    private JLabel birthsLabel;
    private JLabel deathsLabel;
    private JLabel energyLabel;
    private JLabel creaturesLabel;

    // Control buttons
    private ModernButton pauseButton;
    private ModernButton addFishButton;
    private ModernButton addSharkButton;
    private ModernButton fishermanButton;
    private ModernButton planktonButton;
    private ModernButton currentsButton;
    private ModernButton jellyfishButton;
    private ModernButton turtleButton;
    private ModernButton dayNightButton;
    private ModernButton particlesButton;

    // Sliders
    private JSlider speedSlider;

    // FPS tracking
    private long lastUpdateTime;
    private int frameCount;
    private double currentFPS;
    private boolean isPaused = false;

    private final DecimalFormat fpsFormat = new DecimalFormat("0.0");

    public ControlPanel(Ocean ocean) {
        this.ocean = ocean;
        this.lastUpdateTime = System.currentTimeMillis();
        this.frameCount = 0;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 5));
        setBackground(new Color(20, 20, 30, 230));
        setPreferredSize(new Dimension(SimulationConfig.WINDOW_WIDTH, 100));

        // Left Panel - Control Buttons organized in two rows
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        // Top row - Creature buttons
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        topRow.setOpaque(false);

        // Pause button
        pauseButton = new ModernButton("⏸ Pause", new Color(255, 165, 0));
        topRow.add(pauseButton);

        // Exit button
        ModernButton exitButton = new ModernButton("❌ Exit", new Color(220, 53, 69));
        exitButton.addActionListener(e -> System.exit(0));
        topRow.add(exitButton);

        // Add Fish with dropdown
        JPanel addFishPanel = new JPanel(new BorderLayout());
        addFishPanel.setOpaque(false);
        addFishButton = new ModernButton("🐟 Fish", new Color(0, 191, 255));
        addFishButton.addActionListener(e -> ocean.addFish(1));
        addFishPanel.add(addFishButton, BorderLayout.CENTER);

        JComboBox<String> fishCountCombo = createStyledCombo(
                new String[] { "+ 1", "+ 10", "+ 50", "+ 100", "+ 500" });
        fishCountCombo.addActionListener(e -> {
            String selected = (String) fishCountCombo.getSelectedItem();
            int count = parseCount(selected);
            ocean.addFish(count);
            fishCountCombo.setSelectedIndex(0);
        });
        addFishPanel.add(fishCountCombo, BorderLayout.EAST);
        topRow.add(addFishPanel);

        // Shark button
        addSharkButton = new ModernButton("🦈 Shark", new Color(255, 69, 0));
        addSharkButton.addActionListener(e -> ocean.addShark());
        topRow.add(addSharkButton);

        // Jellyfish button
        jellyfishButton = new ModernButton("🎐 Jelly", new Color(186, 85, 211));
        jellyfishButton.addActionListener(e -> ocean.addJellyfish());
        topRow.add(jellyfishButton);

        // Sea Turtle button
        turtleButton = new ModernButton("🐢 Turtle", new Color(60, 179, 113));
        turtleButton.addActionListener(e -> ocean.addSeaTurtle());
        topRow.add(turtleButton);

        // Fisherman button
        fishermanButton = new ModernButton("🎣 Fisher", new Color(147, 112, 219));
        fishermanButton.addActionListener(e -> ocean.toggleFisherman());
        topRow.add(fishermanButton);

        // Plankton button
        planktonButton = new ModernButton("🟢 Plankton", new Color(50, 205, 50));
        planktonButton.addActionListener(e -> ocean.spawnPlanktonPatch());
        topRow.add(planktonButton);

        leftPanel.add(topRow);

        // Bottom row - Toggle and pattern buttons
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        bottomRow.setOpaque(false);

        // Currents toggle
        currentsButton = new ModernButton("🌊 Currents", new Color(70, 130, 180));
        currentsButton.addActionListener(e -> {
            ocean.showCurrents = !ocean.showCurrents;
            currentsButton.setBaseColor(ocean.showCurrents ? new Color(70, 130, 180) : new Color(90, 90, 90));
        });
        bottomRow.add(currentsButton);

        // Day/Night toggle
        dayNightButton = new ModernButton("🌙 Day/Night", new Color(100, 100, 180));
        dayNightButton.addActionListener(e -> {
            ocean.dayNightEnabled = !ocean.dayNightEnabled;
            dayNightButton.setBaseColor(ocean.dayNightEnabled ? new Color(100, 100, 180) : new Color(90, 90, 90));
        });
        bottomRow.add(dayNightButton);

        // Particles toggle
        particlesButton = new ModernButton("💫 Bubbles", new Color(100, 180, 220));
        particlesButton.addActionListener(e -> {
            ocean.particlesEnabled = !ocean.particlesEnabled;
            particlesButton.setBaseColor(ocean.particlesEnabled ? new Color(100, 180, 220) : new Color(90, 90, 90));
        });
        bottomRow.add(particlesButton);

        // Separator
        bottomRow.add(createSeparator());

        // Current pattern buttons
        ModernButton calmButton = new ModernButton("😌 Calm", new Color(100, 150, 200));
        calmButton.setPreferredSize(new Dimension(75, 30));
        calmButton.addActionListener(e -> ocean.environmentalField.setPattern(EnvironmentalField.CurrentPattern.CALM));
        bottomRow.add(calmButton);

        ModernButton swirlButton = new ModernButton("🌀 Swirl", new Color(70, 130, 180));
        swirlButton.setPreferredSize(new Dimension(75, 30));
        swirlButton
                .addActionListener(e -> ocean.environmentalField.setPattern(EnvironmentalField.CurrentPattern.SWIRL));
        bottomRow.add(swirlButton);

        ModernButton whirlButton = new ModernButton("🌪️ Whirl", new Color(150, 100, 200));
        whirlButton.setPreferredSize(new Dimension(75, 30));
        whirlButton.addActionListener(
                e -> ocean.environmentalField.setPattern(EnvironmentalField.CurrentPattern.WHIRLPOOL));
        bottomRow.add(whirlButton);

        leftPanel.add(bottomRow);

        add(leftPanel, BorderLayout.WEST);

        // Center Panel - Stats
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        centerPanel.setOpaque(false);

        fpsLabel = createLabel("FPS: 0.0");
        fishCountLabel = createLabel("Fish: " + ocean.poissons.length);
        obstacleCountLabel = createLabel("Obstacles: 0");
        birthsLabel = createLabel("Births: 0");
        deathsLabel = createLabel("Deaths: 0");
        energyLabel = createLabel("Avg E: 0");
        creaturesLabel = createLabel("🦈0 🎐0 🐢0");

        centerPanel.add(fpsLabel);
        centerPanel.add(fishCountLabel);
        centerPanel.add(creaturesLabel);
        centerPanel.add(obstacleCountLabel);
        centerPanel.add(birthsLabel);
        centerPanel.add(deathsLabel);
        centerPanel.add(energyLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Right Panel - Sliders
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        rightPanel.setOpaque(false);

        // Current strength slider
        JPanel currentStrengthPanel = createSliderPanel("Current", 0, 200, 100);
        JSlider currentStrengthSlider = (JSlider) currentStrengthPanel.getComponent(1);
        currentStrengthSlider.addChangeListener(e -> {
            double strength = currentStrengthSlider.getValue() / 100.0;
            ocean.environmentalField.setCurrentStrength(strength);
        });
        rightPanel.add(currentStrengthPanel);

        // Day/Night speed slider (NEW)
        JPanel dayNightSpeedPanel = createSliderPanel("Time", 0, 500, 100);
        JSlider dayNightSpeedSlider = (JSlider) dayNightSpeedPanel.getComponent(1);
        dayNightSpeedSlider.addChangeListener(e -> {
            double speed = dayNightSpeedSlider.getValue() / 100.0;
            ocean.dayNightCycle.setTimeMultiplier(speed);
        });
        rightPanel.add(dayNightSpeedPanel);

        // Separator
        rightPanel.add(createSeparator());

        // Simulation speed slider
        JPanel sliderPanel = createSliderPanel("Sim Speed", 10, 200, 100);
        speedSlider = (JSlider) sliderPanel.getComponent(1);
        rightPanel.add(sliderPanel);

        add(rightPanel, BorderLayout.EAST);
    }

    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setPreferredSize(new Dimension(80, 30));
        combo.setBackground(new Color(50, 50, 60));
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        combo.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? new Color(0, 191, 255) : new Color(50, 50, 60));
                c.setForeground(Color.WHITE);
                return c;
            }
        });
        return combo;
    }

    private int parseCount(String text) {
        if (text == null)
            return 1;
        if (text.contains("500"))
            return 500;
        if (text.contains("100"))
            return 100;
        if (text.contains("50"))
            return 50;
        if (text.contains("10"))
            return 10;
        return 1;
    }

    private JPanel createSliderPanel(String label, int min, int max, int value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lbl, BorderLayout.NORTH);

        JSlider slider = new JSlider(min, max, value);
        slider.setPreferredSize(new Dimension(100, 20));
        slider.setOpaque(false);
        panel.add(slider, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 30));
        separator.setForeground(new Color(100, 100, 100));
        return separator;
    }

    public void updateStats() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastUpdateTime;

        if (elapsed >= 500) {
            currentFPS = (frameCount * 1000.0) / elapsed;
            fpsLabel.setText("FPS: " + fpsFormat.format(currentFPS));
            frameCount = 0;
            lastUpdateTime = currentTime;
        }

        fishCountLabel.setText("Fish: " + ocean.poissons.length);
        obstacleCountLabel.setText("Obstacles: " + ocean.obstacles.size());
        birthsLabel.setText("Births: " + ocean.stats.getBirths());
        deathsLabel.setText("Deaths: " + ocean.stats.getDeaths());
        energyLabel.setText("Avg E: " + fpsFormat.format(ocean.stats.getAvgEnergy()));

        // Update creatures count
        creaturesLabel.setText(String.format("🦈%d 🎐%d 🐢%d",
                ocean.sharks.size(),
                ocean.jellyfish.size(),
                ocean.seaTurtles.size()));
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getAddFishButton() {
        return addFishButton;
    }

    public JSlider getSpeedSlider() {
        return speedSlider;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        pauseButton.setText(paused ? "▶ Resume" : "⏸ Pause");
        pauseButton.setBaseColor(paused ? new Color(50, 205, 50) : new Color(255, 165, 0));
    }

    /**
     * Modern button with gradient and hover effects.
     */
    private static class ModernButton extends JButton {
        private Color baseColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(95, 32));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public void setBaseColor(Color color) {
            this.baseColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color color1 = isHovered ? baseColor.brighter() : baseColor;
            Color color2 = isHovered ? baseColor : baseColor.darker();
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 12, 12);

            g2.setColor(new Color(255, 255, 255, 50));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int textX = (w - stringBounds.width) / 2;
            int textY = (h - stringBounds.height) / 2 + fm.getAscent();

            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }
}
