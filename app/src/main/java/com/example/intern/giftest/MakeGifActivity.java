package com.example.intern.giftest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.libvpx.LibVpxEnc;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialin.android.encoder.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MakeGifActivity extends ActionBarActivity {


    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");

    private GifImageView gifImageView;
    private SeekBar seekBar;
    private SeekBar seekBarSize;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;

    private GifDrawable gifDrawable = null;

    private Intent intent;
    private ProgressDialog progressDialog;

    private int gifImageSize;
    private int frameCount;
    private int speed;

    public static final float min = 0.1f;

    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<GalleryItem> array = new ArrayList<>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<Bitmap> selected = new ArrayList<>();
    private String gifPath;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        intent = getIntent();
        arrayList = intent.getStringArrayListExtra("image_paths");
        for (int i = 0; i < arrayList.size(); i++) {
            GalleryItem galleryItem = new GalleryItem();
            galleryItem.setImagePath(arrayList.get(i));
            galleryItem.setIsSeleted(true);
            array.add(galleryItem);
        }

        gifImageView = (GifImageView) findViewById(R.id.gif_image);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setProgress(9);
        seekBarSize = (SeekBar) findViewById(R.id.seek_bar_size);
        seekBarSize.setProgress(50);

        imageView = (ImageView) findViewById(R.id.image);

        /*progressDialog = new ProgressDialog(MakeGifActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();*/
        adapter = new Adapter(array, this);

        recyclerView = (RecyclerView) findViewById(R.id.rec_view);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));

        new MyTask().execute();

        /*seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = (1000 - (progress * 49));
                gifDrawable.setSpeed(min + (progress / 10.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/

        /*seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(gifImageSize + 2 * (progress - 50), gifImageSize + 2 * (progress - 50));
                layoutParams.setMargins(0, 20, 0, 0);
                gifImageView.setLayoutParams(layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/

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

            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync("file://" + arrayList.get(i));
                bitmap = Utils.scaleCenterCrop(bitmap, 400, 400);
                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmaps.add(mutableBitmap);
            }
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(root + "/test_images/test.gif");
                outStream.write(generateGIF(bitmaps, speed));
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public byte[] generateGIF(ArrayList<Bitmap> bitmaps, int speed) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
        encoder.setDelay(speed);
        encoder.setRepeat(0);
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


            for (int i = 0; i < arrayList.size(); i++) {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync("file://" + arrayList.get(i));
                bitmap = Utils.scaleCenterCrop(bitmap, 300, 300);
                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmaps.add(mutableBitmap);
            }
            /*FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(root + "/test.gif");
                outStream.write(generateGIF(bitmaps, 100));
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /*gifPath = root + "/test.gif";
            try {
                gifDrawable = new GifDrawable(gifPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            gifImageView.setImageDrawable(gifDrawable);
            gifImageSize = gifImageView.getWidth();
            progressDialog.dismiss();*/

            MyTask1 myTask1=new MyTask1();
            myTask1.execute(bitmaps);

            Encoder encoder=new Encoder();
            encoder.init(300,300,15,null);
            encoder.startVideoGeneration(new File(root + "/vid.mp4"));
            for (int i=0;i<bitmaps.size();i++){
                encoder.addFrame(bitmaps.get(i),50);
                Log.d("encoded frames", i + "  /  " + bitmaps.size());
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(root + "/vid.mp4"));
            intent.setDataAndType(Uri.parse(root + "/vid.mp4"), "video/mp4");
            MainActivity.getContext().startActivity(intent);

        }
    }

    class MyTask1 extends AsyncTask<ArrayList<Bitmap>, Bitmap, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(ArrayList<Bitmap>... urls) {
            try {
                for (int i = 0; i < 1000; i++) {
                    downloadFile();
                    publishProgress(bitmaps.get(i % urls[0].size()));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            imageView.setImageBitmap(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }


        private void downloadFile() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(50);
        }
    }


}
