package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;

public class HottestFilterDialog {

    private Activity activity;

    public interface ActionInterface {
        void selection(int gender, int time);
    }

    int actualTime;

    int actualGender;

    ActionInterface actionInterface;

    public void showDialog(Activity activity, ActionInterface actionInterface, int gender, int time) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.hottest_filter_dialog);


        TextView apply = dialog.findViewById(R.id.nextButton);

        ThemedToggleButtonGroup themedButtonGroup = dialog.findViewById(R.id.genderTags);

        SegmentedButtonGroup buttonGroup = dialog.findViewById(R.id.buttonGroup);

        buttonGroup.setPosition(gender, false);

        selectTime(time, themedButtonGroup);


        actualGender = gender;

        actualTime = time;

        buttonGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                actualGender = position;
            }
        });


        themedButtonGroup.setOnSelectListener((ThemedButton btn) -> {
            // handle selected button

            Log.d("jñalsdkaf", (btn.getId() == R.id.tag1) + " ");

            switch (btn.getId()) {

                case R.id.tag1:
                    actualTime = 1;
                    break;

                case R.id.tag2:
                    actualTime = 7;
                    break;

                case R.id.tag3:
                    actualTime = 30;
                    break;

                default:
                    actualTime = 365;
                    break;

            }
            return kotlin.Unit.INSTANCE;
        });


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionInterface.selection(actualGender, actualTime);
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

    private void selectTime(int pos, ThemedToggleButtonGroup themedButtonGroup) {
        switch (pos) {
            case 1:
                themedButtonGroup.selectButton(R.id.tag1);
                break;
            case 7:
                themedButtonGroup.selectButton(R.id.tag2);
                break;
            case 30:
                themedButtonGroup.selectButton(R.id.tag3);
                break;
            case 365:
                themedButtonGroup.selectButton(R.id.tag4);
                break;


        }

    }
}
