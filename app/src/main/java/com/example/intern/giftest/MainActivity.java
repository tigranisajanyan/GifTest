package com.example.intern.giftest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("gif");
    }

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");

    GifImageView gifImageView;
    ImageView imageView;

    Button button;
    int frameCount;
    int duration;
    GifDrawable gifDrawable = null;
    AnimatedGifEncoder animatedGifEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gifImageView = (GifImageView) findViewById(R.id.gif_image);
        imageView = (ImageView) findViewById(R.id.image);


        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.open);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gifDrawable.setSpeed(2.0f);
        Log.d("gagagagagag", gifDrawable.getDuration() + "");


        gifImageView.setImageDrawable(gifDrawable);

        frameCount = gifDrawable.getNumberOfFrames();
        duration = gifDrawable.getDuration();

        animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.setFrameRate(5);

        button = (Button) findViewById(R.id.but);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageView.setImageBitmap(gifDrawable.seekToFrameAndGet(gifDrawable.getNumberOfFrames() - 1));


                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aaa);
                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutableBitmap);
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
                }

                Log.d("gagagaga","done");

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


}
