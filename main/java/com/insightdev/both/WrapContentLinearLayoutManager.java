package com.insightdev.both;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.TintInfo;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.masoudss.lib.WaveformSeekBar;

import org.greenrobot.eventbus.EventBus;

public class WrapContentLinearLayoutManager extends LinearLayoutManager {
    String typeRecyclerView;

    private onStopScroll mListener;

    public void setOnStopScroll(onStopScroll listener) {
        this.mListener=listener;
    }

    public interface onStopScroll{
        void stopScroll(boolean completed);
    }

    public WrapContentLinearLayoutManager(Context context, String typeRecyclerView) {
        super(context);
        this.typeRecyclerView = typeRecyclerView;
    }

    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout, String typeRecyclerView) {
        super(context, orientation, reverseLayout);
        this.typeRecyclerView = typeRecyclerView;
    }

    public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, String typeRecyclerView) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.typeRecyclerView = typeRecyclerView;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            Log.d("Exception_", "init");
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            Log.d("Exception_",e.getMessage());
            if (typeRecyclerView.contentEquals("chat")) {
                Log.d("LogX","chatBloqued"+state);
                EventBus.getDefault().post(new ChatRoomEvent());
                return;
            }
            if (typeRecyclerView.contentEquals("chatList")) {
                Log.d("LogX","chatBloqued"+state);
                EventBus.getDefault().post(new ChatListEvent());
                return;
            }
            if (typeRecyclerView.contentEquals("contactList")){
                EventBus.getDefault().post(new ContactListEvent());
            }

            e.printStackTrace();
        }
    }
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        mListener.stopScroll(state == RecyclerView.SCROLL_STATE_IDLE);

    }

}
