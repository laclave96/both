package com.insightdev.both;

import android.view.View;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

public class AskSlowTransformer implements ViewPager.PageTransformer {

    ImageView imageView;

    @Override
    public void transformPage(View page, float position) {


        imageView = page.findViewById(R.id.image);


        if (position <= 1) { // [-1,1]


            imageView.setTranslationX(position * 0.45f * page.getWidth());


        }
    }
}
