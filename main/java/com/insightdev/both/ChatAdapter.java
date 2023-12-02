package com.insightdev.both;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.masoudss.lib.SeekBarOnProgressChanged;
import com.masoudss.lib.WaveformSeekBar;

import org.checkerframework.checker.lock.qual.EnsuresLockHeldIf;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_IN_AUDIO = R.layout.in_audio;
    private static final int ITEM_OUT_AUDIO = R.layout.out_audio;
    private static final int ITEM_IN_TEXT = R.layout.in_text;
    private static final int ITEM_OUT_TEXT = R.layout.out_text;

    private static Context context;

    private ArrayList<Object> items;

    private OnItemCLickListener mListener;

    public interface OnItemCLickListener {

        void OnPlayClick(int position, LottieAnimationView play_pause, WaveformSeekBar waveformSeekBar);

        void OnPauseClick();

        void OnTouch(WaveformSeekBar waveformSeekBar);

        void OnMoveToReply(int position);

        void OnProgressChanged(float progress, TextView textView, int position);

    }

    public void setOnItemClickListener(OnItemCLickListener listener) {
        mListener = listener;
    }

    public ChatAdapter(ArrayList<Object> items, Context context) {
        this.items = items;
        this.context = context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(viewType, parent, false);

        if (viewType == ITEM_IN_AUDIO) {
            return new inAudioHolder(view, mListener);
        } else if (viewType == ITEM_IN_TEXT) {
            return new inTextHolder(view, mListener);
        } else if (viewType == ITEM_OUT_AUDIO) {
            return new outAudioHolder(view, mListener);
        } else {
            return new outTextHolder(view, mListener);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Log.d("dayV_", "ViewH" + ((MsgItem) items.get(position)).isDayVisible());
        if (viewHolder instanceof inAudioHolder) {
            ((inAudioHolder) viewHolder).bind((AudioMsgItem) items.get(position));
        } else if (viewHolder instanceof inTextHolder) {
            ((inTextHolder) viewHolder).bind((TextMsgItem) items.get(position));
        } else if (viewHolder instanceof outTextHolder) {
            ((outTextHolder) viewHolder).bind((TextMsgItem) items.get(position));
        } else {
            ((outAudioHolder) viewHolder).bind((AudioMsgItem) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        MsgItem item = (MsgItem) items.get(position);
        if (item instanceof AudioMsgItem) {
            if (item.getType().contentEquals("in"))
                return ITEM_IN_AUDIO;
            else
                return ITEM_OUT_AUDIO;
        } else {
            if (item.getType().contentEquals("in"))
                return ITEM_IN_TEXT;
            else
                return ITEM_OUT_TEXT;
        }
    }

    public static class outAudioHolder extends RecyclerView.ViewHolder {
        LinearLayout infoL;
        TextView date, day, bodyReply;
        ImageView status;
        WaveformSeekBar waveformSeekBar;
        TextView durationTv;
        LottieAnimationView play_pause;

        @SuppressLint("ClickableViewAccessibility")
        public outAudioHolder(@NonNull View itemView, final OnItemCLickListener onItemCLickListener) {
            super(itemView);
            infoL = itemView.findViewById(R.id.timell);
            day = itemView.findViewById(R.id.dayTime);
            date = itemView.findViewById(R.id.dateTime);
            status = itemView.findViewById(R.id.status);
            waveformSeekBar = itemView.findViewById(R.id.waveformSeekBar);
            play_pause = itemView.findViewById(R.id.button);
            durationTv = itemView.findViewById(R.id.duration);
            bodyReply = itemView.findViewById(R.id.bodyReply);
            play_pause.setMaxFrame(12);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (infoL.getVisibility() == View.VISIBLE)
                        infoL.setVisibility(View.GONE);
                    else
                        infoL.setVisibility(View.VISIBLE);
                }
            });

            play_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCLickListener != null) {
                        if (play_pause.getFrame() != 12) {
                            onItemCLickListener.OnPlayClick(getAdapterPosition(), play_pause, waveformSeekBar);
                            play_pause.setSpeed(1.5f);

                        } else {
                            onItemCLickListener.OnPauseClick();
                            play_pause.setSpeed(-1.5f);

                        }

                        play_pause.playAnimation();
                    }

                }
            });

            waveformSeekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (onItemCLickListener != null)
                        onItemCLickListener.OnTouch(waveformSeekBar);
                    return false;
                }
            });

            bodyReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCLickListener != null) {
                        onItemCLickListener.OnMoveToReply(getAdapterPosition());
                    }

                }
            });

            waveformSeekBar.setOnProgressChanged(new SeekBarOnProgressChanged() {
                @Override
                public void onProgressChanged(@NotNull WaveformSeekBar waveformSeekBar, float v, boolean b) {
                    if (onItemCLickListener != null) {
                        onItemCLickListener.OnProgressChanged(v, durationTv, getAdapterPosition());
                    }
                }
            });


        }

        public void bind(AudioMsgItem outAudio) {
            date.setText(outAudio.getTime());

            String s = outAudio.getStatus();
            switch (s) {
                case "received":
                    status.setImageResource(R.drawable.ic_received);
                    break;
                case "read":
                case "listen":
                    status.setImageResource(R.drawable.ic_read);
                    break;
                case "sent":
                    status.setImageResource(R.drawable.ic_sent);
                    break;
                default:
                    status.setImageResource(R.drawable.ic_pending);
                    break;
            }


            if (outAudio.isDayVisible()) {
                day.setText(outAudio.getDayTime());
                day.setVisibility(View.VISIBLE);
            } else {
                day.setVisibility(View.GONE);
            }

            if (outAudio.isReply()) {
                bodyReply.setText(outAudio.getTextReply());
                bodyReply.setVisibility(View.VISIBLE);
            } else {
                bodyReply.setVisibility(View.GONE);
            }


            waveformSeekBar.setSample(outAudio.getRandomWave());

            if (outAudio.isListen())
                waveformSeekBar.setProgress(1);
            else
                waveformSeekBar.setProgress(0);

            durationTv.setText(outAudio.getDuration());

        }


    }

    public static class inAudioHolder extends RecyclerView.ViewHolder {

        TextView date, day, bodyReply;
        WaveformSeekBar waveformSeekBar;
        TextView durationTv;
        LottieAnimationView play_pause;


        @SuppressLint("ClickableViewAccessibility")
        public inAudioHolder(@NonNull View itemView, final OnItemCLickListener onItemCLickListener) {
            super(itemView);
            day = itemView.findViewById(R.id.dayTime);
            date = itemView.findViewById(R.id.dateTime);
            waveformSeekBar = itemView.findViewById(R.id.waveformSeekBar);
            play_pause = itemView.findViewById(R.id.button);
            durationTv = itemView.findViewById(R.id.duration);

            bodyReply = itemView.findViewById(R.id.bodyReply);
            play_pause.setMaxFrame(12);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (date.getVisibility() == View.VISIBLE)
                        date.setVisibility(View.GONE);
                    else
                        date.setVisibility(View.VISIBLE);
                }
            });

            play_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCLickListener != null) {
                        if (play_pause.getFrame() != 12) {
                            onItemCLickListener.OnPlayClick(getAdapterPosition(), play_pause, waveformSeekBar);
                            play_pause.setSpeed(1.5f);

                        } else {
                            onItemCLickListener.OnPauseClick();
                            play_pause.setSpeed(-1.5f);

                        }

                        play_pause.playAnimation();
                    }

                }
            });
            waveformSeekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (onItemCLickListener != null)
                        onItemCLickListener.OnTouch(waveformSeekBar);
                    return false;
                }
            });


            bodyReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCLickListener != null) {
                        onItemCLickListener.OnMoveToReply(getAdapterPosition());
                    }

                }
            });

            waveformSeekBar.setOnProgressChanged(new SeekBarOnProgressChanged() {
                @Override
                public void onProgressChanged(@NotNull WaveformSeekBar waveformSeekBar, float v, boolean b) {
                    if (onItemCLickListener != null) {
                        onItemCLickListener.OnProgressChanged(v, durationTv, getAdapterPosition());
                    }

                }
            });


        }

        public void bind(AudioMsgItem inAudio) {
            date.setText(inAudio.getTime());

            if (inAudio.isDayVisible()) {
                day.setText(inAudio.getDayTime());
                day.setVisibility(View.VISIBLE);
            } else {
                day.setVisibility(View.GONE);
            }

            if (inAudio.isReply()) {
                bodyReply.setText(inAudio.getTextReply());
                bodyReply.setVisibility(View.VISIBLE);
            } else {
                bodyReply.setVisibility(View.GONE);
            }


            waveformSeekBar.setSample(inAudio.getRandomWave());

            if (inAudio.isListen())
                waveformSeekBar.setProgress(1);
            else
                waveformSeekBar.setProgress(0);

            durationTv.setText(inAudio.getDuration());

        }

    }

    public static class inTextHolder extends RecyclerView.ViewHolder {
        TextView body, date, day, bodyReply;
        ImageView reply;

        public inTextHolder(@NonNull View itemView, final OnItemCLickListener onItemCLickListener) {
            super(itemView);
            day = itemView.findViewById(R.id.dayTime);
            body = itemView.findViewById(R.id.body);
            date = itemView.findViewById(R.id.dateTime);

            bodyReply = itemView.findViewById(R.id.bodyReply);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (date.getVisibility() == View.VISIBLE)
                        date.setVisibility(View.GONE);
                    else
                        date.setVisibility(View.VISIBLE);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("message", body.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


            bodyReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCLickListener != null) {
                        onItemCLickListener.OnMoveToReply(getAdapterPosition());
                    }

                }
            });
        }

        public void bind(TextMsgItem inText) {

            if (inText.isReply()) {
                bodyReply.setText(inText.getTextReply());
                bodyReply.setVisibility(View.VISIBLE);
            } else {
                bodyReply.setVisibility(View.GONE);
            }

            body.setText(inText.getText());

            date.setText(inText.getTime());

            if (inText.isDayVisible()) {
                day.setText(inText.getDayTime());
                day.setVisibility(View.VISIBLE);
            } else {
                day.setVisibility(View.GONE);
            }

        }

    }

    public static class outTextHolder extends RecyclerView.ViewHolder {
        LinearLayout infoL;
        TextView body, date, day, bodyReply;
        ImageView status;

        public outTextHolder(@NonNull View itemView, final OnItemCLickListener onItemCLickListener) {
            super(itemView);
            infoL = itemView.findViewById(R.id.timell);
            day = itemView.findViewById(R.id.dayTime);
            body = itemView.findViewById(R.id.body);
            date = itemView.findViewById(R.id.dateTime);
            status = itemView.findViewById(R.id.status);
            bodyReply = itemView.findViewById(R.id.bodyReply);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (infoL.getVisibility() == View.VISIBLE)
                        infoL.setVisibility(View.GONE);
                    else
                        infoL.setVisibility(View.VISIBLE);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("message", body.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            bodyReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemCLickListener != null) {
                        onItemCLickListener.OnMoveToReply(getAdapterPosition());
                    }

                }
            });
        }

        public void bind(TextMsgItem outText) {
            Log.d("dayV_", "Bind" + outText.isDayVisible());
            if (outText.isReply()) {
                bodyReply.setText(outText.getTextReply());
                bodyReply.setVisibility(View.VISIBLE);
            } else {
                bodyReply.setVisibility(View.GONE);
            }
            body.setText(outText.getText());

            date.setText(outText.getTime());

            if (outText.isDayVisible()) {
                day.setVisibility(View.VISIBLE);
                day.setText(outText.getDayTime());
                Log.d("dayV_", "visible" + outText.isDayVisible());
            } else {
                day.setVisibility(View.GONE);
            }

            String s = outText.getStatus();
            switch (s) {
                case "received":
                    status.setImageResource(R.drawable.ic_received);
                    break;
                case "read":
                    status.setImageResource(R.drawable.ic_read);
                    break;
                case "sent":
                    status.setImageResource(R.drawable.ic_sent);
                    break;
                default:
                    status.setImageResource(R.drawable.ic_pending);
                    break;
            }

        }

    }

    public RecyclerView.ItemAnimator animator = new RecyclerView.ItemAnimator() {
        @Override
        public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
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
            return false;
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
