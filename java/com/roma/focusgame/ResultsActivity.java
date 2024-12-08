package com.roma.focusgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    private TextView tvFinalScore;
    private Button btnReturnToMenu;
    private boolean isMusicOn = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        tvFinalScore = findViewById(R.id.tvFinalScore);
        btnReturnToMenu = findViewById(R.id.btnReturnToMenu);

        int finalScore = getIntent().getIntExtra("score", 0);

        isMusicOn = getIntent().getBooleanExtra("isMusicOn", false); // El valor por defecto es 'false'

        tvFinalScore.setText("Puntaje final: " + finalScore);

        SharedPreferences prefs = getSharedPreferences("MemoryGame", MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0);
        if (finalScore > highScore) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", finalScore);
            editor.apply();
        }

        btnReturnToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultsActivity.this, MenuActivity.class);
                intent.putExtra("isMusicOn", isMusicOn);
                startActivity(intent);
                finish();
            }
        });
    }
}

