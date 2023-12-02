package com.insightdev.both;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class PendingMessagesDriver extends DatabaseDriver {

    public PendingMessagesDriver(Context context) {
        super(new DatabaseHelper(context, "pending_messages.db", PendingMessageScheme.getSqlCreateEntries(),
                PendingMessageScheme.getSqlDeleteEntries()));
    }

    public boolean addMessage(PendingMessage message) {
        ContentValues values = new ContentValues();
        values.put(PendingMessageScheme.COLUMN_NAME_BODY, message.getBody());
        values.put(PendingMessageScheme.COLUMN_NAME_WITH, message.getTo());
        values.put(PendingMessageScheme.COLUMN_NAME_ID, message.getId());
        return insert(PendingMessageScheme.TABLE_NAME, values);
    }

    public void deleteMessage(PendingMessage message) {
        String sentence = PendingMessageScheme.COLUMN_NAME_WITH + " = ? AND " + PendingMessageScheme.COLUMN_NAME_ID + " = ?";
        String[] args = new String[]{message.getTo(), message.getId()};
        delete(PendingMessageScheme.TABLE_NAME, sentence, args);
    }

    public void cleanAll() {
        delete(PendingMessageScheme.TABLE_NAME, null, null);
    }

    public ArrayList<PendingMessage> getPendingMessages() {
        Cursor cursor = query(PendingMessageScheme.TABLE_NAME, null, null, null);
        ArrayList<PendingMessage> pendingMessages = new ArrayList<>();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String body = cursor.getString(cursor.getColumnIndex(PendingMessageScheme.COLUMN_NAME_BODY));
                    String to = cursor.getString(cursor.getColumnIndex(PendingMessageScheme.COLUMN_NAME_WITH));
                    String id = cursor.getString(cursor.getColumnIndex(PendingMessageScheme.COLUMN_NAME_ID));
                    pendingMessages.add(new PendingMessage(body, to, id));
                    cursor.moveToNext();
                }
            }
        }

        cursor.close();
        close();
        return pendingMessages;
    }
}
