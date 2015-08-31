package com.example.intern.giftest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;


import java.util.ArrayList;

/**
 * Created by Tigran on 8/31/15.
 */
public class MergeTwoGifs {
    private ArrayList<GalleryItem> mainFrames = new ArrayList<>();
    private ArrayList<Bitmap> gifArts = new ArrayList<>();
    private Context context;
    ArrayList<Bitmap> bitmaps;

    public MergeTwoGifs(ArrayList<GalleryItem> mainFrames, ArrayList<Bitmap> gifArts, Context context) {
        this.mainFrames = mainFrames;
        this.gifArts = gifArts;
        this.context = context;
    }

    public ArrayList<Bitmap> mergeFrames() {
        bitmaps = new ArrayList<>();
        //new MyTask().execute();
        for (int i = 0; i < mainFrames.size(); i++) {
            if (i > gifArts.size()-1)
                break;
            Bitmap resultBitmap = mainFrames.get(i).getBitmap();
            Canvas canvas = new Canvas(resultBitmap);
            Paint paint = new Paint();
            canvas.drawBitmap(gifArts.get(i), 0, 0, paint);
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

}
