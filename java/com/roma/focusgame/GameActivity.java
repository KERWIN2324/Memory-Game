package com.roma.focusgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private TextView tvScore;
    private ProgressBar progressBarTime;
    private GridLayout gameBoard;
    private Button btnPause;

    private List<Integer> cardImages;
    private Button firstCard, secondCard;
    private boolean isChecking = false;
    private int score = 0;
    private int tiempo;
    private int nivel = 1;
    private CountDownTimer timer;
    private int totalCards,cartas_correctas;

    private boolean isMusicOn;
    private MediaPlayer successSound, failSound;

    private int tiempo_pausa = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvScore = findViewById(R.id.tvScore);
        progressBarTime = findViewById(R.id.progressBarTime);
        gameBoard = findViewById(R.id.gameBoard);
        btnPause = findViewById(R.id.btnPause);

        isMusicOn = getIntent().getBooleanExtra("isMusicOn", false);

        if(!isMusicOn){
            MusicManager.pauseMusic();
        }

        successSound = MediaPlayer.create(this, R.raw.success_sound);
        failSound = MediaPlayer.create(this, R.raw.fail_sound);

        setupGame();

        btnPause.setOnClickListener(view -> showPauseMenu());
    }

    private void setupGame() {
        generateCardImages();
        loadNivel(nivel);
    }

    private void generateCardImages() {
        cardImages = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            cardImages.add(getResources().getIdentifier("image_" + i, "raw", getPackageName()));
        }
        Collections.shuffle(cardImages);
    }

    private void loadNivel(int nivel) {
        int rows, columns;

        if (nivel <= 4) {
            rows = 3;
            columns = 4;
            tiempo = 30 - (nivel - 1) * 2;
        } else if (nivel <= 9) {
            rows = 4;
            columns = 4;
            tiempo = 60 - (nivel - 5) * 5;
        } else {
            rows = 8;
            columns = 5;
            tiempo = Math.max(20, 100 - (nivel - 10) * 2);
        }

        totalCards = rows * columns;
        cartas_correctas = 0;
        setupGame(rows, columns, totalCards);

        progressBarTime.setMax(tiempo);
        startTimer();
    }


    private void setupGame(int rows, int columns, int totalCards) {
        gameBoard.removeAllViews();
        gameBoard.setRowCount(rows);
        gameBoard.setColumnCount(columns);

        //selecciona la mitad del total de cartas
        List<Integer> selectedImages = mesclarCartas(totalCards / 2);

        // Duplicar las imágenes para formar parejas
        selectedImages.addAll(new ArrayList<>(selectedImages));
        Collections.shuffle(selectedImages);

        int tamaño_carta = (int) (getResources().getDisplayMetrics().density * 60);

        for (int imageId : selectedImages) {
            Button card = new Button(this);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = tamaño_carta;
            params.height = tamaño_carta;
            params.setMargins(8, 8, 8, 8);
            card.setLayoutParams(params);

            card.setBackground(getDrawable(R.drawable.card_back));
            card.setTag(imageId);

            card.setOnClickListener(view -> cardClick(card));
            gameBoard.addView(card);
        }
    }

    private List<Integer> mesclarCartas(int count) {
        List<Integer> uniqueImages = new ArrayList<>(cardImages);
        Collections.shuffle(uniqueImages);
        return uniqueImages.subList(0, count);
    }

    private void cardClick(Button card) {
        if (isChecking || card == firstCard) return;

        // Animación de volteo
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(card, "rotationY", 0f, 90f);
        flipOut.setDuration(150);

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Drawable cardImage = getDrawable((int) card.getTag());
                card.setBackground(cardImage);

                ObjectAnimator flipIn = ObjectAnimator.ofFloat(card, "rotationY", -90f, 0f);
                flipIn.setDuration(150);
                flipIn.start();
            }
        });

        flipOut.start();

        if (firstCard == null) {
            firstCard = card;
        } else {
            secondCard = card;
            isChecking = true;
            gameBoard.postDelayed(this::verificar_pareja, 1000);
        }
    }

    private void verificar_pareja() {

        int firstImage = (int) firstCard.getTag();
        int secondImage = (int) secondCard.getTag();

        if (firstImage == secondImage) {
            cartas_correctas += 2;
            score += 50;
            timer.cancel();
            startTimerFrom(tiempo + 2);
            successSound.start();

            firstCard.setEnabled(false);
            secondCard.setEnabled(false);
            firstCard.setClickable(false);
            secondCard.setClickable(false);
        } else {
            flipCardBack(firstCard);
            flipCardBack(secondCard);
            score -= 20;
            failSound.start();
        }

        firstCard = null;
        secondCard = null;
        isChecking = false;

        tvScore.setText("Puntos: " + score);

        if (cartas_correctas == totalCards) {
            nextNivel();
        }
    }

    private void flipCardBack(Button card) {
        if (card != null) {
            ObjectAnimator flipOut = ObjectAnimator.ofFloat(card, "rotationY", 0f, 90f);
            flipOut.setDuration(150);

            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    card.setBackground(getDrawable(R.drawable.card_back));

                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(card, "rotationY", -90f, 0f);
                    flipIn.setDuration(150);
                    flipIn.start();
                }
            });

            flipOut.start();
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(tiempo * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempo = (int) (millisUntilFinished / 1000);
                progressBarTime.setProgress(tiempo);
            }

            @Override
            public void onFinish() {
                endGame();
            }
        };
        timer.start();
    }

    private void showPauseMenu() {
        if (timer != null) {
            timer.cancel();
            tiempo_pausa = tiempo;
        }

        TextView titleView = new TextView(this);
        titleView.setText("Juego en pausa");
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(16); // Tamaño del texto
        titleView.setPadding(0, 16, 0, 16); // Espaciado (opcional)

        String[] options = {"Continuar", "Reiniciar", "Salir", "Activar/Desactivar Música"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView item = (TextView) super.getView(position, convertView, parent);
                item.setGravity(Gravity.CENTER);
                item.setTextSize(12);
                return item;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(titleView)
                .setAdapter(adapter, (dialog, i) -> {
                    switch (i) {
                        case 0:
                            startTimerFrom(tiempo_pausa);
                            break;
                        case 1:
                            setupGame();
                            break;
                        case 2:
                            finish();
                            break;
                        case 3:
                            toggleMusic();
                            startTimerFrom(tiempo_pausa);
                            break;
                    }
                })
                .show();
    }

    private void toggleMusic() {
        isMusicOn = !isMusicOn;
        if (isMusicOn) {
            MusicManager.startMusic(this);
        } else {
            MusicManager.pauseMusic();
        }
    }

    private void startTimerFrom(int remainingTime) {
        tiempo = remainingTime;
        startTimer();
    }

    private void endGame() {
        if (isMusicOn) {
            MusicManager.stopMusic();
        }
        Intent intent = new Intent(GameActivity.this, ResultsActivity.class);
        intent.putExtra("isMusicOn", isMusicOn);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    private void nextNivel() {
        timer.cancel();
        nivel++;
        loadNivel(nivel);
    }
}
