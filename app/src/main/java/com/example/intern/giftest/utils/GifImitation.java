package com.example.intern.giftest.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.intern.giftest.items.GalleryItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tigran on 8/6/15.
 */
public class GifImitation extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private ImageView imageView;
    private ArrayList<GalleryItem> bitmaps;
    private int duration;
    private boolean play = false;
    private int k = 0;

    public GifImitation(Context context, ImageView imageView, ArrayList<GalleryItem> bitmaps, int duration) {

        this.imageView = imageView;
        this.bitmaps = bitmaps;
        this.duration = duration;
        this.context = context;
    }

    public void changeDuration(int duration) {
        this.duration = duration;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        play = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (play) {
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            int i = 0;
            while (!bitmaps.get(k % bitmaps.size()).isSeleted()) {
                if (i == bitmaps.size())
                    break;
                i++;
                k++;
            }
            if (i != bitmaps.size())
                publishProgress(k % bitmaps.size());
            k++;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        imageView.setImageBitmap(bitmaps.get(values[0]).getBitmap());
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        play = false;
    }

}
