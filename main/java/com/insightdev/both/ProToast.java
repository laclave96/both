package com.insightdev.both;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProToast {


    static public void makeText(Context context, int imageResource, String text, int duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);

        ImageView image = view.findViewById(R.id.imageView);
        image.setImageResource(imageResource);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(text);


        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }

    static public void makeText(Context context, Bitmap imageResource, String text, int duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);

        ImageView image = view.findViewById(R.id.imageView);
        image.setImageBitmap(imageResource);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }


}
