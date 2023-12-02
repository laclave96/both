package com.insightdev.both;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.iielse.switchbutton.SwitchView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.insightdev.both.viewmodels.UpdateDatabaseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AutoBillingDialogFragment extends BottomSheetDialogFragment {

    boolean autoBillingFlag = false;

    SwitchView switchButton0;

    ConstraintLayout layout;

    View blockView;

    CircularProgressIndicator indicator;

    UpdateDatabaseViewModel databaseViewModel;

    public AutoBillingDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View dialog = inflater.inflate(R.layout.auto_billing_dialog, container, false);

        switchButton0 = dialog.findViewById(R.id.check1);

        layout = dialog.findViewById(R.id.touchAutoBilling);

        indicator = dialog.findViewById(R.id.indicator);

        indicator.hide();
        blockView = dialog.findViewById(R.id.overLay);

        ImageView close = dialog.findViewById(R.id.close);

        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }).start();

        databaseViewModel = ViewModelProviders.of(this).get(UpdateDatabaseViewModel.class);

        databaseViewModel.getResponseLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {


                indicator.hide();
                blockView.setVisibility(View.GONE);
                Objects.requireNonNull(getDialog()).setCancelable(true);
                if (!Objects.equals(s, "true")) {
                    ProToast.makeText(getContext(), R.drawable.ic_info, "No se pudo guardar su configuracion, inténtelo más tarde", Toast.LENGTH_LONG);
                    return;
                }
                autoBillingFlag = !autoBillingFlag;
                switchButton0.toggleSwitch(autoBillingFlag);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String toSave = autoBillingFlag ? "ok" : "";

                        Tools.saveSettings("auto_billing_enable", toSave, getContext());
                    }
                }).start();
            }
        });


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Profile.isPremium(getContext())) {
                    ProToast.makeText(getContext(), R.drawable.ic_info, "Debes tener una cuenta de pago añadida", Toast.LENGTH_LONG);
                    return;

                }

                clickToggle();
            }
        });

        blockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        switchButton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToggle();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }

    private void init() {

        String aBilling = Tools.getSettings("auto_billing_enable", getContext());

        if (!aBilling.isEmpty())
            autoBillingFlag = true;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switchButton0.setOpened(autoBillingFlag);
            }
        });


    }


    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public void onCancel(@NonNull @NotNull DialogInterface dialog) {
        dialog.dismiss();
        super.onCancel(dialog);
    }

    private void clickToggle() {
        Objects.requireNonNull(getDialog()).setCancelable(false);
        indicator.show();
        blockView.setVisibility(View.VISIBLE);
        String option = autoBillingFlag ? "0" : "1";

        databaseViewModel.updateAutoBilling(getContext(), option, 0);
    }

}
