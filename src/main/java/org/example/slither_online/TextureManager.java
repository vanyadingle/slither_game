package org.example.slither_online;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private static final Map<String, BufferedImage> textures = new HashMap<>();

    public static void loadAllTextures() {
        loadTexture("menu_background", "/menu_background.png");
        loadTexture("background", "/background.png");
        loadTexture("snake_head", "/snake_head.png");
        loadTexture("snake_body", "/snake_body.png");
        loadTexture("snake_bot_head", "/snake_bot_head.png");
        loadTexture("snake_bot_body", "/snake_bot_body.png");
        loadTexture("food_normal", "/food_normal.png");
        loadTexture("food_bomb", "/food_bomb.png");
        loadTexture("food_boost", "/food_boost.png");
        loadTexture("food_epic", "/food_epic.png");
        loadTexture("gorgon_head", "/gorgon_head.png");
        loadTexture("gorgon_hair_head", "/gorgon_hair_head.png");
        loadTexture("gorgon_hair_body", "/gorgon_hair_body.png");
        loadTexture("button_background", "/button_background.png");
    }

    public static BufferedImage getTexture(String name) {
        return textures.get(name);
    }

    private static void loadTexture(String name, String path) {
        try {
            InputStream inputStream = TextureManager.class.getResourceAsStream(path);
            if (inputStream != null) {
                BufferedImage image = ImageIO.read(inputStream);
                textures.put(name, image);
            } else {
                System.out.println("Could not find texture at path: " + path);
            }
        } catch (IOException e) {
            System.err.println("Error loading texture '" + name + "' from '" + path + "': " + e.getMessage());
        }
    }
}
