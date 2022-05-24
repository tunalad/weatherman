package com.example.weatherman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.widget.ThemedSpinnerAdapter;

import java.sql.SQLDataException;


public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private Context c;
    private SQLiteDatabase db;

    public DatabaseManager(Context context){
        c = context;
    }

    public DatabaseManager open() throws SQLDataException{
        dbHelper = new DatabaseHelper(c);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public void insert (String place_name, String place_key){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.PLACE_NAME, place_name);
        cv.put(DatabaseHelper.PLACE_KEY, place_key);
        db.insert(DatabaseHelper.TABLE_PLACE, null, cv);
    }

    public Cursor fetch(){
        String[] columns = new String[] {DatabaseHelper.PLACE_ID, DatabaseHelper.PLACE_NAME, DatabaseHelper.PLACE_KEY};
        Cursor crs = db.query(DatabaseHelper.DATABASE_NAME, columns,null, null, null, null, null);

        if (crs != null)
            crs.moveToFirst();
        return crs;
    }

    public int update(long id, String place_name, String place_key){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.PLACE_NAME, place_name);
        cv.put(DatabaseHelper.PLACE_KEY, place_key);
        return db.update(DatabaseHelper.TABLE_PLACE, cv, DatabaseHelper.PLACE_ID + "=" + id, null);
    }

    public void delete(long id){
        db.delete(DatabaseHelper.TABLE_PLACE, DatabaseHelper.PLACE_ID + "=" + id, null);
    }
}
