package com.example.movie_planner;

import android.content.SharedPreferences; // ✅ added
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    TextView tvHistory;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tvHistory = findViewById(R.id.tvHistory);
        dbHelper = new DBHelper(this);

        //  changed: get logged-in username
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        //  changed: fetch only that user's plans
        Cursor cursor = dbHelper.getPlansByUser(username);

        StringBuilder data = new StringBuilder();

        if (cursor.getCount() == 0) {
            tvHistory.setText("No History Found ");
            return;
        }

        while (cursor.moveToNext()) {
            data.append("🎬 Plan\n");
            data.append("━━━━━━━━━━\n");

            int nameIndex = cursor.getColumnIndex("real_name");
            data.append("👤 Name: ").append(cursor.getString(nameIndex)).append("\n");
            data.append("👥 People: ").append(cursor.getString(2)).append("\n");
            data.append("🎭 Genre: ").append(cursor.getString(3)).append("\n");
            data.append("🍿 Snacks: ").append(cursor.getString(4)).append("\n");


                data.append("📺 Platform: ").append(cursor.getString(5)).append("\n");


                data.append("📅 Date: ").append(cursor.getString(6)).append("\n");


                data.append("⏰ Time: ").append(cursor.getString(7)).append("\n");
            

            data.append("\n\n");
        }
        cursor.close();

        tvHistory.setText(data.toString());
    }
}