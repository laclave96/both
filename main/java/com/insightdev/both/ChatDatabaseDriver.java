package com.insightdev.both;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ChatDatabaseDriver extends DatabaseDriver {
    private Context context;
    private final String sep = "%chapusa%both%";

    public ChatDatabaseDriver(Context context) {
        super(new DatabaseHelper(context, "conversations.db", ChatScheme.getSqlCreateEntries(),
                ChatScheme.getSqlDeleteEntries()));
        this.context = context;
    }

    public boolean addChat(Chat chat) throws Exception {
        String with = chat.getUsername();
        String name = Encryption.encryptTextUsingAES(chat.getName(), chat.getMyAESKey());
        ContentValues values = new ContentValues();
        values.put(ChatScheme.COLUMN_NAME_NAME, name);
        values.put(ChatScheme.COLUMN_NAME_WITH, with);
        values.put(ChatScheme.COLUMN_NAME_MESSAGES,with+".txt");
        String encryptedMessage = Encryption.encryptTextUsingAES(GsonMsgUtils.serialize(chat.getMessages().get(0)), chat.getMyAESKey());
        writeMessages(with+".txt", encryptedMessage);
        values.put(ChatScheme.COLUMN_NAME_YOUR_PUBLIC_KEY, chat.getPublicKeyAsString() + "");
        values.put(ChatScheme.COLUMN_NAME_MY_AES_KEY, Encryption.encryptText(chat.getMyAESKey() + "", Profile.publicKey));
        values.put(ChatScheme.COLUMN_NAME_YOUR_AES_KEY, Encryption.encryptText(chat.getYourAESKey() + "", Profile.publicKey));
        values.put(ChatScheme.COLUMN_NAME_PENDING_READING, chat.isPendingReading() ? 1 : 0);
        values.put(ChatScheme.COLUMN_NAME_PENDING_SEND_AES_KEY, chat.isPendingSendAesKey() ? 1 : 0);
        values.put(ChatScheme.COLUMN_NAME_IS_MUTED, chat.isMuted() ? 1 : 0);
        return insert(ChatScheme.TABLE_NAME, values);
    }

    public void updateName(String with, String name) {

        ContentValues values = new ContentValues();
        String encrypted_name = null;

        try {
            encrypted_name = Encryption.encryptTextUsingAES(name, Conversations.getChat(with).getMyAESKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        values.put(ChatScheme.COLUMN_NAME_NAME, encrypted_name);

        String sentence = ChatScheme.COLUMN_NAME_WITH + " = ?";
        String[] args = new String[]{with};

        update(ChatScheme.TABLE_NAME, values, sentence, args);
    }

    public void addMessage(String with, Msg msg) {

        String encrypted_messages = getEncryptedMessages(with);

        String decrypted_messages = null;

        try {
            decrypted_messages = Encryption.decryptTextUsingAES(encrypted_messages, Conversations.getChat(with).getMyAESKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = sep;
        if (decrypted_messages.isEmpty())
            s = "";

        String new_messages = decrypted_messages + s +GsonMsgUtils.serialize(msg);
        setEncryptedMessages(with, new_messages);

    }

    public void deleteChat(String with) {
        String sentence = ChatScheme.COLUMN_NAME_WITH + " = ?";
        String[] args = {with};
        delete(ChatScheme.TABLE_NAME, sentence, args);
    }

    public void changeMessageStatus(String with, String id, String status, String type) {
        String encrypted_messages = getEncryptedMessages(with);
        String decrypted_messages = null;
        try {
            decrypted_messages = Encryption.decryptTextUsingAES(encrypted_messages, Conversations.getChat(with).getMyAESKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] serialized_messages = decrypted_messages.split(sep);
        ArrayList<Msg> messages = new ArrayList<>();

        for (String msg : serialized_messages) {
            messages.add(GsonMsgUtils.deserialize(msg));
        }

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getType().contains(type))
                if (messages.get(i).getId().contains(id)) {
                    messages.get(i).setStatus(status);
                    break;
                }

        }
        String text_messages = "";

        for (Msg msg : messages) {
            text_messages = text_messages + sep + GsonMsgUtils.serialize(msg);
        }

       setEncryptedMessages(with, text_messages.substring(sep.length()));

    }

    public void setToListenAudio(String with, String id) {
        String encrypted_messages = getEncryptedMessages(with);

        String decrypted_messages = null;
        try {
            decrypted_messages = Encryption.decryptTextUsingAES(encrypted_messages, Conversations.getChat(with).getMyAESKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] serialized_messages = decrypted_messages.split(sep);
        ArrayList<Msg> messages = new ArrayList<>();

        for (String msg : serialized_messages) {
            messages.add(GsonMsgUtils.deserialize(msg));
        }

        for (int i = 0; i < messages.size(); i++) {
            Msg message = messages.get(i);
            if (message instanceof AudioMessage)
                if (message.getId().contentEquals(id)) {
                    ((AudioMessage) message).setListen(true);
                    break;
                }

        }
        String text_messages = "";
        for (Msg msg : messages) {
            text_messages = text_messages + sep + GsonMsgUtils.serialize(msg);
        }
        setEncryptedMessages(with, text_messages.substring(sep.length()));

    }

    public void updateYourPublicKey(String with, String key) {

        ContentValues values = new ContentValues();
        values.put(ChatScheme.COLUMN_NAME_YOUR_PUBLIC_KEY, key);

        String sentence = ChatScheme.COLUMN_NAME_WITH + " = ?";
        String[] args = new String[]{with};

        update(ChatScheme.TABLE_NAME, values, sentence, args);
    }

    public void updateYourAesKey(String with, String key) {

        ContentValues values = new ContentValues();
        String encrypted_key = null;
        try {
            encrypted_key = Encryption.encryptText(key, Profile.publicKey);
            Log.d("UpdateYourAes","true");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UpdateYourAes","exec"+e.getMessage());
        }
        values.put(ChatScheme.COLUMN_NAME_YOUR_AES_KEY, encrypted_key);

        String sentence = ChatScheme.COLUMN_NAME_WITH + " = ?";
        String[] args = new String[]{with};

        update(ChatScheme.TABLE_NAME, values, sentence, args);
    }

    public void updateMyAesKey(String with, String key) {

        ContentValues values = new ContentValues();

        String encrypted_key = null;

        try {
            encrypted_key = Encryption.encryptText(key, Profile.publicKey);
            Log.d("UpdateMyAes","true");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UpdateMyAes","exce"+e.getMessage());
        }
        values.put(ChatScheme.COLUMN_NAME_MY_AES_KEY, encrypted_key);
        String sentence = ChatScheme.COLUMN_NAME_WITH + " = ?";
        String[] args = new String[]{with};

        update(ChatScheme.TABLE_NAME, values, sentence, args);

    }

    public void changeMuted(String with, boolean state) {
        ContentValues values = new ContentValues();
        values.put(ChatScheme.COLUMN_NAME_IS_MUTED, state ? 1 : 0);
        String sentence = ChatScheme.COLUMN_NAME_WITH + " = ?";
        String[] args = new String[]{with};

        update(ChatScheme.TABLE_NAME, values, sentence, args);
    }

    public void changePendingReading(String with, boolean state) {

        ContentValues values = new ContentValues();
        values.put(ChatScheme.COLUMN_NAME_PENDING_READING, state ? 1 : 0);

        String sentence = ChatScheme.COLUMN_NAME_WITH + " = ?";
        String[] args = new String[]{with};

        update(ChatScheme.TABLE_NAME, values, sentence, args);
    }

    public void changePendingSendAesKey(String with, boolean state) {

        ContentValues values = new ContentValues();
        values.put(ChatScheme.COLUMN_NAME_PENDING_SEND_AES_KEY, state ? 1 : 0);

        String sentence = ChatScheme.COLUMN_NAME_WITH + " = ?";
        String[] args = new String[]{with};
        update(ChatScheme.TABLE_NAME, values, sentence, args);
    }

    private String getEncryptedMessages(String with) {
        return readMessages(with+".txt");
    }

    public void setEncryptedMessages(String with, String messages) {

        String encrypted_messages = null;
        try {
            encrypted_messages = Encryption.encryptTextUsingAES(messages, Conversations.getChat(with).getMyAESKey());
            Log.d("WriteM_","encrypting"+encrypted_messages.length());
        } catch (Exception e) {
            Log.d("WriteM_","exc"+e.getMessage());
            e.printStackTrace();
        }
        writeMessages(with+".txt",encrypted_messages);
    }

    public ArrayList<Chat> getChats() {
        Log.d("ReadM_","LoadingChats");
        Cursor cursor = query(ChatScheme.TABLE_NAME, null, null, null);
        ArrayList<Chat> chats = new ArrayList<>();
        if (cursor.getCount() > 0) {
            Log.d("ReadM_",">0");
            if (cursor.moveToFirst()) {
                Log.d("ReadM_","moveTofirst");
                while (!cursor.isAfterLast()) {
                    Log.d("ReadM_","decrypted");
                    String encrypted_name = cursor.getString(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_NAME));
                    String with = cursor.getString(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_WITH));
                    Log.d("ReadM_","with"+with);
                    String filename = cursor.getString(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_MESSAGES));
                    String your_public_key = cursor.getString(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_YOUR_PUBLIC_KEY));
                    String encrypted_my_aes_key = cursor.getString(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_MY_AES_KEY));
                    String encrypted_your_aes_key = cursor.getString(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_YOUR_AES_KEY));
                    int pending_reading = cursor.getInt(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_PENDING_READING));
                    int pending_send_aes_key = cursor.getInt(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_PENDING_SEND_AES_KEY));
                    int is_muted = cursor.getInt(cursor.getColumnIndex(ChatScheme.COLUMN_NAME_IS_MUTED));
                    String name = null, decrypted_messages = null, my_aes_key = null, your_aes_key = null;
                    try {
                        Log.d("ReadM_","init"+encrypted_my_aes_key);
                        my_aes_key = Encryption.decryptText(encrypted_my_aes_key, Profile.privateKey);
                        Log.d("ReadM_","myaes");
                        name = Encryption.decryptTextUsingAES(encrypted_name, my_aes_key);
                        Log.d("ReadM_","name");
                        your_aes_key = Encryption.decryptText(encrypted_your_aes_key, Profile.privateKey);
                        Log.d("ReadM_","youreaskey");
                        decrypted_messages = Encryption.decryptTextUsingAES(readMessages(filename), my_aes_key);
                        Log.d("ReadM_","decrypt"+decrypted_messages);
                    } catch (Exception e) {
                        Log.d("ReadM_","exception"+e.getMessage());
                        e.printStackTrace();
                    }
                    String[] serialized_messages = decrypted_messages.split(sep);
                    ArrayList<Msg> messages = new ArrayList<>();

                    for (String msg : serialized_messages) {
                        messages.add(GsonMsgUtils.deserialize(msg));
                    }

                    chats.add(new Chat(name, with, messages, Tools.getPublicKeyFromString(your_public_key), my_aes_key, your_aes_key, pending_reading == 1, pending_send_aes_key == 1, is_muted == 1));
                    cursor.moveToNext();

                }
            }
        }
        cursor.close();
        close();
        return chats;
    }

    private void writeMessages(String filename, String fileContents){
        FileOutputStream fos = null;
        Log.d("WriteM_","saving");
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(fileContents.getBytes());
            fos.close();
            Log.d("WriteM_","saving"+filename);
        } catch (Exception e) {
            Log.d("WriteM_","exc"+e.getMessage());
            e.printStackTrace();
        }

    }

    private String readMessages(String filename){
        FileInputStream fis = null;
        InputStreamReader inputStreamReader = null;
        StringBuilder stringBuilder = null;
        Log.d("ReadM_","readFile");
        try {
            fis = context.openFileInput(filename);
            Log.d("ReadM_","read");
            inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
                Log.d("ReadM_",stringBuilder.toString());
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ReadM_",e.getMessage());
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.d("ReadM_",e.getMessage());
            e.printStackTrace();
            return "";
        }


    }


}
