package org.example.slither_online;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Snake {
    private int x;
    private int y;
    private final BufferedImage headTexture;
    private final BufferedImage bodyTexture;
    private final List<Point2D.Double> bodyParts = new ArrayList<>();
    private int score = 0;
    private final int speed = 5;
    private boolean isBoosting = false;
    private boolean isAlive = true;


    public Snake(int x, int y, BufferedImage headTexture, BufferedImage bodyTexture) {
        this.x = x;
        this.y = y;
        this.headTexture = headTexture;
        this.bodyTexture = bodyTexture;
    }

    public void draw(Graphics2D g2d) {
        if(isAlive) {
            g2d.drawImage(headTexture, x, y, null);
            for (Point2D.Double part : bodyParts) {
                g2d.drawImage(bodyTexture, (int) part.getX(), (int) part.getY(), null);
            }
        }
    }

    public void grow() {
        bodyParts.add(new Point2D.Double(x, y));
        score++;
    }

    public void moveTowards(Point2D.Double target, boolean isBoost) {
        if(isAlive){
            double dx = target.getX() - x;
            double dy = target.getY() - y;
            double angle = Math.atan2(dy, dx);

            double speedValue = isBoost || isBoosting ? speed * 2 : speed;

            int newX = x + (int) (speedValue * Math.cos(angle));
            int newY = y + (int) (speedValue * Math.sin(angle));

            x = newX;
            y = newY;
            if (x < 0) {
                x = 1920;
            } else if (x > 1920) {
                x = 0;
            }
            if (y < 0) {
                y = 1080;
            } else if (y > 1080) {
                y = 0;
            }
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
    }

    public boolean collidesWith(Rectangle2D other) {
        Rectangle2D headBounds = new Rectangle2D.Double(x, y, headTexture.getWidth(), headTexture.getHeight());
        return headBounds.intersects(other);
    }
    public boolean collidesWithBody(Rectangle2D other) {
        for (Point2D.Double part : bodyParts) {
            Rectangle2D bodyPart = new Rectangle2D.Double(part.getX(), part.getY(), bodyTexture.getWidth(), bodyTexture.getHeight());
            if (bodyPart.intersects(other)) {
                return true;
            }
        }
        return false;
    }
    public void kill(){
        isAlive = false;
    }

    public int getScore() {
        return score;
    }
    public void updateScore() {
        if (score > 0) {
            score--;
            if (bodyParts.size() > 0){
                bodyParts.remove(bodyParts.size()-1);
            }
        }

    }

    public void setBoosting(boolean isBoosting) {
        this.isBoosting = isBoosting;
    }

    public boolean canBoost() {
        return score > 0;
    }
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, headTexture.getWidth(), headTexture.getHeight());
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        bodyParts.clear();
        score = 0;
        isAlive = true;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
