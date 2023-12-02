package com.insightdev.both;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yashoid.instacropper.InstaCropperView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropHolder> {

    Context context;

    ArrayList<Uri> images;

    private OnItemClickListener mListener;

    String dimensionRatio;

    static public ArrayList<InstaCropperView> cropperViews=new ArrayList<>();

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public CropAdapter(Context context, ArrayList<Uri> images, String dimensionRatio) {

        this.context = context;
        this.images = images;
        this.dimensionRatio = dimensionRatio;
    }

    @NonNull
    @NotNull
    @Override
    public CropHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crop_item, parent, false);
        return new CropHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CropHolder holder, int position) {

        cropperViews.add(holder.imageView);

        holder.imageView.setImageUri(images.get(position));

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class CropHolder extends RecyclerView.ViewHolder {

        InstaCropperView imageView;


        public CropHolder(@NonNull @NotNull View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.croper);

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();

            layoutParams.dimensionRatio = dimensionRatio;

            imageView.setLayoutParams(layoutParams);


        }
    }
}