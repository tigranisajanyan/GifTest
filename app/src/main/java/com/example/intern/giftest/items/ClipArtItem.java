package com.example.intern.giftest.items;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Tigran on 9/4/15.
 */
public class ClipArtItem extends BaseItem {

    private Bitmap clipartBitmap;

    public ClipArtItem() {
        super();
    }

    public ClipArtItem(Bitmap clipartBitmap) {
        super();
        this.clipartBitmap = clipartBitmap;
    }

    public Bitmap getClipartBitmap() {
        return clipartBitmap;
    }

    public void setClipartBitmap(Bitmap clipartBitmap) {
        this.clipartBitmap = clipartBitmap;
    }

    @Override
    public void draw(Canvas canvas, int... index) {

    }

}
