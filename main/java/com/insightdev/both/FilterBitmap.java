package com.insightdev.both;

import android.graphics.Bitmap;

import net.alhazmy13.imagefilter.ImageFilter;

public class FilterBitmap extends BasicFilter {

    Bitmap bitmapWithFilter;

    public FilterBitmap(Bitmap bitmap, ImageFilter.Filter filter) {
        super(bitmap, filter);

    }

    @Override
    public void setFilter(ImageFilter.Filter filter) {

        super.setFilter(filter);

        if (filter != null)
            bitmapWithFilter = ImageFilter.applyFilter(super.getBitmap(), filter);
    }

    @Override
    public Bitmap getBitmap() {

        if (getFilter() != null)
            return bitmapWithFilter;

        return super.getBitmap();
    }
}
