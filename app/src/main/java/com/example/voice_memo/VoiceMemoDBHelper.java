package com.example.voice_memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public class VoiceMemoDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "voice_memos.db";
    private static final int DB_VERSION = 2;

    private static final String TABLE_NAME = "memos";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PATH = "path";
    private static final String COL_DURATION = "duration";
    private static final String COL_DATE = "date";

    public VoiceMemoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_PATH + " TEXT, " +
                COL_DURATION + " INTEGER, " +
                COL_DATE + " TEXT)";
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple upgrade logic for now
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertMemo(String name, String path, long duration, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PATH, path);
        values.put(COL_DURATION, duration);
        values.put(COL_DATE, date);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public ArrayList<VoiceMemo> getAllMemos() {
        ArrayList<VoiceMemo> memos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(COL_PATH));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(COL_DURATION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                memos.add(new VoiceMemo(name, path, duration, date));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return memos;
    }
    public void updateMemoName(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, newName);
        db.update(TABLE_NAME, values, COL_NAME + "=?", new String[]{oldName});
        db.close();
    }

    public void deleteMemo(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_NAME + "=?", new String[]{name});
        db.close();
    }

}
