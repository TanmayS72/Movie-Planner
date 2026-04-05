package com.example.movie_planner;

import android.content.*;
import android.database.sqlite.*;
import android.database.Cursor;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "MovieDB", null, 2); // version updated
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE plans(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "people TEXT, " +
                "genre TEXT, " +
                "snacks TEXT, " +
                "platform TEXT, " +
                "date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS plans");
        onCreate(db);
    }

    public void insertPlan(String name, String people, String genre,
                           String snacks, String platform, String date) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("people", people);
        cv.put("genre", genre);
        cv.put("snacks", snacks);
        cv.put("platform", platform);
        cv.put("date", date);

        db.insert("plans", null, cv);
    }

    public Cursor getAllPlans() {
        return getReadableDatabase().rawQuery("SELECT * FROM plans", null);
    }
}