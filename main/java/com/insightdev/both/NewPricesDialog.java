package com.insightdev.both;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.stripe.android.model.CardBrand;
import com.stripe.android.view.CardNumberEditText;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class NewPricesDialog {


    ArrayList<PriceItem> priceItems = new ArrayList<>();

    public void showDialog(Activity activity) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_pricing_dialog);


        ImageView close = dialog.findViewById(R.id.close);
        DiscreteScrollView recyclerView = dialog.findViewById(R.id.priceSelector);
        Button go = dialog.findViewById(R.id.ok);

        ShimmerFrameLayout shimmerFrameLayout = dialog.findViewById(R.id.shimmerLayout);


        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);

        PriceSelectorAdapter priceSelectorAdapter = new PriceSelectorAdapter(priceItems, "€", activity);

        recyclerView.setAdapter(priceSelectorAdapter);


        priceItems.add(new PriceItem(3.99, 12, 60));
        priceItems.add(new PriceItem(5.99, 6, 40));
        priceItems.add(new PriceItem(7.99, 3, 20));
        priceItems.add(new PriceItem(9.99, 1, 0));

        priceSelectorAdapter.notifyDataSetChanged();
        recyclerView.setSlideOnFling(true);
        recyclerView.setSlideOnFlingThreshold(1500);


        recyclerView.setItemTransformer(new ScaleAlphaTransformer.Builder()
                .setMaxScale(1.1f)
                .setMinScale(1f)
                .setMinAlpha(0.55f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                .build());

        recyclerView.setItemTransitionTimeMillis(150);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.startActivity(new Intent(activity, CheckoutActivity.class));
                Intent intent = new Intent(activity, CheckoutActivity.class);

                intent.putExtra("price_item", new Gson().toJson(priceItems.get(recyclerView.getCurrentItem())));

                activity.startActivity(intent);
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
                dialog.dismiss();
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

