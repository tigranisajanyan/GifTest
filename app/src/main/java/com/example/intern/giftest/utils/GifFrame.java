package com.example.intern.giftest.utils;

import android.graphics.Bitmap;

/**
 * Created by Tigran on 9/4/15.
 */
public class GifFrame {

    private Bitmap frameBitmap;
    private int frameDelay;

    public GifFrame() {

    }

    public GifFrame(Bitmap frameBitmap, int frameDelay) {
        this.frameBitmap = frameBitmap;
        this.frameDelay = frameDelay;
    }

    public Bitmap getFrameBitmap() {
        return frameBitmap;
    }

    public void setFrameBitmap(Bitmap frameBitmap) {
        this.frameBitmap = frameBitmap;
    }

    public int getFrameDelay() {
        return frameDelay;
    }

    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }


}
