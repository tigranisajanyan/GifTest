package com.example.intern.giftest.effects;

import android.graphics.Bitmap;

/**
 * Created by AramNazaryan on 9/4/15.
 */
public class EffectItem {

    private Bitmap bitmap = null;
    private BaseVideoAction action = null;

    public EffectItem(BaseVideoAction action, Bitmap bitmap) {
        this.action = action;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {

        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BaseVideoAction getAction() {
        return action;
    }

    public void setAction(BaseVideoAction action) {
        this.action = action;
    }

}
