package org.example.slither_online;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundManager {

    public static synchronized void playSound(String soundFile) {
        new Thread(() -> {
            try {
                InputStream audioStream = SoundManager.class.getClassLoader().getResourceAsStream("sounds/" + soundFile);

                if(audioStream == null){
                    System.err.println("Could not find file: " + soundFile);
                    return;
                }

                AudioInputStream audioInput = AudioSystem.getAudioInputStream(audioStream);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                while (clip.isRunning()) {
                    Thread.sleep(10);
                }
                clip.close();
                audioInput.close();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
                System.err.println("Error playing sound: " + soundFile);
                e.printStackTrace();
            }
        }).start();
    }
}
