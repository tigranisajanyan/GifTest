package com.example.intern.giftest.items;

import android.graphics.Canvas;

/**
 * Created by Tigran on 9/4/15.
 */
public abstract class BaseItem {

    protected int left = 0;
    protected int top = 0;
    protected float scaleX = 1f;
    protected float scaleY = 1f;
    protected float rotation = 0f;

    public BaseItem() {

    }

    public BaseItem(int left, int top, float scaleX, float scaleY, float rotation) {
        this.left = left;
        this.top = top;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.rotation = rotation;
    }


    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public abstract void draw(Canvas canvas, int... index);

}
