package com.example.movie_planner;

import android.content.*;
import android.database.sqlite.*;
import android.database.Cursor;

public class DBHelper extends SQLiteOpenHelper {

    //  Constructor (Database name + version)
    public DBHelper(Context context) {
        super(context, "MovieDB", null, 5); // version updated for users table
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //  ================== MOVIE PLANS TABLE ==================
        db.execSQL("CREATE TABLE plans(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "people TEXT, " +
                "genre TEXT, " +
                "snacks TEXT, " +
                "platform TEXT, " +
                "date TEXT, " +
                "time TEXT)");

        //  ================== USERS TABLE ==================
        db.execSQL("CREATE TABLE users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "username TEXT UNIQUE, " +
                "gender TEXT, " +
                "dob TEXT, " +
                "password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS plans");
        db.execSQL("DROP TABLE IF EXISTS users");

        onCreate(db);
    }

    //  ================== MOVIE PLAN METHODS ==================

    public void insertPlan(String name, String people, String genre,
                           String snacks, String platform, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("people", people);
        cv.put("genre", genre);
        cv.put("snacks", snacks);
        cv.put("platform", platform);
        cv.put("date", date);
        cv.put("time", time);

        db.insert("plans", null, cv);
    }



    //  ONLY NEW METHOD ADDED
    public Cursor getPlansByUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT plans.*, users.name AS real_name " +
                        "FROM plans INNER JOIN users " +
                        "ON plans.name = users.username " +
                        "WHERE users.username=?",
                new String[]{username}
        );
    }

    //  ================== USER AUTHENTICATION METHODS ==================

    public boolean insertUser(String name, String email, String username,
                              String gender, String dob, String password) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("email", email);
        cv.put("username", username);
        cv.put("gender", gender);
        cv.put("dob", dob);
        cv.put("password", password);

        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=?",
                new String[]{username}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}