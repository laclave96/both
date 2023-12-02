package com.insightdev.both;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {

    private ArrayList<FilterItem> filterItems;
    private Context context;
    int counter = 0;

    private OnItemCLickListener mListener;


    public interface OnItemCLickListener {
        void OnItemClick(int position);
    }


    public void setOnItemClickListener(OnItemCLickListener listener) {

        mListener = listener;
    }

    public FilterAdapter(ArrayList<FilterItem> filterItems, Context context) {
        this.filterItems = filterItems;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item, parent, false);
        return new FilterHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FilterHolder holder, int position) {


        Glide.with(context).load(filterItems.get(position).getBitmap()).centerCrop().into(holder.image);

        holder.name.setText(filterItems.get(position).getFilterName());


    }

    @Override
    public int getItemCount() {
        return filterItems.size();
    }

    public class FilterHolder extends RecyclerView.ViewHolder {


        RoundedImageView image;
        TextView name;


        public FilterHolder(@NonNull @NotNull View itemView, OnItemCLickListener onItemCLickListener) {
            super(itemView);

            name = itemView.findViewById(R.id.nameFilter);

            image = itemView.findViewById(R.id.editorImage);


            //
            // mPhotoEditor.setFilterEffect(filterItems.get(counter++).getPhotoFilter());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemCLickListener.OnItemClick(getAdapterPosition());
                }
            });
        }


    }
}
