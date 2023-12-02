package com.insightdev.both;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class PremiumLikesAdapter extends RecyclerView.Adapter<PremiumLikesAdapter.PremiumLikeHolder> {

    Context context;
    ArrayList<PremiumLikeItem> likeItems;
    private ImageRequest request;
   // ResizeOptions resizeOptions = new ResizeOptions(300, 150);
    private OnItemCLickListener mListener;


    public interface OnItemCLickListener {
        void OnItemClick(int position);
    }


    public void setOnItemClickListener(OnItemCLickListener listener) {
        mListener = listener;
    }


    public PremiumLikesAdapter(Context context, ArrayList<PremiumLikeItem> likeItems) {
        this.context = context;
        this.likeItems = likeItems;
    }


    @NotNull
    @Override
    public PremiumLikeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.like_premiun_item, parent, false);
        return new PremiumLikeHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PremiumLikeHolder holder, int position) {

        PremiumLikeItem premiumLikeItem = likeItems.get(position);
        switch (position) {
            default:
                holder.layout.getLayoutParams().height = new Random().nextInt((holder.max - holder.min) + 1) + holder.min;
                break;
            case 0:
                holder.layout.getLayoutParams().height = holder.max;
                break;
            case 1:
                holder.layout.getLayoutParams().height = holder.min;
                break;
        }


        request = ImageRequestBuilder.newBuilderWithSource(premiumLikeItem.getImage())
                .setProgressiveRenderingEnabled(true)
               // .setResizeOptions(resizeOptions)
                .build();

        holder.imageView.setImageRequest(request);
        holder.name.setText(premiumLikeItem.getName());
        holder.cantLikes.setText(premiumLikeItem.getLikes());

    }

    @Override
    public int getItemCount() {
        return likeItems.size();
    }

    public static class PremiumLikeHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView imageView;
        TextView name, cantLikes;
        ConstraintLayout layout;
        int max, min;
        ImageRequest request;


        public PremiumLikeHolder(@NonNull View itemView, final OnItemCLickListener itemCLickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mainImage);
            name = itemView.findViewById(R.id.textName);
            cantLikes = itemView.findViewById(R.id.cantLikes);
            layout = itemView.findViewById(R.id.baseLayout);

            max = (int) (MainActivity.screenHeight / 2.8);
            min = (int) (MainActivity.screenHeight / 3.4);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCLickListener.OnItemClick(getAdapterPosition());
                }
            });

        }

    }

}
