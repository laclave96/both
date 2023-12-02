package com.insightdev.both;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

public class MatchActivity extends AppCompatActivity {


    CircularImageView myImage, matchImage;
    TextView textView;
    String match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        matchImage = findViewById(R.id.matchImage);
        myImage = findViewById(R.id.myImage);
        textView = findViewById(R.id.info);

        match = getIntent().getExtras().getString(Tools.getString(R.string.match, getApplicationContext()));
        Contact contact = ContactsManager.getContact(match, this);
        Picasso.get().load(Profile.getProfilePhotoMedium(this)).config(Bitmap.Config.RGB_565).fit()
                .centerCrop().into(myImage);
        Picasso.get().load(contact.getProfilePhotoMedium(this)).config(Bitmap.Config.RGB_565).fit()
                .centerCrop().into(matchImage);
        textView.setText(getString(R.string.tu_y_) + contact.getName() + getString(R.string._se_han_dado_like));
        EventBus.getDefault().post(new ProfileDismissEvent());
    }

    public void chatting(View view) {
        Intent intent = new Intent(this, ChatRoom.class);
        intent.putExtra("username", match);
        startActivity(intent);
        finishAfterTransition();
    }

    public void close(View view) {
        finish();
    }
}