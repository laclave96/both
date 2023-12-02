package com.insightdev.both;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> implements Filterable {


    private OnItemCLickListener mListener;
    Context context;

    ArrayList<ImageFolder> folders, mOriginalValues;

    public interface OnItemCLickListener {
        void OnItemClick(int position);
    }


    public void setOnItemClickListener(OnItemCLickListener listener) {

        mListener = listener;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();
            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>(folders);
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<ImageFolder> list;

                list = new ArrayList<>(mOriginalValues);

                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<ImageFolder> values;

                values = new ArrayList<>(mOriginalValues);


                final int count = values.size();
                final ArrayList<ImageFolder> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final ImageFolder value = values.get(i);
                    final String valueText = value.getFolderName().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.contains(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            folders = (ArrayList<ImageFolder>) results.values;
            if (results.count <= 0) {
                folders.clear();
            }
            notifyDataSetChanged();
        }
    };

    public AlbumAdapter(ArrayList<ImageFolder> folders, Context context) {
        this.folders = folders;

        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        return new AlbumHolder(view, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull AlbumHolder holder, int position) {


        Glide.with(context).load(folders.get(position).getFirstPic()).centerCrop().into(holder.albumImg);


        holder.albumName.setText(folders.get(position).getFolderName());
        holder.albumCantImg.setText(String.valueOf(folders.get(position).getNumberOfPics()));


    }


    @Override
    public int getItemCount() {
        return folders.size();
    }


    public class AlbumHolder extends RecyclerView.ViewHolder {

        RoundedImageView albumImg;
        TextView albumName, albumCantImg;

        public AlbumHolder(@NonNull View itemView, final OnItemCLickListener itemCLickListener) {
            super(itemView);

            albumImg = itemView.findViewById(R.id.albumImg);
            albumImg.getLayoutParams().height = (int) (PickImage.galleryHeight / 3.3);
            albumName = itemView.findViewById(R.id.albumName);
            albumCantImg = itemView.findViewById(R.id.albumCantImg);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCLickListener.OnItemClick(getAdapterPosition());
                }
            });

        }


    }

}
