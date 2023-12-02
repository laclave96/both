package com.insightdev.both;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class ContactsDBDriver extends DatabaseDriver {


    public ContactsDBDriver(Context context) {

        super(new DatabaseHelper(context, "contacts.db", ContactSchemeDB.getSqlCreateEntries(),
                ContactSchemeDB.getSqlDeleteEntries()));

    }

    public boolean addContact(String id) {
        ContentValues values = new ContentValues();
        values.put(ContactSchemeDB._ID, id);
        values.put(ContactSchemeDB.TIMESTAMP, System.currentTimeMillis());
        return insert(ContactSchemeDB.EXP_DATE_TABLE, values);
    }


    public void deleteContact(String id) {
        String sentence = ContactSchemeDB._ID + " = ?";
        String[] args = new String[]{id};
        delete(ContactSchemeDB.EXP_DATE_TABLE, sentence, args);
    }

    public ArrayList<ExpDateContact> getContacts() {
        Cursor cursor = query(ContactSchemeDB.EXP_DATE_TABLE, null, null, null);
        ArrayList<ExpDateContact> ids = new ArrayList<>();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int id = cursor.getInt(cursor.getColumnIndex(ContactSchemeDB._ID));
                    long timestamp = cursor.getLong(cursor.getColumnIndex(ContactSchemeDB.TIMESTAMP));
                    ids.add(new ExpDateContact(id, timestamp));
                    cursor.moveToNext();
                }
            }
        }

        cursor.close();
        close();
        return ids;
    }


}
