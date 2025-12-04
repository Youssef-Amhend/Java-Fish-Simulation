package com.dtp5.ui;

import com.dtp5.config.SimulationConfig;
import com.dtp5.model.Ocean;

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
    private ModernButton pauseButton;
    private ModernButton addFishButton;
    private ModernButton addSharkButton;
    private ModernButton fishermanButton;
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
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        setBackground(new Color(20, 20, 30, 220)); // Dark semi-transparent
        setPreferredSize(new Dimension(
                SimulationConfig.WINDOW_WIDTH,
                70)); // Slightly taller for modern look

        // Stats Panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statsPanel.setOpaque(false);

        fpsLabel = createLabel("FPS: 0.0");
        fishCountLabel = createLabel("Fish: " + ocean.poissons.length);
        obstacleCountLabel = createLabel("Obstacles: 0");

        statsPanel.add(fpsLabel);
        statsPanel.add(fishCountLabel);
        statsPanel.add(obstacleCountLabel);
        add(statsPanel);

        // Separator
        add(createSeparator());

        // Controls
        pauseButton = new ModernButton("⏸ Pause", new Color(255, 165, 0));
        pauseButton.addActionListener(e -> {
            // Logic handled by OceanJPanel, button state updated via setPaused
        });
        add(pauseButton);

        addFishButton = new ModernButton("🐟 Add Fish", new Color(0, 191, 255));
        addFishButton.addActionListener(e -> ocean.addFish());
        add(addFishButton);

        addSharkButton = new ModernButton("🦈 Shark", new Color(255, 69, 0));
        addSharkButton.addActionListener(e -> ocean.addShark());
        add(addSharkButton);

        fishermanButton = new ModernButton("🎣 Fish", new Color(147, 112, 219));
        fishermanButton.addActionListener(e -> ocean.toggleFisherman());
        add(fishermanButton);

        // Speed Slider
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setOpaque(false);
        JLabel speedLabel = new JLabel("Speed");
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sliderPanel.add(speedLabel, BorderLayout.NORTH);

        speedSlider = new JSlider(10, 200, 100);
        speedSlider.setPreferredSize(new Dimension(120, 20));
        speedSlider.setOpaque(false);
        speedSlider.setBackground(new Color(0, 0, 0, 0));
        sliderPanel.add(speedSlider, BorderLayout.CENTER);
        add(sliderPanel);
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
