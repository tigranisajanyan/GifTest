package com.example.intern.giftest.effects.Utils;


import android.graphics.Bitmap;

import com.example.intern.giftest.effects.BaseVideoAction;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class EffectsItem {
    private Bitmap bitmap;
    private BaseVideoAction action;

    public EffectsItem(Bitmap bitmap, BaseVideoAction action) {
        this.bitmap = bitmap;
        this.action = action;
    }

    public BaseVideoAction getAction() {
        return action;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
