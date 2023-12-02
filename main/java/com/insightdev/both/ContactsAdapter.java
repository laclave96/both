package com.insightdev.both;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import jp.wasabeef.fresco.processors.BlurPostprocessor;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private Context context;
    private ArrayList<ContactItem> items, mOriginalValues;
    private String textLikes;
    private String lastImage;
    private OnItemCLickListener mListener;
    private BlurPostprocessor postprocessor;
    private ImageRequest request;
    //ResizeOptions resizeOptions = new ResizeOptions(300, 150);

    public interface OnItemCLickListener {
        void OnItemClick(int position);
    }


    public void setOnItemClickListener(OnItemCLickListener listener) {
        mListener = listener;
    }


    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();
            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>(items);
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<ContactItem> list;

                list = new ArrayList<>(mOriginalValues);

                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<ContactItem> values;

                values = new ArrayList<>(mOriginalValues);


                final int count = values.size();
                final ArrayList<ContactItem> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final ContactItem value = values.get(i);
                    final String valueText = value.getName().toString().toLowerCase();

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
            items = (ArrayList<ContactItem>) results.values;
            if (results.count <= 0) {
                items.clear();
            }
            notifyDataSetChanged();
        }
    };

    public ContactsAdapter(Context context, ArrayList<ContactItem> items, String textLikes, String lastImage) {
        this.context = context;
        this.items = items;
        this.textLikes = textLikes;
        this.lastImage = lastImage;
        postprocessor = new BlurPostprocessor(context, 15);
    }


    @NonNull
    @NotNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_item, parent, false);
        return new ContactsViewHolder(view, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactsViewHolder holder, int position) {

        request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(items.get(position)
                .getPhoto()))
                //.setResizeOptions(resizeOptions)
                .setProgressiveRenderingEnabled(true)
                .build();

        holder.imageView.setImageRequest(request);
        holder.name.setText(items.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    ArrayList<ContactItem> getList(){

        return items;
    }


    public class ContactsViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView imageView;
        TextView name, cantLikes;
        View viewBack;
        LottieAnimationView animationView;

        public ContactsViewHolder(@NonNull @NotNull View itemView, final OnItemCLickListener itemCLickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.textName);
            animationView = itemView.findViewById(R.id.animation);
            viewBack = itemView.findViewById(R.id.backView);
            cantLikes = itemView.findViewById(R.id.cantLikes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCLickListener.OnItemClick(getAdapterPosition());
                }
            });

        }
    }




}
