package com.insightdev.both;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqHolder> {

    ArrayList<FaqItem> faqItems;
    Context context;


    private OnItemCLickListener mListener;


    public interface OnItemCLickListener {
        void OnItemClick(int position);
    }


    public void setOnItemClickListener(OnItemCLickListener listener) {

        mListener = listener;
    }

    public FaqAdapter(ArrayList<FaqItem> filterItems, Context context) {
        this.faqItems = filterItems;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public FaqHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faq_item, parent, false);
        return new FaqHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FaqHolder holder, int position) {

        holder.question.setText(faqItems.get(position).getQuest());
        holder.answer.setText(faqItems.get(position).getAnsw());

        if (faqItems.get(position).getImage() != 0) {
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageResource(faqItems.get(position).getImage());
        } else {
            holder.image.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    public class FaqHolder extends RecyclerView.ViewHolder {


        TextView question, answer;
        ImageView image;


        public FaqHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.quest);
            answer = itemView.findViewById(R.id.answ);
            image = itemView.findViewById(R.id.image);

        }


    }
}
