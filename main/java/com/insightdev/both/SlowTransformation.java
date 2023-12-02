package com.insightdev.both;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

public class SlowTransformation implements ViewPager.PageTransformer {

    TextView text;
    ImageView imageView, circleBack, part;

    @Override
    public void transformPage(View page, float position) {

        text = page.findViewById(R.id.text);
        imageView = page.findViewById(R.id.advant);
        circleBack = page.findViewById(R.id.circle_back);
        part = page.findViewById(R.id.diamon_back);

        if (position <= 1) { // [-1,1]

           // text.setTranslationX(position * 0.1f * page.getWidth());

            imageView.setTranslationX(position * 0.35f * page.getWidth());

            circleBack.setTranslationX(position * 0.55f * page.getWidth());

            part.setTranslationX(position * 0.1f * page.getWidth());
//            // The 0.5, 1.0, 2.0, 2.5 values you see here are what makes the view move in a different speed.
//            // The bigger the number, the faster the view will translate.
//            // The result float is preceded by a minus because the views travel in the opposite direction of the movement.

        }
    }
}