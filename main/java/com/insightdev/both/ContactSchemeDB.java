package com.insightdev.both;

import android.provider.BaseColumns;

public class ContactSchemeDB implements BaseColumns {

    public static final String EXP_DATE_TABLE = "contacts_exp_date";
    public static final String TIMESTAMP = "timestamp";

    public static String getSqlCreateEntries() {

        return "CREATE TABLE " + EXP_DATE_TABLE + " (" +
                _ID + " INTEGER PRIMARY KEY , " +
                TIMESTAMP + " INTEGER )";

    }

    public static String getSqlDeleteEntries() {

        return "DROP TABLE IF EXISTS " + EXP_DATE_TABLE;

    }
}
