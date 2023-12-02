package com.insightdev.both;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PriceSelectorAdapter extends RecyclerView.Adapter<PriceSelectorAdapter.PriceItemHolder> {


    ArrayList<PriceItem> priceItems;

    String currency;

    Context context;

    private OnItemCLickListener mListener;

    public interface OnItemCLickListener {
        void OnItemClick(int position);
    }


    public void setOnItemClickListener(OnItemCLickListener listener) {

        mListener = listener;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PriceSelectorAdapter(ArrayList<PriceItem> priceItems, String currency, Context context) {
        this.priceItems = priceItems;
        this.currency = currency;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public PriceItemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.price_item, parent, false);
        return new PriceItemHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PriceItemHolder holder, int position) {

        holder.priceTv.setText(currency + Tools.checkEndPointZero(priceItems.get(position).getPrice()));
        int duration = priceItems.get(position).getDuration();
        holder.durationTv.setText(String.valueOf(duration));
        if (duration == 1)
            holder.monthLabl.setText("mes");
        else
            holder.monthLabl.setText("meses");

        holder.savingsTv.setText("Ahorra " + priceItems.get(position).getSavings() + "%");


    }

    @Override
    public int getItemCount() {
        return priceItems.size();
    }

    public class PriceItemHolder extends RecyclerView.ViewHolder {

        TextView priceTv, durationTv, savingsTv, monthLabl;


        public PriceItemHolder(@NonNull @NotNull View itemView, OnItemCLickListener onItemCLickListener) {
            super(itemView);

            priceTv = itemView.findViewById(R.id.price);

            durationTv = itemView.findViewById(R.id.cantMonths);

            savingsTv = itemView.findViewById(R.id.tag);

            monthLabl = itemView.findViewById(R.id.monthLabel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    onItemCLickListener.OnItemClick(pos);
                }
            });

        }
    }


}
