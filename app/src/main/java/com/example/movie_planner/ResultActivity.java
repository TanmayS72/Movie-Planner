package com.example.movie_planner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tv = findViewById(R.id.tvResult);
        Button btnMap = findViewById(R.id.btnMap);


        String name = getIntent().getStringExtra("name");
        String people = getIntent().getStringExtra("people");
        String genre = getIntent().getStringExtra("genre");
        String snacks = getIntent().getStringExtra("snacks");
        String platform = getIntent().getStringExtra("platform");
        String date = getIntent().getStringExtra("date");


        int cost = getIntent().getIntExtra("cost", 0);
        String suggestion = getIntent().getStringExtra("suggestion");
        String time = getIntent().getStringExtra("time");


        if (name == null) name = "Guest";
        if (people == null) people = "0";
        if (genre == null) genre = "Not selected";
        if (snacks == null) snacks = "None";
        if (platform == null) platform = "Not selected";
        if (date == null) date = "Not selected";
        if (suggestion == null) suggestion = "No suggestion";
        if (time == null) time = "Not selected";

        // Greeting
        String greeting;
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) greeting = "Good Morning ";
        else if (hour < 17) greeting = "Good Afternoon ";
        else greeting = "Good Evening ";

        // Final Output
        String resultText =
                greeting + ", " + name + "!\n\n" +

                        "🎬 Your Movie Plan\n" +
                        "━━━━━━━━━━━━━━━\n\n" +

                        "👥 People: " + people + "\n" +
                        "🎭 Genre: " + genre + "\n" +
                        "📺 Platform: " + platform + "\n" +
                        "📅 Date: " + date + "\n" +
                        "⏰ Time: " + time + "\n" +
                        "🍿 Snacks: " + snacks + "\n\n" +

                        " Suggested Movie: " + suggestion + "\n" +
                        " Total Cost: ₹" + cost + "\n\n" +

                        " Sit back, relax & enjoy your movie night! 🍿🎉🎊";

        tv.setText(resultText);


        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=movie theatre near me"));
            startActivity(intent);
        });
    }
}