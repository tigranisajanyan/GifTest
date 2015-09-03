package com.example.intern.giftest.effects.Utils;


/**
 * Created by AramNazaryan on 7/24/15.
 */
public class BoostEffectParams {

    public static final int TYPE_RED = 1;
    public static final int TYPE_GREEN = 2;
    public static final int TYPE_BLUE = 3;

    public int percentage = 100;
    public int type = TYPE_RED;

    public BoostEffectParams(int percentage, int type) {
        this.percentage = percentage;
        this.type = type;
    }

}
