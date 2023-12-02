package com.insightdev.both;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yashoid.instacropper.InstaCropperView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class CropActivity extends AppCompatActivity {

    private InstaCropperView instaCropperView;
    private LinearLayout cancelB, okB;
    private ImageView backButton;
    private Bitmap myBitmap;
    private String uri;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        instaCropperView = findViewById(R.id.instacropper);
        cancelB = findViewById(R.id.cancel);
        okB = findViewById(R.id.ok);
        backButton = findViewById(R.id.backButton);

        Bundle bundle = getIntent().getExtras();
        uri = bundle.getString("uri", "");
        Log.d("ImageUri_", "uri" + uri);
        imageUri = Uri.parse(uri);

        instaCropperView.setRatios(1f, 9F/16F, 1F);

        instaCropperView.setImageUri(imageUri);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                instaCropperView.setRatios(1f, 1F, 1F);
            }
        }, 2000);


        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okB.setEnabled(false);
                new LoadingDialog().showDialog(CropActivity.this, "Cargando");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        instaCropperView.crop(800,
                                1062, new InstaCropperView.BitmapCallback() {
                                    @Override
                                    public void onBitmapReady(Bitmap bitmap) {
                                        Log.d("PathCrop", "bitmapReady");
                                        myBitmap = bitmap;
                                        String file = "cropImage.jpeg";
                                        String path = getFilesDir().getAbsolutePath() + "/" + file;
                                        Log.d("PathCrop", path);
                                        Tools.write(file, myBitmap, CropActivity.this);
                                        EventBus.getDefault().post(new CropImageEvent(path));
                                        finish();
                                    }
                                });

                    }
                }, 500);
            }
        });

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}