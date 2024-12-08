package com.roma.focusgame;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class MusicManager {

    private static MediaPlayer backgroundMusic;

    public static void startMusic(Context context) {
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
            backgroundMusic.setLooping(true);  // Hacer la música en bucle

            // Ajustar el volumen (por ejemplo, un 50% del volumen máximo)
            float volume = getMusicVolume(context);
            backgroundMusic.setVolume(volume, volume);

            backgroundMusic.start();
        } else if (!backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    // Función para obtener el volumen en función de un valor fijo
    public static float getMusicVolume(Context context) {
        // Puedes ajustar este valor a un porcentaje (ej. 0.5f para 50%)
        float volume = 0.9f;
        return volume;
    }

    public static void pauseMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    public static void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}
