package com.example.intern.giftest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;


import com.example.intern.giftest.items.GalleryItem;

import java.util.ArrayList;

/**
 * Created by Tigran on 8/31/15.
 */
public class MergeTwoGifs {

    private ArrayList<GalleryItem> mainFrames = new ArrayList<>();
    private ArrayList<Bitmap> gifArts = new ArrayList<>();
    private Context context;

    private int length1;
    private int length2;

    int x;
    int y;

    private long minSumMulty;

    ArrayList<Bitmap> bitmaps;

    public MergeTwoGifs(ArrayList<GalleryItem> mainFrames, ArrayList<Bitmap> gifArts, Context context, int x, int y) {
        this.mainFrames = mainFrames;
        this.gifArts = gifArts;
        this.context = context;
        this.x = x;
        this.y = y;
        length1 = mainFrames.size();
        length2 = gifArts.size();
        minSumMulty = lcm(length1, length2);
    }

    public ArrayList<Bitmap> mergeFrames() {
        bitmaps = new ArrayList<>();
        for (int i = 0; i < mainFrames.size(); i++) {
            if (i > gifArts.size() - 1)
                break;
            Bitmap resultBitmap = mainFrames.get(i).getBitmap();
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            canvas.scale(640f / 1080, 640f / 1080, 0, 0);
            canvas.drawBitmap(gifArts.get(i), x, y, paint);

            bitmaps.add(resultBitmap);
        }
        return bitmaps;
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < mainFrames.size(); i++) {
                Bitmap resultBitmap = mainFrames.get(i).getBitmap();
                Canvas canvas = new Canvas(resultBitmap);
                Paint paint = new Paint();

                Matrix transformMatrix = new Matrix();
                //transformMatrix.postRotate(gifArts.get(i), gifArts.get(i).getWidth() / 2, gifArts.get(i).getHeight() / 2);
                //transformMatrix.postTranslate(clipart.getX(), clipart.getY());
                //transformMatrix.postScale(clipart.getScaleX(), clipart.getScaleY());
                canvas.drawBitmap(resultBitmap, 0, 0, paint);
                canvas.scale(400f / gifArts.get(i).getWidth(), 400f / gifArts.get(i).getWidth(), 0, 0);
                //canvas.drawBitmap(clipart.getBitmap(), transformMatrix, paint);

                canvas.drawBitmap(gifArts.get(gifArts.size() % i), 0, 0, paint);
                bitmaps.add(resultBitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    public long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    public long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }


    public int getCurrentFrameNum(int i, int minSum, int length) {
        return (i / (minSum / length)) + 1;
    }

}
