package com.example.intern.giftest.effects;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.intern.giftest.adapter.Adapter;
import com.example.intern.giftest.clipart.OnVideoActionFinishListener;
import com.example.intern.giftest.effects.Utils.OnEffectApplyFinishedListener;
import com.example.intern.giftest.items.GalleryItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * Created by AramNazaryan on 7/17/15.
 */
public abstract class BaseVideoAction<T> {

    private Activity activity = null;
    private OnVideoActionFinishListener listener = null;

    protected abstract Bitmap doActionOnBitmap(Bitmap videoFrameBitmap);

    public BaseVideoAction(Activity activity, T... params) {
        this.activity = activity;
    }

    public void startAction(final ArrayList<GalleryItem> galleryItems, final Adapter adapter, final T... parameters) {

        AsyncTask<Void, Integer, Void> doActionTask = new AsyncTask<Void, Integer, Void>() {
            ProgressDialog progressDialog;

            @Override
            protected Void doInBackground(Void... params) {

                int count = galleryItems.size();
                for (int i = 0; i < count; i++) {
                    GalleryItem item = galleryItems.get(i);
                    if (item.isSeleted()) {
                        Bitmap bitmapAfterAction = doActionOnBitmap(item.getBitmap());
                        item.setBitmap(bitmapAfterAction);
                        galleryItems.set(i, item);
                        onProgressUpdate(i, count);
                    }
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(adapter.getSelected().size());
                progressDialog.setCancelable(false);
                progressDialog.show();

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                if (listener != null) {
                    listener.onSuccess();
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if (progressDialog != null) {
                    progressDialog.setProgress(values[0] + 1);
                }
            }
        };

        doActionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setOnVideoFinishListener(OnVideoActionFinishListener listener) {
        this.listener = listener;
    }

    public void applyOnOneFrame(final Bitmap bitmap, final OnEffectApplyFinishedListener listener, final T... parameters) {
        AsyncTask<Void, Void, Bitmap> doActionTask = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap bitmapAfterAction = doActionOnBitmap(mutableBitmap);
//                mutableBitmap.recycle();
                return bitmapAfterAction;
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                if (listener != null) {
                    listener.onFinish(bmp);
                }
            }
        };

        doActionTask.execute();
    }

}
