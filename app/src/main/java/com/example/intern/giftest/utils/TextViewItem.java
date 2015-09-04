package com.example.intern.giftest.utils;

import android.graphics.Canvas;
import android.graphics.Typeface;

import com.example.intern.giftest.items.BaseItem;

/**
 * Created by Tigran on 9/4/15.
 */
public class TextViewItem extends BaseItem {

    private String text;
    private int textColor;
    private Typeface textFont;

    public TextViewItem() {
        super();
    }

    public TextViewItem(String text, int textColor, Typeface textFont) {
        super();
        this.text = text;
        this.textColor = textColor;
        this.textFont = textFont;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public Typeface getTextFont() {
        return textFont;
    }

    public void setTextFont(Typeface textFont) {
        this.textFont = textFont;
    }

    @Override
    public void draw(Canvas canvas, int... index) {

    }


}
