package com.dtp5;

import com.dtp5.config.SimulationConfig;
import com.dtp5.ui.OceanJPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Main class - Entry point for the high-performance ocean simulation.
 */
public class Main {
    private static JFrame fenetre;
    private static boolean isFullscreen = false;

    public static void main(String[] args) {
        // Use proper Swing threading
        SwingUtilities.invokeLater(() -> {
            // Create window
            fenetre = new JFrame();
            fenetre.setTitle(SimulationConfig.WINDOW_TITLE);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setResizable(true);

            // Create ocean panel
            OceanJPanel oceanPanel = new OceanJPanel();

            // Create main container
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(oceanPanel, BorderLayout.CENTER);

            fenetre.setContentPane(mainPanel);

            // Setup fullscreen toggle with F11 or ESC
            fenetre.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_F11 ||
                            e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        toggleFullscreen();
                    }
                }
            });

            // Set initial size or fullscreen
            if (SimulationConfig.START_FULLSCREEN) {
                setFullscreen(true);
            } else {
                fenetre.setSize(SimulationConfig.WINDOW_WIDTH, SimulationConfig.WINDOW_HEIGHT);
                fenetre.setLocationRelativeTo(null);
            }

            // Show window
            fenetre.setVisible(true);

            // Start simulation (after window is visible)
            oceanPanel.Lancer();

            // Add control panel to top
            mainPanel.add(oceanPanel.getControlPanel(), BorderLayout.NORTH);
            mainPanel.revalidate();
        });
    }

    /**
     * Toggles fullscreen mode.
     */
    private static void toggleFullscreen() {
        setFullscreen(!isFullscreen);
    }

    /**
     * Sets fullscreen mode on or off.
     */
    private static void setFullscreen(boolean fullscreen) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (fullscreen && device.isFullScreenSupported()) {
            // Enter fullscreen
            fenetre.dispose();
            fenetre.setUndecorated(true);
            fenetre.setVisible(true);
            device.setFullScreenWindow(fenetre);
            isFullscreen = true;
        } else {
            // Exit fullscreen
            device.setFullScreenWindow(null);
            fenetre.dispose();
            fenetre.setUndecorated(false);
            fenetre.setSize(SimulationConfig.WINDOW_WIDTH, SimulationConfig.WINDOW_HEIGHT);
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
            isFullscreen = false;
        }
    }
}