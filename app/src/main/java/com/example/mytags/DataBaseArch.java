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
    public static final String COLUMN_MEDIA_IMAGE_URI = "MEDIA_IMAGE_URI";
    public static final String COLUMN_MEDIA_FILE_URI = "MEDIA_FILE_URI";
    public static final String COLUMN_MEDIA_TYPE = "MEDIA_TYPE";
    public static final String COLUMN_TAG = "TAG";
    public static final String COLUMN_ID = "ID";

    public DataBaseArch(@Nullable Context context) {
        super(context, "mediaDataBase", null, 1);
    }

    //Is called on first call of DATABASE => We must create new DB here
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + MEDIA_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MEDIA_IMAGE_URI + " TEXT, " + COLUMN_MEDIA_FILE_URI + " TEXT, " + COLUMN_MEDIA_TYPE + " TEXT, " + COLUMN_TAG + " TEXT )";
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

        cv.put(COLUMN_MEDIA_IMAGE_URI , media.getImageUri());
        cv.put(COLUMN_MEDIA_FILE_URI , media.getFileUri());
        cv.put(COLUMN_MEDIA_TYPE , media.getMediaType());
        cv.put(COLUMN_TAG , media.getTag());

        long insert = db.insert(MEDIA_TABLE, null, cv);
        if (insert == -1){
            return false;
        }else{
            return true;
        }

    }

    //List all media in table
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
                String mediaImageUri = cursor.getString(1);
                String mediaFileUri = cursor.getString(2);
                String mediaType = cursor.getString(3);
                String tag = cursor.getString(4);

                //Create a new media
                Media newMedia = new Media(mediaID,mediaImageUri,mediaFileUri,mediaType,tag);
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


    //Select All images
    public List<Media> selectAllImages(){
        return selectGeneric("image");
    }

    //Select All audio
    public List<Media> selectAllAudio(){
        return selectGeneric("audio");
    }

    //Select All video
    public List<Media> selectAllVideo(){
        return selectGeneric("video");
    }

    //Select All documents
    public List<Media> selectDocumentTxt(){
        return selectGeneric("text");
    }
    public List<Media> selectDocumentPdfApk(){
        return selectGeneric("application");
    }

    //Select All jpeg
    public List<Media> selectAllPng(){
        return selectGeneric("png");
    }

    //Select From Tags
    public List<Media> selectFromTag(String tags){
        List<Media> returnList = new ArrayList<>();
        //get data fom database


        String queryString = "SELECT * FROM " + MEDIA_TABLE + " WHERE "+ COLUMN_TAG+" LIKE '%" + tags + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            //Loop through the result and create new media objects for each row
            do{
                int mediaID = cursor.getInt(0);
                String mediaImageUri = cursor.getString(1);
                String mediaFileUri = cursor.getString(2);
                String mediaType = cursor.getString(3);
                String tag = cursor.getString(4);

                //Create a new media
                Media newMedia = new Media(mediaID,mediaImageUri,mediaFileUri,mediaType,tag);
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

    //Delete All entries
    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + MEDIA_TABLE);
        db.close();
    }

    //Delete a Media
    public void deleteMedia(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + MEDIA_TABLE+ " WHERE "+COLUMN_MEDIA_FILE_URI+"='"+value+"'");
        db.close();
    }

    //Select Généric
    private List<Media> selectGeneric(String subcategory){
        List<Media> returnList = new ArrayList<>();
        //get data fom database


        String queryString = "SELECT * FROM " + MEDIA_TABLE + " WHERE "+ COLUMN_MEDIA_TYPE+" LIKE '%" + subcategory + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            //Loop through the result and create new media objects for each row
            do{
                int mediaID = cursor.getInt(0);
                String mediaImageUri = cursor.getString(1);
                String mediaFileUri = cursor.getString(2);
                String mediaType = cursor.getString(3);
                String tag = cursor.getString(4);

                //Create a new media
                Media newMedia = new Media(mediaID,mediaImageUri,mediaFileUri,mediaType,tag);
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

    //select All unique tags
    protected List<String> selectTags(){
        List<String> returnList = new ArrayList<>();
        //get data fom database
        String queryString = "SELECT DISTINCT " + COLUMN_TAG + " FROM " + MEDIA_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            //Loop through the result and create new media objects for each row
            do{
                String tag = cursor.getString(0);
                returnList.add(tag);
            }while(cursor.moveToNext()); // while we can move to new line
        }else{
            //failure case
        }
        //Close db and cursor after usage
        cursor.close();
        db.close();
        return returnList;
    }

    //Select Tags and Type Count
    protected int selectCount(String tag, String type){
        int occurence = 0;
        //get data fom database


        String queryString = "SELECT DISTINCT COUNT(*) FROM " + MEDIA_TABLE + " WHERE "+ COLUMN_MEDIA_TYPE +" LIKE '%" + type + "%'  AND " + COLUMN_TAG +" = '"+tag+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(null != cursor){
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                occurence = cursor.getInt(0);
            }
        cursor.close();
    }

    db.close();
    return occurence;
    }

}
