package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class OrientationDialog {

    AnimatedCheckBox animatedCheckBox;
    TextView textCheck;

    @SuppressLint("ClickableViewAccessibility")
    public void showDialog(Activity activity, TextView textOrientation) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.orientacion_dialog);

        animatedCheckBox = dialog.findViewById(R.id.check1);
        textCheck = dialog.findViewById(R.id.showOrientation);

        animatedCheckBox.setChecked(Profile.isShowOrientation());

        ListView listView = dialog.findViewById(R.id.list);

        EditText searchView = dialog.findViewById(R.id.searchView);
        ImageView icon = dialog.findViewById(R.id.iconSearch);

        ArrayAdapter<String> arr;
        ImageView close=dialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        arr = new ArrayAdapter<String>(
                activity.getApplicationContext(),
                R.layout.string_layout,
                activity.getResources().getStringArray(R.array.sexs_list)
        );

        listView.setAdapter(arr);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = view.findViewById(R.id.text);

                String orientation = textView.getText().toString();

                int pos = Tools.getPosOfInStringArray(activity.getResources().getStringArray(R.array.sexs_list), orientation);

                if (pos == -1)
                    return;

                textOrientation.setText(orientation);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.orientation,activity),pos+"",activity);
                    }
                }).start();
                dialog.dismiss();

            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    icon.startAnimation(AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.fade_off));
                    icon.setVisibility(View.INVISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            icon.setImageResource(R.drawable.ic_round_close_24);
                        }
                    }, 300);
                }
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arr.getFilter().filter(s);
                if (s.toString().length() > 0 & icon.getVisibility() == View.INVISIBLE) {

                    icon.startAnimation(AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.fade_in));
                    icon.setVisibility(View.VISIBLE);

                } else if (s.toString().length() == 0 & icon.getVisibility() == View.VISIBLE) {

                    icon.startAnimation(AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.fade_off));
                    icon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        textCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatedCheckBox.setChecked(!animatedCheckBox.isChecked(), true);
                int id = animatedCheckBox.isChecked()?1:0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.show_orientation,activity),id+"",activity);
                    }
                }).start();


            }
        });

        animatedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setText("");
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
