package com.example.zacharius.sma;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by zacharius on 11/22/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MA.db";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        Log.d("DatabaseHelper", "Creating Database");
        db.execSQL(DatabaseContract.Create_ContactTable);
        db.execSQL(DatabaseContract.Create_MessageTable);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d("DatabaseHelper", "upgrading database");
        db.execSQL(DatabaseContract.Delete_Tables);
        onCreate(db);
    }
}