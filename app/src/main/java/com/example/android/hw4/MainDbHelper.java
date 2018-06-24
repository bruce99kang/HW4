package com.example.android.hw4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "class.db";
    private static final int DATABASE_VERSION = 1;

    public MainDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_CLASS_TABLE = "CREATE TABLE "+ MainContract.MainEntry.TABLE_NAME +" ("+
                MainContract.MainEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MainContract.MainEntry.COLUMN_CLASS_NAME + " TEXT NOT NULL, "+
                MainContract.MainEntry.COLUMN_CLASS_DAY + " TEXT NOT NULL, "+
                MainContract.MainEntry.COLUMN_CLASS_TIME + " TEXT NOT NULL, "+
                MainContract.MainEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"+
                ");";
        db.execSQL(SQL_CREATE_CLASS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MainContract.MainEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

