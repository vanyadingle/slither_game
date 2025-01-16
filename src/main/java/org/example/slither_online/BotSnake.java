package org.example.slither_online;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

public class BotSnake {
    private final LinkedList<Point> body;
    private final int width = 20;
    private final int height = 20;
    private final int speed = 3;
    private int directionX = 1;
    private int directionY = 0;
    private final BufferedImage headTexture;
    private final BufferedImage bodyTexture;
    private int score;
    private  boolean isAlive = true;
    private final int maxSize = 25;
    private final Random random = new Random();


    public BotSnake(int x, int y, BufferedImage headTexture, BufferedImage bodyTexture, int size) {
        this.headTexture = headTexture;
        this.bodyTexture = bodyTexture;
        body = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            body.add(new Point(x - i * 20, y));
        }
        this.score = size;

    }

    public void move() {

        int newX = body.getFirst().x + directionX * speed;
        int newY = body.getFirst().y + directionY * speed;


        if (newX < 0) {
            newX = 1920;
        } else if (newX > 1920) {
            newX = 0;
        }
        if (newY < 0) {
            newY = 1080;
        } else if (newY > 1080) {
            newY = 0;
        }

        body.addFirst(new Point(newX, newY));
        body.removeLast();
    }
    public void moveRandomly() {
        if(random.nextDouble() < 0.05) {
            directionX = random.nextInt(3) -1 ;
            directionY = random.nextInt(3) - 1;
            if(directionX == 0 && directionY == 0) {
                directionX = 1;
            }

        }
        move();
    }
    public void draw(Graphics2D g2d) {
        if (isAlive) {
            if (headTexture != null) {
                g2d.drawImage(headTexture, body.getFirst().x, body.getFirst().y, null);
            }
            for (int i = 1; i < body.size(); i++) {
                if (bodyTexture != null) {
                    g2d.drawImage(bodyTexture, body.get(i).x, body.get(i).y, null);
                }
            }
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(body.getFirst().x, body.getFirst().y, width, height);
    }
    public boolean collidesWithBody(Rectangle2D headBounds) {
        for (int i = 1; i < body.size(); i++) {
            Rectangle2D bodyPart = new Rectangle2D.Double(body.get(i).x, body.get(i).y, width, height);
            if (bodyPart.intersects(headBounds)) {
                return true;
            }
        }
        return false;
    }

    public LinkedList<Point> getBody() {
        return body;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void kill() {
        isAlive = false;
    }
    public int getScore() {
        return score;
    }

    public int getHeadX() {
        return body.getFirst().x;
    }

    public int getHeadY() {
        return body.getFirst().y;
    }
}
