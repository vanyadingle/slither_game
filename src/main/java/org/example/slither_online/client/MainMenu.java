package org.example.slither_online.client;

import org.example.slither_online.TextureManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class MainMenu extends JFrame {
    private BufferedImage backgroundTexture;
    private BufferedImage buttonTexture;

    public MainMenu() {
        TextureManager.loadAllTextures();
        backgroundTexture = TextureManager.getTexture("menu_background");
        buttonTexture = TextureManager.getTexture("button_background");
        setTitle("Slither Online");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Slither Online");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 80));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, gbc);


        gbc.gridy++;
        JButton playButton = new JButton("Играть") {
            @Override
            protected void paintComponent(Graphics g) {
                if (buttonTexture != null) {
                    g.drawImage(buttonTexture, 0, 0, getWidth(), getHeight(), this);
                }
                super.paintComponent(g);
            }
        };
        playButton.setFont(new Font("Arial", Font.BOLD, 50));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        add(playButton, gbc);

        gbc.gridy++;
        JButton exitButton = new JButton("Выйти из игры") {
            @Override
            protected void paintComponent(Graphics g) {
                if (buttonTexture != null) {
                    g.drawImage(buttonTexture, 0, 0, getWidth(), getHeight(), this);
                }
                super.paintComponent(g);
            }
        };
        exitButton.setFont(new Font("Arial", Font.BOLD, 50));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
        add(exitButton, gbc);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (backgroundTexture != null) {
            int bgWidth = backgroundTexture.getWidth();
            int bgHeight = backgroundTexture.getHeight();
            for (int x = 0; x < getWidth(); x += bgWidth) {
                for (int y = 0; y < getHeight(); y += bgHeight) {
                    g.drawImage(backgroundTexture, x, y, null);
                }
            }
        } else {
            System.out.println("Texture background is null!");
        }
    }


    private void startGame() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new GameWindow();
        });
    }

    private void exitGame() {
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}