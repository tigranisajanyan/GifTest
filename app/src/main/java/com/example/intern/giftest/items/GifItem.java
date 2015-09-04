package com.example.intern.giftest.items;

import android.graphics.Canvas;

import pl.droidsonroids.gif.GifDrawable;


/**
 * Created by Tigran on 9/4/15.
 */
public class GifItem extends BaseItem {

    private GifDrawable gifDrawable;

    public GifItem() {
        super();
    }

    public GifItem(GifDrawable gifDrawable) {
        super();
        this.gifDrawable = gifDrawable;
    }

    public GifDrawable getGifDrawable() {
        return gifDrawable;
    }

    public void setGifDrawable(GifDrawable gifDrawable) {
        this.gifDrawable = gifDrawable;
    }

    @Override
    public void draw(Canvas canvas, int... index) {

    }
}
