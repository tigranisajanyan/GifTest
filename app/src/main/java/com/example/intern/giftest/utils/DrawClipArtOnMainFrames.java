package com.example.intern.giftest.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.intern.giftest.adapter.Adapter;
import com.example.intern.giftest.clipart.Clipart;
import com.example.intern.giftest.clipart.MainView;
import com.example.intern.giftest.items.GalleryItem;

import java.util.ArrayList;

/**
 * Created by Tigran on 9/14/15.
 */
public class DrawClipArtOnMainFrames extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private MainView mainView;
    private ArrayList<GalleryItem> galleryItemArrayList;
    private ProgressDialog progressDialog;
    private Clipart clipart;
    private Adapter adapter;
    private ViewGroup container;
    private LinearLayout clipartLayout;
    private boolean isHide = true;


    public DrawClipArtOnMainFrames(Context context, Adapter adapter, MainView mainView, ArrayList<GalleryItem> galleryItemArrayList, ViewGroup container, LinearLayout clipartLayout, boolean isHide) {
        this.context = context;
        this.adapter = adapter;
        this.mainView = mainView;
        this.galleryItemArrayList = galleryItemArrayList;
        this.container = container;
        this.clipartLayout = clipartLayout;
        this.isHide = isHide;
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < galleryItemArrayList.size(); i++) {
            if (galleryItemArrayList.get(i).isSeleted()) {
                Bitmap resultBitmap = galleryItemArrayList.get(i).getBitmap();
                Canvas canvas = new Canvas(resultBitmap);
                Paint paint = new Paint();

                if (clipart != null) {
                    Matrix transformMatrix = new Matrix();
                    transformMatrix.postRotate(clipart.getRotation(), clipart.getBitmap().getWidth() / 2, clipart.getBitmap().getHeight() / 2);
                    transformMatrix.postTranslate(clipart.getX() * Math.min(resultBitmap.getWidth(), resultBitmap.getHeight()) / Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()), clipart.getY());
                    transformMatrix.postScale(clipart.getScaleX(), clipart.getScaleY());
                    canvas.drawBitmap(resultBitmap, 0, 0, paint);
                    canvas.scale((float) Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()) / mainView.getWidth(), (float) Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()) / mainView.getWidth(), 0, 0);
                    canvas.drawBitmap(clipart.getBitmap(), transformMatrix, paint);
                } else {
                    canvas.drawBitmap(resultBitmap, 0, 0, paint);
                }
                publishProgress(i);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressDialog != null) {
            progressDialog.setProgress(values[0] + 1);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        clipart = mainView.getClipartItem();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(galleryItemArrayList.size());
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
        container.removeView(mainView);
        clipartLayout.animate().translationY(200);
        isHide = true;
    }

}
