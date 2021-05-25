package com.curenta.driver.dto;

import android.graphics.Bitmap;

/**
 * Created by faheem on 25,May,2021
 */
public class ImageModel {
    Bitmap mThumbIds;
    boolean clickable = false;

    public Bitmap getmThumbIds() {
        return mThumbIds;
    }

    public void setmThumbIds(Bitmap mThumbIds) {
        this.mThumbIds = mThumbIds;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }
}
