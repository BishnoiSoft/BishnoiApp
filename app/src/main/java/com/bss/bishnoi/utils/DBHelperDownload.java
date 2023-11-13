package com.bss.bishnoi.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bss.bishnoi.models.BhajanModel;

public class DBHelperDownload extends SQLiteOpenHelper {

    public DBHelperDownload(Context context) {
        super(context, "Music_Download.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table MUSIC_DOWNLOAD_DETAILS(bhajan_id TEXT primary key, title TEXT, imageUrl TEXT, audioUrl TEXT, artist TEXT, duration TEXT, type TEXT, lyrics TEXT, keywords TEXT, size TEXT, downloads NUMBER, download_timestamp INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        DB.execSQL("drop Table if exists MUSIC_DOWNLOAD_DETAILS");
    }

    public Boolean insertDownloadData(BhajanModel bhajan) {
        long currentTimeMillis = System.currentTimeMillis();
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", bhajan.getTitle());
        contentValues.put("imageUrl", "file://" + bhajan.getImageUrl());
        contentValues.put("audioUrl", "file://" + bhajan.getAudioUrl());
        contentValues.put("artist", bhajan.getArtist());
        contentValues.put("duration", bhajan.getDuration());
        contentValues.put("type", bhajan.getType());
        contentValues.put("lyrics", bhajan.getLyrics());
        contentValues.put("keywords", bhajan.getKeywords());
        contentValues.put("size", bhajan.getKeywords());
        contentValues.put("downloads", bhajan.getDownloads());
        contentValues.put("bhajan_id", bhajan.getId());
        contentValues.put("download_timestamp", currentTimeMillis);

        long result = DB.insert("MUSIC_DOWNLOAD_DETAILS", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean updateDownloadData(BhajanModel bhajan) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("imageUrl", bhajan.getImageUrl());
        contentValues.put("audioUrl", bhajan.getAudioUrl());
        contentValues.put("artist", bhajan.getArtist());
        contentValues.put("duration", bhajan.getDuration());
        contentValues.put("type", bhajan.getType());
        contentValues.put("lyrics", bhajan.getLyrics());
        contentValues.put("keywords", bhajan.getKeywords());
        contentValues.put("size", bhajan.getKeywords());
        contentValues.put("downloads", bhajan.getDownloads());
        contentValues.put("bhajan_id", bhajan.getId());

        String title = bhajan.getTitle();
        Cursor cursor = DB.rawQuery("Select * from MUSIC_DOWNLOAD_DETAILS where title=?", new String[] {title});
        if (cursor.getCount() > 0) {
            long result = DB.update("MUSIC_DOWNLOAD_DETAILS", contentValues, "title=?", new String[] {title});

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    public Boolean deleteDownloadData(String title) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from MUSIC_DOWNLOAD_DETAILS where title=?", new String[]{title});
        if (cursor.getCount() > 0) {
            long result = DB.delete("MUSIC_DOWNLOAD_DETAILS", "title=?", new String[]{title});

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Cursor getData() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from MUSIC_DOWNLOAD_DETAILS", null);
        return cursor;
    }

    public Boolean checkDataExists(String title) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "Select * from MUSIC_DOWNLOAD_DETAILS where title=?";
        String[] selectionArgs = {title};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean dataExists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return dataExists;
    }

    public Cursor getSpecificData(String title) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from MUSIC_DOWNLOAD_DETAILS where title=?", new String[]{title});
        return cursor;
    }

    public void closeDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
