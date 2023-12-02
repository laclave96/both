package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class TagBottomDialog {

    ArrayList<String> chipsText = new ArrayList<String>(Arrays.asList("Teatro", "Comedia", "Fotografía", "Investing", "Sushi", "Fiestas", "Vino", "Música", "Fútbol", "Perros", "Gatos", "Café", "Moda", "Zapatos", "Libros", "Autos", "Motos", "Baile"
            , "Cerveza", "Viajar", "Golosinas", "Videojuegos", "Cocinar", "K-Pop", "Animes", "Surf", "Fitness", "Películas", "Playa", "LGTB", "Naturaleza", "Tatuajes", "Foodie", "Netflix", "Verano", "Escalar", "Comida callejera",
            "NFTs", "Feminismo", "Super bowl", "Bodas","Salir de compras"));
    ArrayList<String> emojies = new ArrayList<String>(Arrays.asList("U+1F3AD", "U+1F602", "U+1F4F8", "U+1F4B8", "U+1F363", "u+1f37e", "u+1f377", "u+266b", "u+26bd", "u+1f436", "u+1f431", "U+2615", "u+1f457", "U+1F45F", "u+1f4d6", "u+1f697", "U+1F3CD"
            , "U+1F483", "U+1F37A", "U+2708", "u+1f9c1", "u+1f3ae", "u+1f373", "u+1f399", "u+1f47a", "U+1F3C4", "u+1f4aa", "u+1f3ac", "u+1f3d6", "U+1F308", "U+1F343", "U+2660", "U+1F37D", "U+1F37F", "U+2600", "U+1F9D7","U+1F354","U+1F5BC",
            " U+2640", "U+1F3C8", "U+1F470", "U+1F6CD"));
    ArrayList<String> tagsWOEmoji;
    ArrayList<String> emojisTag;
    Activity activity;
    Chip chipG;
    ChipGroup defaultGroup;
    String initial;
    ArrayList<Chip> chips;
    String preferences;
    EmojIconActions emojIconActions;

    public void showDialog(Activity activity) {
        this.activity = activity;
        chips = new ArrayList<>();
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.tag_dialog);

        ConstraintLayout base = dialog.findViewById(R.id.baseL);

        EmojiconEditText editTextTag = dialog.findViewById(R.id.edit);
        TextView save = dialog.findViewById(R.id.saveButton);
        defaultGroup = dialog.findViewById(R.id.tags);
        chipG = dialog.findViewById(R.id.chipGuide);

        ImageView addTag = dialog.findViewById(R.id.addButton);

        emojIconActions = new EmojIconActions(activity, base, editTextTag, null);

        editTextTag.setUseSystemDefault(true);
        preferences = Profile.getPreferences();

        emojIconActions.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
            }

            @Override
            public void onKeyboardClose() {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (preferences == null)
                    preferences = "";
                initial = preferences;
                tagsWOEmoji = new ArrayList<>();
                emojisTag = new ArrayList<>();
                if (!preferences.isEmpty()) {
                    String[] tags = preferences.split("///");
                    for (String tag : tags) {
                        String tagWOEmoji = EmojiUtils.removeEmoji(tag).trim();
                        tagsWOEmoji.add(tagWOEmoji);
                        emojisTag.add(tag.replace(tagWOEmoji, "").trim());
                    }
                }


                for (int i = 0; i < emojies.size(); i++) {
                    String text = chipsText.get(i);
                    addNewChip(text, Tools.convertEmoji(emojies.get(i)), false, isSelectTag(tagsWOEmoji, text));
                }

                for (int i = 0; i < tagsWOEmoji.size(); i++) {
                    String text = tagsWOEmoji.get(i);
                    if (!isSelectTag(chipsText, text)) {
                        addNewChip(text, emojisTag.get(i), false, true);
                    }
                }

            }
        }).start();


        Handler nHandler = new Handler();


        nHandler.post(new Runnable() {
            @Override
            public void run() {

                editTextTag.setHint("Añade los tuyos " + Tools.convertEmoji(emojies.get(new Random().nextInt(emojies.size()))));


                nHandler.postDelayed(this, 1500);
            }
        });


        addTag.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String tag = editTextTag.getText().toString().trim();
                if (!EmojiUtils.containsEmoji(tag)) {
                    Toast.makeText(activity, "Añada un emoji al final del texto", Toast.LENGTH_LONG).show();
                    emojIconActions.setUseSystemEmoji(true);
                    emojIconActions.showPopup();
                    return;
                }
                String tagWOEmoji = EmojiUtils.removeEmoji(tag).trim();
                String emoji = tag.replace(tagWOEmoji, "").trim();
                addNewChip(tagWOEmoji, emoji, true, true);
                editTextTag.setText("");

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.isEmpty() || initial.contentEquals(preferences)) {
                    Toast.makeText(activity, "Añada un nuevo interés", Toast.LENGTH_SHORT).show();
                    return;
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.preferences, activity), preferences, activity);
                        EventBus.getDefault().post(new PreferencesEvent());
                    }
                }).start();
                dialog.dismiss();
            }
        });

        editTextTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() > 0 & addTag.getVisibility() == View.INVISIBLE) {


                    addTag.startAnimation(AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.fade_in));
                    addTag.setVisibility(View.VISIBLE);

                } else if (s.toString().length() == 0 & addTag.getVisibility() == View.VISIBLE) {

                    addTag.startAnimation(AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.fade_off));
                    addTag.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });


        setupFullHeight(dialog);

        dialog.show();

    }


    private void addNewChip(String chipText, String emoji, boolean isNew, boolean isSelected) {
        Chip chip = new Chip(activity);
        chip.setText(chipText);
        chip.setChipBackgroundColorResource(R.color.white);
        //chip.setCloseIconVisible(false)
        //chip.setCloseIconResource(R.drawable.ic_round_close);
        TextViewCompat.setTextAppearance(chip, R.style.chipFontF);
        chip.setChipStrokeWidth(chipG.getChipStrokeWidth());
        if (isSelected) {
            chip.setTextColor(Color.parseColor("#ff0046"));
            chip.setChipStrokeColorResource(R.color.redSecundary);
            chips.add(chip);
            if (isNew) {
                tagsWOEmoji.add(chipText);
                emojisTag.add(emoji);
                preferences = addStrTag(preferences, chipText + " " + emoji);
            }
        } else {
            chip.setTextColor(Color.parseColor("#FF888888"));
            chip.setChipStrokeColorResource(R.color.gray);
            // chip.setChipBackgroundColorResource(R.color.dBackground);
        }

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chip.getChipStrokeColor().getDefaultColor() == Color.parseColor("#FF888888")) {
                    chip.setChipStrokeColorResource(R.color.redSecundary);
                    //chip.setChipBackgroundColorResource(R.color.white);
                    chip.setTextColor(Color.parseColor("#ff0046"));
                    chips.add(chip);
                    if (isNew) {
                        tagsWOEmoji.add(chipText);
                        emojisTag.add(emoji);
                    }
                    preferences = addStrTag(preferences, chipText + " " + emoji);

                } else {
                    if (isNew) {
                        tagsWOEmoji.remove(chipText);
                        emojisTag.remove(emoji);
                    }
                    chips.remove(chip);
                    chip.setTextColor(Color.parseColor("#FF888888"));
                    chip.setChipStrokeColorResource(R.color.gray);
                    preferences = removeStrTag(preferences, chipText + " " + emoji);
                }


            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                defaultGroup.addView(chip);
            }
        });

    }

    private boolean isSelectTag(ArrayList<String> tags, String chipText) {
        return tags.indexOf(chipText) > -1;
    }


    String addStrTag(String str, String newTag) {
        if (str.isEmpty()) {
            return newTag + "///";
        }
        return str + newTag + "///";
    }

    String removeStrTag(String str, String tag) {

        return str.replace(tag + "///", "");
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
        (activity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels - 100;
    }
}
