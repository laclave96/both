package com.insightdev.both;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class PricesDialog {

    ArrayList<ConstraintLayout> plans = new ArrayList<>();
    ArrayList<TextView> prices = new ArrayList<>();
    ArrayList<TextView> perMonth = new ArrayList<>();
    ArrayList<TextView> months = new ArrayList<>();
    TextView payButton;

    static final int RECEIVE_SMS = 0;
    PayDialog payDialog = new PayDialog();

    public void showDialog(Activity activity) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.precios_dialog);

        ConstraintLayout preciosLayout = dialog.findViewById(R.id.options);
        plans.clear();
        months.clear();
        prices.clear();
        perMonth.clear();

        final int[] cantTotal = {0};
        ConstraintLayout payment = dialog.findViewById(R.id.prices);
        ConstraintLayout promo = dialog.findViewById(R.id.promo);
        TextView nextButt = dialog.findViewById(R.id.nextButton);
        TextView title = dialog.findViewById(R.id.title);
        TextView info = dialog.findViewById(R.id.comment);
        TextView total = dialog.findViewById(R.id.total);
        TextView titlePago = dialog.findViewById(R.id.titlePago);
        TextView iPay = dialog.findViewById(R.id.iPay);
        TextView backButt = dialog.findViewById(R.id.backButton);

        payButton = dialog.findViewById(R.id.iPay);

        months.add(dialog.findViewById(R.id.m6));
        months.add(dialog.findViewById(R.id.m3));
        months.add(dialog.findViewById(R.id.m1));

        perMonth.add(dialog.findViewById(R.id.cup));
        perMonth.add(dialog.findViewById(R.id.cup3));
        perMonth.add(dialog.findViewById(R.id.cup1));

        prices.add(dialog.findViewById(R.id.n6));
        prices.add(dialog.findViewById(R.id.n3));
        prices.add(dialog.findViewById(R.id.n1));

        plans.add(dialog.findViewById(R.id.meses6));
        plans.add(dialog.findViewById(R.id.meses3));
        plans.add(dialog.findViewById(R.id.meses1));

        int[] p = Tools.getPremiumPrices(activity);
        cantTotal[0] = p[1] * 3;
        total.setText("Monto . . . . . . . . . " + cantTotal[0] + " CUP");
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            prices.get(i).setText("$" + p[i]);
            plans.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    plans.get(finalI).setBackgroundResource(R.drawable.back_15dp);
                    prices.get(finalI).setTextColor(Color.parseColor("#D4AF37"));
                    perMonth.get(finalI).setTextColor(Color.parseColor("#D4AF37"));
                    months.get(finalI).setTextColor(Color.parseColor("#D4AF37"));
                    setBlackExcept(finalI);
                    switch (finalI) {
                        default:
                            cantTotal[0] = p[finalI] * 6;
                            break;
                        case 1:
                            cantTotal[0] = p[finalI] * 3;
                            break;
                        case 2:
                            cantTotal[0] = p[finalI];
                            break;
                    }
                    total.setText("Monto . . . . . . . . . " + cantTotal[0] + " CUP");
                }
            });

        }


        nextButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButt.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                nextButt.setVisibility(View.INVISIBLE);
                preciosLayout.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                preciosLayout.setVisibility(View.INVISIBLE);
                title.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                title.setVisibility(View.INVISIBLE);
                info.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                info.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        titlePago.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        titlePago.setVisibility(View.VISIBLE);
                        total.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        total.setVisibility(View.VISIBLE);
                        iPay.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        iPay.setVisibility(View.VISIBLE);
                        backButt.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        backButt.setVisibility(View.VISIBLE);
                    }
                }, 50);


            }
        });

        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titlePago.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                titlePago.setVisibility(View.GONE);
                total.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                total.setVisibility(View.GONE);
                iPay.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                iPay.setVisibility(View.GONE);
                backButt.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                backButt.setVisibility(View.GONE);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextButt.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        nextButt.setVisibility(View.VISIBLE);
                        preciosLayout.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        preciosLayout.setVisibility(View.VISIBLE);
                        title.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        title.setVisibility(View.VISIBLE);
                        info.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        info.setVisibility(View.VISIBLE);
                    }
                }, 50);


            }
        });

        iPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS);
                    return;
                }
                payDialog.showDialog(activity, cantTotal[0]);
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

        if (Tools.isPaymentAvailable(activity)) {
            promo.setVisibility(View.GONE);
            payment.setVisibility(View.VISIBLE);
        } else {
            promo.setVisibility(View.VISIBLE);
            payment.setVisibility(View.INVISIBLE);
        }
    }

    public void setBlackExcept(int p) {
        for (int i = 0; i < 3; i++) {
            if (i != p) {
                plans.get(i).setBackground(null);
                prices.get(i).setTextColor(Color.BLACK);
                perMonth.get(i).setTextColor(Color.BLACK);
                months.get(i).setTextColor(Color.BLACK);
            }

        }

    }

}
