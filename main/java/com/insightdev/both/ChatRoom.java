package com.insightdev.both;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.gson.Gson;
import com.masoudss.lib.WaveformSeekBar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jivesoftware.smack.roster.Roster;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ChatRoom extends AppCompatActivity {
    private String username;
    private int toSendId;
    private NetworkReceiver receiver;
    private final Handler handler = new Handler();
    private ChatAdapter.OnItemCLickListener onItemCLickListener;
    private LottieAnimationView playPause;
    private WaveformSeekBar seekBar;
    private int audioPosition, pendingRead = 0;
    private View gradientRecord;
    private MotionLayout chatMotion;
    private long recordingTime;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private ImageView backButton, moreButton, closeReply, lockIc;
    private boolean isBackPressed = false;
    private ConstraintLayout replyLayout, recordLayout;
    private TextView replyText, timeRecord, infoCipher;
    private int replyPos = -1, time;
    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private ChatAdapter chatAdapter;
    private DisplayMetrics displayMetrics;
    private LinearLayout userBox, cancelLayout, goButton, counterL;
    private HoldingButtonLayout holdingButtonLayout;
    private CircularImageView userImage;
    private LottieAnimationView recordDot;
    private TextView name, status, repName, counter;
    static int SLIDE_TO_CANCEL_ALPHA_MULTIPLIER = 3;
    private String path, fileName;
    private File file;
    private ArrayList<Msg> messages = new ArrayList<>();
    private ArrayList<Object> items = new ArrayList<>();
    private String audioString = null;
    private int audioDuration;
    private ImageView sendButton;
    private EmojiconEditText inText;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private LoadingDialog loadingDialog;
    private int firstItemVisible, lastItemVisible, arraySize, moveToPos;
    private boolean isAutomaticScrooll, moveToReply, sendMessage, isKeyboardVisible;
    private MediaPlayer sendMedia;
    private MediaPlayer receiveMedia;
    private WrapContentLinearLayoutManager.onStopScroll onStopScrollListener;
    private boolean newOpen = false;
    private ProfileDialogFragment profileDialogFragment;

    ImageView insertEmoji;
    EmojIconActions emojIconActions;
    boolean emojiOpen = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        Supplier<MemoryCacheParams> memoryCacheParamsSupplier = new Supplier<MemoryCacheParams>() {
            private MemoryCacheParams cacheParams = new MemoryCacheParams(1000000, 1, 0, 0, 1000000);

            @Override
            public MemoryCacheParams get() {
                return cacheParams;
            }
        };

        if (!Fresco.hasBeenInitialized())
            Fresco.initialize(
                    this,
                    ImagePipelineConfig.newBuilder(this)
                            .setBitmapMemoryCacheParamsSupplier(memoryCacheParamsSupplier)
                            .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                            .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                            .experiment().setNativeCodeDisabled(true)
                            .build());

        setContentView(R.layout.activity_chat_room);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        loadingDialog = new LoadingDialog();
        userImage = findViewById(R.id.userImage);
        goButton = findViewById(R.id.goBottom);
        counterL = findViewById(R.id.counterL);
        userBox = findViewById(R.id.userBox);
        chatMotion = findViewById(R.id.chatMotion);
        repName = findViewById(R.id.repName);
        recordLayout = findViewById(R.id.recordConstraint);
        insertEmoji = findViewById(R.id.insertEmoji);
        timeRecord = findViewById(R.id.timeRecord);
        name = findViewById(R.id.textName);
        status = findViewById(R.id.status);
        recordDot = findViewById(R.id.recordLottie);
        gradientRecord = findViewById(R.id.gradient);
        sendButton = findViewById(R.id.sendButton);
        cancelLayout = findViewById(R.id.deslizaLayout);
        replyLayout = findViewById(R.id.replyLayout);
        replyText = findViewById(R.id.textReply);
        counter = findViewById(R.id.counter);
        infoCipher = findViewById(R.id.infoCifrado);
        lockIc = findViewById(R.id.lockImg);
        closeReply = findViewById(R.id.closeReply);
        holdingButtonLayout = findViewById(R.id.holdButton);
        inText = findViewById(R.id.editWrite);
        backButton = findViewById(R.id.backButton);
        moreButton = findViewById(R.id.moreButton);
        recycler = findViewById(R.id.chatMainRecylcer);
        profileDialogFragment = new ProfileDialogFragment();

        newOpen = true;
        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("notification", false)) {
            AppHandler.setOpenFromChat(true);
        }

        emojIconActions = new EmojIconActions(this, chatMotion, inText, insertEmoji);
        emojIconActions.setIconsIds(R.drawable.ic_keyboard, R.drawable.ic_insert_emoji);
        inText.setUseSystemDefault(true);
        username = bundle.getString("username");

        emojIconActions.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
            }

            @Override
            public void onKeyboardClose() {
                emojiOpen = false;
            }
        });

        Contact contact = ContactsManager.getContact(username, getApplicationContext());
        toSendId = Integer.parseInt(contact.getId());
        name.setText(Tools.getFirstWords(contact.getName(), 1));
        processStatus(contact.getStatus());

        Glide.with(this).load(contact.getProfilePhotoLow(this)).centerCrop().into(userImage);

        sendMedia = MediaPlayer.create(ChatRoom.this, R.raw.send_sms);
        receiveMedia = MediaPlayer.create(ChatRoom.this, R.raw.receive_sms);

        receiver = new NetworkReceiver(this);
        receiver.setOnConnectivityListener(new NetworkReceiver.OnConnectivityListener() {
            @Override
            public void Available(Network network) {
                loadContactStatus();

            }

            @Override
            public void Lost(Network network) {
                EventBus.getDefault().post(new UpdateContactStatusEvent(""));
            }
        });
        receiver.register();

        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

        onStopScrollListener = new WrapContentLinearLayoutManager.onStopScroll() {
            @Override
            public void stopScroll(boolean completed) {
                Log.d("Scroll_", "complete" + completed);
                if (completed) {
                    recycler.requestLayout();
                }
            }
        };

        insertEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emojiOpen) {
                    emojIconActions.showPopup();
                    emojIconActions.setUseSystemEmoji(true);
                    insertEmoji.setImageResource(R.drawable.ic_keyboard);
                    emojiOpen = true;
                } else {
                    emojIconActions.hidePopup();
                    insertEmoji.setImageResource(R.drawable.ic_insert_emoji);
                    emojiOpen = false;
                }
            }
        });

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                firstItemVisible = layoutManager.findFirstVisibleItemPosition();
                lastItemVisible = layoutManager.findLastVisibleItemPosition();
                arraySize = messages.size() - 1;

                if (isAutomaticScrooll) {
                    if (sendMessage && layoutManager.findViewByPosition(arraySize) != null) {
                        isAutomaticScrooll = false;
                        LinearLayout layout = ((LinearLayout) layoutManager.findViewByPosition(arraySize));
                        if (layout.findViewById(R.id.timell) != null)
                            layout.findViewById(R.id.timell).setVisibility(View.VISIBLE);
                        chatMotion.transitionToState(R.id.start);
                        sendMessage = false;
                        layoutManager.scrollToPosition(arraySize);
                        chatMotion.transitionToStart();
                        sendMedia.start();
                        return;
                    }
                    if (moveToReply && layoutManager.findViewByPosition(moveToPos) != null) {
                        isAutomaticScrooll = false;
                        moveToReply = false;
                        TransitionDrawable transitionDrawable = (TransitionDrawable) layoutManager.findViewByPosition(moveToPos).getBackground();
                        transitionDrawable.startTransition(500);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                transitionDrawable.reverseTransition(500);
                            }
                        }, 1000);
                    }
                }

                if (lastItemVisible != arraySize) {
                    if (chatMotion.getCurrentState() == R.id.end)
                        chatMotion.transitionToState(R.id.goBotFromEnd);
                    else if (chatMotion.getCurrentState() == R.id.start)
                        chatMotion.transitionToState(R.id.goBot);

                } else if (replyPos == -1) {
                    pendingRead = 0;
                    setCounter();
                    chatMotion.transitionToState(R.id.start);
                }

                if (firstItemVisible != 0 || lastItemVisible == arraySize) {
                    infoCipher.setVisibility(View.VISIBLE);
                    lockIc.setVisibility(View.VISIBLE);
                    return;
                }

                infoCipher.setVisibility(View.GONE);
                lockIc.setVisibility(View.GONE);

            }
        });


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
                layoutManager.scrollToPosition(messages.size() - 3);
                layoutManager.smoothScrollToPosition(recycler, new RecyclerView.State(), messages.size() - 1);
                pendingRead = 0;
                setCounter();

            }
        });
        onItemCLickListener = new ChatAdapter.OnItemCLickListener() {
            @Override
            public void OnPlayClick(int position, LottieAnimationView play_pause, WaveformSeekBar waveformSeekBar) {
                AudioMessage msg = (AudioMessage) messages.get(position);
                if (!msg.isListen()) {
                    if (msg.getType().contentEquals("in")) {
                        Conversations.setToListenAudio(username, msg.getId(), false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ChatHandler.sendCommand("listen" + msg.getId() + "@", username);
                            }
                        }).start();

                    }
                }
                stopPlaying();
                playPause = play_pause;
                seekBar = waveformSeekBar;
                audioPosition = position;
                String fileName = file + "/play" + ".mp4";

                decodeAudio(msg.getAudio(), fileName);

            }

            @Override
            public void OnPauseClick() {
                stopPlaying();
            }

            @Override
            public void OnTouch(WaveformSeekBar waveformSeekBar) {
                if (seekBar == waveformSeekBar && mediaPlayer != null) {
                    int timeMillis = (int) (waveformSeekBar.getProgress() * mediaPlayer.getDuration());
                    mediaPlayer.seekTo(timeMillis);
                }
            }


            @Override
            public void OnMoveToReply(int position) {
                position = getPosReplyById(messages.get(position).getReplyId(), messages.get(position).getTextReply());
                Log.d("Reply_", "pos" + position);
                if (position == -1) {
                    return;
                }
                //layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
                View view = layoutManager.findViewByPosition(position);
                int pos = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (view != null && pos <= position) {
                    TransitionDrawable transitionDrawable = (TransitionDrawable) view.getBackground();
                    transitionDrawable.startTransition(500);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            transitionDrawable.reverseTransition(500);
                        }
                    }, 1000);
                    return;
                }
                isAutomaticScrooll = true;
                moveToReply = true;
                moveToPos = position;
                layoutManager.smoothScrollToPosition(recycler, new RecyclerView.State(), position);

            }

            @Override
            public void OnProgressChanged(float progress, TextView textView, int position) {
                if (position == -1)
                    return;
                int timeMillis = (int) (progress * ((AudioMessage) messages.get(position)).getMillis());
                String minutes = Tools.convertMillisToMinutes(timeMillis);
                textView.setText(minutes);
            }
        };


        SwipeController swipeController = new SwipeController(ChatRoom.this, new ISwipeControllerActions() {
            @Override
            public void onSwipePerformed(int position) {
                Msg msg = messages.get(position);
                String text = msg.getBody();

                if (msg instanceof AudioMessage) {
                    text = getString(R.string.mensaje_de_voz_) + ((AudioMessage) msg).getDuration() + ")";
                }
                replyText.setText(text);
                replyLayout.setVisibility(View.VISIBLE);
                replyPos = position;
                if (msg.getType().contentEquals("in"))
                    repName.setText(name.getText().toString());
                else
                    repName.setText(getString(R.string.tu));
                chatMotion.transitionToState(R.id.end);
                inText.requestFocus();
                if (!isKeyboardVisible)
                    KeyboardUtils.toggleKeyboardVisibility(ChatRoom.this);
                recycler.requestLayout();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recycler);

        closeReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeReply();
                replyPos = -1;
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ChatRoom.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.chat_menu, popupMenu.getMenu());

                boolean isMuted = false;
                Chat chat = Conversations.getChat(username);
                if (chat != null)
                    isMuted = chat.isMuted();
                if (isMuted)
                    popupMenu.getMenu().getItem(1).setTitle(getString(R.string.notificarme));
                boolean finalIsMuted = isMuted;

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked

                        switch (menuItem.getItemId()) {
                            case R.id.menuClean:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Conversations.cleanAllMessages(username);
                                    }
                                }).start();
                                break;

                            case R.id.menuMute:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Conversations.setMuted(username, !finalIsMuted);
                                    }
                                }).start();
                                break;

                            case R.id.menuBlock:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ContactsManager.getContact(username, ChatRoom.this).getType().contentEquals("match"))
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new DislikeDialog().showDialog(ChatRoom.this, username);
                                                }
                                            });
                                    }
                                }).start();

                                break;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inText.getText().toString().trim();
                if (!text.trim().isEmpty()) {
                    if (replyPos != -1) {
                        Msg msg = messages.get(replyPos);
                        String body = msg.getBody();
                        if (msg instanceof AudioMessage) {
                            body = getString(R.string.mensaje_de_voz_) + ((AudioMessage) msg).getDuration() + ")";
                        }
                        text = Tools.getMsgWithReply(text, body, msg.getId());
                    }

                    actionSend(text);

                    inText.setText("");

                }
            }
        });

        inText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler.requestLayout();
            }
        });

        holdingButtonLayout.addListener(new HoldingButtonLayoutListener() {
            @Override
            public void onBeforeExpand() {
                try {
                    if (ActivityCompat.checkSelfPermission(ChatRoom.this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ChatRoom.this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
                inText.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off_100));
                inText.setVisibility(View.INVISIBLE);
                cancelLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_100));
                cancelLayout.setVisibility(View.VISIBLE);
                recordLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_100));
                recordLayout.setVisibility(View.VISIBLE);
                gradientRecord.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_100));
                gradientRecord.setVisibility(View.VISIBLE);
                insertEmoji.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off_100));
                insertEmoji.setVisibility(View.GONE);
                recordDot.playAnimation();
                stopPlaying();
                startRecording();
                time = 0;
                onProgressRecord.run();
            }


            @Override
            public void onExpand() {


            }

            @Override
            public void onBeforeCollapse() {
                if (ActivityCompat.checkSelfPermission(ChatRoom.this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                inText.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_100));
                inText.setVisibility(View.VISIBLE);
                cancelLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off_100));
                cancelLayout.setVisibility(View.INVISIBLE);
                audioDuration = (int) (new Date().getTime() - recordingTime);
                stopRecording();
                recordLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off_100));
                recordLayout.setVisibility(View.GONE);
                gradientRecord.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off_100));
                gradientRecord.setVisibility(View.GONE);
                insertEmoji.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_100));
                insertEmoji.setVisibility(View.VISIBLE);
                recordDot.cancelAnimation();

                if (audioDuration > 1000) {
                    encodeAudio(fileName);
                }

            }

            @Override
            public void onCollapse(boolean b) {
                if (ActivityCompat.checkSelfPermission(ChatRoom.this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                handler.removeCallbacks(onProgressRecord);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppHandler.sendPresence("en línea");
                    }
                }).start();

                if (audioDuration > 1200) {
                    if (!b) {
                        if (replyPos != -1) {
                            Msg msg = messages.get(replyPos);
                            String body = msg.getBody();
                            if (msg instanceof AudioMessage) {
                                body = getString(R.string.mensaje_de_voz_) + ((AudioMessage) msg).getDuration() + ")";
                            }
                            String finalBody = body;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    actionSend(Tools.getMsgWithReply(Tools.getJsonAudio(audioString, audioDuration), finalBody, msg.getId()));
                                }
                            }, 300);

                        } else
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    actionSend(Tools.getJsonAudio(audioString, audioDuration));
                                }
                            }, 300);


                    }
                }
            }

            @Override
            public void onOffsetChanged(float v, boolean b) {
                cancelLayout.setTranslationX(-holdingButtonLayout.getWidth() * v);
                cancelLayout.setAlpha(1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * v);
            }
        });

        inText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        inText.addTextChangedListener(new TextWatcher() {
            boolean isTyping = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (holdingButtonLayout.getVisibility() == View.VISIBLE) {
                        holdingButtonLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off_100));
                        holdingButtonLayout.setVisibility(View.INVISIBLE);
                        sendButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_100));
                        sendButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    holdingButtonLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_100));
                    holdingButtonLayout.setVisibility(View.VISIBLE);
                    sendButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off_100));
                    sendButton.setVisibility(View.INVISIBLE);
                }
            }

            private Timer timer = new Timer();
            private final long delay = 800;

            @Override
            public void afterTextChanged(Editable s) {
                if (!isTyping && !s.toString().isEmpty()) {
                    AppHandler.sendPresence(getActionPresence("escribiendo...", username));
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isTyping = false;
                        AppHandler.sendPresence("en línea");
                    }
                }, delay);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contactAux = ContactsManager.getContact(username, getApplicationContext());
                if (contactAux != null) {

                    if (profileDialogFragment.isAdded())
                        return;
                    Bundle bundle = new Bundle();
                    bundle.putString("person", new Gson().toJson(contactAux));
                    profileDialogFragment.setArguments(bundle);

                    profileDialogFragment.show((ChatRoom.this).getSupportFragmentManager(), "ProfileDialog");
                }

            }
        });
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                isKeyboardVisible = isVisible;
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contactAux = ContactsManager.getContact(username, getApplicationContext());
                if (contactAux != null) {


                    if (profileDialogFragment.isAdded())
                        return;
                    Bundle bundle = new Bundle();
                    bundle.putString("person", new Gson().toJson(contact));
                    profileDialogFragment.setArguments(bundle);

                    profileDialogFragment.show((ChatRoom.this).getSupportFragmentManager(), "ProfileDialog");
                }

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                path = getApplicationContext().getFilesDir().getPath();
                file = new File(path);
            }
        }).start();
        AppHandler.setChatActivityOpen(true);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new ChatRoomEvent());
        Log.d("Lifecycle", "create");
        Conversations.setInsideChat(new Pair<Boolean, String>(true, username));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundle = intent.getExtras();
        username = bundle.getString("username");

        items.clear();
        EventBus.getDefault().post(new ChatRoomEvent());

        Contact contact = ContactsManager.getContact(username, getApplicationContext());
        toSendId = Integer.parseInt(contact.getId());
        name.setText(contact.getName());
        processStatus(contact.getStatus());
        Picasso.get().load(contact.getProfilePhotoLow(this)).config(Bitmap.Config.RGB_565).fit().
                centerCrop().into(userImage);

        Conversations.setInsideChat(new Pair<Boolean, String>(true, username));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackPressed = true;
        receiver.unregister();
        EventBus.getDefault().unregister(this);
        Conversations.setInsideChat(new Pair<Boolean, String>(false, ""));
        //AppHandler.setChatActivityOpen(false);
        if (isKeyboardVisible)
            KeyboardUtils.forceCloseKeyboard(inText);
        if (AppHandler.isOpenFromChat() && !AppHandler.isMainActivityOpen()) {
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppHandler.setChatActivityOpen(true);
        AppHandler.runService(getApplicationContext());

    }

    @Override
    protected void onResume() {
        Log.d("Lifecycle", "resume" + username);
        super.onResume();
        Conversations.setInsideChat(new Pair<Boolean, String>(true, username));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Conversations.setPendingReading(username, false);
                ChatHandler.sendReadState(username);
            }
        }).start();
        int pos = Conversations.getPosByUser(username);
        if (pos != -1)
            EventBus.getDefault().post(new UpdateChatItemEvent(pos));
        if (ChatNotifications.notificationManager == null) {
            ChatNotifications.loadNotificationManager(ChatRoom.this);
        }
        ChatNotifications.removeNotificationsFromList(toSendId);
        ChatNotifications.notificationManager.cancel(toSendId);
        AppHandler.sendPresence("en línea");
        loadContactStatus();

    }


    @Override
    protected void onPause() {
        super.onPause();
        Conversations.setInsideChat(new Pair<Boolean, String>(false, ""));
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlaying();
        stopRecording();
        Tools.closeMainService(getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isBackPressed) {
                    AppHandler.sendPresence("offline/" + new Date().getTime() + "/");
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppHandler.setChatActivityOpen(false);
        Log.d("aklsd", "chatClose ");
        /*
        if (Tools.isMyServiceRunning(MainService.class, getApplicationContext()))
            stopService(new Intent(ChatRoom.this, MainService.class));*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) finish();

    }

    private void initializeMediaPlayer(String filename) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filename);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
        }
    }

    private void startPlaying(String filename) {
        initializeMediaPlayer(filename);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int timeMillis = 0;
                float progress = seekBar.getProgress();
                int totalMillis = mediaPlayer.getDuration();
                if (progress != 1.0)
                    timeMillis = (int) (progress * totalMillis);
                if (totalMillis - timeMillis < 650)
                    timeMillis = totalMillis - 660;

                mediaPlayer.seekTo(timeMillis);
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        mediaPlayer.start();
                        onProgressMediaPlayer.run();
                    }
                });
            }
        });

    }


    private void stopPlaying() {
        if (mediaPlayer != null) {
            if (playPause != null) {
                if (playPause.getFrame() == 12) {
                    playPause.setSpeed(-1.5f);
                    playPause.playAnimation();
                }
            }
            mediaPlayer.release();
            handler.removeCallbacks(onProgressMediaPlayer);
            mediaPlayer = null;

        }

    }

    private void startRecording() {
        stopRecording();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        fileName = file + "/" + System.currentTimeMillis() + ".3gp";
        mediaRecorder.setOutputFile(fileName);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
        }

        mediaRecorder.start();
        recordingTime = new Date().getTime();
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppHandler.sendPresence(getActionPresence("grabando audio...", username));
            }
        }).start();
    }

    private void stopRecording() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encodeAudio(String selectedPath) {

        byte[] audioBytes;
        try {

            File audioFile = new File(selectedPath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(audioFile);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            audioBytes = baos.toByteArray();

            audioString = Base64.encodeToString(audioBytes, Base64.DEFAULT);

            audioFile.delete();

        } catch (Exception e) {

        }


    }

    private void decodeAudio(String base64AudioData, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(Base64.decode(base64AudioData.getBytes(), Base64.DEFAULT));
            fos.close();
            startPlaying(path);
        } catch (Exception e) {
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadingShow(LoadingShowEvent event) {
        if (loadingDialog != null) {
            loadingDialog.showDialog(ChatRoom.this, getString(R.string.cargando));
            Log.d("LogXX", "loadingShow");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadingDismiss(LoadingDismissEvent event) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            Log.d("LogXX", "loadingDismiss");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void newChat(NewChatEvent event) {
        String with = event.getWith();
        if (with.contentEquals(username)) {
            messages = Conversations.getChat(with).getMessages();
            if (messages.get(0).getType().contentEquals("in")) {
                loadItems();
                configChatAdapter();
                receiveMedia.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Conversations.setPendingReading(with, false);
                        ChatHandler.sendReadState(with);
                    }
                }).start();

            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateState(UpdateMessageEvent event) {
        int pos = event.getPosition();
        if (event.getStatus().contentEquals("listen"))
            ((AudioMsgItem) items.get(pos)).setListen(true);
        else
            ((MsgItem) items.get(pos)).setStatus(event.getStatus());
        chatAdapter.notifyItemChanged(pos);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(ReceiveMessageEvent event) {
        messages = Conversations.getChat(username).getMessages();
        int last = messages.size() - 1;
        addItem(messages.get(last), messages.get(last - 1));
        chatAdapter.notifyItemInserted(last);
        Log.d("ReceiveM", "before");
        if (layoutManager.findLastVisibleItemPosition() == last - 1)
            layoutManager.scrollToPosition(last);
        else {
            pendingRead++;
            setCounter();
        }

        receiveMedia.start();
        Log.d("ReceiveM", "aftersound");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Conversations.setPendingReading(username, false);
                ChatHandler.sendReadState(username);
            }
        }).start();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendMessage(SendMessageEvent event) {
        messages = Conversations.getChat(username).getMessages();
        int last = messages.size() - 1;
        addItem(messages.get(last), messages.get(last - 1));
        chatAdapter.notifyItemInserted(last);
        Log.d("AddMessage", "UiSended");
        isAutomaticScrooll = true;
        sendMessage = true;
        layoutManager.smoothScrollToPosition(recycler, new RecyclerView.State(), last);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cleanMessages(CleanMessagesEvent event) {
        if (event.getUsername().contentEquals(username)) {
            closeReply();
            chatMotion.transitionToStart();
            EventBus.getDefault().post(new ChatRoomEvent());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteChat(DeleteChatEvent event) {
        if (event.getUsername().contentEquals(username)) {
            if (ChatNotifications.notificationManager == null)
                ChatNotifications.loadNotificationManager(ChatRoom.this);
            ChatNotifications.notificationManager.cancel(toSendId);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chatRoom(ChatRoomEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Chat chat = Conversations.getChat(username);
                if (chat != null) {
                    messages = chat.getMessages();
                    loadItems();
                } else {
                    messages = new ArrayList<>();
                    items = new ArrayList<>();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        configChatAdapter();
                        if (ChatNotifications.notificationManager != null) {
                            ChatNotifications.notificationManager.cancel(toSendId);
                        }
                    }
                });
            }
        }).start();

    }

    public void loadItems() {
        Msg prev = null;
        for (int i = 0; i < messages.size(); i++) {
            try {
                prev = messages.get(i - 1);
            } catch (Exception ignored) {
            }
            addItem(messages.get(i), prev);

        }
    }

    public void addItem(Msg msg, Msg prev) {
        boolean dayVisibility = false;

        try {
            if (new Date(msg.getTime()).getDate() - new Date(prev.getTime()).getDate() != 0)
                dayVisibility = true;
        } catch (Exception e) {
            dayVisibility = true;
        }

        Date date = new Date(msg.getTime());
        Date now = new Date();
        String day;
        SimpleDateFormat sdf;
        int dif = now.getDate() - date.getDate();
        if (dif == 0 && date.getMonth() == now.getMonth() && date.getYear() == now.getYear()) {
            day = getString(R.string.hoy);
        } else if (dif == 1 || ((dif > -31 && dif < -27) && date.getDate() == 1)) {
            day = getString(R.string.ayer);
        } else {
            DateFormatSymbols symbols = new DateFormatSymbols();
            symbols.setMonths(getResources().getStringArray(R.array.meses_lowerc));
            symbols.setWeekdays(getResources().getStringArray(R.array.days_lowerc));
            String pattern = "EEEEEEEE dd MMMM yyyy";
            sdf = new SimpleDateFormat(pattern, symbols);
            day = sdf.format(msg.getTime());
        }
        if (msg instanceof AudioMessage) {
            AudioMessage audio = (AudioMessage) msg;
            items.add(new AudioMsgItem(audio.getDuration(), audio.isListen(), audio.getRandomWave(),
                    audio.getStatus(), audio.getType(), audio.getHours(), dayVisibility, day, audio.isReply()
                    , audio.getTextReply(), audio.getReplyId()));
        } else {
            items.add(new TextMsgItem(msg.getBody(), msg.getStatus(), msg.getType(), msg.getHours(), dayVisibility,
                    day, msg.isReply(), msg.getTextReply(), msg.getReplyId()));
        }
    }


    public void actionSend(String msg) {
        Chat chat = Conversations.getChat(username);
        String message;
        String id = Tools.getSha256(msg + "" + new Date().getTime());
        String myAESKey = null;
        String encryptedMessage = null;
        String encryptedId = null;
        final ArrayList<Msg>[] messages = new ArrayList[]{new ArrayList<>()};
        Contact contact = ContactsManager.getContact(username, getApplicationContext());

        if (chat == null) {
            String encryptedAESKey = null;
            try {
                myAESKey = Encryption.getSecretAESKeyAsString();
                Log.d("MyAes", "aes" + myAESKey);
                encryptedAESKey = Encryption.encryptText(myAESKey, contact.getPublicKey());
                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);
                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                Log.d("MyAes", "Excaes" + e.getMessage());
                e.printStackTrace();
            }
            if (contact.getType().contains("expDate") && !contact.getType().contains("match"))
                message = Tools.getJsonMsgWithContact(encryptedMessage, encryptedId, encryptedAESKey, Tools.profileToContactJson(Profile.person, "expDate"));
            else
                message = Tools.getJsonMsg(encryptedMessage, encryptedId, encryptedAESKey);
        } else if (chat.isPendingSendAesKey()) {
            String encryptedAESKey = null;
            myAESKey = chat.getMyAESKey();
            try {

                encryptedAESKey = Encryption.encryptText(myAESKey, contact.getPublicKey());
                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);
                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                e.printStackTrace();

            }
            if (chat.getMessages().size() < 2 && (contact.getType().contains("expDate") && !contact.getType().contains("match")))
                message = Tools.getJsonMsgWithContact(encryptedMessage, encryptedId, encryptedAESKey, Tools.profileToContactJson(Profile.person, "expDate"));
            else
                message = Tools.getJsonMsg(encryptedMessage, encryptedId, encryptedAESKey);
        } else {
            myAESKey = chat.getMyAESKey();
            try {
                encryptedMessage = Encryption.encryptTextUsingAES(msg, myAESKey);
                encryptedId = Encryption.encryptTextUsingAES(contact.getId() + "", myAESKey);
            } catch (Exception e) {
                e.printStackTrace();

            }
            if (chat.getMessages().size() < 2 && (contact.getType().contains("expDate") && !contact.getType().contains("match")))
                message = Tools.getJsonMsgWithContact(encryptedMessage, encryptedId, Tools.profileToContactJson(Profile.person, "expDate"));
            else
                message = Tools.getJsonMsg(encryptedMessage, encryptedId);
        }

        boolean isSent = ChatHandler.sendMessage(message, username, id);


        boolean isMms = false, isReply = replyPos != -1;
        String name = contact.getName();
        String username = contact.getChatUsername(getApplicationContext());
        PublicKey publicKey = contact.getPublicKey();

        String bodyWithOutReply = msg;
        if (isReply)
            bodyWithOutReply = Tools.getValue(msg, "body");

        if (!Tools.getValue(bodyWithOutReply, "audio").isEmpty())
            isMms = true;

        long time = new Date().getTime();
        if (isSent) {
            if (chat == null) {
                if (isMms)
                    messages[0].add(new AudioMessage(msg, id, "sent", "out", time, isReply));
                else
                    messages[0].add(new Msg(msg, id, "sent", "out", time, isReply));

                Conversations.addConversation(new Chat(name, username, messages[0], publicKey, myAESKey, null, false, false, false), getApplicationContext());

            } else {
                Msg m = new Msg(msg, id, "sent", "out", time, isReply);
                if (isMms)
                    m = new AudioMessage(msg, id, "sent", "out", time, isReply);
                Conversations.addMsgToChat(username, m, getApplicationContext());
            }

            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (!Tools.isUserOnline(username))
                        AppHandler.sendPushRequest(getApplicationContext(), String.valueOf(toSendId));
                }
            }).start();

        } else {
            if (chat == null) {
                if (isMms)
                    messages[0].add(new AudioMessage(msg, id, "pending", "out", time, isReply));
                else
                    messages[0].add(new Msg(msg, id, "pending", "out", time, isReply));
                Conversations.addConversation(new Chat(name, username, messages[0], publicKey, myAESKey, null, false, false, false), getApplicationContext());
            } else {
                Msg m = new Msg(msg, id, "pending", "out", time, isReply);
                if (isMms)
                    m = new AudioMessage(msg, id, "pending", "out", time, isReply);

                Conversations.addMsgToChat(username, m, getApplicationContext());

            }
            Conversations.addPendingToSendMessage(new PendingMessage(message, username, id));

        }

        this.messages = Conversations.getChat(username).getMessages();
        if (messages[0].size() == 1) {
            addItem(messages[0].get(0), null);
            recycler.setVisibility(View.VISIBLE);
            configChatAdapter();
            sendMedia.start();
        }
        closeReply();

    }

    private void configChatAdapter() {
        recycler.setHasFixedSize(true);
        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, "chat");
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOnStopScroll(onStopScrollListener);
        recycler.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(items, this);
        recycler.setAdapter(chatAdapter);
        recycler.setHasFixedSize(false);
        SimpleItemAnimator animator = (SimpleItemAnimator) recycler.getItemAnimator();
        animator.setSupportsChangeAnimations(false);
        animator.setChangeDuration(0);
        chatAdapter.setOnItemClickListener(onItemCLickListener);
        layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
        layoutManager.scrollToPosition(messages.size() - 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateContactStatus(UpdateContactStatusEvent event) {
        String s = event.getStatus();
        processStatus(s);

    }

    private void processStatus(String s) {

        if (s == null)
            return;

        if (s.contentEquals("en línea"))
            s = getString(R.string.en_linea);
        else if (s.contentEquals("escribiendo..."))
            s = getString(R.string.escribiendo);
        else if (s.contentEquals("grabando audio..."))
            s = getString(R.string.grabando_audio);
        else if(!s.isEmpty())
            s = getLastTime(Long.parseLong(s));

        status.setText(s);
    }

    private void loadContactStatus() {
        if (XMPPMessageServer.getConnection() != null) {
            Roster roster = Roster.getInstanceFor(XMPPMessageServer.getConnection());
            BareJid jid = null;
            try {
                jid = JidCreate.bareFrom(username);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
            AppHandler.updateStatusByPresence(roster.getPresence(jid), getApplicationContext());
        } else
            EventBus.getDefault().post(new UpdateContactStatusEvent(""));
    }

    private Runnable onProgressRecord = new Runnable() {
        @Override
        public void run() {
            timeRecord.setText(Tools.convertMillisToMinutes(time * 1000));
            time++;
            handler.postDelayed(this, 1000);
        }
    };

    private Runnable onProgressMediaPlayer = new Runnable() {
        @Override
        public void run() {
            double progress = mediaPlayer.getCurrentPosition() / (mediaPlayer.getDuration() * 1.0);
            Log.d("Play_", "OnProgressMediaPlayer" + mediaPlayer.getCurrentPosition() + "//" + mediaPlayer.getDuration() + "//" + progress);
            seekBar.setProgress((float) progress);
            if (progress < 0.98) {
                handler.postDelayed(this, 100);
            } else {
                AudioMessage msg = (AudioMessage) messages.get(audioPosition);
                if (!msg.isListen() && msg.getType().contentEquals("out"))
                    seekBar.setProgress(0);
                else if (msg.getType().contentEquals("in")) {
                    msg.setStatus("listen");
                    msg.setListen(true);
                }
                stopPlaying();
                EventBus.getDefault().post(new UpdateMessageEvent(audioPosition, msg.getStatus()));
            }

        }
    };

    private String getActionPresence(String action, String to) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", action);
            object.put("to", to);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void closeReply() {
        chatMotion.transitionToState(R.id.start);
        replyPos = -1;
    }

    private String getLastTime(long time) {

        Date dateOffline = new Date(time);
        if (!messages.isEmpty()) {
            Date d = new Date(getLastTimeInMsg());
            if (d.after(dateOffline))
                dateOffline = d;
        }
        Date now = new Date();
        String offlineStatus = "";
        String pattern = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String hours = sdf.format(dateOffline);
        String str = getString(R.string.ult_vez);

        int dif = now.getDate() - dateOffline.getDate();
        if (dif == 0 && dateOffline.getMonth() == now.getMonth() && dateOffline.getYear() == now.getYear()) {
            offlineStatus = str + getString(R.string.hoy_a_las) + hours;
        } else if (dif == 1 || ((dif > -31 && dif < -27) && dateOffline.getDate() == 1)) {
            offlineStatus = str + getString(R.string.ayer_a_las) + hours;
        } else {
            DateFormatSymbols symbols = new DateFormatSymbols();
            symbols.setMonths(getResources().getStringArray(R.array.meses_lowerc));
            pattern = "dd MMMM yyyy";
            sdf = new SimpleDateFormat(pattern, symbols);
            offlineStatus = str + sdf.format(dateOffline);
        }
        return offlineStatus;
    }

    private long getLastTimeInMsg() {
        Msg msg;
        for (int i = messages.size() - 1; i > -1; i--) {
            msg = messages.get(i);
            if (msg.getType().contentEquals("in") || (msg.getType().contentEquals("out") && msg.getStatus().contentEquals("read")))
                return messages.get(i).getTime();
        }
        return 0;
    }

    private int getPosReplyById(String replyId, String textReply) {
        Log.d("Reply_", "id" + replyId + "// text" + textReply);
        Msg msg;
        String text = "";
        for (int i = messages.size() - 1; i > -1; i--) {
            msg = messages.get(i);
            text = msg.getBody();
            if (msg instanceof AudioMessage)
                text = "Mensaje de voz (" + ((AudioMessage) msg).getDuration() + ")";
            Log.d("Reply_", "id" + msg.getId() + "// text" + text);
            if (msg.getId().contentEquals(replyId) && text.contentEquals(textReply)) {
                return i;
            }
        }
        return -1;
    }

    private void setCounter() {
        if (pendingRead == 0) {
            counterL.setVisibility(View.GONE);
            return;
        }
        counter.setText(pendingRead + "");
        counterL.setVisibility(View.VISIBLE);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void serviceShutDown(ServiceShutDownEvent event) {
        Log.d("aklsd", "shutdown");
        finish();
    }


}
