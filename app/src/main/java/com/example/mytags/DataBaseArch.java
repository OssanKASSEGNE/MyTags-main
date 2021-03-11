package com.example.mytags;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public  class DataBaseArch extends SQLiteOpenHelper {

    public static final String MEDIA_TABLE = "MEDIA_TABLE";
    public static final String COLUMN_MEDIA_URI = "MEDIA_URI";
    public static final String COLUMN_MEDIA_TYPE = "MEDIA_TYPE";
    public static final String COLUMN_TAG = "TAG";
    public static final String COLUMN_ID = "ID";

    public DataBaseArch(@Nullable Context context) {
        super(context, "mediaDataBase", null, 1);
    }

    //Is called on first call of DATABASE => We must create new DB here
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + MEDIA_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MEDIA_URI + " TEXT, " + COLUMN_MEDIA_TYPE + " TEXT, " + COLUMN_TAG + " TEXT )";
        db.execSQL(createTableStatement);
    }

    //Modify automatically DB, backwards compatibility
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne (dqsd)
}
