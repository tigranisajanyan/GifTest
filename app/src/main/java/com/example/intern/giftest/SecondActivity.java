package com.example.intern.giftest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class SecondActivity extends ActionBarActivity {

    static {
        System.loadLibrary("gif");
    }

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");

    private GifImageView gifImageView;
    private SeekBar seekBar;
    private Button button;

    private GifDrawable gifDrawable = null;
    private AnimatedGifEncoder animatedGifEncoder;

    private Intent intent;

    public static final float min=0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        intent=getIntent();

        gifImageView = (GifImageView) findViewById(R.id.gif_image);
        seekBar=(SeekBar)findViewById(R.id.seek_bar);
        button = (Button) findViewById(R.id.but);

        try {
            gifDrawable = new GifDrawable(intent.getStringExtra("path"));
            gifDrawable.setSpeed(0.5f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifImageView.setImageDrawable(gifDrawable);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gifDrawable.setSpeed(min+(progress/10.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aaa);
                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
                for (int i = 0; i < 4; i++) {

                    //save(gifDrawable.seekToFrameAndGet(i), i);
                    bitmaps.add(mutableBitmap);
                    animatedGifEncoder.addFrame(mutableBitmap);

                }

                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(myDir + "/test.gif");
                    outStream.write(generateGIF(bitmaps));
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                Log.d("gagagaga", "done");

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private native String helloLog();

    //ndk.dir=/Users/intern/Documents/ndk/android-ndk-r10e

    public void save(Bitmap bitmap, int name) {

        String fname = "image_" + String.format("%03d", name) + ".jpg";

        try {
            File file = new File(myDir, fname);
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error while SaveToMemory", Toast.LENGTH_SHORT).show();
        }
    }

    public byte[] generateGIF(ArrayList<Bitmap> bitmaps) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

}
