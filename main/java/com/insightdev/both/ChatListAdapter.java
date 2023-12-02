package com.insightdev.both;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Objects;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> implements Filterable {

    private ArrayList<ChatItem> chatItems, mOriginalValues;
    private final Context context;
    private OnItemCLickListener mListener;

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();
            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>(chatItems);
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<ChatItem> list;

                list = new ArrayList<>(mOriginalValues);

                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<ChatItem> values;

                values = new ArrayList<>(mOriginalValues);


                final int count = values.size();
                final ArrayList<ChatItem> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final ChatItem value = values.get(i);
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
            chatItems = (ArrayList<ChatItem>) results.values;
            if (results.count <= 0) {
                chatItems.clear();
            }
            notifyDataSetChanged();
        }
    };

    public interface OnItemCLickListener {
        void OnItemClick(int position);

        void OnImageClick(int position);

    }

    public void setOnItemClickListener(OnItemCLickListener listener) {
        mListener = listener;
    }

    public ChatListAdapter(ArrayList<ChatItem> chatItems, Context context) {
        this.chatItems = chatItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem chatItem = chatItems.get(position);
        Glide.with(context).load(chatItem.getPhoto()).centerCrop().into(holder.image);
        holder.name.setText(chatItem.getName());
        holder.time.setText(chatItem.getTime());

        String status = chatItem.getStatus();
        Log.d("Status_", "ChatAdapter" + status);
        if (status.contentEquals("escribiendo...") || status.contentEquals("grabando audio...")) {
            holder.last_message.setTextColor(context.getResources().getColor(R.color.redPrimary));
            holder.msg_status.setVisibility(View.GONE);

            if (status.contentEquals("escribiendo..."))
                holder.last_message.setText(context.getString(R.string.escribiendo));

            if (status.contentEquals("grabando audio..."))
                holder.last_message.setText(context.getString(R.string.grabando_audio));


        } else {
            if (chatItem.getLastType().contentEquals("out")) {
                holder.msg_status.setVisibility(View.VISIBLE);
                String s = chatItem.getMsgStatus();
                switch (s) {
                    case "received":
                        holder.msg_status.setImageResource(R.drawable.ic_received);
                        break;
                    case "read":
                        holder.msg_status.setImageResource(R.drawable.ic_read);
                        break;
                    case "sent":
                        holder.msg_status.setImageResource(R.drawable.ic_sent);
                        break;
                    default:
                        holder.msg_status.setImageResource(R.drawable.ic_pending);
                        break;
                }
            } else
                holder.msg_status.setVisibility(View.GONE);
            holder.last_message.setTextColor(context.getResources().getColor(R.color.gray));
            holder.last_message.setText(chatItem.getLastMessage());

        }
        int pending = chatItem.getCantPending();
        if (chatItem.isPending() && pending > 0) {
            holder.time.setTextColor(context.getResources().getColor(R.color.redPrimary));
            holder.pending.setVisibility(View.VISIBLE);
            holder.cant_messages.setText(pending + "");
        } else {
            holder.pending.setVisibility(View.GONE);
            holder.time.setTextColor(context.getResources().getColor(R.color.gray));
        }
        if (chatItem.isMuted())
            holder.mute.setVisibility(View.VISIBLE);
        else
            holder.mute.setVisibility(View.GONE);


        if (chatItem.getPersonType().contains("expDate"))
            holder.back.setBackgroundResource(R.drawable.yellow_circle_gradient);
        else if (chatItem.getPersonType().contains("iceb"))
            holder.back.setBackgroundResource(R.drawable.blue_circle_gradient);
        else
            holder.back.setBackground(null);
        //  holder.back.setBackgroundResource(R.drawable.post_circle_gradient);


    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    ArrayList<ChatItem> getList() {

        return chatItems;
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {


        View back;
        CircularImageView image;
        TextView name, last_message, time, cant_messages;
        ImageView msg_status, mute;
        RelativeLayout pending;

        @SuppressLint("ClickableViewAccessibility")
        public ChatViewHolder(@NonNull View itemView, final OnItemCLickListener itemCLickListener) {
            super(itemView);

            back = itemView.findViewById(R.id.imageBack);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.textName);
            last_message = itemView.findViewById(R.id.lastSms);
            msg_status = itemView.findViewById(R.id.status);
            mute = itemView.findViewById(R.id.mute);
            pending = itemView.findViewById(R.id.pending);
            time = itemView.findViewById(R.id.time);
            cant_messages = itemView.findViewById(R.id.cantMessages);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCLickListener.OnItemClick(getAdapterPosition());
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCLickListener.OnImageClick(getAdapterPosition());
                }
            });


        }


    }

    public RecyclerView.ItemAnimator animator = new RecyclerView.ItemAnimator() {
        @Override
        public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
            //viewHolder.itemView.setAnimation(null);
            return false;
        }

        @Override
        public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            //viewHolder.itemView.setAnimation(null);
            return false;
        }

        @Override
        public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            oldHolder.itemView.setAnimation(null);
            newHolder.itemView.setAnimation(null);
            return true;
        }

        @Override
        public void runPendingAnimations() {

        }

        @Override
        public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }
    };


}
