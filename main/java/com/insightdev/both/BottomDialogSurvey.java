package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;


public class BottomDialogSurvey {

    public void showDialog(Activity activity) {
        AsksPagerAdapter asksPagerAdapter;
        ViewPager viewPager;
        ArrayList<Ask> asks = new ArrayList<>();
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.survey_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        String[] answers1 = activity.getResources().getStringArray(R.array.answers_question_1);
        String[] answers2 = activity.getResources().getStringArray(R.array.answers_question_2);
        String[] answers3 = activity.getResources().getStringArray(R.array.answers_question_3);
        String[] answers4 = activity.getResources().getStringArray(R.array.answers_question_4);

        asks.add(new Ask("Cuéntanos... ¿que piensas hacer por aquí?", answers1[0], answers1[1], answers1[2], R.drawable.ask_1));
        asks.add(new Ask("¿Qué te hace sentir orgulloso de tí mismo?", answers2[0], answers2[1], answers2[2], R.drawable.ask_2));
        asks.add(new Ask("¿Qué desearías que se te diera mejor?", answers3[0], answers3[1], answers3[2], R.drawable.ask_3));
        asks.add(new Ask("Si pudieras viajar a cualquier parte del mundo, ¿a dónde irías?", answers4[0], answers4[1], answers4[2], R.drawable.ask_4));


        viewPager = dialog.findViewById(R.id.pager);
        asksPagerAdapter = new AsksPagerAdapter(activity, asks);

        viewPager.setAdapter(asksPagerAdapter);


        TextView nextButton = dialog.findViewById(R.id.nextButton);
        TextView prevButton = dialog.findViewById(R.id.prevButton);

        viewPager.setPageTransformer(true, new AskSlowTransformer());

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    //title.setVisibility(View.INVISIBLE);
                }
                if (position == 0) {
                    prevButton.setVisibility(View.INVISIBLE);
                } else if (position == 1) {
                    prevButton.setVisibility(View.VISIBLE);
                }
                if (position == asks.size() - 1) {
                    nextButton.setText("Finalizar");
                } else if (position == asks.size() - 2) {
                    nextButton.setText("Siguiente");
                    nextButton.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextButton.getText().toString().contentEquals("Siguiente"))
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                else
                    dialog.dismiss();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

    }

}
