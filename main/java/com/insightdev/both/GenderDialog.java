package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.animsh.animatedcheckbox.AnimatedCheckBox;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

public class GenderDialog {


    @SuppressLint("ClickableViewAccessibility")
    public void showDialog(Activity activity, TextView textGender) {

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.gender_dialog);

        LinearLayout linearLayout = dialog.findViewById(R.id.notify);

        TransitionDrawable transitionDrawable = (TransitionDrawable) linearLayout.getBackground();

        ListView listView = dialog.findViewById(R.id.list);

        ImageView icon = dialog.findViewById(R.id.iconSearch);

        TextView men = dialog.findViewById(R.id.men);
        TextView woman = dialog.findViewById(R.id.woman);
        ImageView close = dialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        AnimatedCheckBox menCheck = dialog.findViewById(R.id.check1);
        AnimatedCheckBox womanCheck = dialog.findViewById(R.id.check2);

        //  listView.getLayoutParams().height = (int) (MainActivity.screenHeight * 0.75);
        EditText searchView = dialog.findViewById(R.id.searchView);

        ArrayAdapter<String> arr;

        String search_by = Profile.getSearchBy();
        String gender = Profile.getGender();
        if (!search_by.isEmpty() && !gender.isEmpty()) {
            if (search_by.contentEquals("0"))
                womanCheck.setChecked(true, false);
            else
                menCheck.setChecked(true, false);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    transitionDrawable.startTransition(500);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            transitionDrawable.reverseTransition(500);
                        }
                    }, 1000);
                }
            }, 1000);
        }

        men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = menCheck.isChecked();
                menCheck.setChecked(!isChecked, true);
                if (!isChecked) {
                    womanCheck.setChecked(false, false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Profile.setData(Tools.getString(R.string.search_by, activity), "1", activity);
                            if (Profile.getGender().isEmpty() || Profile.getGender().contentEquals("1"))
                                Profile.setData(Tools.getString(R.string.gender, activity), "0", activity);
                            AppHandler.checkSetupIsComplete(activity);
                            EventBus.getDefault().post(new BasicInfoEvent());
                        }
                    }).start();
                }
            }
        });

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = womanCheck.isChecked();
                womanCheck.setChecked(!isChecked, true);
                if (!isChecked) {
                    menCheck.setChecked(false, false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Profile.setData(Tools.getString(R.string.search_by, activity), "0", activity);
                            if (Profile.getGender().isEmpty() || Profile.getGender().contentEquals("0"))
                                Profile.setData(Tools.getString(R.string.gender, activity), "1", activity);
                            AppHandler.checkSetupIsComplete(activity);
                            EventBus.getDefault().post(new BasicInfoEvent());
                        }
                    }).start();
                }
            }
        });

        menCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        womanCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        arr = new ArrayAdapter<String>(
                activity.getApplicationContext(),
                R.layout.string_layout,
                activity.getResources().getStringArray(R.array.genders_list)
        );

        listView.setAdapter(arr);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = view.findViewById(R.id.text);

                String gender = textView.getText().toString();

                int pos = Tools.getPosOfInStringArray(activity.getResources().getStringArray(R.array.genders_list), gender);

                if (pos == -1)
                    return;
                Log.d("aslkdnadadasd", " " + pos);
                textGender.setText(gender);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.gender, activity), pos + "", activity);
                        if (pos == 0)
                            Profile.setData(Tools.getString(R.string.search_by, activity), "1", activity);
                        else if (pos == 1)
                            Profile.setData(Tools.getString(R.string.search_by, activity), "0", activity);
                        AppHandler.checkSetupIsComplete(activity);
                        EventBus.getDefault().post(new BasicInfoEvent());
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

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setText("");
            }
        });
        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = MainActivity.screenHeight;
        bottomSheet.setLayoutParams(layoutParams);
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
