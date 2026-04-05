package com.example.movie_planner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2200; // ⏱ duration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 🔗 Link UI
        TextView title = findViewById(R.id.title);
        TextView subtitle = findViewById(R.id.subtitle);

        // 🎬 Initial state (invisible + slightly smaller)
        title.setAlpha(0f);
        title.setScaleX(0.8f);
        title.setScaleY(0.8f);

        subtitle.setAlpha(0f);

        // 🔥 Title animation (zoom + fade)
        title.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1200)
                .start();

        // 🔥 Subtitle animation (delayed fade)
        subtitle.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(600)
                .start();

        // ⏳ Delay → Move to next screen
        new Handler().postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);

            // 🔐 Check login session
            if (prefs.getBoolean("isLoggedIn", false)) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish(); // close splash

        }, SPLASH_TIME);
    }
}