package com.example.movie_planner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        TextView title = findViewById(R.id.title);
        TextView subtitle = findViewById(R.id.subtitle);


        title.setAlpha(0f);
        title.setScaleX(0.8f);
        title.setScaleY(0.8f);

        subtitle.setAlpha(0f);


        title.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1200)
                .start();


        subtitle.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(600)
                .start();


        new Handler().postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);


            if (prefs.getBoolean("isLoggedIn", false)) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish();

        }, SPLASH_TIME);
    }
}