package com.example.movie_planner;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.*;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText name, people;
    RadioGroup genreGroup;
    CheckBox popcorn, drink, nachos;
    Button submit, btnDate, btnTime, btnHistory, btnSignOut;
    Spinner spinner;
    DBHelper dbHelper;

    String selectedDate = "";
    String selectedTime = "";
    private static final String CHANNEL_ID = "movie_plan_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.etName);
        people = findViewById(R.id.etPeople);
        genreGroup = findViewById(R.id.rgGenre);
        popcorn = findViewById(R.id.cbPopcorn);
        drink = findViewById(R.id.cbDrink);
        nachos = findViewById(R.id.cbNachos);
        submit = findViewById(R.id.btnSubmit);

        spinner = findViewById(R.id.spinnerPlatform);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        btnHistory = findViewById(R.id.btnHistory);
        btnSignOut = findViewById(R.id.btnSignOut);

        dbHelper = new DBHelper(this);

        // Notification permission request
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // SharedPref
        SharedPreferences userSession = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = userSession.getString("username", "");

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT name FROM users WHERE username=?",
                new String[]{username}
        );

        if(cursor.moveToFirst()){
            name.setText(cursor.getString(0));
        }
        cursor.close();



        // Spinner
        String[] platforms = {"Choose", "Netflix", "Theatre", "Amazon Prime"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, platforms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (view != null) ((TextView) view).setTextColor(Color.WHITE);
                validate();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        btnDate.setText(selectedDate);
                        validate();
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        btnTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            TimePickerDialog dialog = new TimePickerDialog(this,
                    (view, h, m) -> {
                        selectedTime = h + ":" + m;
                        btnTime.setText(selectedTime);
                        validate();
                    }, hour, minute, true);
            dialog.show();
        });

        TextWatcher watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
                validate();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        };

        name.addTextChangedListener(watcher);
        people.addTextChangedListener(watcher);

        CompoundButton.OnCheckedChangeListener checkListener = (b, isChecked) -> validate();
        popcorn.setOnCheckedChangeListener(checkListener);
        drink.setOnCheckedChangeListener(checkListener);
        nachos.setOnCheckedChangeListener(checkListener);

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
        btnSignOut.setOnClickListener(v -> {

            SharedPreferences session = getSharedPreferences("UserSession", MODE_PRIVATE);
            session.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
        });

        submit.setOnClickListener(v -> showDialog());
    }

    private void validateInputs() {
        String nameText = name.getText().toString().trim();
        if (nameText.isEmpty()) name.setError("Enter your name");
        else name.setError(null);

        String peopleText = people.getText().toString().trim();
        if (peopleText.isEmpty()) {
            people.setError("Enter number of people");
        } else {
            try {
                int num = Integer.parseInt(peopleText);
                if (num <= 0) people.setError("Invalid number");
                else people.setError(null);
            } catch (Exception e) {
                people.setError("Invalid input");
            }
        }
    }

    private void validate() {
        boolean valid = !name.getText().toString().trim().isEmpty()
                && !people.getText().toString().trim().isEmpty()
                && genreGroup.getCheckedRadioButtonId() != -1
                && (popcorn.isChecked() || drink.isChecked() || nachos.isChecked())
                && spinner.getSelectedItemPosition() != 0
                && !selectedDate.isEmpty()
                && !selectedTime.isEmpty();

        submit.setEnabled(valid);
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Plan")
                .setMessage("Are you sure you want to plan this movie night?")
                .setPositiveButton("Yes", (d, w) -> proceed())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void proceed() {
        String genre = ((RadioButton) findViewById(
                genreGroup.getCheckedRadioButtonId())).getText().toString();

        String snacks = "";
        if (popcorn.isChecked()) snacks += "Popcorn ";
        if (drink.isChecked()) snacks += "Drink ";
        if (nachos.isChecked()) snacks += "Nachos ";

        String platform = spinner.getSelectedItem().toString();

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        dbHelper.insertPlan(
                username,
                people.getText().toString(),
                genre,
                snacks,
                platform,
                selectedDate,
                selectedTime
        );

        int cost = Integer.parseInt(people.getText().toString()) * 150;
        if (popcorn.isChecked()) cost += 100;
        if (drink.isChecked()) cost += 80;
        if (nachos.isChecked()) cost += 120;

        String suggestion = "";
        if (genre.equals("Action")) suggestion = "Avengers 🔥";
        if (genre.equals("Comedy")) suggestion = "Hangover 😂";
        if (genre.equals("Horror")) suggestion = "Conjuring 👻";

        getSharedPreferences("MovieApp", MODE_PRIVATE).edit()
                .putString("name", name.getText().toString()).apply();

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("people", people.getText().toString());
        intent.putExtra("genre", genre);
        intent.putExtra("snacks", snacks);
        intent.putExtra("platform", platform);
        intent.putExtra("date", selectedDate);
        intent.putExtra("time", selectedTime);
        intent.putExtra("cost", cost);
        intent.putExtra("suggestion", suggestion);

        Toast.makeText(this, "🎬 Movie Plan Created!", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        sendNotification(intent);
    }

    private void sendNotification(Intent intent) {
        // Create Notification Channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Movie Planner Alerts";
            String description = "Alerts for your movie plans";
            int importance = NotificationManager.IMPORTANCE_HIGH; // HIGH for heads-up pop-up
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.YELLOW);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Prepare Intent for Notification Click
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Build Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("🎬 Movie Night Ready!")
                .setContentText("Tap to view your movie plan details")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // For pre-Oreo
                .setDefaults(NotificationCompat.DEFAULT_ALL)   // Sound, Vibrate, Lights
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Final Permission Check for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // Notify with a unique ID
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
