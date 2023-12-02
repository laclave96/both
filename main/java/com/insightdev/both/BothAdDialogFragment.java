package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class BothAdDialogFragment extends BottomSheetDialogFragment {

    BothAdModel bothAdModel;


    public BothAdDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View dialog = inflater.inflate(R.layout.both_ad_dialog, container, false);

        ImageView close = dialog.findViewById(R.id.close);

        SimpleDraweeView simpleDraweeView = dialog.findViewById(R.id.image);

        bothAdModel = new Gson().fromJson(getArguments().getString("adModel"), BothAdModel.class);


        // Uri lowResUri = Uri.parse("http://173.249.52.47/jqhneTWEQSDMSAugatysd5623hcbaI127EMLaWVKJ512xsHQasd/20/profile_low.jpeg");
        //Uri highResUri = Uri.parse("http://173.249.52.47/jqhneTWEQSDMSAugatysd5623hcbaI127EMLaWVKJ512xsHQasd/20/public1.jpeg");

        // bothAdModel=new BothAdModel(lowResUri, highResUri,"");

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(Uri.parse(bothAdModel.getLowResUri())))
                .setImageRequest(ImageRequest.fromUri(Uri.parse(bothAdModel.getHighResUri())))
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);

        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bothAdModel.isHasWebLink())
                    MainActivity.openPremiumFragment();
                else
                    openWebview(bothAdModel.getLink());

                dismiss();

            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Tools.saveSettings("new_both_ad", "", getContext());
            }
        }).start();

        return dialog;
    }


    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                //setupFullHeight(d);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public void onCancel(@NonNull @NotNull DialogInterface dialog) {
        dialog.dismiss();
        super.onCancel(dialog);
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels - 100;
    }

    private void openWebview(String url) {

        WebviewDialogFragment profileDialogFragment = new WebviewDialogFragment();

        if (profileDialogFragment.isAdded())
            return;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        profileDialogFragment.setArguments(bundle);

        profileDialogFragment.show((getActivity()).getSupportFragmentManager(), "WebviewDialogFragment");
    }
}
