package com.insightdev.both;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

public class InfoActivity extends AppCompatActivity {

    TextView title, descrip;

    CircularImageView circularImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        circularImageView = findViewById(R.id.image);
        title = findViewById(R.id.comment);
        descrip = findViewById(R.id.details);
        Bundle bundle = getIntent().getExtras();
        showInfo(bundle.getString("cause"));

    }

    private void showInfo(String cause){
        if(cause.contentEquals("service")){
            circularImageView.setImageResource(R.drawable.reparing2);
            title.setText(getString(R.string.reparaciones));
            descrip.setText(getString(R.string.estaremos_de_vuelta_en_) +Tools.getDaysForDeployment(InfoActivity.this)+getString(R.string.dias_disculpe_molestias));
        }else {
            circularImageView.setImageResource(R.drawable.baned);
            title.setText(getString(R.string.bloqueo));
            descrip.setText(getString(R.string.bloqueo_info));

        }

    }



}