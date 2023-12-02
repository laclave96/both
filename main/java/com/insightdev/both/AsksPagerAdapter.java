package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.animsh.animatedcheckbox.AnimatedCheckBox;


import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AsksPagerAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<Ask> asks = new ArrayList<>();


    public AsksPagerAdapter(Activity activity, ArrayList<Ask> asks) {
        this.activity = activity;
        this.asks = asks;
    }

    @Override
    public int getCount() {
        return asks.size();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.ask_layout, null);
        AnimatedCheckBox checkBox1, checkBox2, checkBox3;
        TextView question = layoutScreen.findViewById(R.id.question);
        TextView answ1 = layoutScreen.findViewById(R.id.answ1);
        TextView answ2 = layoutScreen.findViewById(R.id.answ2);
        TextView answ3 = layoutScreen.findViewById(R.id.answ3);
        EditText editNewAnswer = layoutScreen.findViewById(R.id.editNewAnswer);
        ImageView imageView = layoutScreen.findViewById(R.id.image);

        question.setText(asks.get(position).getQuestion());

        answ1.setText(asks.get(position).getAnsw1());

        answ2.setText(asks.get(position).getAnsw2());

        answ3.setText(asks.get(position).getAnsw3());

        imageView.setImageResource(asks.get(position).getImage());


        checkBox1 = layoutScreen.findViewById(R.id.check1);
        checkBox2 = layoutScreen.findViewById(R.id.check2);
        checkBox3 = layoutScreen.findViewById(R.id.check3);

        String key;

        key = "question" + (position + 1);

        String survey = Profile.getSurvey();

        if (survey != null)
            if (!survey.isEmpty()) {

                String answer = Tools.getValue(survey, key);

                if (!answer.isEmpty()) {
                    if (answer.contentEquals("1"))
                        checkBox1.setChecked(true);
                    else if (answer.contentEquals("2"))
                        checkBox2.setChecked(true);
                    else if (answer.contentEquals("3"))
                        checkBox3.setChecked(true);
                    else
                        editNewAnswer.setText(answer);
                }
            }

        answ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isCheck = checkBox1.isChecked();
                checkBox1.setChecked(!isCheck, true);
                String answer = "";
                if (!isCheck) {
                    checkBox2.setChecked(false, false);
                    checkBox3.setChecked(false, false);
                    editNewAnswer.setText("");
                    answer = "1";
                }

                String strSurvey = Profile.getSurvey();
                if (strSurvey == null)
                    strSurvey = "{}";
                if (strSurvey.isEmpty())
                    strSurvey = "{}";
                String newJson = Tools.putValue(strSurvey, key, answer);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.survey, activity), newJson, activity);
                        EventBus.getDefault().post(new SurveyEvent());
                    }
                }).start();

            }
        });

        answ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox2.isChecked();
                checkBox2.setChecked(!isCheck, true);
                String answer = "";
                if (!isCheck) {
                    checkBox1.setChecked(false, false);
                    checkBox3.setChecked(false, false);
                    editNewAnswer.setText("");
                    answer = "2";
                }

                String strSurvey = Profile.getSurvey();
                if (strSurvey == null)
                    strSurvey = "{}";
                if (strSurvey.isEmpty())
                    strSurvey = "{}";
                String newJson = Tools.putValue(strSurvey, key, answer);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.survey, activity), newJson, activity);
                        EventBus.getDefault().post(new SurveyEvent());

                    }
                }).start();

            }
        });

        answ3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = checkBox3.isChecked();
                checkBox3.setChecked(!isCheck, true);
                String answer = "";
                if (!isCheck) {
                    checkBox2.setChecked(false, false);
                    checkBox1.setChecked(false, false);
                    editNewAnswer.setText("");
                    answer = "3";
                }

                String strSurvey = Profile.getSurvey();
                if (strSurvey == null)
                    strSurvey = "{}";
                if (strSurvey.isEmpty())
                    strSurvey = "{}";
                String newJson = Tools.putValue(strSurvey, key, answer);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.survey, activity), newJson, activity);
                        EventBus.getDefault().post(new SurveyEvent());

                    }
                }).start();
            }
        });

        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        editNewAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() > 0) {
                    checkBox1.setChecked(false, false);
                    checkBox2.setChecked(false, false);
                    checkBox3.setChecked(false, false);

                    String strSurvey = Profile.getSurvey();
                    if (strSurvey == null)
                        strSurvey = "{}";
                    if (strSurvey.isEmpty())
                        strSurvey = "{}";
                    String newJson = Tools.putValue(strSurvey, key, s.toString().trim());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Profile.setData(Tools.getString(R.string.survey, activity), newJson, activity);
                            EventBus.getDefault().post(new SurveyEvent());

                        }
                    }).start();

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        container.addView(layoutScreen);

        return layoutScreen;


    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }

}
