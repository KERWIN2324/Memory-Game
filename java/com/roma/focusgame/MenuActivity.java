package com.roma.focusgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

public class MenuActivity extends AppCompatActivity {

    private TextView tvPlayerName, tvHighScore;
    private LinearLayoutCompat btnPlay;
    private ImageView btnSettings;

    private boolean isMusicOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvHighScore = findViewById(R.id.tvHighScore);
        btnPlay = findViewById(R.id.btnPlay);
        btnSettings = findViewById(R.id.btnSettings);

        isMusicOn = getIntent().getBooleanExtra("isMusicOn", false);

        if(!isMusicOn){
            MusicManager.pauseMusic();
        }else{
            MusicManager.startMusic(this);
        }

        // Cargar datos del jugador
        SharedPreferences datos = getSharedPreferences("MemoryGame", MODE_PRIVATE);
        String playerName = datos.getString("playerName", "Jugador");
        int highScore = datos.getInt("highScore", 0);

        tvPlayerName.setText(""+playerName);
        tvHighScore.setText(""+highScore);

        btnPlay.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, GameActivity.class);
            intent.putExtra("isMusicOn", isMusicOn);
            startActivity(intent);
        });


        btnSettings.setOnClickListener(view -> showSettingsDialog());
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        builder.setView(dialogView);

        TextView tvContact = dialogView.findViewById(R.id.tvContact);
        Button btnChangeName = dialogView.findViewById(R.id.btnChangeName);
        Button btnToggleSound = dialogView.findViewById(R.id.btnToggleSound);

        tvContact.setText("Contactar: support@roma.com");

        btnChangeName.setOnClickListener(view -> changePlayerName());

        btnToggleSound.setOnClickListener(view -> toggleSound());

        builder.setNegativeButton("Cerrar", null);
        builder.create().show();
    }


    private void changePlayerName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar nombre de jugador");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                SharedPreferences datos = getSharedPreferences("MemoryGame", MODE_PRIVATE);
                datos.edit().putString("playerName", newName).apply();
                tvPlayerName.setText(""+ newName);
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void toggleSound() {
        isMusicOn = !isMusicOn;
        if (isMusicOn) {
            MusicManager.startMusic(this); // Inicia la música
        } else {
            MusicManager.pauseMusic(); // Pausa la música
        }
    }
}
