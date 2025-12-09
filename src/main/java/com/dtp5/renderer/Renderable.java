package com.dtp5.renderer;

import java.awt.Graphics2D;

/**
 * Interface for entities that can be rendered to the screen.
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public interface Renderable {

    /**
     * Renders this entity to the graphics context.
     * 
     * @param g2d        The graphics context to render to
     * @param frameCount Current frame number for animations
     */
    void render(Graphics2D g2d, long frameCount);

    /**
     * Gets the rendering layer for this entity.
     * Lower values are rendered first (background).
     * 
     * @return Rendering layer (0-100)
     */
    default int getRenderLayer() {
        return 50; // Default middle layer
    }
}
