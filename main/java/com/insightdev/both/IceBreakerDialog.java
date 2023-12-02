package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.insightdev.both.viewmodels.HomeViewModel;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class IceBreakerDialog {

    // String image = "http://173.249.52.47/jqhneTWEQSDMSAugatysd5623hcbaI127EMLaWVKJ512xsHQasd/12/profile_medium.jpeg";
    private Activity activity;

    ImageRequest request;

    String contactName;

    int sendedIceb, remIcebInt;

    EmojIconActions emojIconActions;

    boolean emojiOpen = false;


    public void showDialog(Activity activity, Contact contact, int from, String date) {
        this.activity = activity;
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.icebreaker_dialog);
        SimpleDraweeView toUserImage = dialog.findViewById(R.id.drawable);
        ImageView close = dialog.findViewById(R.id.close);
        ConstraintLayout container = dialog.findViewById(R.id.dialogContainer);

        ImageView sendButton = dialog.findViewById(R.id.sendButton);

        ImageView insertEmoji = dialog.findViewById(R.id.insertEmoji);

        TextView name = dialog.findViewById(R.id.tagsTitle);

        TextView sending = dialog.findViewById(R.id.sending);

        TextView remIceb = dialog.findViewById(R.id.messageRemain);

        EmojiconEditText emojiconEditText = dialog.findViewById(R.id.editWrite);

        emojIconActions = new EmojIconActions(activity, container, emojiconEditText, insertEmoji);

        emojIconActions.setIconsIds(R.drawable.ic_keyboard, R.drawable.ic_insert_emoji);

        emojiconEditText.setUseSystemDefault(true);


        emojIconActions.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
            }

            @Override
            public void onKeyboardClose() {
                emojiOpen = false;
            }
        });

        insertEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emojiOpen) {
                    emojIconActions.showPopup();
                    emojIconActions.setUseSystemEmoji(true);
                    insertEmoji.setImageResource(R.drawable.ic_keyboard);
                    emojiOpen = true;
                } else {
                    emojIconActions.hidePopup();
                    insertEmoji.setImageResource(R.drawable.ic_insert_emoji);
                    emojiOpen = false;
                }
            }
        });

        String image = from == 0 ? contact.getPublicPhotos(activity).get(0) : contact.getProfilePhotoMedium(activity);

        request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(image))
                // .setResizeOptions(new ResizeOptions(200, 200))
                .setProgressiveRenderingEnabled(true)
                .build();

        toUserImage.setImageRequest(request);

        contactName = Tools.getFirstWords(contact.getName(), 1);


        sendedIceb = Tools.icebreakerSendedToday(activity, date);

        remIcebInt = !Profile.person.isPremium(activity) ? 1 - sendedIceb : 5 - sendedIceb;


        if (remIcebInt <= 0) {
            if (!Profile.person.isPremium(activity))

                new LimitIcebreakerDialog().showDialog(activity);
            else
                ProToast.makeText(activity, R.drawable.ic_info, "Has alcanzado el límite de icebreakers diario", Toast.LENGTH_SHORT);

            return;
        }
        Log.d("alskd", sendedIceb + "");
        String remIcebTv = remIcebInt == 1 ? "Queda 1 mensaje" : "Quedan " + remIcebInt + " mensajes";


        remIceb.setText(remIcebTv);
        // remIceb.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
        //remIceb.setVisibility(View.VISIBLE);


        name.setText("Dile algo a " + contactName);


        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emojiconEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
                imm.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (emojiconEditText.getText().toString().trim().isEmpty())
                    return;


/*

                name.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_off));
                name.setVisibility(View.INVISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        sending.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in));
                        sending.setVisibility(View.VISIBLE);
                    }
                }, 50);
*/

                if (!ContactsManager.checkAddContact(contact, activity))

                    ContactsManager.addType(contact.getChatUsername(activity), "iceb", activity);


                AppHandler.executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.contacts, activity), ContactsManager.getSerializedContacts(), activity);
                    }
                });

                AppHandler.actionSendMsg(emojiconEditText.getText().toString(), contact, activity);

                sendedIceb++;

                AppHandler.updateIcebAction(activity, sendedIceb, date);

                ProToast.makeText(activity, R.drawable.toast_iceb, "Mensaje enviado a " + contactName, Toast.LENGTH_SHORT);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        AppHandler.sendIcebPushRequest(activity, contact.getId());
                    }
                }).start();

                Tools.saveSettings("iceb_timestamp", date, activity);

                // sendedIceb = sendedIceb == null ? "0" : String.valueOf(Integer.parseInt(sendedIceb) + 1);
                Tools.saveSettings("iceb_cant", String.valueOf(sendedIceb), activity);

                dialog.dismiss();


/*
                if (true) {


                    AppHandler.actionSendMsg(emojiconEditText.getText().toString(), contact, activity);

                    //

                    ProToast.makeText(activity, R.drawable.toast_iceb, "Mensaje enviado a " + contactName, Toast.LENGTH_LONG);
                    dialog.dismiss();
                } else
                    ProToast.makeText(activity, R.drawable.toast_offline, "Compruebe su conexión a internet", Toast.LENGTH_LONG);*/


            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}
