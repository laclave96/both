package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

public class EditPostDialog {

    private Activity activity;

    public void showDialog(Activity activity, String path, String comment) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edit_post_dialog);

        final boolean[] singleLine = {true};
        TextView coment = dialog.findViewById(R.id.comment);


        TextView delete = dialog.findViewById(R.id.deletePostButt);
        TextView edit = dialog.findViewById(R.id.editPostButt);

        View view = dialog.findViewById(R.id.view);

        SimpleDraweeView image = dialog.findViewById(R.id.postImage);

        ImageView close = dialog.findViewById(R.id.close);

        ResizeOptions resizeOptions = new ResizeOptions(300, 300);

        if (!comment.isEmpty()) {
            coment.setVisibility(View.VISIBLE);
            coment.setText(comment);
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(path)))
                .disableMemoryCache()
                .setResizeOptions(resizeOptions)
                .setProgressiveRenderingEnabled(true)
                .disableDiskCache()
                .disableMemoryCache()
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //imagePipeline.clearCaches();
        imagePipeline.evictFromCache(Uri.fromFile(new File(path)));
        image.setImageURI(Uri.fromFile(new File(path)));


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isConnected(activity)) {
                    Toast.makeText(activity, "Sin Conexión", Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBus.getDefault().post(new LoadingShowEvent());
                AppHandler.deletePostMeth(activity);
                File fdelete = new File(path);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Log.d("alskdj","file Deleted :" + path);
                    } else {
                        Log.d("alskdj","file Not Deleted :" + path);
                    }
                }
                dialog.dismiss();
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditor(path, comment);
                dialog.dismiss();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditor(path, comment);
                dialog.dismiss();
            }
        });

        coment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (singleLine[0]) {
                    coment.setSingleLine(false);
                    singleLine[0] = false;
                } else {
                    coment.setSingleLine(true);
                    singleLine[0] = true;
                }
            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        bottomSheet.setLayoutParams(layoutParams);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        dialog.show();

    }

    private void openEditor(String path, String comment) {
        Intent intent = new Intent(activity, EditImage.class);
        intent.putExtra("path", path);
        intent.putExtra("from", 0);
        intent.putExtra("comment", comment);
        activity.startActivity(intent);
    }



}
