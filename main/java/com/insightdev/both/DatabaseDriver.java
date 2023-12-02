package com.insightdev.both;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.Executor;

public class DatabaseDriver {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    public DatabaseDriver(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    protected boolean insert(String table_name, ContentValues values){
        db = dbHelper.getWritableDatabase();

        long newRowId = db.insert(table_name, null, values);
        db.close();
        return newRowId != -1;

    }

    protected Cursor query(String table_name, String[] fields, String sentence, String[] args){
        db = dbHelper.getReadableDatabase();

        return db.query(
                table_name,
                fields,
                sentence,
                args,
                null,
                null,
                null,
                null
        );

    }

    protected boolean update(String table_name, ContentValues values, String sentence, String [] args){

        db = dbHelper.getWritableDatabase();

        int count = db.update(
                table_name,
                values,
                sentence,
                args);

        db.close();
        return count == 1;
    }

    protected void delete(String table_name, String sentence, String [] args){
        db = dbHelper.getWritableDatabase();
        db.delete(table_name,sentence,args);
        db.close();
    }

    public void close(){
        db.close();
    }


}
