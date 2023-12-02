package com.insightdev.both;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yashoid.instacropper.InstaCropperView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

public class PickImage extends AppCompatActivity {

    private static final int REQUEST_CHOOSE_IMAGE = 1002;
    private RecyclerView gallery;
    private GridLayoutManager gridLayoutManager;
    private GalleryAdapter galleryAdapter;
    private boolean isSelectedImage = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    static public Uri imageUri;

    FolderSelectorDialog.ActionInterface actionInterface;

    boolean adjustIsPosible = true;

    static boolean multipleSelection = false;
    public static DisplayMetrics displayMetrics;
    public static int galleryHeight, screenHeight;
    public static int fiveDPtoPxl;
    private int from;
    private boolean multiFlag, updateFlag;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    InstaCropperView instaCropperView;

    ImageView cropImg, mSelection, backButton;

    LinearLayout crop;
    int picturesLimit = 5;


    MotionLayout motionLayout, baseMotion;
    boolean is1x1 = true;
    CircularProgressIndicator indicator;

    static public ArrayList<PictureFacer> galleryItems = new ArrayList<>();

    ArrayList<ImageFolder> picFolders = new ArrayList<>();

    TextView folderName;

    ConstraintLayout folderButton;

    CustomViewPager cropPager;

    Uri actualUri = null;

    CropPagerAdapter cropPagerAdapter;

    ArrayList<Uri> uris = new ArrayList<>();

    TextView title, ok;
    RecyclerView.SmoothScroller smoothScroller;

    View guide1;

    int cantSelected = 0, lastPosition = 1;

    boolean readytSave = false;

    final float landscapeRatio = 12F / 9F, portraitRatio = 9F / 15F, portraitRatioPost = 9F / 13F, defaultRatio = 1F / 1.05F;
    static float actualRatio;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image);

        folderButton = findViewById(R.id.folderButton);

        gallery = findViewById(R.id.gal);

        actionInterface = new FolderSelectorDialog.ActionInterface() {
            @Override
            public void methodOk(int pos) {
                selectFolder(pos);
            }
        };

        folderName = findViewById(R.id.folderName);


        crop = findViewById(R.id.cropButtLayout);

        cropImg = findViewById(R.id.crop);

        guide1 = findViewById(R.id.guide1);

        mSelection = findViewById(R.id.mSelection);

        motionLayout = findViewById(R.id.croperLayout);

        baseMotion = findViewById(R.id.base);

        ok = findViewById(R.id.ok);

        indicator = findViewById(R.id.indicator);

        indicator.hide();

        cropPager = findViewById(R.id.instacropperPager);

        cropPager.setOffscreenPageLimit(picturesLimit);

        instaCropperView = findViewById(R.id.instacropperview);

        title = findViewById(R.id.title);

        backButton = findViewById(R.id.back);

        Bundle bundle = getIntent().getExtras();

        from = bundle.getInt("from");
        multiFlag = bundle.getBoolean("multi");
        updateFlag = bundle.getBoolean("update");

        if (from == 0) {

            adjustIsPosible = false;

            goToPortrait();

            crop.setVisibility(View.GONE);

        } else {

            adjustIsPosible = false;

            goToPortrait();

            crop.setVisibility(View.GONE);

            picturesLimit = 5 - Profile.getCantPublicPhotos();

        }

        if (!multiFlag)
            mSelection.setVisibility(View.INVISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        putAspectRatio(guide1, "9:15");

        displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenHeight = displayMetrics.heightPixels;

        galleryHeight = (displayMetrics.widthPixels - (4 * Tools.dpIntoPx(7, this)));

        fiveDPtoPxl = Tools.dpIntoPx(1, this);

        gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);

        gallery.setLayoutManager(gridLayoutManager);

        galleryAdapter = new GalleryAdapter(galleryItems, this);

        gallery.setAdapter(galleryAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadFirstImages();
            }
        }).start();


        folderButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                new FolderSelectorDialog().showDialog(PickImage.this, picFolders, actionInterface);
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (multipleSelection && uris.size() != 0) {
                    getBitmaps();
                } else
                    saveBitmap(lastPosition);
            }
        });


        //instaCropperView.setRatios(actualRatio, actualRatio, actualRatio);


        mSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (multipleSelection) {

                    cantSelected = 0;

                    readytSave = false;

                    title.setText("Foto de perfil");

                    if (cropPager.getVisibility() == View.VISIBLE)
                        disableMulti();

                    for (int i = 0; i < galleryItems.size(); i++)
                        galleryItems.get(i).setSelected(false);

                    cropImg.setVisibility(View.VISIBLE);

                } else {

                    title.setText(cantSelected + " Seleccionadas");

                    cropImg.setVisibility(View.GONE);

                }

                multipleSelection = !multipleSelection;

                Tools.bitmaps.clear();


                galleryAdapter.notifyDataSetChanged();


            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (is1x1) {

                    indicator.show();
                    adjustImageCroperRatio(actualUri);

                  /*  instaCropperView.setRatios(13F / 9F, 13F / 9F, 13F / 9F);
                    motionLayout.transitionToState(R.id.view916);*/
                } else {

                    actualRatio = 1F / 1.05F;
                    instaCropperView.setRatios(actualRatio, actualRatio, actualRatio);
                    motionLayout.transitionToState(R.id.start);
                }


                is1x1 = !is1x1;
            }

        });


//        actualUri = Uri.fromFile(new File(arrPath[arrPath.length - 1]));
        selectImage();

        galleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {

                if (position == 0)
                    startCameraIntentForResult();

                else {

                    actualUri = galleryItems.get(position).getImageUri();

                    if (multipleSelection) {

                        if (!galleryItems.get(position).getSelected()) {
                            if (uris.size() == picturesLimit) {
                                ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "El límite es de 5 fotos", Toast.LENGTH_LONG);
                                return;
                            }


                            galleryItems.get(position).setSelected(true);
                            title.setText(++cantSelected + " Seleccionadas");

                            uris.add(0, actualUri);

                            if (baseMotion.getCurrentState() != R.id.start)
                                baseMotion.transitionToState(R.id.start);

                            if (cropPager.getVisibility() == View.GONE)
                                enableMulti();
                            else if (cropPager.getVisibility() == View.INVISIBLE) {
                                instaCropperView.setVisibility(View.INVISIBLE);

                                cropPager.setVisibility(View.VISIBLE);

                            }


                            if (cropPagerAdapter != null)
                                cropPagerAdapter.notifyDataSetChanged();

                            //cropPager.setCurrentItem(uris.size() - 1, false);

                            gallery.post(new Runnable() {
                                @Override
                                public void run() {
                                    Tools.smoothScroll(gallery, position, 300);
                                }
                            });


                        } else {

                            remove(galleryItems.get(position).getImageUri());
                            cropPagerAdapter.notifyDataSetChanged();
                            //cropPager.setCurrentItem(uris.size() - 1, false);

                            galleryItems.get(position).setSelected(false);
                            title.setText(--cantSelected + " Seleccionadas");
                        }


                    } else {

                        selectImage();

                        gallery.post(new Runnable() {
                            @Override
                            public void run() {
                                Tools.smoothScroll(gallery, position, 300);
                            }
                        });
                    }
                    lastPosition = position;


                }


            }


        });

        EventBus.getDefault().register(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Tools.bitmaps.clear();
    }


    private void enableMulti() {

        Log.d("alkwkjda", "enable");

        cropPagerAdapter = new CropPagerAdapter(this, uris);

        cropPager.setAdapter(cropPagerAdapter);

        instaCropperView.setVisibility(View.INVISIBLE);

        cropPager.setVisibility(View.VISIBLE);
    }

    private void disableMulti() {


        cropPager.setVisibility(View.INVISIBLE);

        uris.clear();

        if (cropPagerAdapter != null) {
            CropPagerAdapter.cropperViews.clear();
            Log.d("nalskdansñmlda", "desotry to -1 ");
            cropPagerAdapter.notifyDataSetChanged();
            CropPagerAdapter.lastDestroy = -1;
        }
        if (instaCropperView.getVisibility() != View.VISIBLE) {
            selectImage();
            instaCropperView.setVisibility(View.VISIBLE);
        }

    }

    private void disableInteraction() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void enableInteraction() {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            actualUri = imageUri;
            //selectImage();

            instaCropperView.setImageUri(actualUri);
            galleryItems.add(1, new PictureFacer());
            galleryItems.get(1).setImageUri(actualUri);

            if (multipleSelection) {


                if (uris.size() == picturesLimit) {
                    ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "El límite es de 5 fotos", Toast.LENGTH_LONG);
                    return;
                }


                galleryItems.get(1).setSelected(true);
                title.setText(++cantSelected + " Seleccionadas");

                uris.add(0, actualUri);

                if (baseMotion.getCurrentState() != R.id.start)
                    baseMotion.transitionToState(R.id.start);

                if (cropPager.getVisibility() == View.GONE)
                    enableMulti();
                else if (cropPager.getVisibility() == View.INVISIBLE) {
                    instaCropperView.setVisibility(View.INVISIBLE);

                    cropPager.setVisibility(View.VISIBLE);

                }


                if (cropPagerAdapter != null)
                    cropPagerAdapter.notifyDataSetChanged();

                //cropPager.setCurrentItem(uris.size() - 1, false);

                gallery.post(new Runnable() {
                    @Override
                    public void run() {
                        Tools.smoothScroll(gallery, 1, 300);
                    }
                });


            }
            galleryAdapter.notifyDataSetChanged();

        }
    }

    void selectFolder(int position) {

        galleryItems.clear();

        folderName.setText(picFolders.get(picFolders.size() - position - 1).getFolderName());

        galleryItems = getAllImagesByFolder(picFolders.get(picFolders.size() - position - 1).getPath());

        actualUri = galleryItems.get(0).getImageUri();

        instaCropperView.setImageUri(actualUri);

        galleryItems.add(0, galleryItems.get(0));


        galleryAdapter.notifyDataSetChanged();


        if (baseMotion.getCurrentState() != R.id.start)
            baseMotion.transitionToState(R.id.start);

        gallery.smoothScrollToPosition(0);


    }

    public void adjustImageCroperRatio(Uri uri) {

        if (!adjustIsPosible)
            return;

        disableInteraction();

        new Thread(new Runnable() {
            @Override
            public void run() {

                float ratio = 1F;
                Bitmap bitmap = null;
                ContentResolver contentResolver = getContentResolver();
                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bitmap != null)
                    ratio = (float) bitmap.getWidth() / bitmap.getHeight();

                if (ratio > 1F && motionLayout.getCurrentState() != R.id.view916 && !is1x1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            actualRatio = landscapeRatio;
                            instaCropperView.setRatios(actualRatio, actualRatio, actualRatio);
                            motionLayout.transitionToState(R.id.view916);

                        }
                    });

                } else if (ratio < 1F && motionLayout.getCurrentState() != R.id.view169 && !is1x1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goToPortrait();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indicator.hide();
                        instaCropperView.setImageUri(actualUri);
                        enableInteraction();
                    }
                });


            }
        }).start();


    }

    @Override
    public void onLowMemory() {
        Glide.get(getApplicationContext()).clearMemory();
        super.onLowMemory();

    }

    @Override
    public void onBackPressed() {
        if (baseMotion.getCurrentState() != R.id.start) {
            baseMotion.transitionToState(R.id.start);
            return;
        }

        if (multipleSelection)
            mSelection.performClick();
        else
            super.onBackPressed();
    }

    public void startCameraIntentForResult() {
        // Clean up last time's image
        imageUri = null;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            values.put(MediaStore.Images.Media.TITLE, "Both Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            String sentence = MediaStore.Images.Media.TITLE + " = ? AND " + MediaStore.Images.Media.DESCRIPTION + " = ?";
            String[] args = new String[]{"Both Picture", "From Camera"};
            getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, sentence, args);
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            isSelectedImage = true;

        }
    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (multipleSelection)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Imagenes"), REQUEST_CHOOSE_IMAGE);
        isSelectedImage = true;
    }


    private void selectImage() {

        if (baseMotion.getCurrentState() != R.id.start)
            baseMotion.transitionToState(R.id.start);


        if (!is1x1) {
            indicator.show();
            adjustImageCroperRatio(actualUri);
        } else
            instaCropperView.setImageUri(actualUri);


    }


    private void saveBitmap(int position) {


        indicator.show();

        instaCropperView.crop(800, 1200, new InstaCropperView.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                Log.d("laksdm", position + " ");
                Tools.bitmaps.add(bitmap);
                indicator.hide();
                passToFilterActivity();


            }
        });


    }


    private void getPicturePaths() {

        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = this.getContentResolver().query(allImagesuri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do {
                ImageFolder folds = new ImageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
                folderpaths = folderpaths + folder + "/";
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics();
                    picFolders.add(folds);
                } else {
                    for (int i = 0; i < picFolders.size(); i++) {
                        if (picFolders.get(i).getPath().equals(folderpaths)) {
                            picFolders.get(i).setFirstPic(datapath);
                            picFolders.get(i).addpics();
                        }
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < picFolders.size(); i++) {
            Log.d("picture folders", picFolders.get(i).getFolderName() + " and path = " + picFolders.get(i).getPath() + " " + picFolders.get(i).getNumberOfPics());
        }

    }

    public ArrayList<PictureFacer> getAllImagesByFolder(String path) {
        ArrayList<PictureFacer> images = new ArrayList<>();

        images.clear();
        Uri allVideosuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = getContentResolver().query(allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[]{"%" + path + "%"}, null);
        try {
            cursor.moveToFirst();
            do {
                PictureFacer pic = new PictureFacer();

                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                images.add(pic);
            } while (cursor.moveToNext());
            cursor.close();
            ArrayList<PictureFacer> reSelection = new ArrayList<>();
            for (int i = images.size() - 1; i > -1; i--) {
                reSelection.add(images.get(i));
            }
            images = reSelection;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    public void loadFirstImages() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);

        int count = cursor.getCount();


        String[] arrPath = new String[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = cursor.getString(dataColumnIndex);
        }

        cursor.close();
        if (arrPath.length > 0) {
            galleryItems.add(new PictureFacer("", arrPath[0], ""));
            if (arrPath.length > 1) {
                for (int i = arrPath.length - 1; i > -1; i--) {
                    galleryItems.add(new PictureFacer("", arrPath[i], ""));
                }

            }

        }

        if (galleryItems.size() > 1)
            actualUri = galleryItems.get(1).getImageUri();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instaCropperView.setImageUri(actualUri);
                galleryAdapter.notifyDataSetChanged();
            }
        });


        getPicturePaths();
    }

    void putAspectRatio(View view, String dimensionRatio) {

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

        layoutParams.dimensionRatio = dimensionRatio;

        view.setLayoutParams(layoutParams);
    }


    private void getBitmaps() {

        indicator.show();


        Log.d("alkwkjda", "-----");
        Log.d("alkwkjda", CropPagerAdapter.cropperViews.get(0) + "");

        for (int i = 0; i < CropPagerAdapter.cropperViews.size(); i++) {
            Log.d("alkwkjda", "wtf" + CropPagerAdapter.cropperViews.get(i));


            CropPagerAdapter.cropperViews.get(i).crop(800, 1200, new InstaCropperView.BitmapCallback() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {

                    Tools.bitmaps.add(bitmap);


                    if (Tools.bitmaps.size() == CropPagerAdapter.cropperViews.size()) {
                        indicator.hide();
                        passToFilterActivity();
                    }
                }
            });
        }
    }

    private void passToFilterActivity() {
        Intent intent = new Intent(PickImage.this, FilterActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("update", updateFlag);
        startActivity(intent);
    }

    void remove(Uri uri) {

        Log.d("jasñdasd", CropPagerAdapter.cropperViews.size() + "");
        for (int i = 0; i < uris.size(); i++)
            if (uris.get(i) == uri) {
                uris.remove(i);
                CropPagerAdapter.cropperViews.remove(CropPagerAdapter.cropperViews.size() - 1 - i);
            }
    }

    private void goToPortrait() {
        actualRatio = portraitRatio;
        instaCropperView.setRatios(actualRatio, actualRatio, actualRatio);
        motionLayout.transitionToState(R.id.view169);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeActivity(ClosePickImageEvent event) {

        Log.d("naklsdmad", "finish act");
        if (multipleSelection)
            mSelection.performClick();
        finish();

    }

}