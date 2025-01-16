
package org.example.slither_online.client;

import com.sun.management.OperatingSystemMXBean;
import org.example.slither_online.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private Snake snake;
    private final List<Food> foodList = new ArrayList<>();
    private final int foodCount = 5;
    private final List<BotSnake> botSnakes = new ArrayList<>();
    private final int maxBots = 4;
    private final javax.swing.Timer timer;
    private final Random random = new Random();
    private boolean mousePressed = false;
    private Point2D.Double mousePosition = new Point2D.Double(0, 0);
    private boolean isGameOver = false;
    private boolean isGameWon = false;
    private Rectangle2D playAgainButton;
    private Rectangle2D exitButton;
    private Rectangle2D mainMenuButton;
    private int score = 0;
    private long lastBotSpawnTime = 0;
    private final long botSpawnInterval = 5000;
    private final List<EpicFood> epicFoodList = new ArrayList<>();
    private int epicFoodGrowCount = 0;
    private int botsKilled = 0;
    private Gorgon gorgon;
    private boolean isGorgonDefeated = false;
    private static final int BOTS_TO_SPAWN_GORGON = 15;
    private long lastFoodSpawnTime = 0;
    private final long foodSpawnInterval = 5000;
    private boolean isBoosting = false;
    private long lastBoostUpdate = 0;
    private final long boostUpdateInterval = 100;
    private long lastFrameTime = 0;
    private int frames = 0;
    private double fps = 0;
    private long lastLoopTime = 0;
    private OperatingSystemMXBean osBean;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocusInWindow();

        TextureManager.loadAllTextures();
        snake = new Snake(WIDTH / 2, HEIGHT / 2,
                TextureManager.getTexture("snake_head"),
                TextureManager.getTexture("snake_body"));
        generateInitialFood();
        timer = new javax.swing.Timer(20, this);
        timer.start();
        osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    if(snake != null && snake.canBoost()){
                        isBoosting = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isBoosting = false;
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                if (isGameOver || isGameWon) {
                    if (playAgainButton != null && playAgainButton.contains(e.getPoint())) {
                        resetGame();
                    } else if (exitButton != null && exitButton.contains(e.getPoint())) {
                        System.exit(0);
                    } else if (mainMenuButton != null && mainMenuButton.contains(e.getPoint())) {
                        backToMenu();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition.setLocation(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePosition.setLocation(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // РћС‚СЂРёСЃРѕРІРєР° С„РѕРЅР°
        BufferedImage background = TextureManager.getTexture("background");
        if (background != null) {
            int bgWidth = background.getWidth();
            int bgHeight = background.getHeight();
            for (int x = 0; x < WIDTH; x += bgWidth) {
                for (int y = 0; y < HEIGHT; y += bgHeight) {
                    g2d.drawImage(background, x, y, null);
                }
            }
        } else {
            System.out.println("Texture background is null!");
        }

        if (!isGameOver && !isGameWon) {
            // РћС‚СЂРёСЃРѕРІРєР° РµРґС‹
            for (Food food : foodList) {
                if (food != null) {
                    food.draw(g2d);
                }

            }

            // РћС‚СЂРёСЃРѕРІРєР° Р·РјРµРё
            if (snake != null) {
                snake.draw(g2d);
            }
            for (BotSnake botSnake : botSnakes) {
                botSnake.draw(g2d);
            }
            epicFoodList.forEach(epicFood -> epicFood.draw(g2d));
            if (gorgon != null && gorgon.isAlive()) {
                gorgon.draw(g2d);
            }
            drawScore(g2d);
        } else if (isGameOver) {
            drawGameOverScreen(g2d);
        } else if (isGameWon) {
            drawGameWonScreen(g2d);
        }
        drawBottomText(g2d);

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastFrameTime;
        if (deltaTime > 0) {
            frames++;
            if (deltaTime >= 1000) {
                fps = frames * 1000.0 / deltaTime;
                frames = 0;
                lastFrameTime = currentTime;
            }
        }
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("FPS: " + String.format("%.2f", fps), 10, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        if (!isGameOver && !isGameWon) {
            Point mousePositionOnScreen = getMousePosition();
            if(mousePositionOnScreen != null){
                mousePosition.setLocation(mousePositionOnScreen.getX(), mousePositionOnScreen.getY());
            }
            boolean isBoost = false;
            for (Food food : foodList) {
                if (snake != null && snake.collidesWith(food.getBounds()) && food.isBoost()){
                    isBoost = true;
                    break;
                }
            }
            if(isBoosting && snake != null && snake.isAlive()){
                if (currentTime - lastBoostUpdate >= boostUpdateInterval) {
                    snake.updateScore();
                    lastBoostUpdate = currentTime;
                }
            }
            if (epicFoodGrowCount > 0) {
                if (snake != null) {
                    snake.grow();
                }
                epicFoodGrowCount--;
            }
            if (gorgon != null && gorgon.isAlive()) {
                for (GorgonHair gorgonHair : gorgon.getHairList()) {
                    gorgonHair.move();
                }
            }
            if (snake != null) {
                snake.setBoosting(isBoosting);
                snake.moveTowards(mousePosition, isBoost);
            }
            checkCollisions();
            updateGorgon();
            updateBots();
            spawnBotSnake();
            spawnGorgon();
            spawnFood();
            if(snake != null){
                score = snake.getScore();
            }
        }
        repaint();
    }

    private void checkCollisions() {
        if (snake == null || !snake.isAlive()) return;
        for (int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            if (snake.collidesWith(food.getBounds())) {
                if (food.isBomb()) {
                    snake.kill();
                } else {
                    snake.grow();
                    score += food.getScoreValue() - 1;
                }
                foodList.remove(i);
                generateFood();
                break;
            }
        }
        Rectangle2D snakeHeadBounds = snake.getBounds();
        for (Iterator<BotSnake> iterator = botSnakes.iterator(); iterator.hasNext(); ) {
            BotSnake botSnake = iterator.next();
            if (botSnake.isAlive()) {
                if (botSnake.collidesWithBody(snakeHeadBounds)) {
                    snake.kill();
                    break;
                }
                Rectangle2D botHeadBounds = botSnake.getBounds();
                if (snake.collidesWithBody(botHeadBounds)) {
                    score += botSnake.getScore();
                    int x = botSnake.getHeadX();
                    int y = botSnake.getHeadY();
                    botSnake.kill();
                    iterator.remove();
                    spawnEpicFood(x, y);
                    botsKilled++;
                    break;
                }
            }
        }
        if (gorgon != null && gorgon.isAlive()) {
            if (snake.collidesWith(gorgon.getBounds())) {
                snake.kill();
                return;
            }
            for (Iterator<GorgonHair> iterator = gorgon.getHairList().iterator(); iterator.hasNext(); ) {
                GorgonHair gorgonHair = iterator.next();
                if (gorgonHair.isAlive()) {
                    if (snake.collidesWith(gorgonHair.getBounds())) {
                        snake.kill();
                        return;
                    }
                    for (Rectangle2D bodyPartBounds : gorgonHair.getBodyBounds()) {
                        if (snake.collidesWith(bodyPartBounds)) {
                            snake.kill();
                            return;
                        }
                    }
                }
            }
            if (gorgon.getHairList().isEmpty()) {
                gorgon.kill();
                isGorgonDefeated = true;
                isGameWon = true;
            }
        }
        for (Iterator<EpicFood> iterator = epicFoodList.iterator(); iterator.hasNext(); ) {
            EpicFood epicFood = iterator.next();
            if (snake != null && snake.collidesWith(epicFood.getBounds())) {
                epicFoodGrowCount = 10;
                score += 10;
                iterator.remove();
            }
        }
        if (snake == null || !snake.isAlive()) {
            isGameOver = true;
        }
    }


    private void updateGorgon() {
        if (gorgon != null && gorgon.isAlive()) {
            for (GorgonHair gorgonHair : gorgon.getHairList()) {
                gorgonHair.move();
            }
        }
    }


    private void spawnFood() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFoodSpawnTime >= foodSpawnInterval) {
            generateFood();
            lastFoodSpawnTime = currentTime;
        }
    }


    private void spawnEpicFood(int x, int y) {
        BufferedImage epicFoodTexture = TextureManager.getTexture("food_epic");
        if (epicFoodTexture != null) {
            epicFoodList.add(new EpicFood(x, y, epicFoodTexture));
        }
    }


    private void spawnGorgon() {
        if (gorgon == null && botsKilled >= BOTS_TO_SPAWN_GORGON) {
            BufferedImage gorgonHeadTexture = TextureManager.getTexture("gorgon_head");
            BufferedImage gorgonHairTexture = TextureManager.getTexture("gorgon_hair_head");
            BufferedImage gorgonHairBodyTexture = TextureManager.getTexture("gorgon_hair_body");
            gorgon = new Gorgon(WIDTH / 2 - 50, HEIGHT / 2 - 50, gorgonHeadTexture, gorgonHairTexture, gorgonHairBodyTexture);
        }
    }

    private void spawnBotSnake() {
        long currentTime = System.currentTimeMillis();
        if (botSnakes.size() < maxBots && currentTime - lastBotSpawnTime >= botSpawnInterval) {
            int x = random.nextInt(WIDTH - 20);
            int y = random.nextInt(HEIGHT - 20);
            int size = (random.nextInt(5) + 1) * 5;
            botSnakes.add(new BotSnake(x, y, TextureManager.getTexture("snake_bot_head"), TextureManager.getTexture("snake_bot_body"), size));
            lastBotSpawnTime = currentTime;
        }
    }

    private void updateBots() {
        for (BotSnake botSnake : botSnakes) {
            botSnake.moveRandomly();
        }
    }
    private void generateInitialFood() {
        for (int i = 0; i < foodCount; i++) {
            int x = random.nextInt(WIDTH - 20);
            int y = random.nextInt(HEIGHT - 20);
            foodList.add(new Food(x, y, false, random.nextDouble() < 0.2));
        }
    }

    private void generateFood() {
        int x = random.nextInt(WIDTH - 20);
        int y = random.nextInt(HEIGHT - 20);
        foodList.add(new Food(x, y, random.nextDouble() < 0.2, false));
    }

    private void drawGameWonScreen(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        String gameWonText = "You Won! Your score: " + score;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameWonText);
        int textHeight = fm.getHeight();
        g2d.drawString(gameWonText, (WIDTH - textWidth) / 2, (HEIGHT - textHeight) / 2 - 50);


        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        String playAgainText = "Play Again";
        fm = g2d.getFontMetrics();
        int playAgainWidth = fm.stringWidth(playAgainText);
        int playAgainHeight = fm.getHeight();
        playAgainButton = new Rectangle2D.Double((WIDTH - playAgainWidth) / 2, (HEIGHT - playAgainHeight) / 2 + 20, playAgainWidth, playAgainHeight);
        g2d.draw(playAgainButton);
        g2d.drawString(playAgainText, (WIDTH - playAgainWidth) / 2, (HEIGHT - playAgainHeight) / 2 + 20 + playAgainHeight);


        String exitText = "Exit Game";
        fm = g2d.getFontMetrics();
        int exitWidth = fm.stringWidth(exitText);
        int exitHeight = fm.getHeight();
        exitButton = new Rectangle2D.Double((WIDTH - exitWidth) / 2, (HEIGHT - exitHeight) / 2 + 70, exitWidth, exitHeight);
        g2d.draw(exitButton);
        g2d.drawString(exitText, (WIDTH - exitWidth) / 2, (HEIGHT - exitHeight) / 2 + 70 + exitHeight);
    }

    private void drawGameOverScreen(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        String gameOverText = "Вы проиграли! Счет: " + score;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        int textHeight = fm.getHeight();
        g2d.drawString(gameOverText, (WIDTH - textWidth) / 2, (HEIGHT - textHeight) / 2 - 50);

        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        String playAgainText = "Играть Снова";
        fm = g2d.getFontMetrics();
        int playAgainWidth = fm.stringWidth(playAgainText);
        int playAgainHeight = fm.getHeight();
        playAgainButton = new Rectangle2D.Double((WIDTH - playAgainWidth) / 2, (HEIGHT - playAgainHeight) / 2 + 20, playAgainWidth, playAgainHeight);
        g2d.draw(playAgainButton);
        g2d.drawString(playAgainText, (WIDTH - playAgainWidth) / 2, (HEIGHT - playAgainHeight) / 2 + 20 + playAgainHeight);

        String exitText = "Выход";
        fm = g2d.getFontMetrics();
        int exitWidth = fm.stringWidth(exitText);
        int exitHeight = fm.getHeight();
        exitButton = new Rectangle2D.Double((WIDTH - exitWidth) / 2, (HEIGHT - exitHeight) / 2 + 70, exitWidth, exitHeight);
        g2d.draw(exitButton);
        g2d.drawString(exitText, (WIDTH - exitWidth) / 2, (HEIGHT - exitHeight) / 2 + 70 + exitHeight);

        String mainMenuText = "Главное Меню";
        fm = g2d.getFontMetrics();
        int mainMenuWidth = fm.stringWidth(mainMenuText);
        int mainMenuHeight = fm.getHeight();
        mainMenuButton = new Rectangle2D.Double((WIDTH - mainMenuWidth) / 2, (HEIGHT - mainMenuHeight) / 2 + 120, mainMenuWidth, mainMenuHeight);
        g2d.draw(mainMenuButton);
        g2d.drawString(mainMenuText, (WIDTH - mainMenuWidth) / 2, (HEIGHT - mainMenuHeight) / 2 + 120 + mainMenuHeight);
    }


    private void drawBottomText(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        String ivanText = "ivanbogdanovoptimization";
        int textWidth = fm.stringWidth(ivanText);
        g2d.drawString(ivanText, 10, HEIGHT - 30);
    }
    private void drawScore(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g2d.getFontMetrics();
        String botsKilledText = "Bots Killed: " + botsKilled;
        int textWidth = fm.stringWidth(botsKilledText);
        int textHeight = fm.getHeight();
        Rectangle2D botsKilledRect = new Rectangle2D.Double(
                WIDTH - textWidth - 20,
                HEIGHT - textHeight - 120,
                textWidth,
                textHeight
        );
        g2d.drawString(botsKilledText, (int) botsKilledRect.getX(), (int) botsKilledRect.getY() + textHeight);

        String scoreText = "Score: " + score;
        textWidth = fm.stringWidth(scoreText);
        textHeight = fm.getHeight();
        Rectangle2D scoreRect = new Rectangle2D.Double(
                WIDTH - textWidth - 20,
                HEIGHT - textHeight - 70,
                textWidth,
                textHeight
        );
        g2d.drawString(scoreText, (int) scoreRect.getX(), (int) scoreRect.getY() + textHeight);
    }

    private void resetGame() {
        isGameOver = false;
        isGameWon = false;
        snake = new Snake(WIDTH / 2, HEIGHT / 2,
                TextureManager.getTexture("snake_head"),
                TextureManager.getTexture("snake_body"));
        foodList.clear();
        botSnakes.clear();
        epicFoodList.clear();
        gorgon = null;
        botsKilled = 0;
        generateInitialFood();
        isGorgonDefeated = false;
        isBoosting = false;
    }
    private void backToMenu() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.dispose();
        SwingUtilities.invokeLater(MainMenu::new);
    }
}