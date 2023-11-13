package com.bss.bishnoi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadBhajanSqlitHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bishnoiapp.db";
    private static final int DATABASE_VERSION = 1;

    // Define the table name and column names
    private static final String TABLE_NAME = "music_downloads_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUDIO_URI = "audio_uri";

    private static final String COLUMN_TYPE = "type";

    private static final String COLUMN_DOWNLOAD_TIMESTAMP = "download_timestamp";

    private static final String COLUMN_BHAJAN_ID = "bhajan_id";

    // Define the create table query
    public DownloadBhajanSqlitHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database table
        final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +  // Add a space after COLUMN_TITLE and before TEXT
                COLUMN_TYPE + " TEXT, " +   // Add a space after COLUMN_TYPE and before TEXT
                COLUMN_AUDIO_URI + " TEXT, " + // Add a space after COLUMN_AUDIO_URI and before TEXT
                COLUMN_DOWNLOAD_TIMESTAMP + " INTEGER, " +  // Add a comma at the end
                COLUMN_BHAJAN_ID + " TEXT UNIQUE)";
        db.execSQL(CREATE_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertAudioItem(String bhajan_id, String title, String downloadUri, String type, int timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("download_uri", downloadUri);
        values.put("type", type);
        values.put("download_timestamp", timestamp);
        values.put("bhajan_id", bhajan_id);

        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();
    }
}
