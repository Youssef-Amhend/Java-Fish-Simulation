package com.dtp5.renderer;

import com.dtp5.model.Algae;
import java.awt.*;
import java.awt.geom.*;

/**
 * Renders animated algae/seaweed that sways realistically.
 */
public class AlgaeRenderer {
    
    public static void render(Algae algae, Graphics2D g2d, long frameCount, double currentVx, double currentVy) {
        double baseX = algae.baseX;
        double baseY = algae.baseY;
        double height = algae.height;
        double width = algae.width;
        
        // Calculate sway based on phase and current
        double swayAmount = Math.sin(algae.phase + frameCount * 0.01) * (width * 0.3);
        double currentInfluence = (currentVx + currentVy) * 0.5;
        swayAmount += currentInfluence * 2;
        
        // Draw algae blade
        drawAlgaeBlade(g2d, baseX, baseY, height, width, swayAmount, algae.type);
    }
    
    private static void drawAlgaeBlade(Graphics2D g2d, double x, double y, double h, double w, double sway, Algae.AlgaeType type) {
        // Create curved path for swaying blade
        GeneralPath blade = new GeneralPath();
        
        // Start at base
        blade.moveTo(x, y);
        
        // Create smooth curve with multiple segments
        int segments = 15;
        for (int i = 1; i <= segments; i++) {
            double t = i / (double) segments;
            double segmentY = y - h * t;
            
            // Sway increases towards top
            double segmentSway = sway * (t * t); // Quadratic increase
            // Add some wave variation
            segmentSway += Math.sin(t * Math.PI * 3) * w * 0.1;
            
            double segmentX = x + segmentSway;
            
            if (i == 1) {
                blade.lineTo(segmentX, segmentY);
            } else {
                blade.curveTo(
                    segmentX - w/4, segmentY + h/segments,
                    segmentX + w/4, segmentY - h/segments,
                    segmentX, segmentY
                );
            }
        }
        
        // Choose color based on type
        Color baseColor;
        Color tipColor;
        
        switch (type) {
            case SHORT_ALGAE:
                baseColor = new Color(30, 80, 40);
                tipColor = new Color(50, 120, 60);
                break;
            case MEDIUM_ALGAE:
                baseColor = new Color(25, 70, 35);
                tipColor = new Color(45, 110, 55);
                break;
            case TALL_ALGAE:
                baseColor = new Color(20, 60, 30);
                tipColor = new Color(40, 100, 50);
                break;
            case KELP:
                baseColor = new Color(15, 50, 25);
                tipColor = new Color(35, 90, 45);
                break;
            default:
                baseColor = new Color(30, 80, 40);
                tipColor = new Color(50, 120, 60);
        }
        
        // Create gradient along blade
        LinearGradientPaint gradient = new LinearGradientPaint(
            new Point2D.Double(x, y),
            new Point2D.Double(x, y - h),
            new float[]{0f, 0.7f, 1f},
            new Color[]{
                baseColor,
                new Color(
                    (baseColor.getRed() + tipColor.getRed()) / 2,
                    (baseColor.getGreen() + tipColor.getGreen()) / 2,
                    (baseColor.getBlue() + tipColor.getBlue()) / 2
                ),
                tipColor
            }
        );
        
        // Draw blade outline
        g2d.setStroke(new BasicStroke((float)w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setPaint(gradient);
        g2d.draw(blade);
        
        // Add some texture lines
        g2d.setStroke(new BasicStroke(1f));
        g2d.setColor(new Color(20, 50, 25, 100));
        for (int i = 2; i < segments; i += 2) {
            double t = i / (double) segments;
            double segmentY = y - h * t;
            double segmentSway = sway * (t * t);
            double segmentX = x + segmentSway;
            g2d.drawLine((int)(segmentX - w/3), (int)segmentY, (int)(segmentX + w/3), (int)segmentY);
        }
    }
    
}

