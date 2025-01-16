package org.example.slither_online;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Food {
    private int x;
    private int y;
    private final BufferedImage texture;
    private final boolean isBomb;
    private final boolean isBoost;

    public Food(int x, int y, boolean isBomb, boolean isBoost) {
        this.x = x;
        this.y = y;
        this.texture = TextureManager.getTexture(isBomb ? "food_bomb" : (isBoost ? "food_boost" : "food_normal"));
        this.isBomb = isBomb;
        this.isBoost = isBoost;
    }
    public boolean isBoost(){
        return isBoost;
    }
    public void draw(Graphics2D g2d) {
        if (texture != null) {
            g2d.drawImage(texture, x, y, null);
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, texture.getWidth(), texture.getHeight());
    }

    public boolean isBomb() {
        return isBomb;
    }

    public int getScoreValue() {
        return isBoost ? 3 : 1;
    }
}
