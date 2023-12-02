package com.insightdev.both;

import android.provider.BaseColumns;

public class PendingMessageScheme implements BaseColumns {
    public static final String TABLE_NAME = "pending_messages";
    public static final String COLUMN_NAME_BODY = "body";
    public static final String COLUMN_NAME_WITH = "with";
    public static final String COLUMN_NAME_ID = "id";

    public static String getSqlCreateEntries() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_BODY + " TEXT, " +
                COLUMN_NAME_WITH + " TEXT, " +
                COLUMN_NAME_ID + " TEXT)";

    }

    public static String getSqlDeleteEntries() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
