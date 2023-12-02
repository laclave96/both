package com.insightdev.both;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final String SQL_CREATE_ENTRIES;
    private final String SQL_DELETE_ENTRIES;
    public DatabaseHelper(Context context, String name, String SQL_CREATE_ENTRIES, String SQL_DELETE_ENTRIES ) {
        super(context, name, null, 1);
        this.SQL_CREATE_ENTRIES = SQL_CREATE_ENTRIES;
        this.SQL_DELETE_ENTRIES = SQL_DELETE_ENTRIES;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}