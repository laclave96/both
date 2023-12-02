package com.insightdev.both;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.yashoid.instacropper.InstaCropperView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class CropPagerAdapter extends PagerAdapter {

    Activity activity;

    ArrayList<Uri> images;

    static int lastDestroy = -1, pivot = 0;

    static public ArrayList<InstaCropperView> cropperViews;
    int prevSize;

    public CropPagerAdapter(Activity activity, ArrayList<Uri> images) {
        this.activity = activity;
        this.images = images;
        cropperViews = new ArrayList<>();
    }


    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.crop_item, null);

        InstaCropperView instaCropperView = layoutScreen.findViewById(R.id.croper);

        instaCropperView.setImageUri(images.get(0));

        instaCropperView.setRatios(PickImage.actualRatio, PickImage.actualRatio, PickImage.actualRatio);

        container.addView(layoutScreen);



        if (0 == position && lastDestroy != images.size()) {

            cropperViews.add(instaCropperView);
        }
        if (lastDestroy == images.size())
            pivot++;

        // addItemCrop(instaCropperView);

        return layoutScreen;
    }

    @Override
    public int getItemPosition(@NonNull @NotNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {


        //  cropperViews.clear();
        Log.d("laksdasf", " ------ ");

        super.notifyDataSetChanged();
    }


    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {


        return images.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {


        lastDestroy = position;

        container.removeView((View) object);

    }

    private void addItemCrop(InstaCropperView instaCropperView) {

       /* if (images.size() - 1 == cropperViews.size())
            cropperViews.add(instaCropperView);
        else
            cropperViews.add(0, instaCropperView);*/


    }
}
