package com.insightdev.both;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainFilterAdapter extends RecyclerView.Adapter<MainFilterAdapter.MainFilterHolder> {

    Context context;

    ArrayList<FilterBitmap> items;

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public MainFilterAdapter(Context context, ArrayList<FilterBitmap> items) {

        this.context = context;
        this.items = items;
    }

    @NonNull
    @NotNull
    @Override
    public MainFilterHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_filter_item, parent, false);
        return new MainFilterHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MainFilterHolder holder, int position) {


        if (position == 0)
            Glide.with(context)
                    .load(items.get(position).getBitmap())
                    .into(holder.imageView).getSize(new SizeReadyCallback() {
                @Override
                public void onSizeReady(int width, int height) {

                    Log.d("alskjdla", width+" "+height);
                    FilterActivity.imageHeight = height;
                    FilterActivity.imageWidht = width;

                }
            });
        else
            Glide.with(context)
                    .load(items.get(position).getBitmap())
                    .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MainFilterHolder extends RecyclerView.ViewHolder {

        RoundedImageView imageView;


        public MainFilterHolder(@NonNull @NotNull View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoEditor);

           /* ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();

            layoutParams.dimensionRatio = dimensionRatio;

            imageView.setLayoutParams(layoutParams);*/


        }
    }
}
