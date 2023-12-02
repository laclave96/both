
package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class OccupationDialog {
    public void showDialog(Activity activity, TextView textOccupation) {
        final short[] id = new short[1];
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.occupation_dialog);

        TextView studyButt = dialog.findViewById(R.id.studyButt);
        TextView workButt = dialog.findViewById(R.id.workButt);
        View studyView = dialog.findViewById(R.id.studyView);
        View workView = dialog.findViewById(R.id.workView);
        EditText editPlace = dialog.findViewById(R.id.editPlace);
        EditText editOcup = dialog.findViewById(R.id.editOcup);
        TextView saveButt = dialog.findViewById(R.id.nextButton);

        String occupation = Profile.getOccupation();
        if (occupation != null)
            if (!occupation.isEmpty()) {
                String str = Tools.getValue(occupation, Tools.getString(R.string.occupation, activity));
                if (str.contentEquals(Tools.getString(R.string.work, activity))) {
                    workView.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                    workView.setVisibility(View.VISIBLE);
                    workButt.setTextColor(Color.WHITE);
                    String work_center = Tools.getValue(occupation, Tools.getString(R.string.work_center, activity));
                    String job = Tools.getValue(occupation, Tools.getString(R.string.job, activity));
                    if (!work_center.isEmpty())
                        editPlace.setText(work_center);
                    else
                        editPlace.setHint("¿Dónde?");
                    if (!job.isEmpty())
                        editOcup.setText(job);
                    else
                        editOcup.setHint("¿Como qué? (Opcional)");
                    id[0] = 1;
                } else {
                    studyView.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                    studyView.setVisibility(View.VISIBLE);
                    studyButt.setTextColor(Color.WHITE);
                    String study_center = Tools.getValue(occupation, Tools.getString(R.string.study_center, activity));
                    String course = Tools.getValue(occupation, Tools.getString(R.string.course, activity));
                    if (!study_center.isEmpty())
                        editPlace.setText(study_center);
                    else
                        editPlace.setHint("Centro de Estudios");
                    if (!course.isEmpty())
                        editOcup.setText(course);
                    else
                        editOcup.setHint("Carrera o Curso (Opcional)");
                    id[0] = 0;
                }


            }

        studyButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyView.getVisibility() == View.INVISIBLE) {
                    studyView.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                    studyView.setVisibility(View.VISIBLE);
                    workView.setVisibility(View.INVISIBLE);
                    studyButt.setTextColor(Color.WHITE);
                    workButt.setTextColor(Color.BLACK);
                    editPlace.setText("");
                    editOcup.setText("");
                    editPlace.setHint("Centro de Estudios");
                    editOcup.setHint("Carrera o Curso (Opcional)");
                    id[0] = 0;
                }
            }
        });

        workButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (workView.getVisibility() == View.INVISIBLE) {
                    workView.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                    workView.setVisibility(View.VISIBLE);
                    studyView.setVisibility(View.INVISIBLE);
                    editPlace.setText("");
                    studyButt.setTextColor(Color.BLACK);
                    workButt.setTextColor(Color.WHITE);
                    editOcup.setText("");
                    editPlace.setHint("¿Dónde?");
                    editOcup.setHint("¿Como qué? (Opcional)");
                    id[0] = 1;
                }

            }
        });

        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (workView.getVisibility() == View.INVISIBLE && studyView.getVisibility() == View.INVISIBLE) {
                    Toast.makeText(activity, "Elija si trabaja o estudia", Toast.LENGTH_SHORT).show();
                    return;
                }
                String place = editPlace.getText().toString().trim();
                String ocup = editOcup.getText().toString().trim();
                if (place.isEmpty() && ocup.isEmpty()) {
                    Toast.makeText(activity, "Complete al menos un campo", Toast.LENGTH_SHORT).show();
                    return;
                }

                Pair<String, String> keys, occupation;
                if (id[0] == 0) {
                    occupation = new Pair<>(Tools.getString(R.string.occupation, activity), Tools.getString(R.string.study, activity));
                    keys = new Pair<>(Tools.getString(R.string.study_center, activity), Tools.getString(R.string.course, activity));
                } else {
                    occupation = new Pair<>(Tools.getString(R.string.occupation, activity), Tools.getString(R.string.work, activity));
                    keys = new Pair<>(Tools.getString(R.string.work_center, activity), Tools.getString(R.string.job, activity));
                }

                ArrayList<Pair<String, String>> data = new ArrayList<>();
                data.add(occupation);
                data.add(new Pair<>(keys.first, place));
                data.add(new Pair<>(keys.second, ocup));

                String strOccupation = Tools.getJson(data);
                String str = Tools.getValue(strOccupation, Tools.getString(R.string.occupation, activity));
                textOccupation.setTextColor(activity.getResources().getColor(R.color.black));
                if (str.contentEquals(Tools.getString(R.string.work, activity))) {
                    Log.d("Log_", "work");
                    String work_center = Tools.getValue(strOccupation, Tools.getString(R.string.work_center, activity));
                    String job = Tools.getValue(strOccupation, Tools.getString(R.string.job, activity));
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!work_center.isEmpty()) {
                                textOccupation.setText("Trabajo en " + work_center);
                            } else {
                                textOccupation.setText("Trabajo");
                            }
                            if (!job.isEmpty())
                                textOccupation.setText(textOccupation.getText().toString() + " como " + job);

                            textOccupation.setText(textOccupation.getText().toString() + " " + "\uD83D\uDCBC");
                            textOccupation.setVisibility(View.VISIBLE);
                        }
                    });

                } else {
                    String study_center = Tools.getValue(strOccupation, Tools.getString(R.string.study_center, activity));
                    String course = Tools.getValue(strOccupation, Tools.getString(R.string.course, activity));
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!course.isEmpty())
                                textOccupation.setText("Estudio" + " " + course);
                            else {
                                textOccupation.setText("Estudio");
                            }
                            if (!study_center.isEmpty())
                                textOccupation.setText(textOccupation.getText().toString() + " en " + study_center);

                            textOccupation.setText(textOccupation.getText().toString() + " " + "📚");//"\uD83C\uDF93";
                            textOccupation.setVisibility(View.VISIBLE);
                        }
                    });
                }

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.occupation, activity), Tools.getJson(data), activity);
                    }
                });

                dialog.dismiss();

            }
        });


        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

    }
}
