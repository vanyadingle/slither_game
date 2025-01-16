package org.example.slither_online;


import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Gorgon {
    private int x;
    private int y;
    private final BufferedImage headTexture;
    private final BufferedImage hairHeadTexture;
    private final BufferedImage hairBodyTexture;
    private final List<GorgonHair> hairList = new ArrayList<>();
    private boolean isAlive = true;


    public Gorgon(int x, int y, BufferedImage headTexture, BufferedImage hairHeadTexture, BufferedImage hairBodyTexture) {
        this.x = x;
        this.y = y;
        this.headTexture = headTexture;
        this.hairHeadTexture = hairHeadTexture;
        this.hairBodyTexture = hairBodyTexture;
        initializeHair();
    }

    public void draw(Graphics2D g2d) {
        if (isAlive) {
            g2d.drawImage(headTexture, x, y, null);
            for (GorgonHair hair : hairList) {
                hair.draw(g2d);
            }
        }
    }

    private void initializeHair() {
        int startX = x + headTexture.getWidth() / 2;
        int startY = y + headTexture.getHeight() / 2;

        for (int i = 0; i < 20; i++) {
            double angle = Math.toRadians(i * 360.0 / 20);
            int hairX = startX + (int)(150 * Math.cos(angle));
            int hairY = startY + (int)(150 * Math.sin(angle));
            hairList.add(new GorgonHair(hairX, hairY, hairHeadTexture, hairBodyTexture, 10));
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, headTexture.getWidth(), headTexture.getHeight());
    }
    public List<GorgonHair> getHairList() {
        return hairList;
    }

    public boolean isAlive() {
        return isAlive;
    }
    public void kill() {
        isAlive = false;
    }
}
