package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

public class TermsDialog {

    Activity activity;

    public void showDialog(Activity activity,String sourceSignup) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.term_dialog);
        TextView agree = dialog.findViewById(R.id.goPrem);
        ImageView close = dialog.findViewById(R.id.close);
        customTextView(dialog.findViewById(R.id.exp1));


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new TermsAcceptedEvent(sourceSignup));
                dialog.dismiss();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

    }

    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "Es necesario que acepte nuestra ");
        spanTxt.append("Política de privacidad");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Tools.getPrivacyUrl(activity).isEmpty()) {
                    if (!Tools.isConnected(activity)) {
                        Toast.makeText(activity, "Sin Conexión", Toast.LENGTH_SHORT).show();
                    } else {
                        AppHandler.loadConfig(activity);
                        Toast.makeText(activity, "Sincronizando datos, espere un momento por favor", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                activity. startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Tools.getPrivacyUrl(activity))));

            }
        }, spanTxt.length() - "Política de privacidad".length(), spanTxt.length(), 0);
        spanTxt.append(" y ");
        //spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 32, spanTxt.length(), 0);
        spanTxt.append("Términos de uso");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Tools.getTermsUrl(activity).isEmpty()) {
                    if (!Tools.isConnected(activity)) {
                        Toast.makeText(activity, "Sin Conexión", Toast.LENGTH_SHORT).show();
                    } else {
                        AppHandler.loadConfig(activity);
                        Toast.makeText(activity, "Sincronizando datos, espere un momento por favor", Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Tools.getTermsUrl(activity))));

            }
        }, spanTxt.length() - "Términos de uso".length(), spanTxt.length(), 0);
        spanTxt.append(" para continuar.");
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }
}
