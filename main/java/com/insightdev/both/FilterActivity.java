package com.insightdev.both;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import net.alhazmy13.imagefilter.ImageFilter;
import net.gotev.uploadservice.data.UploadInfo;
import net.gotev.uploadservice.data.UploadNotificationConfig;
import net.gotev.uploadservice.data.UploadNotificationStatusConfig;
import net.gotev.uploadservice.network.ServerResponse;
import net.gotev.uploadservice.observer.request.RequestObserverDelegate;
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;


import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.PhotoFilter;
import kotlin.jvm.functions.Function2;

public class FilterActivity extends AppCompatActivity {

    static int imageWidht = 0, imageHeight = 0;

    DiscreteScrollView recyclerView;

    private MainFilterAdapter mainFilterAdapter;

    private FilterAdapter filterAdapter;

    TextView pagerIndicator;

    private RecyclerView filterRecylcer;

    LoadingDialog loadingDialog;

    ArrayList<FilterItem> filterItems = new ArrayList<>();

    ArrayList<FilterBitmap> mainfilterItems = new ArrayList<>();

    SnapHelper snapHelper;

    TextView save;
    ImageView backButton;

    int from;

    boolean updateFlag;
    //ArrayList<Bitmap> bitmaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        recyclerView = findViewById(R.id.mainRecycler);

        pagerIndicator = findViewById(R.id.pagerIndicator);

        filterRecylcer = findViewById(R.id.filterRecylcer);
        save = findViewById(R.id.ok);

        snapHelper = new LinearSnapHelper();

        snapHelper.attachToRecyclerView(filterRecylcer);

        Bundle bundle = getIntent().getExtras();

        backButton = findViewById(R.id.back);

        from = bundle.getInt("from");

        updateFlag = bundle.getBoolean("update");

        loadingDialog = new LoadingDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                initFilterArray();
            }
        }).start();


        initPhotoArray();

        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        filterRecylcer.setDrawingCacheEnabled(true);

        filterRecylcer.setItemViewCacheSize(10);


        filterRecylcer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        filterAdapter = new FilterAdapter(filterItems, this);

        filterRecylcer.setAdapter(filterAdapter);


        /*if (ratio == landscapeRatio)
            actualRatioS = "13:9";
        else if (ratio == portraitRatio)
            actualRatioS = "9:14";
        else if (ratio == portraitRatioPost)
            actualRatioS = "9:13";
        else if (ratio == defaultRatio)
            actualRatioS = "1:1.05";*/

        //  putAspectRatio(recyclerView, actualRatioS);

        mainFilterAdapter = new MainFilterAdapter(this, mainfilterItems);

        recyclerView.setAdapter(mainFilterAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        filterAdapter.setOnItemClickListener(new FilterAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {

                mainfilterItems.get(recyclerView.getCurrentItem()).setFilter(filterItems.get(position).getFilter());

                mainFilterAdapter.notifyItemChanged(recyclerView.getCurrentItem());

                //initPhotoArray(filterItems.get(position).getFilter());

            }
        });

        recyclerView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable @org.jetbrains.annotations.Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                pagerIndicator.setText((adapterPosition + 1) + "/" + mainfilterItems.size());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isConnected(FilterActivity.this)) {
                    ProToast.makeText(getApplicationContext(), R.drawable.toast_offline, "Revise su conexión a internet", Toast.LENGTH_SHORT);
                    return;

                }

                uploadMultipart();
            }
        });


    }

    void putAspectRatio(View view, String dimensionRatio) {

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

        layoutParams.dimensionRatio = dimensionRatio;

        view.setLayoutParams(layoutParams);
    }

    private void initFilterArray() {

        filterItems.clear();

        Bitmap bitmap = Tools.scaleBitmap(Tools.bitmaps.get(0), Tools.dpIntoPx(110, this));
        filterItems.add(new FilterItem(bitmap, "Ninguno", null));
        filterItems.add(new FilterItem(bitmap, "HDR", ImageFilter.Filter.HDR));
        filterItems.add(new FilterItem(bitmap, "Lomo", ImageFilter.Filter.LOMO));
        filterItems.add(new FilterItem(bitmap, "TV", ImageFilter.Filter.TV));
        filterItems.add(new FilterItem(bitmap, "Neon", ImageFilter.Filter.NEON));
        filterItems.add(new FilterItem(bitmap, "Old", ImageFilter.Filter.OLD));
        filterItems.add(new FilterItem(bitmap, "Sharpen", ImageFilter.Filter.SHARPEN));
        filterItems.add(new FilterItem(bitmap, "Motion Blur", ImageFilter.Filter.MOTION_BLUR));
        filterItems.add(new FilterItem(bitmap, "Sketch", ImageFilter.Filter.SKETCH));
        filterItems.add(new FilterItem(bitmap, "Gray", ImageFilter.Filter.GRAY));
        filterItems.add(new FilterItem(bitmap, "Blur", ImageFilter.Filter.GAUSSIAN_BLUR));


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filterAdapter.notifyDataSetChanged();
            }
        });


    }

    private void initPhotoArray() {
        mainfilterItems.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Tools.bitmaps.size(); i++) {

                    mainfilterItems.add(new FilterBitmap(Tools.bitmaps.get(i), null));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mainFilterAdapter != null) {
                            mainFilterAdapter.notifyDataSetChanged();
                            if (mainfilterItems.size() == 1)
                                pagerIndicator.setVisibility(View.GONE);
                            pagerIndicator.setText("1/" + mainfilterItems.size());
                        }
                    }
                });

            }
        }).start();

    }

    private void initPhotoArray(ImageFilter.Filter filter) {

        mainfilterItems.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Tools.bitmaps.size(); i++) {

                    if (imageHeight != 0)
                        mainfilterItems.add(new FilterBitmap(Tools.scaleBitmap(Tools.bitmaps.get(i),
                                Math.min(imageHeight, imageWidht)), filter));
                    else
                        mainfilterItems.add(new FilterBitmap(Tools.bitmaps.get(i), null));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mainFilterAdapter != null)
                            mainFilterAdapter.notifyDataSetChanged();
                    }
                });

            }
        }).start();


        //  mainFilterAdapter.notifyDataSetChanged();

    }

    public void uploadMultipart() {

        ArrayList<Uri> uris = new ArrayList<>();

        Uri uriAux;

        loadingDialog.showDialog(FilterActivity.this, "Subiendo");

        String upload_url = Tools.getString(R.string.http, getApplicationContext()) + "173.249.52.47" + "/" + "storePhotov2.1.php";

        int cant_photos = mainfilterItems.size();

        try {


            MultipartUploadRequest uploadRequest = new MultipartUploadRequest(this, upload_url)
                    .addParameter("id", Profile.getId()) //Adding text parameter to the request
                    .addParameter("cant", String.valueOf(cant_photos)) //Adding text parameter to the request
                    .addParameter("from_pos", String.valueOf(from))
                    .addParameter("update", updateFlag ? "1" : "0")
                    //.setNotificationConfig(MultiUploadUtils.createNotificationChannel(this))
                    .setMaxRetries(2);
            //.setAutoDeleteFilesAfterSuccessfulUpload(true);

            for (int i = 0; i < cant_photos; i++) {
                uriAux = MultiUploadUtils.getImageUri(this, Tools.scaleBitmap(mainfilterItems.get(i).getBitmap(), 900));
                uris.add(uriAux);

                uploadRequest.addFileToUpload(MultiUploadUtils.getRealPathFromURI(this, uriAux), String.valueOf(i + from));
            }


            uploadRequest.subscribe(this, this, new RequestObserverDelegate() {
                @Override
                public void onProgress(@NotNull Context context, @NotNull UploadInfo uploadInfo) {

                }

                @Override
                public void onSuccess(@NotNull Context context, @NotNull UploadInfo uploadInfo, @NotNull ServerResponse serverResponse) {

                    if (!Profile.isPhotoUpLoad()) {
                        Profile.setData(Tools.getString(R.string.photo_upload, getApplicationContext()), 1 + "", getApplicationContext());
                        Profile.setData(Tools.getString(R.string.cant_photos, getApplicationContext()), Profile.getCantPublicPhotos() + 1 + "", getApplicationContext());

                        AppHandler.checkSetupIsComplete(getApplicationContext());
                        Log.d("PhotoCompleted", "yes");


                    } else {

                        if (Profile.getCantPublicPhotos() < from) {

                            Log.d("aslkndasda", "wtf");
                            Profile.setCantPhotos(Profile.getCantPublicPhotos() + cant_photos);
                            Profile.updateProfileData(getApplicationContext());
                        }

                    }
                    EventBus.getDefault().post(new UpdateProfilePhotoEvent());

                    EventBus.getDefault().post(new UpdatePublicPhotoEvent(true));
                    AppHandler.checkSetupIsComplete(getApplicationContext());
                    loadingDialog.dismiss();


                }

                @Override
                public void onError(@NotNull Context context, @NotNull UploadInfo uploadInfo, @NotNull Throwable throwable) {
                    Log.d("Hlasjkdalñsdka", throwable.toString());
                    ProToast.makeText(getApplicationContext(), R.drawable.ic_info, "Ha ocurrido algún error, inténtelo más tarde", Toast.LENGTH_SHORT);


                }

                @Override
                public void onCompleted(@NotNull Context context, @NotNull UploadInfo uploadInfo) {

                    deleteImages(uris);

                    EventBus.getDefault().post(new ClosePickImageEvent());
                    loadingDialog.dismiss();
                    finish();
                }

                @Override
                public void onCompletedWhileNotObserving() {

                }
            });

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteImages(ArrayList<Uri> uris) {

        File fdelete;
        Log.d("anñslkdjañlsdka", uris.size() + "");

        for (int i = 0; i < uris.size(); i++) {
            Log.d("anñslkdjañlsdka", uris.get(i).getPath());
            fdelete = new File(uris.get(i).getPath());
            Log.d("anñslkdjañlsdka", fdelete.exists() + "");
            getContentResolver().delete(uris.get(i), null, null);
            if (fdelete.exists()) {
                if (fdelete.getAbsoluteFile().delete())
                    Log.d("anñslkdjañlsdka", "deleted");
                else
                    Log.d("anñslkdjañlsdka", "nop");
            }

        }


    }


}