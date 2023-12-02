package com.insightdev.both;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.insightdev.both.viewmodels.GetPricesViewModel;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class PricesDialogFragment extends BottomSheetDialogFragment {


    GetPricesViewModel getPricesViewModel;

    ArrayList<PriceItem> priceItemsArray = new ArrayList<>();

    String currency, currencySymbol;

    static final int RECEIVE_SMS = 0;

    public PricesDialogFragment() {
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

        View dialog = inflater.inflate(R.layout.new_pricing_dialog, container, false);


        ImageView close = dialog.findViewById(R.id.close);
        DiscreteScrollView recyclerView = dialog.findViewById(R.id.priceSelector);
        Button go = dialog.findViewById(R.id.ok);

        ShimmerFrameLayout shimmerFrameLayout = dialog.findViewById(R.id.shimmerLayout);

        shimmerFrameLayout.startShimmer();
        go.setEnabled(false);

        PriceSelectorAdapter priceSelectorAdapter = new PriceSelectorAdapter(priceItemsArray, "", getActivity());
        recyclerView.setAdapter(priceSelectorAdapter);
        getPricesViewModel = ViewModelProviders.of(this).get(GetPricesViewModel.class);

        getPricesViewModel.getPricesList(getContext());

        getPricesViewModel.getLiveDataPricesList().observe(this, new Observer<GetPriceResponseModel>() {
            @Override
            public void onChanged(GetPriceResponseModel getPriceResponseModel) {

                priceItemsArray.addAll(getPriceResponseModel.getPriceItems());
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                currency = getPriceResponseModel.getCurrency();
                currencySymbol = getPriceResponseModel.getCurrencySymbol();

                priceSelectorAdapter.setCurrency(currencySymbol);
                priceSelectorAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(1);
                go.setEnabled(true);
            }
        });


        priceSelectorAdapter.notifyDataSetChanged();
        recyclerView.setSlideOnFling(true);
        recyclerView.setSlideOnFlingThreshold(1500);


        recyclerView.setItemTransformer(new ScaleAlphaTransformer.Builder()
                .setMaxScale(1.1f)
                .setMinScale(1f)
                .setMinAlpha(0.6f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                .build());

        recyclerView.setItemTransitionTimeMillis(150);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.startActivity(new Intent(activity, CheckoutActivity.class));

              /*  if (Profile.getCountryCode().contentEquals("CU")) {

                    PriceItem priceItem = priceItemsArray.get(recyclerView.getCurrentItem());

                    openCubanPayment((int) (priceItem.getDuration() * priceItem.getPrice()));

                    return;
                }*/

                Intent intent = new Intent(getActivity(), CheckoutActivity.class);

                intent.putExtra("price_item", new Gson().toJson(priceItemsArray.get(recyclerView.getCurrentItem())));
                intent.putExtra("currency", currency);
                intent.putExtra("currencySymbol", currencySymbol);

                getActivity().startActivity(intent);
                dismiss();
            }
        });

        priceSelectorAdapter.setOnItemClickListener(new PriceSelectorAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {

                recyclerView.smoothScrollToPosition(position);

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

    private void openCubanPayment(int cant) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS);
            return;
        }
        new PayDialog().showDialog(getActivity(), cant);
    }
}
