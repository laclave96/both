package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class InstagramAuthFragmentDialog extends BottomSheetDialogFragment {

    String redirect_url;
    String request_url;

    WebViewClient webViewClient;

    CircularProgressIndicator indicator;

    public interface ActionInterface {
        void codeRecived(String code);
    }

    ActionInterface actionInterface;
    ImageView close;

    public InstagramAuthFragmentDialog() {
        // Required empty public constructor
    }

    public InstagramAuthFragmentDialog(ActionInterface actionInterface) {
        // Required empty public constructor
        this.actionInterface = actionInterface;
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

        View dialog = inflater.inflate(R.layout.instagram_auth_dialog, container, false);

        this.redirect_url = getActivity().getResources().getString(R.string.callback_url);
        this.request_url = "https://api.instagram.com/oauth/authorize?client_id=548723226855110&client_secret=6d268297358fc687840cb938c1568715&redirect_uri=https://both.social/&scope=user_profile,user_media&response_type=code";

        close = dialog.findViewById(R.id.close);

        indicator = dialog.findViewById(R.id.indicator);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith(redirect_url + "?code=")) {

                    Uri uri = Uri.parse(url);
                    String codeEncoded = uri.getEncodedQuery();
                    String afterDecode = null;
                    try {
                        afterDecode = URLDecoder.decode(codeEncoded, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        return false;
                    }
                    String[] token = afterDecode.split("code=");
                    String codePart1 = token[1];
                    String[] codePart2 = codePart1.split("#_");
                    String code = codePart2[0];

                    actionInterface.codeRecived(code);
                    dismiss();
                    return true;
                }
                return false;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                indicator.hide();
                indicator.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }


        };

        WebView webView = dialog.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(request_url);
        webView.setWebViewClient(webViewClient);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


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

                setupFullHeight(d);
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
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
