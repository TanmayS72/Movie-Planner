package com.example.movie_planner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, username, password, confirmPassword;
    RadioGroup genderGroup;
    Button signup, btnDOB;
    DBHelper db;

    String selectedDOB = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        confirmPassword = findViewById(R.id.etConfirmPassword);
        genderGroup = findViewById(R.id.rgGender);
        signup = findViewById(R.id.btnSignup);
        btnDOB = findViewById(R.id.btnDOB);

        db = new DBHelper(this);


        btnDOB.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        selectedDOB = day + "/" + (month + 1) + "/" + year;
                        btnDOB.setText(selectedDOB);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });


        signup.setOnClickListener(v -> {

            String n = name.getText().toString().trim();
            String e = email.getText().toString().trim();
            String u = username.getText().toString().trim();
            String p = password.getText().toString().trim();
            String cp = confirmPassword.getText().toString().trim();

            int genderId = genderGroup.getCheckedRadioButtonId();


            if(n.isEmpty() || e.isEmpty() || u.isEmpty() || p.isEmpty() || cp.isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            if(!n.matches("[a-zA-Z ]+")){
                Toast.makeText(this, "Name cannot contain numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            if(u.contains(" ")){
                Toast.makeText(this, "Username cannot contain spaces", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(genderId == -1){
                Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show();
                return;
            }

            if(selectedDOB.isEmpty()){
                Toast.makeText(this, "Select Date of Birth", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!p.equals(cp)){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }


            if(db.isUsernameExists(u)){
                Toast.makeText(this, "Username already taken ", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedGender = findViewById(genderId);


            boolean inserted = db.insertUser(
                    n,
                    e,
                    u,
                    selectedGender.getText().toString(),
                    selectedDOB,
                    p
            );

            if(inserted){
                Toast.makeText(this, "Signup Successful ", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Signup Failed ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}