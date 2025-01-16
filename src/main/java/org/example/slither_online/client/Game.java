package org.example.slither_online.client;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {
    private GamePanel gamePanel;


    public Game() {
        SwingUtilities.invokeLater(() -> {
            initializeGame();
        });
    }

    private void initializeGame() {
        gamePanel = new GamePanel();
        add(gamePanel);
        setTitle("Slither Online");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        new Game();
    }
}