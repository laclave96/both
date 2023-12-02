package com.insightdev.both;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import kr.co.prnd.StepProgressBar;

public class DescriptionAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<DescriptionModel> listItems = new ArrayList<>();

    public DescriptionAdapter(Context context, ArrayList<DescriptionModel> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.description_item, null);

        ImageView imageView = layoutScreen.findViewById(R.id.advant);

        ImageView bacck = layoutScreen.findViewById(R.id.circle_back);

        ImageView part = layoutScreen.findViewById(R.id.diamon_back);

        TextView textView = layoutScreen.findViewById(R.id.text);

        textView.setText(listItems.get(position).getText());

        imageView.setImageResource(listItems.get(position).getImg());

        bacck.setImageResource(listItems.get(position).getBack());

        part.setImageResource(listItems.get(position).getPart());

        container.addView(layoutScreen);


        return layoutScreen;


    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }
}
