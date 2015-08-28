package com.example.intern.giftest.gifutils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.intern.giftest.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tigran on 8/28/15.
 */
public class GetGifFrames extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private String gifPath;
    private int delay;
    private int frameCount;
    private int loopCount;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ProgressDialog progressDialog;

    public int getDelay() {
        return delay;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }


    public GetGifFrames(String gifPath, Context context) {
        this.context = context;
        this.gifPath = gifPath;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(bitmaps.size());
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        byte[] bytes = Utils.fileToByteArray(gifPath);
        GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(bytes);
        gifDecoder.advance();
        delay = gifDecoder.getNextDelay();
        frameCount = gifDecoder.getFrameCount();
        loopCount = gifDecoder.getLoopCount();

        for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
            Bitmap bitmap = gifDecoder.getNextFrame();
            bitmaps.add(bitmap);
            gifDecoder.advance();
            publishProgress(i);
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
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
    }

}
