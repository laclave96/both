package com.insightdev.both;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

public class HoleConstraintLayout extends LinearLayout {

    Paint eraser;

    View childView;
    int childId;
    float radius=20f;

    RectF childRect = new RectF();

    public HoleConstraintLayout(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);

        setupEraser();

       // radius = Tools.dpIntoPx(20, context);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        childView=child;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        childRect.set(childView.getLeft(), childView.getTop(),
                childView.getRight(), childView.getBottom());
        canvas.drawRoundRect(childRect, radius, radius, eraser);
    }

    private void setupEraser() {

        eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setColor(Color.TRANSPARENT);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

    }

}


