package com.insightdev.both;

import android.graphics.Bitmap;

import net.alhazmy13.imagefilter.ImageFilter;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class

FilterItem extends BasicFilter {

    private String filterName;

    public FilterItem(Bitmap bitmap, String filterName, ImageFilter.Filter filter) {
        super(bitmap, filter);
        this.filterName = filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }


    public String getFilterName() {
        return filterName;
    }
}
