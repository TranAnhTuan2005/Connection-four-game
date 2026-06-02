/**
 * @file    SoundManager.java
 * @package vn.edu.nlu.fit.audio
 * @author  [Người 3]
 * @date    2026-06-01
 * @version 1.0
 * @desc    Phát âm thanh cho các sự kiện trong game.
 *          Sử dụng javax.sound.sampled.Clip để phát file .wav.
 *          File âm thanh đặt trong src/main/resources/sounds/
 *          Cần có: drop.wav, win.wav, draw.wav, click.wav
 * @history v1.0 2026-06-01 - Tạo mới
 */
package vn.edu.nlu.fit.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {

    private static boolean enabled = true;

    public static void setEnabled(boolean on) {
        enabled = on;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void playDrop()  { play("/sounds/drop.wav"); }
    public static void playWin()   { play("/sounds/win.wav"); }
    public static void playDraw()  { play("/sounds/draw.wav"); }
    public static void playClick() { play("/sounds/click.wav"); }

    /** Phát âm thanh trong thread riêng để không block UI */
    private static void play(String resourcePath) {
        if (!enabled) return;

        new Thread(() -> {
            try (InputStream is = SoundManager.class.getResourceAsStream(resourcePath)) {
                if (is == null) {
                    System.err.println("Sound file not found: " + resourcePath);
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                System.err.println("Cannot play sound: " + e.getMessage());
            }
        }, "SoundThread").start();
    }
}
