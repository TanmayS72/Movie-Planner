package com.example.movie_planner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button login, signup;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        signup = findViewById(R.id.btnSignup);

        db = new DBHelper(this);


        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        if(prefs.getBoolean("isLoggedIn", false)){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        login.setOnClickListener(v -> {

            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if(user.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "Enter username & password", Toast.LENGTH_SHORT).show();
                return;
            }

            if(db.checkUser(user, pass)){


                prefs.edit().putBoolean("isLoggedIn", true)
                        .putString("username", user)
                        .apply();

                Toast.makeText(this, "Login Successful ", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, MainActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Invalid Username or Password ", Toast.LENGTH_SHORT).show();
            }
        });

        signup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }
}