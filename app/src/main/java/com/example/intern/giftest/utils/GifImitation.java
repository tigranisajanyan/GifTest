package com.example.intern.giftest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tigran on 8/6/15.
 */
public class GifImitation {

    private Context context;
    private ImageView imageView;
    private ArrayList<Bitmap> bitmaps;
    private int duration;
    private boolean play = false;
    private MyTask myTask = new MyTask();

    public GifImitation(Context context, ImageView imageView, ArrayList<Bitmap> bitmaps, int duration) {

        this.imageView = imageView;
        this.bitmaps = bitmaps;
        this.duration = duration;
        this.context = context;
    }

    public void start() {
        play = true;
        myTask.execute();
    }

    public void stop() {
        play = false;
        myTask.cancel(true);
    }

    public void changeDuration(int duration) {
        this.duration = duration;
    }

    class MyTask extends AsyncTask<Void, Integer, Void> {

        int k = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            while (play) {
                try {
                    TimeUnit.MILLISECONDS.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(k % bitmaps.size());
                k++;
            }

            /*for (int i = 0; i < bitmaps.size(); i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(i);

            }*/

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            imageView.setImageBitmap(bitmaps.get(values[0]));
        }
    }
}
