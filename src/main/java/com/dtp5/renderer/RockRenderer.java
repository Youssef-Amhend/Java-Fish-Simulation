package com.dtp5.renderer;

import com.dtp5.model.Rock;
import java.awt.*;
import java.awt.geom.*;

/**
 * Renders rocks and reef structures with beautiful gradients and textures.
 */
public class RockRenderer {
    
    public static void render(Rock rock, Graphics2D g2d) {
        double x = rock.posX;
        double y = rock.posY;
        double w = rock.width;
        double h = rock.height;
        
        // Save transform
        AffineTransform original = g2d.getTransform();
        
        // Translate to rock center and rotate
        g2d.translate(x, y);
        g2d.rotate(rock.rotation);
        
        // Draw based on rock type
        switch (rock.type) {
            case SMALL_ROCK:
                drawSmallRock(g2d, w, h);
                break;
            case MEDIUM_ROCK:
                drawMediumRock(g2d, w, h);
                break;
            case LARGE_ROCK:
                drawLargeRock(g2d, w, h);
                break;
            case REEF_CLUSTER:
                drawReefCluster(g2d, w, h);
                break;
        }
        
        // Restore transform
        g2d.setTransform(original);
    }
    
    private static void drawSmallRock(Graphics2D g2d, double w, double h) {
        Ellipse2D.Double rock = new Ellipse2D.Double(-w/2, -h/2, w, h);
        
        // Dark gray gradient
        RadialGradientPaint gradient = new RadialGradientPaint(
            new Point2D.Double(0, -h/4),
            (float)w,
            new float[]{0f, 0.6f, 1f},
            new Color[]{
                new Color(80, 80, 90),
                new Color(60, 60, 70),
                new Color(40, 40, 50)
            }
        );
        
        g2d.setPaint(gradient);
        g2d.fill(rock);
        
        // Highlight
        g2d.setColor(new Color(120, 120, 130, 100));
        g2d.fill(new Ellipse2D.Double(-w/3, -h/3, w/2, h/2));
    }
    
    private static void drawMediumRock(Graphics2D g2d, double w, double h) {
        GeneralPath rock = new GeneralPath();
        rock.moveTo(-w/2, h/2);
        rock.curveTo(-w/3, -h/3, w/3, -h/3, w/2, h/2);
        rock.curveTo(w/4, h/3, -w/4, h/3, -w/2, h/2);
        rock.closePath();
        
        // Brown-gray gradient
        LinearGradientPaint gradient = new LinearGradientPaint(
            new Point2D.Double(0, -h/2),
            new Point2D.Double(0, h/2),
            new float[]{0f, 0.5f, 1f},
            new Color[]{
                new Color(100, 90, 80),
                new Color(70, 65, 60),
                new Color(50, 45, 40)
            }
        );
        
        g2d.setPaint(gradient);
        g2d.fill(rock);
        
        // Texture lines
        g2d.setColor(new Color(60, 55, 50, 80));
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = -2; i <= 2; i++) {
            double offset = i * w / 6;
            g2d.draw(new Line2D.Double(-w/2 + offset, -h/3, w/2 + offset, h/3));
        }
    }
    
    private static void drawLargeRock(Graphics2D g2d, double w, double h) {
        GeneralPath rock = new GeneralPath();
        // Irregular shape
        rock.moveTo(-w/2, h/2);
        rock.lineTo(-w/2 + w*0.1, -h/2);
        rock.lineTo(w/4, -h/2 + h*0.2);
        rock.lineTo(w/2, -h/3);
        rock.lineTo(w/2 - w*0.1, h/2);
        rock.lineTo(-w/4, h/2 - h*0.1);
        rock.closePath();
        
        // Dark stone gradient
        RadialGradientPaint gradient = new RadialGradientPaint(
            new Point2D.Double(-w/4, -h/4),
            (float)(w * 0.8f),
            new float[]{0f, 0.4f, 0.8f, 1f},
            new Color[]{
                new Color(90, 85, 75),
                new Color(70, 65, 55),
                new Color(50, 45, 40),
                new Color(35, 30, 25)
            }
        );
        
        g2d.setPaint(gradient);
        g2d.fill(rock);
        
        // Add moss/algae patches
        g2d.setColor(new Color(40, 80, 50, 120));
        g2d.fillOval((int)(-w/4), (int)(h/4), (int)(w/3), (int)(h/4));
        g2d.fillOval((int)(w/6), (int)(-h/6), (int)(w/4), (int)(h/5));
    }
    
    private static void drawReefCluster(Graphics2D g2d, double w, double h) {
        // Main reef structure
        GeneralPath reef = new GeneralPath();
        reef.moveTo(-w/2, h/2);
        reef.curveTo(-w/3, -h/2, w/3, -h/2, w/2, h/2);
        reef.curveTo(w/3, h/3, -w/3, h/3, -w/2, h/2);
        reef.closePath();
        
        // Coral colors - vibrant gradient
        RadialGradientPaint gradient = new RadialGradientPaint(
            new Point2D.Double(0, -h/3),
            (float)w,
            new float[]{0f, 0.3f, 0.6f, 1f},
            new Color[]{
                new Color(200, 100, 120),  // Pink coral
                new Color(150, 80, 100),   // Darker pink
                new Color(100, 60, 80),    // Deep coral
                new Color(60, 40, 50)      // Dark base
            }
        );
        
        g2d.setPaint(gradient);
        g2d.fill(reef);
        
        // Add coral polyps/details
        g2d.setColor(new Color(255, 150, 170, 150));
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI * 2 / 8;
            double px = Math.cos(angle) * w / 3;
            double py = Math.sin(angle) * h / 3;
            g2d.fillOval((int)(px - 4), (int)(py - 4), 8, 8);
        }
        
        // Add some green algae growth
        g2d.setColor(new Color(60, 120, 80, 100));
        g2d.fillOval((int)(-w/4), (int)(h/4), (int)(w/3), (int)(h/4));
    }
}

