package com.example.intern.giftest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;


public class MainActivity extends AppCompatActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    //private File myDir = new File(root + "/test_images");
    private static Context context;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;

    private GifDrawable gifDrawable = null;
    private AnimatedGifEncoder animatedGifEncoder;

    private CustomGalleryAdapter customGalleryAdapter;
    private ArrayList<CustomGalleryItem> customGalleryArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    public void init() {

        Utils.initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        context = this;
        Utils.craeteDir("test_images");

        customGalleryAdapter = new CustomGalleryAdapter(customGalleryArrayList, this);

        recyclerView = (RecyclerView) findViewById(R.id.gallery_rec_view);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(customGalleryAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2));

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        new MyTask().execute();

        animatedGifEncoder = new AnimatedGifEncoder();

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

            new MyTask1().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Context getContext() {
        return context;
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            customGalleryArrayList.addAll(Utils.getGalleryPhotos(MainActivity.this));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            customGalleryAdapter.notifyDataSetChanged();
        }
    }

    class MyTask1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            String[] pathsForDecoding = new String[customGalleryAdapter.getSelected().size()];
            for (int i = 0; i < customGalleryAdapter.getSelected().size(); i++) {
                pathsForDecoding[i] = customGalleryAdapter.getSelected().get(i).toString();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            for (int i = 0; i < customGalleryAdapter.getSelected().size(); i++) {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync("file://" + customGalleryAdapter.getSelected().get(i).toString());
                bitmap = Utils.scaleCenterCrop(bitmap, 300, 300);
                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmaps.add(mutableBitmap);
                animatedGifEncoder.addFrame(mutableBitmap);
            }
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(root + "/test.gif");
                outStream.write(generateGIF(bitmaps));
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(getContext(), SecondActivity.class);
            intent.putExtra("path", root + "/test.gif");
            startActivity(intent);
            finish();
        }
    }

    public byte[] generateGIF(ArrayList<Bitmap> bitmaps) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
        encoder.setRepeat(0);
        //encoder.setDelay(500);
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }

}
