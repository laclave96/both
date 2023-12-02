package com.insightdev.both;

import android.provider.BaseColumns;

public class ChatScheme implements BaseColumns {
    public static final String TABLE_NAME = "chats";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_WITH = "with";
    public static final String COLUMN_NAME_MESSAGES = "messages";
    public static final String COLUMN_NAME_YOUR_PUBLIC_KEY = "your_public_key";
    public static final String COLUMN_NAME_MY_AES_KEY = "my_aes_key";
    public static final String COLUMN_NAME_YOUR_AES_KEY = "your_aes_key";
    public static final String COLUMN_NAME_PENDING_READING= "pending_reading";
    public static final String COLUMN_NAME_PENDING_SEND_AES_KEY= "pending_send_aes_key";
    public static final String COLUMN_NAME_IS_MUTED = "is_muted";


    public static String getSqlCreateEntries(){
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_NAME + " TEXT, " +
                COLUMN_NAME_WITH + " TEXT, "+
                COLUMN_NAME_MESSAGES + " TEXT, "+
                COLUMN_NAME_YOUR_PUBLIC_KEY + " TEXT, "+
                COLUMN_NAME_MY_AES_KEY + " TEXT, "+
                COLUMN_NAME_YOUR_AES_KEY + " TEXT, "+
                COLUMN_NAME_PENDING_READING + " INTEGER, "+
                COLUMN_NAME_PENDING_SEND_AES_KEY + " INTEGER, "+
                COLUMN_NAME_IS_MUTED + " INTEGER)";

    }

    public static String getSqlDeleteEntries(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
