package com.insightdev.both;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.ExpViewModel;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

public class ExpDateActivity extends AppCompatActivity {

    CircularImageView mImage;
    TextView textView, checkProfile;
    String dateExpId;
    Contact contact;

    ExpViewModel expViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_date);
        mImage = findViewById(R.id.image);
        textView = findViewById(R.id.info);
        checkProfile = findViewById(R.id.checkP);

        Tools.saveSettings("pending_to_cancel", "kk", this);

        dateExpId = getIntent().getExtras().getString("expDate");


        if (dateExpId.isEmpty())
            dateExpId = Tools.getSettings("is_new_exp_date", getApplicationContext());

        contact = ContactsManager.getContact(Integer.valueOf(dateExpId));


        expViewModel = ViewModelProviders.of(this).get(ExpViewModel.class);

        expViewModel.getContactDataLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                s = Tools.putValue(s, "status", "");
                s = Tools.putValue(s, "type", "expDate");

                contact = new Gson().fromJson(s, Contact.class);

                if (!ContactsManager.checkAddContact(contact, getApplicationContext()))

                    ContactsManager.addType(contact.getChatUsername(getApplicationContext()), "expDate", getApplicationContext());

                init();

            }
        });

        if (contact != null)
            init();
        else
            expViewModel.getContactData(getApplicationContext(), dateExpId);


        checkProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("asdasgagaa2ad", contact.getName());
                Tools.saveSettings("back_from_exp_activity", contact.getId(), getApplicationContext());
                MainActivity.openChatFragment();
                finish();


            }
        });


    }


    public void close(View view) {
        onBackPressed();
    }

    public void init() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                fakeSend("¡Dile hola a tu cita express\uD83E\uDD17!");
            }
        }).start();

        String contactName = Tools.getFirstWords(contact.getName(), 1);

        String firstPart = "Tienes 24 horas para conocer a ";

        String secondPart = ". Si decides que te gusta dale un buen like.";

        Spannable wordtoSpan = new SpannableString(firstPart + contactName + secondPart);

        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#E49B0F")), firstPart.length(), firstPart.length() + contactName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(wordtoSpan);


        Glide.with(this).load(contact.getProfilePhotoMedium(getApplicationContext()))
                .centerCrop()
                .into(mImage);

        ContactsManager.addContactToDB(contact.getId(), getApplicationContext());

        Tools.saveSettings("is_new_exp_date", "", getApplicationContext());

        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                Profile.setData(Tools.getString(R.string.contacts, getApplicationContext()), ContactsManager.getSerializedContacts(), getApplicationContext());
            }
        });

    }


    public void fakeSend(String msg) {

        String username = contact.getChatUsername(getApplicationContext());

        Chat chat = Conversations.getChat(username);

        if (chat != null)
            return;

        String message;
        String id = Tools.getSha256(msg + "" + new Date().getTime());
        String myAESKey = null;
        String encryptedMessage = null;
        String encryptedId = null;
        final ArrayList<Msg>[] messages = new ArrayList[]{new ArrayList<>()};
        Contact contact = ContactsManager.getContact(username, getApplicationContext());

        if (chat == null) {
            String encryptedAESKey = null;
            try {
                myAESKey = Encryption.getSecretAESKeyAsString();
                Log.d("MyAes", "aes" + myAESKey);
                encryptedAESKey = Encryption.encryptText(myAESKey, contact.getPublicKey());
                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);
                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                Log.d("MyAes", "Excaes" + e.getMessage());
                e.printStackTrace();
            }
            message = Tools.getJsonMsg(encryptedMessage, encryptedId, encryptedAESKey);
        } else if (chat.isPendingSendAesKey()) {
            String encryptedAESKey = null;
            myAESKey = chat.getMyAESKey();
            try {
                encryptedAESKey = Encryption.encryptText(myAESKey, contact.getPublicKey());
                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);
                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                e.printStackTrace();

            }

            message = Tools.getJsonMsg(encryptedMessage, encryptedId, encryptedAESKey);
        } else {
            myAESKey = chat.getMyAESKey();
            try {
                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);
                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                e.printStackTrace();

            }
            message = Tools.getJsonMsg(encryptedMessage, encryptedId);
        }


        String name = contact.getName();

        PublicKey publicKey = contact.getPublicKey();



        long time = new Date().getTime();

        messages[0].add(new Msg(msg, id, "sent", "out", time, false));

        Conversations.addConversation(new Chat(name, username, messages[0], publicKey, myAESKey, null, false, false, false), getApplicationContext());





    }
}