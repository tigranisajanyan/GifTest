package com.example.intern.giftest.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.intern.giftest.adapter.Adapter;
import com.example.intern.giftest.gifutils.AnimatedGifEncoder;
import com.example.intern.giftest.items.GalleryItem;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tigran on 8/24/15.
 */
public class SaveGIFAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private String outputDir;
    private int speed;
    private Adapter adapter;
    private ArrayList<GalleryItem> bitmaps = new ArrayList<>();
    private ProgressDialog progressDialog;

    public SaveGIFAsyncTask(String outputDir, ArrayList<GalleryItem> bitmaps, int speed, Adapter adapter, Context context) {
        this.outputDir = outputDir;
        this.adapter = adapter;
        this.bitmaps = bitmaps;
        this.context = context;
        this.speed = speed;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(adapter.getSelected().size());
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        File outFile = new File(outputDir);
        try {

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
            animatedGifEncoder.setDelay(speed);
            animatedGifEncoder.setRepeat(0);
            animatedGifEncoder.start(bos);

            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i).isSeleted()) {
                    animatedGifEncoder.addFrame(bitmaps.get(i).getBitmap());
                    publishProgress(i);
                }
            }

            animatedGifEncoder.finish();
            bufferedOutputStream.write(bos.toByteArray());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

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
