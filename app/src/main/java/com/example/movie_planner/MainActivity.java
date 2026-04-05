package com.example.movie_planner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText name, people;
    RadioGroup genreGroup;
    CheckBox popcorn, drink, nachos;
    Button submit, btnDate;
    Spinner spinner;
    DBHelper dbHelper;

    String selectedDate = "";

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

        dbHelper = new DBHelper(this);

        // Notification Permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // SharedPref load
        SharedPreferences prefs = getSharedPreferences("MovieApp", MODE_PRIVATE);
        name.setText(prefs.getString("name", ""));

        // Spinner
        String[] platforms = {"Choose", "Netflix", "Theatre", "Amazon Prime"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, platforms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Date Picker
        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        btnDate.setText(selectedDate);
                        validate();
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        });

        // TextWatcher
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                validate();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        submit.setOnClickListener(v -> showDialog());
    }

    // ✅ MOVED OUTSIDE onCreate (FIX)
    private void validateInputs() {

        String nameText = name.getText().toString().trim();
        if (nameText.isEmpty()) {
            name.setError("Enter your name");
        } else {
            name.setError(null);
        }

        String peopleText = people.getText().toString().trim();

        if (peopleText.isEmpty()) {
            people.setError("Enter number of people");
        } else {
            try {
                int num = Integer.parseInt(peopleText);
                if (num <= 0) {
                    people.setError("Invalid number");
                } else {
                    people.setError(null);
                }
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
                && !selectedDate.isEmpty();

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

        dbHelper.insertPlan(
                name.getText().toString(),
                people.getText().toString(),
                genre,
                snacks,
                platform,
                selectedDate
        );



        SharedPreferences.Editor editor = getSharedPreferences("MovieApp", MODE_PRIVATE).edit();
        editor.putString("name", name.getText().toString());
        editor.apply();

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("people", people.getText().toString());
        intent.putExtra("genre", genre);
        intent.putExtra("snacks", snacks);
        intent.putExtra("platform", platform);
        intent.putExtra("date", selectedDate);

        Toast.makeText(this,
                "🎬 Movie Plan Created Successfully!",
                Toast.LENGTH_SHORT).show();

        startActivity(intent);

        showNotification(intent);
    }

    private void showNotification(Intent intent) {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "movie_channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Movie Channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("🎬 Movie Night Ready!")
                .setContentText("Tap to view your plan")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        manager.notify(1, notification);
    }
}