package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.TextViewCompat;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jem.rubberpicker.RubberRangePicker;
import com.jem.rubberpicker.RubberSeekBar;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterDialog {

    private String min_age, max_age, distance;

    static final int AMERICA_LATINA = 1, AMERICA_NORTE = 2, EUROPA = 3, ASIA = 4, AFRICA = 5;

    HashMap<Integer, Chip> regions = new HashMap<>();

    ImageView amLatina, amNorth, europe, asia, africa, australia;

    Activity activity;

    Chip guide;

    ChipGroup defaultGroup;

    SegmentedButtonGroup segmentedButtonGroup;

    RubberRangePicker agePicker;

    RubberSeekBar distancePicker;

    TextView age, textDistance, apply;

    CircularImageView lock;

    View blockView;
    ImageView countryMark, regMark, worldMark;

    HorizontalScrollView hScrollView;

    WorldDialog.ActionInterface actionInterface;


    @SuppressLint("ClickableViewAccessibility")
    public void showDialog(Activity activity, int from) {

        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.filters_dialog);

        age = dialog.findViewById(R.id.ages);
        textDistance = dialog.findViewById(R.id.dist);
        agePicker = dialog.findViewById(R.id.rangeAgePicker);
        distancePicker = dialog.findViewById(R.id.distancePicker);
        segmentedButtonGroup = dialog.findViewById(R.id.buttonGroup);

        blockView = dialog.findViewById(R.id.block);

        apply = dialog.findViewById(R.id.nextButton);
        lock = dialog.findViewById(R.id.lock);

        amLatina = dialog.findViewById(R.id.LAView0);
        amNorth = dialog.findViewById(R.id.NAView0);
        europe = dialog.findViewById(R.id.EUView0);
        asia = dialog.findViewById(R.id.ASView0);
        australia = dialog.findViewById(R.id.AUSView0);
        africa = dialog.findViewById(R.id.AFView0);
        guide = dialog.findViewById(R.id.chipGuide);
        defaultGroup = dialog.findViewById(R.id.listRegions);
        segmentedButtonGroup = dialog.findViewById(R.id.buttonGroup);
        hScrollView = dialog.findViewById(R.id.hScrollView);

        countryMark = dialog.findViewById(R.id.countryMark);
        regMark = dialog.findViewById(R.id.mRegMark);
        worldMark = dialog.findViewById(R.id.worldMark);

      /*  regions.put(AMERICA_LATINA, AMERICA_LATINA);
        regions.put(AMERICA_NORTE, AMERICA_NORTE);
        regions.put(EUROPA, EUROPA);
        regions.put(ASIA, ASIA);
        regions.put(AFRICA, AFRICA);*/

        actionInterface = new WorldDialog.ActionInterface() {
            @Override
            public void goPrem() {

                dialog.dismiss();
            }
        };

        africa.setDrawingCacheEnabled(true);
        amNorth.setDrawingCacheEnabled(true);
        amLatina.setDrawingCacheEnabled(true);
        europe.setDrawingCacheEnabled(true);
        asia.setDrawingCacheEnabled(true);
        australia.setDrawingCacheEnabled(true);

        /*amLatina.setImageDrawable(null);
        amNorth.setImageDrawable(null);
        europe.setImageDrawable(null);
        asia.setImageDrawable(null);
        australia.setImageDrawable(null);
        africa.setImageDrawable(null);*/

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                EventBus.getDefault().post(new ApplyFiltersEvent(from));
                if (from == 0)
                    EventBus.getDefault().post(new LoadingShowEvent());

            }
        });

        final View.OnTouchListener changeColorListenerAfrica = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                int x = (int) event.getX();
                int y = (int) event.getY();
                int color;
                if (x >= bmp.getWidth() || y >= bmp.getWidth() || x < 0 || y < 0)
                    return false;
                else
                    color = bmp.getPixel(x, y);
                if (color == Color.TRANSPARENT) {
                    Log.d("alksdmad", "false");
                    return false;
                } else {
                    Log.d("alksdmad", "true");
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        selectRegion(AFRICA);
                        updateRegions();
                    }
                    //code to execute
                    return true;
                }
            }
        };

        final View.OnTouchListener changeColorListenerAsia = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());

                int x = (int) event.getX();
                int y = (int) event.getY();
                int color;
                if (x >= bmp.getWidth() || y >= bmp.getWidth() || x < 0 || y < 0)
                    return false;
                else
                    color = bmp.getPixel(x, y);
                if (color == Color.TRANSPARENT) {
                    Log.d("alksdmad", "false");
                    return false;
                } else {
                    Log.d("alksdmad", "true");
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        selectRegion(ASIA);
                        updateRegions();
                    }
                    //code to execute
                    return true;
                }
            }
        };

        final View.OnTouchListener changeColorListenerAN = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                australia.getDrawingCache();
                int x = (int) event.getX();
                int y = (int) event.getY();
                int color;
                if (x >= bmp.getWidth() || y >= bmp.getWidth() || x < 0 || y < 0)
                    return false;
                else
                    color = bmp.getPixel(x, y);


                Log.d("alksdmad", "color " + color + " in " + x + " : " + y);
                if (color == Color.TRANSPARENT) {
                    Log.d("alksdmad", "false");
                    return false;
                } else {
                    Log.d("alksdmad", "true");
                    if (event.getAction() == MotionEvent.ACTION_UP) {


                        selectRegion(AMERICA_NORTE);
                        updateRegions();
                    }
                    //code to execute
                    return true;
                }
            }
        };

        final View.OnTouchListener changeColorListenerAL = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                int x = (int) event.getX();
                int y = (int) event.getY();
                int color;
                if (x >= bmp.getWidth() || y >= bmp.getWidth() || x < 0 || y < 0)
                    return false;
                else
                    color = bmp.getPixel(x, y);
                if (color == Color.TRANSPARENT) {
                    Log.d("alksdmad", "false");
                    return false;
                } else {
                    Log.d("alksdmad", "true");
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        selectRegion(AMERICA_LATINA);
                        updateRegions();
                    }
                    //code to execute
                    return true;
                }
            }
        };

        final View.OnTouchListener changeColorListenerEU = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                int x = (int) event.getX();
                int y = (int) event.getY();
                int color;
                if (x >= bmp.getWidth() || y >= bmp.getWidth() || x < 0 || y < 0)
                    return false;
                else
                    color = bmp.getPixel(x, y);
                if (color == Color.TRANSPARENT) {
                    Log.d("alksdmad", "false");
                    return false;
                } else {
                    Log.d("alksdmad", "true");
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        selectRegion(EUROPA);
                        updateRegions();
                    }
                    //code to execute
                    return true;
                }
            }
        };

        final View.OnTouchListener changeColorListenerAUS = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                amNorth.getDrawingCache();
                int x = (int) event.getX();
                int y = (int) event.getY();
                int color;
                if (x >= bmp.getWidth() || y >= bmp.getWidth() || x < 0 || y < 0)
                    return false;
                else
                    color = bmp.getPixel(x, y);

                Log.d("alksdmad", "color " + color + " in " + x + " : " + y);
                if (color == Color.TRANSPARENT) {
                    Log.d("alksdmad", "false");
                    return false;
                } else {
                    Log.d("alksdmad", "true");
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        selectRegion(AMERICA_NORTE);
                        updateRegions();
                    }
                    //code to execute
                    return true;
                }
/*
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("alksdmad", "click" + amNorth.getX() + " " + amNorth.getY());
                    touchViewIn(amNorth, 1, 1);
                }
                return true;*/
            }
        };


        if (Profile.isPremium(activity)) {

            lock.setVisibility(View.GONE);
            blockView.setVisibility(View.GONE);
        }


        africa.setOnTouchListener(changeColorListenerAfrica);
        amLatina.setOnTouchListener(changeColorListenerAL);
        amNorth.setOnTouchListener(changeColorListenerAN);
        asia.setOnTouchListener(changeColorListenerAsia);
        europe.setOnTouchListener(changeColorListenerEU);
        australia.setOnTouchListener(changeColorListenerAUS);


        blockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new WorldDialog().showDialog(activity, actionInterface);
            }
        });

        distancePicker.setOnTouchListener((v, event) ->

        {
            // Disallow the touch request for parent scroll on touch of
            // child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        agePicker.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of
                // child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        agePicker.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(@NotNull RubberRangePicker rubberRangePicker, int i,
                                          int i1, boolean b) {
                age.setText(i + " - " + i1 + " años");

            }

            @Override
            public void onStartTrackingTouch(@NotNull RubberRangePicker rubberRangePicker,
                                             boolean b) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull RubberRangePicker rubberRangePicker,
                                            boolean b) {
                min_age = rubberRangePicker.getCurrentStartValue() + "";
                max_age = rubberRangePicker.getCurrentEndValue() + "";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.saveSettings("min_age", min_age, activity);
                        Tools.saveSettings("max_age", max_age, activity);
                    }
                }).start();
            }
        });

        countryMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePicker.setCurrentValue(500);
                saveDistance(String.valueOf(500));
                //if (Profile.isPremium(activity))
                removeAllRegions();


            }
        });

        regMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePicker.setCurrentValue(750);
                saveDistance(String.valueOf(750));
                // if (Profile.isPremium(activity)) {
                removeAllRegions();
                selectRegion(Profile.person.getRegion());
                updateRegions();


            }
        });

        worldMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePicker.setCurrentValue(1000);
                saveDistance(String.valueOf(1000));
                // if (Profile.isPremium(activity))
                selectAllRegions();


            }
        });

        distancePicker.setOnRubberSeekBarChangeListener(new RubberSeekBar.OnRubberSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@NotNull RubberSeekBar rubberSeekBar, int i, boolean b) {

                if (1000 == i) {
                    textDistance.setText("Todo el mundo");
                    countryMark.setColorFilter(null);
                    regMark.setColorFilter(null);
                    worldMark.setColorFilter(null);
                } else if (i < 1000 && i >= 750) {
                    textDistance.setText("Mi región");
                    countryMark.setColorFilter(null);
                    regMark.setColorFilter(null);
                    worldMark.setColorFilter(Color.parseColor("#E1E1E1"));
                } else if (i < 1000 && i >= 500) {
                    textDistance.setText("Mi país");
                    countryMark.setColorFilter(null);
                    regMark.setColorFilter(Color.parseColor("#E1E1E1"));
                    worldMark.setColorFilter(Color.parseColor("#E1E1E1"));
                } else {
                    textDistance.setText(i + " km");
                    countryMark.setColorFilter(Color.parseColor("#E1E1E1"));
                    regMark.setColorFilter(Color.parseColor("#E1E1E1"));
                    worldMark.setColorFilter(Color.parseColor("#E1E1E1"));
                }


            }

            @Override
            public void onStartTrackingTouch(@NotNull RubberSeekBar rubberSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull RubberSeekBar rubberSeekBar) {
                int val = rubberSeekBar.getCurrentValue();


                Log.d("aasfasfaf", "val");

                if (val < 1000 && val >= 750) {

                    rubberSeekBar.setCurrentValue(750);

                } else if (val < 1000 && val >= 500) {

                    rubberSeekBar.setCurrentValue(500);

                }

                val = rubberSeekBar.getCurrentValue();

                distance = String.valueOf(val);


                if (val == 1000)
                    selectAllRegions();
                else if (val == 750) {
                    removeAllRegions();
                    selectRegion(Profile.person.getRegion());
                    updateRegions();
                } else removeAllRegions();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.saveSettings("distance", distance, activity);


                    }
                }).start();

            }

        });

        segmentedButtonGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                Tools.saveSettings("gender", position + "", activity);
            }
        });

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }).start();


        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                dialog.dismiss();

            }
        });

    }

    private void addNewChip(String chipText, int region) {
        Chip chip = new Chip(activity);
        chip.setText(chipText);
        chip.setChipBackgroundColorResource(R.color.dBackground_250);
        //  chip.setElevation(guide.getElevation());
        chip.setCloseIconVisible(false);
        //    chip.setChipStrokeWidth(guide.getChipStrokeWidth());
        //      chip.setChipStrokeColorResource(R.color.redSecundary);
        //chip.setCloseIconResource(R.drawable.ic_round_close);
        TextViewCompat.setTextAppearance(chip, R.style.chipFontMini);

        regions.put(region, chip);

        Log.d("liaskjdlaas", regions + "");


        defaultGroup.addView(chip);
        if (regions.containsKey(0) && region != 0) {
            removeChip(0);
        }
        hScrollView.post(new Runnable() {
            public void run() {
                hScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });

    }


    private void removeChip(int region) {


        if (regions.containsKey(region)) {
            defaultGroup.removeView(regions.get(region));
            regions.remove(region);
        }


        if (regions.size() == 0 && region != 0)
            addNewChip("Selecciona las regiones", 0);


    }

    private void updateRegions() {
        ArrayList<String> reg = new ArrayList<>();
        Log.d("baslkdjaslda", regions.size() + " size");

        for (int i = 1; i < 6; i++)
            if (regions.containsKey(i))
                reg.add(String.valueOf(i));


        new Thread(new Runnable() {
            @Override
            public void run() {
                Tools.saveSettings("regions", StringUtils.join(reg, ","), activity);
                Log.d("baslkdjaslda", StringUtils.join(reg, ","));


            }
        }).start();
    }

    private void loadData() {

        String gender = Tools.getSettings("gender", activity);
        min_age = Tools.getSettings("min_age", activity);
        max_age = Tools.getSettings("max_age", activity);
        distance = Tools.getSettings("distance", activity);
        String reg = Tools.getSettings("regions", activity);
        String[] arrS = new String[0];

        if (!reg.isEmpty())
            arrS = reg.split(",");

        String[] finalArrS = arrS;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!gender.isEmpty())
                    segmentedButtonGroup.setPosition(Integer.parseInt(gender), true);

                if (!min_age.isEmpty()) {
                    agePicker.setCurrentStartValue(Integer.parseInt(min_age));
                    agePicker.setCurrentEndValue(Integer.parseInt(max_age));
                }

                if (!distance.isEmpty()) {
                    distancePicker.setCurrentValue(Integer.parseInt(distance));
                    Log.d("aasfasfaf", "set ok 1000");
                } else {
                    distancePicker.setCurrentValue(1000);
                    saveDistance(String.valueOf(1000));
                    // if (Profile.isPremium(activity))
                    selectAllRegions();
                }

                if (!reg.isEmpty()) {

                    for (int i = 0; i < finalArrS.length; i++)
                        selectRegion(Integer.valueOf(finalArrS[i]));

                } else {
                    addNewChip("Selecciona las regiones", 0);
                }

            }
        });
    }

    private void selectRegion(int region_tag) {

        if (!regions.containsKey(region_tag)) {
            //Add tag to hash map

            switch (region_tag) {

                case AMERICA_LATINA:
                    amLatina.getDrawingCache();
                    amLatina.setImageDrawable(null);
                    addNewChip("América Latina y el Caribe", AMERICA_LATINA);// remv
                    break;

                case AMERICA_NORTE:
                    amNorth.getDrawingCache();
                    australia.getDrawingCache();
                    amNorth.setImageDrawable(null);
                    australia.setImageDrawable(null);
                    addNewChip("América del Norte y Australia", AMERICA_NORTE);//remv
                    break;
                case EUROPA:
                    europe.getDrawingCache();
                    europe.setImageDrawable(null);
                    addNewChip("Europa", EUROPA);
                    break;

                case ASIA:
                    asia.getDrawingCache();
                    asia.setImageDrawable(null);
                    addNewChip("Asia", ASIA);
                    break;

                default:
                    africa.getDrawingCache();
                    africa.setImageDrawable(null);
                    addNewChip("África", AFRICA);
                    break;
            }
        } else {
            //remv tag

            if (regions.size() == 1 && Integer.valueOf(distance) == 1000 && region_tag != 0) {
                ProToast.makeText(activity, R.drawable.ic_info, "Debes seleccionar al menos una región", Toast.LENGTH_SHORT);
                return;
            }


            switch (region_tag) {

                case AMERICA_LATINA:
                    amLatina.setImageResource(R.drawable.lat_am_o);
                    break;

                case AMERICA_NORTE:
                    amNorth.setImageResource(R.drawable.north_america_0);
                    australia.setImageResource(R.drawable.australia);
                    break;
                case EUROPA:
                    europe.setImageResource(R.drawable.europa);
                    break;

                case ASIA:
                    asia.setImageResource(R.drawable.asia);
                    break;

                default:
                    africa.setImageResource(R.drawable.africa);
                    break;
            }

            removeChip(region_tag);
        }
    }

    private void selectAllRegions() {

        for (int i = 1; i < 6; i++)
            if (!regions.containsKey(i))
                selectRegion(i);

        updateRegions();

    }

    private void removeAllRegions() {


        for (int i = 1; i < 6; i++)
            if (regions.containsKey(i))
                selectRegion(i);

        updateRegions();
    }


    private void saveDistance(String dist) {

        distance = dist;

        new Thread(new Runnable() {
            @Override
            public void run() {


                Tools.saveSettings("distance", dist, activity);


            }
        }).start();
    }

    private void removeAllRegionsExcept(int region) {

        for (int i = 1; i < 6; i++)
            if (regions.containsKey(i) && i != region)
                selectRegion(i);

        updateRegions();
    }

}
