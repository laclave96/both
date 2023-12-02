package com.insightdev.both;

import android.graphics.Bitmap;

import net.alhazmy13.imagefilter.ImageFilter;

public class BasicFilter {

    private Bitmap bitmap;
    private ImageFilter.Filter filter;

    public BasicFilter(Bitmap bitmap, ImageFilter.Filter filter) {

        this.filter = filter;
        if (filter != null)
            this.bitmap = ImageFilter.applyFilter(bitmap, filter);
        else
            this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ImageFilter.Filter getFilter() {
        return filter;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setFilter(ImageFilter.Filter filter) {
        this.filter = filter;
    }
}
