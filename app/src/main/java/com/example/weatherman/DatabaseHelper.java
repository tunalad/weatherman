package com.example.weatherman;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "locations.db";
    static final String TABLE_PLACE = "place";
    static final String PLACE_ID = "place_id";
    static final String PLACE_NAME = "place_name";
    static final String PLACE_KEY = "place_key";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists '"+ TABLE_PLACE +"' (" +
                PLACE_ID + " integer primary key autoincrement, " +
                PLACE_NAME + " varchar not null, " +
                PLACE_KEY + " varchar not null unique);"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ TABLE_PLACE);
        onCreate(db);
    }
}
