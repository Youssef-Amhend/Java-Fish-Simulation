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
 * Control panel displaying statistics and simulation controls.
 * Modernized with custom buttons and sleek layout.
 */
public class ControlPanel extends JPanel {
    private final Ocean ocean;
    private JLabel fpsLabel;
    private JLabel fishCountLabel;
    private JLabel obstacleCountLabel;
    private JLabel birthsLabel;
    private JLabel deathsLabel;
    private JLabel energyLabel;
    private ModernButton pauseButton;
    private ModernButton addFishButton;
    private ModernButton addSharkButton;
    private ModernButton fishermanButton;
    private ModernButton planktonButton;
    private ModernButton currentsButton;
    private JSlider speedSlider;

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
        setBackground(new Color(20, 20, 30, 220)); // Dark semi-transparent
        setPreferredSize(new Dimension(
                SimulationConfig.WINDOW_WIDTH,
                90)); // Taller to fit all controls

        // Left Panel - Control Buttons
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        leftPanel.setOpaque(false);

        pauseButton = new ModernButton("⏸ Pause", new Color(255, 165, 0));
        pauseButton.addActionListener(e -> {
            // Logic handled by OceanJPanel, button state updated via setPaused
        });
        leftPanel.add(pauseButton);

        // Add Fish Dropdown Button
        JPanel addFishPanel = new JPanel(new BorderLayout());
        addFishPanel.setOpaque(false);
        addFishButton = new ModernButton("🐟 Add Fish", new Color(0, 191, 255));
        addFishButton.addActionListener(e -> ocean.addFish(1));
        addFishPanel.add(addFishButton, BorderLayout.CENTER);
        
        // Dropdown menu for fish count
        JComboBox<String> fishCountCombo = new JComboBox<>(new String[]{"Add 1", "Add 10", "Add 50", "Add 100", "Add 500"});
        fishCountCombo.setPreferredSize(new Dimension(95, 32));
        fishCountCombo.setBackground(new Color(50, 50, 60));
        fishCountCombo.setForeground(Color.WHITE);
        fishCountCombo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        fishCountCombo.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        fishCountCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new Color(0, 191, 255));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(new Color(50, 50, 60));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        fishCountCombo.addActionListener(e -> {
            String selected = (String) fishCountCombo.getSelectedItem();
            int count = 1;
            if (selected != null) {
                if (selected.contains("10")) count = 10;
                else if (selected.contains("50")) count = 50;
                else if (selected.contains("100")) count = 100;
                else if (selected.contains("500")) count = 500;
            }
            ocean.addFish(count);
            fishCountCombo.setSelectedIndex(0); // Reset to first option
        });
        addFishPanel.add(fishCountCombo, BorderLayout.EAST);
        leftPanel.add(addFishPanel);

        addSharkButton = new ModernButton("🦈 Shark", new Color(255, 69, 0));
        addSharkButton.addActionListener(e -> ocean.addShark());
        leftPanel.add(addSharkButton);

        fishermanButton = new ModernButton("🎣 Fish", new Color(147, 112, 219));
        fishermanButton.addActionListener(e -> ocean.toggleFisherman());
        leftPanel.add(fishermanButton);

        planktonButton = new ModernButton("🟢 Plankton", new Color(60, 179, 113));
        planktonButton.addActionListener(e -> ocean.spawnPlanktonPatch());
        leftPanel.add(planktonButton);

        currentsButton = new ModernButton("🌊 Currents", new Color(70, 130, 180));
        currentsButton.addActionListener(e -> {
            ocean.showCurrents = !ocean.showCurrents;
            currentsButton.setBaseColor(ocean.showCurrents ? new Color(70, 130, 180) : new Color(90, 90, 90));
        });
        leftPanel.add(currentsButton);

        // Separator
        leftPanel.add(createSeparator());

        // Current Pattern Buttons
        ModernButton calmButton = new ModernButton("😌 Calm", new Color(100, 150, 200));
        calmButton.addActionListener(e -> ocean.environmentalField.setPattern(EnvironmentalField.CurrentPattern.CALM));
        leftPanel.add(calmButton);
        
        ModernButton swirlButton = new ModernButton("🌀 Swirl", new Color(70, 130, 180));
        swirlButton.addActionListener(e -> ocean.environmentalField.setPattern(EnvironmentalField.CurrentPattern.SWIRL));
        leftPanel.add(swirlButton);
        
        ModernButton strongButton = new ModernButton("💪 Strong", new Color(200, 100, 100));
        strongButton.addActionListener(e -> ocean.environmentalField.setPattern(EnvironmentalField.CurrentPattern.STRONG));
        leftPanel.add(strongButton);
        
        ModernButton whirlpoolButton = new ModernButton("🌪️ Whirl", new Color(150, 100, 200));
        whirlpoolButton.addActionListener(e -> ocean.environmentalField.setPattern(EnvironmentalField.CurrentPattern.WHIRLPOOL));
        leftPanel.add(whirlpoolButton);

        add(leftPanel, BorderLayout.WEST);

        // Center Panel - Stats
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        centerPanel.setOpaque(false);

        fpsLabel = createLabel("FPS: 0.0");
        fishCountLabel = createLabel("Fish: " + ocean.poissons.length);
        obstacleCountLabel = createLabel("Obstacles: 0");
        birthsLabel = createLabel("Births: 0");
        deathsLabel = createLabel("Deaths: 0");
        energyLabel = createLabel("Avg E: 0");

        centerPanel.add(fpsLabel);
        centerPanel.add(fishCountLabel);
        centerPanel.add(obstacleCountLabel);
        centerPanel.add(birthsLabel);
        centerPanel.add(deathsLabel);
        centerPanel.add(energyLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Right Panel - Sliders
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        rightPanel.setOpaque(false);

        // Current Strength Slider
        JPanel currentStrengthPanel = new JPanel(new BorderLayout());
        currentStrengthPanel.setOpaque(false);
        JLabel currentStrengthLabel = new JLabel("Current");
        currentStrengthLabel.setForeground(Color.WHITE);
        currentStrengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        currentStrengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentStrengthPanel.add(currentStrengthLabel, BorderLayout.NORTH);
        
        JSlider currentStrengthSlider = new JSlider(0, 200, 100); // 0-200% (0.0-2.0)
        currentStrengthSlider.setPreferredSize(new Dimension(100, 20));
        currentStrengthSlider.setOpaque(false);
        currentStrengthSlider.setBackground(new Color(0, 0, 0, 0));
        currentStrengthSlider.addChangeListener(e -> {
            int value = currentStrengthSlider.getValue();
            double strength = value / 100.0; // Convert to 0.0-2.0 range
            ocean.environmentalField.setCurrentStrength(strength);
        });
        currentStrengthPanel.add(currentStrengthSlider, BorderLayout.CENTER);
        rightPanel.add(currentStrengthPanel);

        // Current Speed Slider
        JPanel currentSpeedPanel = new JPanel(new BorderLayout());
        currentSpeedPanel.setOpaque(false);
        JLabel currentSpeedLabel = new JLabel("Speed");
        currentSpeedLabel.setForeground(Color.WHITE);
        currentSpeedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        currentSpeedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentSpeedPanel.add(currentSpeedLabel, BorderLayout.NORTH);
        
        JSlider currentSpeedSlider = new JSlider(0, 300, 100); // 0-300% (0.0-3.0)
        currentSpeedSlider.setPreferredSize(new Dimension(100, 20));
        currentSpeedSlider.setOpaque(false);
        currentSpeedSlider.setBackground(new Color(0, 0, 0, 0));
        currentSpeedSlider.addChangeListener(e -> {
            int value = currentSpeedSlider.getValue();
            double speed = value / 100.0; // Convert to 0.0-3.0 range
            ocean.environmentalField.setAnimationSpeed(speed);
        });
        currentSpeedPanel.add(currentSpeedSlider, BorderLayout.CENTER);
        rightPanel.add(currentSpeedPanel);

        // Separator
        rightPanel.add(createSeparator());

        // Simulation Speed Slider
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setOpaque(false);
        JLabel speedLabel = new JLabel("Sim Speed");
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sliderPanel.add(speedLabel, BorderLayout.NORTH);

        speedSlider = new JSlider(10, 200, 100);
        speedSlider.setPreferredSize(new Dimension(120, 20));
        speedSlider.setOpaque(false);
        speedSlider.setBackground(new Color(0, 0, 0, 0));
        sliderPanel.add(speedSlider, BorderLayout.CENTER);
        rightPanel.add(sliderPanel);

        add(rightPanel, BorderLayout.EAST);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
     * Modern Button with gradient, rounded corners, and hover effects.
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
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(110, 35));

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

            // Gradient Background
            Color color1 = isHovered ? baseColor.brighter() : baseColor;
            Color color2 = isHovered ? baseColor : baseColor.darker();
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 15, 15);

            // Border
            g2.setColor(new Color(255, 255, 255, 50));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 15, 15);

            // Text
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
