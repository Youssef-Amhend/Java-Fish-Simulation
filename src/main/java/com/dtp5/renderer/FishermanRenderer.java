package com.dtp5.renderer;

import com.dtp5.model.Fisherman;
import java.awt.*;

public class FishermanRenderer {

    public static void render(Fisherman fisherman, Graphics2D g2d) {
        if (!fisherman.isFishing)
            return;

        // Draw Line
        g2d.setColor(new Color(200, 200, 200, 150));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawLine((int) fisherman.posX, 0, (int) fisherman.posX, (int) fisherman.posY);

        // Draw Hook
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2f));
        int hx = (int) fisherman.posX;
        int hy = (int) fisherman.posY;
        g2d.drawArc(hx - 5, hy, 10, 10, 180, 180); // Hook curve
        g2d.drawLine(hx, hy, hx, hy + 5); // Shank

        // Bait
        g2d.setColor(Color.RED);
        g2d.fillOval(hx - 3, hy + 8, 6, 6);
    }
}
