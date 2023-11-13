package com.bss.bishnoi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BhajanSqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bishnoiapp.db";
    private static final int DATABASE_VERSION = 1;

    // Define the table name and column names
    private static final String TABLE_NAME = "music_table";

    // Firebase Columns
    private static final String ARTIST = "artist";
    private static final String AUDIO_URL = "audioUrl";
    private static final String DURATION = "duration";
    private static final String ID = "id";
    private static final String IMAGE_URL = "imageUrl";
    private static final String KEYWORDS = "keywords";
    private static final String LYRICS = "lyrics";
    private static final String SIZE = "size";
    private static final String TITLE = "title";
    private static final String TYPE = "type";


    // Define the create table query
    public BhajanSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database table
        final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
                ARTIST + " TEXT," +
                AUDIO_URL + " TEXT," +
                DURATION + " TEXT," +
                ID + " TEXT UNIQUE," +
                IMAGE_URL + " TEXT," +
                KEYWORDS + " TEXT," +
                LYRICS + " TEXT," +
                SIZE + " TEXT," +
                TITLE + " TEXT," +
                TYPE + " TEXT" +
                ")";

        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertAudioItem(String id, String title, String imageUrl, String audioUrl, String artist, String duration, String type, String lyrics, String keywords, String size) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ID, id);
        values.put(TITLE, title);
        values.put(IMAGE_URL, imageUrl);
        values.put(ARTIST, artist);
        values.put(AUDIO_URL, audioUrl);
        values.put(DURATION, duration);
        values.put(TYPE, type);
        values.put(LYRICS, lyrics);
        values.put(KEYWORDS, keywords);
        values.put(SIZE, size);

        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();
    }
}
