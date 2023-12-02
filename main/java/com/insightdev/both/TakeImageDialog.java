package com.insightdev.both;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TakeImageDialog {

    private Activity activity;
    private static final int REQUEST_CHOOSE_IMAGE = 1002;
    private RecyclerView gallery;
    private GalleryAdapter galleryAdapter;
    private boolean isSelectedImage = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    static public Uri imageUri;
    static public int from;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    int rotation;

    public void showDialog(Activity activity, int from) {
        this.activity = activity;
        TakeImageDialog.from = from;
        rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.take_image_dialog);

        View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

        gallery = dialog.findViewById(R.id.gal);

        ImageView close=dialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });



        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;

        Cursor cursor = activity.getContentResolver().query(
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
        ArrayList<String> newArr = new ArrayList<>();
        if (arrPath.length > 0) {
            newArr.add(arrPath[0]);
            if (arrPath.length > 1) {
                int stop = arrPath.length > 35 ? arrPath.length - 36 : -1;
                for (int i = arrPath.length - 1; i > stop; i--) {
                    newArr.add(arrPath[i]);
                }
            }
        }
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        gallery.setLayoutManager(gridLayoutManager);
      //  galleryAdapter = new GalleryAdapter(newArr, activity);


        gallery.setAdapter(galleryAdapter);
        gallery.getLayoutParams().height = (int) (MainActivity.screenHeight * 0.82);

        galleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {

                switch (position) {
                    case 0:
                        startCameraIntentForResult();
                        dialog.dismiss();
                        break;

                    case 20:
                        startChooseImageIntentForResult();
                        dialog.dismiss();
                        break;

                    default:
                        Intent intent = new Intent(activity, EditImage.class);
                        intent.putExtra("path", newArr.get(position));
                        intent.putExtra("from", from);
                        activity.startActivity(intent);
                        dialog.dismiss();
                        break;
                }


            }
        });


        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(MainActivity.screenHeight);
        dialog.show();


    }

    public void startCameraIntentForResult() {
        // Clean up last time's image
        imageUri = null;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            values.put(MediaStore.Images.Media.TITLE, "Both Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            String sentence = MediaStore.Images.Media.TITLE + " = ? AND " + MediaStore.Images.Media.DESCRIPTION + " = ?";
            String[] args = new String[]{"Both Picture", "From Camera"};
            activity.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, sentence, args);
            imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            isSelectedImage = true;

        }
    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), REQUEST_CHOOSE_IMAGE);
        isSelectedImage = true;
    }


}
