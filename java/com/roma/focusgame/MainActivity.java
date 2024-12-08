package com.roma.focusgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private AppCompatButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences datos = getSharedPreferences("MemoryGame", MODE_PRIVATE);
        if (datos.contains("playerName")) {
            goToMenu();
            return;
        }

        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = etName.getText().toString().trim();
                if (!playerName.isEmpty()) {
                    SharedPreferences.Editor editor = datos.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();
                    goToMenu();
                }
            }
        });
    }

    private void goToMenu() {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
