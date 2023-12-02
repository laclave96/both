package com.insightdev.both;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class EditImage extends AppCompatActivity implements EmojiBSFragment.EmojiListener {
    private PhotoEditorView photoEditorView;
    private LinearLayout topButtons;
    private ImageView undo, redo;
    private MotionLayout editMotion;
    private EditText comment;
    private ArrayList<ImageButton> buttons = new ArrayList<>();
    private TextView saveButton;
    private ArrayList<ImageButton> colors = new ArrayList<>();
    private ArrayList<FilterItem> filterItems = new ArrayList<>();
    private EmojiBSFragment mEmojiBSFragment;
    private String pathImage, uri;
    private Uri imageUri;
    LoadingDialog loadingDialog;
    private RecyclerView recyclerViewFilter;
    private FilterAdapter filterAdapter;
    private PhotoEditor mPhotoEditor;
    private boolean isKeyboardVisible = false, isReady = false;
    private Handler handler = new Handler();
    private Bitmap bm;

    public static native String storePhoto();

    public static native String insertPost();

    public static native String photoPass();

    public static native String commentPass();

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        recyclerViewFilter = findViewById(R.id.filterRecylcer);
        recyclerViewFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        saveButton = findViewById(R.id.saveButton);
        photoEditorView = findViewById(R.id.EditorView);
        editMotion = findViewById(R.id.editMotion);
        buttons.add(findViewById(R.id.crop));
        buttons.add(findViewById(R.id.brush));
        buttons.add(findViewById(R.id.addFilter));
        buttons.add(findViewById(R.id.addComent));
        buttons.add(findViewById(R.id.addEmoji));
        topButtons = findViewById(R.id.topButtons);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        comment = findViewById(R.id.comment);


        colors.add(findViewById(R.id.black));
        colors.add(findViewById(R.id.red));
        colors.add(findViewById(R.id.yellow));
        colors.add(findViewById(R.id.green));
        colors.add(findViewById(R.id.violet));
        colors.add(findViewById(R.id.blue));
        colors.add(findViewById(R.id.gray));
        mEmojiBSFragment = new EmojiBSFragment();
        mEmojiBSFragment.setEmojiListener(this);
        loadingDialog = new LoadingDialog();

        Bundle bundle = getIntent().getExtras();
        pathImage = bundle.getString("path", "");
        uri = bundle.getString("uri", "");
        int from = bundle.getInt("from");
        comment.setText(bundle.getString("comment", ""));

        if (!pathImage.isEmpty())
            imageUri = Uri.fromFile(new File(pathImage));
        else
            imageUri = Uri.parse(uri);

        MainActivity.editImagetUri = imageUri.toString();
        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.comforta_bold);

        mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .build();

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                isKeyboardVisible = isVisible;
            }
        });

        EventBus.getDefault().register(this);

        filterAdapter = new FilterAdapter(filterItems, this);

        recyclerViewFilter.setAdapter(filterAdapter);

        filterAdapter.setOnItemClickListener(new FilterAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {
                loadingDialog.showDialog(EditImage.this, "Cargando");
                centerCrop();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (position) {
                            case 0:
                                mPhotoEditor.setFilterEffect(PhotoFilter.NONE);
                                break;
                            case 1:
                                mPhotoEditor.setFilterEffect(PhotoFilter.AUTO_FIX);
                                break;
                            case 2:
                                mPhotoEditor.setFilterEffect(PhotoFilter.SATURATE);
                                break;
                            case 3:
                                mPhotoEditor.setFilterEffect(PhotoFilter.CONTRAST);
                                break;
                            case 4:
                                mPhotoEditor.setFilterEffect(PhotoFilter.CROSS_PROCESS);
                                break;
                            case 5:
                                mPhotoEditor.setFilterEffect(PhotoFilter.GRAIN);
                                break;
                            case 6:
                                mPhotoEditor.setFilterEffect(PhotoFilter.VIGNETTE);
                                break;
                            case 7:
                                mPhotoEditor.setFilterEffect(PhotoFilter.DOCUMENTARY);
                                break;
                            case 8:
                                mPhotoEditor.setFilterEffect(PhotoFilter.LOMISH);
                                break;
                            case 9:
                                mPhotoEditor.setFilterEffect(PhotoFilter.GRAY_SCALE);
                                break;
                            case 10:
                                mPhotoEditor.setFilterEffect(PhotoFilter.POSTERIZE);
                                break;
                            case 11:
                                mPhotoEditor.setFilterEffect(PhotoFilter.SEPIA);
                                break;

                        }
                        loadingDialog.dismiss();
                    }
                }, 1500);

            }
        });

        for (int i = 0; i < colors.size(); i++) {
            int finalI = i;
            colors.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI) {
                        case 0:
                            mPhotoEditor.setBrushColor(Color.BLACK);
                            selectedColor(0);
                            break;
                        case 1:
                            mPhotoEditor.setBrushColor(Color.parseColor("#ff0046"));
                            selectedColor(1);

                            break;
                        case 2:
                            mPhotoEditor.setBrushColor(Color.parseColor("#D4AF37"));
                            selectedColor(2);

                            break;
                        case 3:
                            mPhotoEditor.setBrushColor(Color.parseColor("#5dc1b9"));
                            selectedColor(3);
                            break;
                        case 4:
                            mPhotoEditor.setBrushColor(Color.parseColor("#ff878d"));
                            selectedColor(4);
                            break;
                        case 5:
                            mPhotoEditor.setBrushColor(Color.parseColor("#00B0FF"));
                            selectedColor(5);
                            break;
                        case 6:
                            mPhotoEditor.setBrushColor(Color.parseColor("#FF888888"));
                            selectedColor(6);
                            break;

                    }
                }
            });

        }

        for (int i = 0; i < buttons.size(); i++) {

            int finalI = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI) {
                        case 0:
                            selected(0, R.id.start);
                            mPhotoEditor.setBrushDrawingMode(false);
                            Intent intent = new Intent(EditImage.this, CropActivity.class);
                            intent.putExtra("uri", MainActivity.editImagetUri);
                            startActivity(intent);
                            break;
                        case 1:
                            selected(1, R.id.end);
                            mPhotoEditor.setBrushDrawingMode(true);
                            break;
                        case 2:
                            selected(2, R.id.filters);
                            mPhotoEditor.setBrushDrawingMode(false);
                            break;
                        case 3:
                            selected(3, R.id.coment);
                            comment.setFocusableInTouchMode(true);
                            comment.setFocusable(true);
                            comment.requestFocus();
                            //KeyboardUtils.toggleKeyboardVisibility(EditImage.this);
                            mPhotoEditor.setBrushDrawingMode(false);
                            break;
                        case 4:
                            selected(4, R.id.start);
                            mPhotoEditor.setBrushDrawingMode(false);
                            mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());

                            break;

                    }
                }
            });
        }

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.undo();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.redo();
            }
        });

        centerCrop();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog(EditImage.this, "Cargando");
                mPhotoEditor.saveAsFile(getApplicationContext().getFilesDir().getPath() + "/photoEditor", new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        ArrayList<String> array = new ArrayList<>();

                        if (from == 0) {
                            String premium_post = Tools.putValue("{}", Tools.getString(R.string.name, getApplicationContext()), "post");
                            premium_post = Tools.putValue(premium_post, "str", Tools.getStringImg(Tools.scaleImage(Uri.fromFile(new File(imagePath)), 800, EditImage.this)));
                            array.add(premium_post);
                        } else {
                            if (from == 1) {
                                array.addAll(getProfilePhotos(imagePath));
                            } else {
                                array.addAll(getPublicPhoto(imagePath, (from - 1) + ""));
                            }
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                storePhoto(array.toString(), from);
                            }
                        }).start();


                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        loadingDialog.dismiss();
                        Toast.makeText(EditImage.this, "Guardado fallido", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (editMotion.getCurrentState() == R.id.end || editMotion.getCurrentState() == R.id.filters || editMotion.getCurrentState() == R.id.coment)
            editMotion.transitionToState(R.id.start);
        else {
            super.onBackPressed();
            finish();
        }
    }


    private void selected(int s, int idTransition) {

        for (int i = 1; i < buttons.size(); i++) {
            if (i == s)
                buttons.get(i).setColorFilter(Color.parseColor("#ff0046"));
            else
                buttons.get(i).setColorFilter(Color.BLACK);

        }

        editMotion.transitionToState(idTransition);

    }

    private void selectedColor(int s) {

        for (int i = 0; i < colors.size(); i++) {
            if (i == s)
                colors.get(i).setImageResource(R.drawable.ic_round_done_24);
            else
                colors.get(i).setImageDrawable(null);

        }


    }


    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);

    }


    private void storePhoto(String str, int from) {
        if (Tools.isConnected(EditImage.this)) {
            String myAddress = Tools.getAddress(Profile.getServer(), getApplicationContext());
            CRequest request = new CRequest(EditImage.this, Tools.getString(R.string.http, getApplicationContext()) + myAddress + "/" + storePhoto(),
                    30);
            ArrayList<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(Tools.getString(R.string.id, getApplicationContext()), Profile.getId()));
            pairs.add(new Pair<>(Tools.getString(R.string.auth, getApplicationContext()), photoPass()));
            pairs.add(new Pair<>(Tools.getString(R.string.images, getApplicationContext()), str));
            request.makeRequest(pairs);
            request.setOnResponseListener(new CRequest.OnResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.d("Response", "rsp" + response);
                    if (response.trim().isEmpty()) {
                        if (from > 0) {
                            if (!Profile.isPhotoUpLoad()) {
                                Profile.setData(Tools.getString(R.string.photo_upload, getApplicationContext()), 1 + "", getApplicationContext());
                                Profile.setData(Tools.getString(R.string.cant_photos, getApplicationContext()), Profile.getCantPublicPhotos() + 1 + "", getApplicationContext());
                                EventBus.getDefault().post(new UpdateProfilePhotoEvent());
                                EventBus.getDefault().post(new UpdatePublicPhotoEvent(true));
                                AppHandler.checkSetupIsComplete(getApplicationContext());
                                Log.d("PhotoCompleted", "yes");
                                finish();
                                return;

                            }
                            if (from == 1) {
                                EventBus.getDefault().post(new UpdateProfilePhotoEvent());
                            } else {

                                if (Profile.getCantPublicPhotos() < from - 1) {
                                    Profile.setData(Tools.getString(R.string.cant_photos, getApplicationContext()), Profile.getCantPublicPhotos() + 1 + "", getApplicationContext());
                                }
                                EventBus.getDefault().post(new UpdatePublicPhotoEvent(true));

                            }
                            AppHandler.checkSetupIsComplete(getApplicationContext());
                            finish();

                        } else {

                            CRequest request = new CRequest(EditImage.this, insertPost(),
                                    20);
                            ArrayList<Pair<String, String>> pairs = new ArrayList<>();
                            pairs.add(new Pair<>(Tools.getString(R.string.user_id, getApplicationContext()), Profile.getId()));
                            pairs.add(new Pair<>(Tools.getString(R.string.auth, getApplicationContext()), commentPass()));
                            pairs.add(new Pair<>(Tools.getString(R.string.comment, getApplicationContext()), comment.getText().toString().trim()));
                            request.makeRequest(pairs);
                            request.setOnResponseListener(new CRequest.OnResponseListener() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.trim().isEmpty()) {
                                        finish();
                                        loadingDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    loadingDialog.dismiss();
                                    Toast.makeText(EditImage.this, "Lo sentimos, no se pudo guardar su post", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    } else
                        loadingDialog.dismiss();
                }

                @Override
                public void onError(String error) {
                    Log.d("ErrorImage", error);
                    loadingDialog.dismiss();
                    Log.d("Editing", "url" + request.getUrl() + " " + error);
                    Toast.makeText(EditImage.this, "Lo sentimos, no se pudo guardar su imagen", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            loadingDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Sin Conexión", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void centerCrop() {
        photoEditorView.getSource().setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(EditImage.this)
                .load(imageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(photoEditorView.getSource());
    }

    private ArrayList<String> getProfilePhotos(String imagePath) {
        ArrayList<String> array = new ArrayList<>();
        String profile_medium = Tools.putValue("{}", Tools.getString(R.string.name, getApplicationContext()), "profile_medium");
        profile_medium = Tools.putValue(profile_medium, "str", Tools.getStringImg(Tools.scaleImage(Uri.fromFile(new File(imagePath)), 800, EditImage.this)));
        array.add(profile_medium);
        String profile_low = Tools.putValue("{}", Tools.getString(R.string.name, getApplicationContext()), "profile_low");
        profile_low = Tools.putValue(profile_low, "str", Tools.getStringImg(Tools.scaleImage(Uri.fromFile(new File(imagePath)), 200, EditImage.this)));
        array.add(profile_low);
        return array;
    }

    private ArrayList<String> getPublicPhoto(String imagePath, String no) {
        ArrayList<String> array = new ArrayList<>();
        String public_photo = Tools.putValue("{}", Tools.getString(R.string.name, getApplicationContext()), "public" + no);
        public_photo = Tools.putValue(public_photo, "str", Tools.getStringImg(Tools.scaleImage(Uri.fromFile(new File(imagePath)), 800, EditImage.this)));
        array.add(public_photo);
        return array;
    }

    public void back(View view) {
        onBackPressed();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void serviceShutDown(ServiceShutDownEvent event) {
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    if (isKeyboardVisible)
                        KeyboardUtils.forceCloseKeyboard(EditImage.this.getCurrentFocus());
                    v.clearFocus();

                }
            }

        }
        return super.dispatchTouchEvent(event);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cropImageEvent(CropImageEvent event) {
        imageUri = Uri.fromFile(new File(event.getPath()));
        centerCrop();
    }

}