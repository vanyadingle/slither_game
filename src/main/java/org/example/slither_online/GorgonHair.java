package org.example.slither_online;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GorgonHair {
    private int x;
    private int y;
    private final BufferedImage headTexture;
    private final BufferedImage bodyTexture;
    private final int bodySize;
    private int moveCounter = 0;
    private boolean isAlive = true;
    private final List<Point2D.Double> bodyParts = new ArrayList<>();


    public GorgonHair(int x, int y, BufferedImage headTexture, BufferedImage bodyTexture, int bodySize) {
        this.x = x;
        this.y = y;
        this.headTexture = headTexture;
        this.bodyTexture = bodyTexture;
        this.bodySize = bodySize;
        initializeBody();
    }
    private void initializeBody() {
        int startX = x;
        int startY = y;
        for(int i = 0; i < bodySize; i++){
            bodyParts.add(new Point2D.Double(startX, startY));
            startY += 5;
        }
    }

    public void draw(Graphics2D g2d) {
        if (isAlive) {
            g2d.drawImage(headTexture, x, y, null);
            for (Point2D.Double part : bodyParts) {
                g2d.drawImage(bodyTexture, (int) part.getX(), (int) part.getY(), null);
            }
        }
    }
    public void move(){
        if(isAlive) {
            double angle = Math.toRadians(moveCounter);
            double radius = 5;
            int newX = x + (int) (radius * Math.cos(angle));
            int newY = y + (int) (radius * Math.sin(angle));
            x = newX;
            y = newY;
            double previousX = x;
            double previousY = y;
            for (int i = 0; i < bodyParts.size(); i++) {
                Point2D.Double part = bodyParts.get(i);
                double tempX = part.getX();
                double tempY = part.getY();
                part.setLocation(previousX, previousY);
                previousX = tempX;
                previousY = tempY;
            }
        }
        moveCounter++;
    }
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, headTexture.getWidth(), headTexture.getHeight());
    }
    public List<Rectangle2D> getBodyBounds() {
        List<Rectangle2D> bodyBounds = new ArrayList<>();
        for (Point2D.Double part : bodyParts) {
            bodyBounds.add(new Rectangle2D.Double(part.getX(), part.getY(), bodyTexture.getWidth(), bodyTexture.getHeight()));
        }
        return bodyBounds;
    }
    public boolean isAlive() {
        return isAlive;
    }

    public void kill() {
        isAlive = false;
        bodyParts.clear();
    }
}
