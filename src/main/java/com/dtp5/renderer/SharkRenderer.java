package com.dtp5.renderer;

import com.dtp5.model.Shark;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

public class SharkRenderer {

    public static void render(Shark shark, Graphics2D g2d) {
        double x = shark.posX;
        double y = shark.posY;
        double angle = Math.atan2(shark.vitesseY, shark.vitesseX);

        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(angle);

        // Draw Shark Body
        g2d.setColor(Color.DARK_GRAY);
        GeneralPath body = new GeneralPath();
        body.moveTo(20, 0);
        body.lineTo(-20, -10);
        body.lineTo(-20, 10);
        body.closePath();
        g2d.fill(body);

        // Fin
        g2d.setColor(Color.GRAY);
        GeneralPath fin = new GeneralPath();
        fin.moveTo(-5, -5);
        fin.lineTo(-15, -20);
        fin.lineTo(-15, -5);
        fin.closePath();
        g2d.fill(fin);

        // Tail
        GeneralPath tail = new GeneralPath();
        tail.moveTo(-20, 0);
        tail.lineTo(-35, -10);
        tail.lineTo(-30, 0);
        tail.lineTo(-35, 10);
        tail.closePath();
        g2d.fill(tail);

        g2d.setTransform(old);
    }
}
