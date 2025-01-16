package org.example.slither_online;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class EpicFood {
    private int x;
    private int y;
    private final BufferedImage texture;


    public EpicFood(int x, int y, BufferedImage texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }
    public void draw(Graphics2D g2d) {
        g2d.drawImage(texture, x, y, null);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, texture.getWidth(), texture.getHeight());
    }
}
