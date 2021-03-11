package com.example.mytags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    //Add a media to the table
    public boolean addOne (Media media){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues ();

        cv.put(COLUMN_MEDIA_URI , media.getUri());
        cv.put(COLUMN_MEDIA_TYPE , media.getMediaType());
        cv.put(COLUMN_TAG , media.getTag());

        long insert = db.insert(MEDIA_TABLE, null, cv);
        if (insert == -1){
            return false;
        }else{
            return true;
        }

    }

    public List<Media> selectAll(){
        List<Media> returnList = new ArrayList<>();
        //get data fom database

        String queryString = "SELECT * FROM " + MEDIA_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            //Loop through the result and create new media objects for each row
            do{
                int mediaID = cursor.getInt(0);
                String mediaUri = cursor.getString(1);
                String mediaType = cursor.getString(2);
                String tag = cursor.getString(3);

                //Create a new media
                Media newMedia = new Media(mediaID,mediaUri,mediaType,tag);
                returnList.add(newMedia);
            }while(cursor.moveToNext()); // while we can move to new line
        }else{
                //failure case
        }
        //Close db and cursor after usage
        cursor.close();
        db.close();
        return returnList;
    }
}
