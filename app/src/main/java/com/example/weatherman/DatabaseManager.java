package com.example.weatherman;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

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

    @SuppressLint("Range")
    public String fetch_place_key(String place_name){
        Cursor crs;
        String query = "select " + DatabaseHelper.PLACE_KEY + " from " + DatabaseHelper.TABLE_PLACE  +
                " where " + DatabaseHelper.PLACE_NAME + " is '" + place_name + "' collate nocase";
        crs = db.rawQuery(query, null);

        crs.moveToFirst();

        if (crs.getCount() <= 0)
            return "PLACE DOESN'T EXIST";
        else
            return crs.getString(crs.getColumnIndex(DatabaseHelper.PLACE_KEY));
    }

    @SuppressLint("Range")
    public String fetch_place_name(int place_id){
        Cursor crs;
        String query = "select " + DatabaseHelper.PLACE_NAME + " from " +
                DatabaseHelper.TABLE_PLACE + " where " + DatabaseHelper.PLACE_ID + " is " + place_id;

        crs = db.rawQuery(query, null);
        crs.moveToFirst();

        return crs.getString(crs.getColumnIndex(DatabaseHelper.PLACE_NAME));
    }

    @SuppressLint("Range")
    public int count_place(){
        Cursor crs;
        String query = "select count(*) from " + DatabaseHelper.TABLE_PLACE;
        crs = db.rawQuery(query, null);
        crs.moveToFirst();
        return crs.getInt(0);
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
