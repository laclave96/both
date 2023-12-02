package com.insightdev.both;

import android.util.Log;
import android.view.View;

import androidx.annotation.FloatRange;

import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer;
import com.yarolegovich.discretescrollview.transform.Pivot;

public class ScaleAlphaTransformer implements DiscreteScrollItemTransformer {

    private Pivot pivotX;
    private Pivot pivotY;
    private float minScale;
    private float minAlpha;
    private float maxMinDiff;
    private float maxMinDiffAlpha;

    public ScaleAlphaTransformer() {
        pivotX = Pivot.X.CENTER.create();
        pivotY = Pivot.Y.CENTER.create();
        minScale = 0.8f;
        minAlpha = 0.8f;
        maxMinDiff = 0.2f;
        maxMinDiffAlpha = 0.2f;
    }

    @Override
    public void transformItem(View item, float position) {
        pivotX.setOn(item);
        pivotY.setOn(item);
        float closenessToCenter = 1f - Math.abs(position);
        float scale = minScale + maxMinDiff * closenessToCenter;
        item.setScaleX(scale);
        item.setScaleY(scale);
        item.setAlpha(minAlpha + maxMinDiffAlpha * closenessToCenter);
        //item.getBackground().setAlpha(normalization(minAlpha + maxMinDiffAlpha * closenessToCenter, 0, 255, 0f, 1f));
        //item.getBackground().setAlpha(125);
        if (closenessToCenter == 1f)
            item.setBackgroundResource(R.drawable.back_15dp_golden_stroke);
        else
            item.setBackground(null);
    }

    private int normalization(float toNorm, int minAllowed, int maxAllowed, float min, float max) {

        return (int) ((maxAllowed - minAllowed) * (toNorm - min) / (max - min) + minAllowed);
    }

    public static class Builder {

        private ScaleAlphaTransformer transformer;
        private float maxScale;

        public Builder() {
            transformer = new ScaleAlphaTransformer();
            maxScale = 1f;
        }

        public Builder setMinScale(@FloatRange(from = 0.01) float scale) {
            transformer.minScale = scale;
            return this;
        }

        public Builder setMaxScale(@FloatRange(from = 0.01) float scale) {
            maxScale = scale;
            return this;
        }

        public Builder setMinAlpha(@FloatRange(from = 0.01) float scale) {
            transformer.minAlpha = scale;
            return this;
        }

        public Builder setPivotX(Pivot.X pivotX) {
            return setPivotX(pivotX.create());
        }

        public Builder setPivotX(Pivot pivot) {
            assertAxis(pivot, Pivot.AXIS_X);
            transformer.pivotX = pivot;
            return this;
        }

        public Builder setPivotY(Pivot.Y pivotY) {
            return setPivotY(pivotY.create());
        }

        public Builder setPivotY(Pivot pivot) {
            assertAxis(pivot, Pivot.AXIS_Y);
            transformer.pivotY = pivot;
            return this;
        }

        public ScaleAlphaTransformer build() {
            transformer.maxMinDiff = maxScale - transformer.minScale;
            transformer.maxMinDiffAlpha = 1f - transformer.minAlpha;
            return transformer;
        }

        private void assertAxis(Pivot pivot, @Pivot.Axis int axis) {
            if (pivot.getAxis() != axis) {
                throw new IllegalArgumentException("You passed a Pivot for wrong axis.");
            }
        }
    }
}
