package com.example.intern.giftest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.com.picsart.studio.gifencoder.GifEncoder;
import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.example.intern.giftest.adapter.Adapter;
import com.example.intern.giftest.gifutils.AnimatedGifEncoder;
import com.example.intern.giftest.utils.GalleryItem;
import com.example.intern.giftest.R;
import com.example.intern.giftest.utils.GifImitation;
import com.example.intern.giftest.utils.SpacesItemDecoration;
import com.example.intern.giftest.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialin.android.encoder.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MakeGifActivity extends ActionBarActivity {


    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");
    public static final String FILE_PREFIX = "file://";

    private GifImageView gifImageView;
    private SeekBar seekBar;
    private TextView delayText;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;

    private GifDrawable gifDrawable = null;

    private Intent intent;
    private ProgressDialog progressDialog;

    private int gifImageSize;
    private int frameCount;
    private int speed = 500;

    public static final float min = 0.1f;

    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<GalleryItem> array = new ArrayList<>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<Bitmap> selected = new ArrayList<>();
    private String gifPath;

    ImageView imageView;
    int index = 0;
    String videoPath;

    private GifEncoder gifEncoder;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        intent = getIntent();


        if (intent.getIntExtra("index", 0) == 1) {

            index = 1;
            arrayList = intent.getStringArrayListExtra("image_paths");
            for (int i = 0; i < arrayList.size(); i++) {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(FILE_PREFIX + arrayList.get(i));
                bitmap = Utils.scaleCenterCrop(bitmap, 640, 640);
                bitmaps.add(bitmap);
                GalleryItem galleryItem = new GalleryItem();
                galleryItem.setImagePath(arrayList.get(i));
                galleryItem.setBitmap(bitmap);
                galleryItem.setIsSeleted(true);
                array.add(galleryItem);
            }
        } else if (intent.getIntExtra("index", 0) == 2) {

            index = 2;
            videoPath = intent.getStringExtra("path");
            File file = new File(root + "/test_images");
            File[] files = file.listFiles();
            int x = files.length / 10 + 1;
            for (int i = 0; i < files.length; i++) {
                if (i % x == 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);
                    bitmaps.add(bitmap);
                    GalleryItem galleryItem = new GalleryItem();
                    galleryItem.setBitmap(bitmap);
                    galleryItem.setIsSeleted(true);
                    array.add(galleryItem);
                }
            }
        } else {

            index = 3;
            videoPath = intent.getStringExtra("path");
            File file = new File(root + "/test_images");
            File[] files = file.listFiles();
            int x = files.length / 10 + 1;
            for (int i = 0; i < files.length; i++) {
                if (i % x == 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);
                    bitmaps.add(bitmap);
                    GalleryItem galleryItem = new GalleryItem();
                    galleryItem.setBitmap(bitmap);
                    galleryItem.setIsSeleted(true);
                    array.add(galleryItem);
                }
            }
        }

        gifImageView = (GifImageView) findViewById(R.id.gif_image);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);

        imageView = (ImageView) findViewById(R.id.image);
        delayText = (TextView) findViewById(R.id.delay_text);

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

        final GifImitation gifImitation = new GifImitation(MakeGifActivity.this, imageView, bitmaps, 200);
        gifImitation.start();
        //new MyTask().execute();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = 100 + (progress * 50);
                delayText.setText("delay: " + speed + " mls");
                gifImitation.changeDuration(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(root + "/test_images/test.gif");
                outStream.write(generateGIF(bitmaps, speed));
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*GifEncoder gifEncoder = new GifEncoder();
            gifEncoder.init(root + "/test.gif", 640, 640, 256, 100, speed);
            int[] pixels = new int[ 640*640];
            bitmaps.get(0).getPixels(pixels, 0, 640, 0, 0, 640, 640);
            gifEncoder.addFrame(pixels);
            gifEncoder.addFrame(pixels);
            gifEncoder.addFrame(pixels);
            gifEncoder.addFrame(pixels);*/

            //new MyTask().execute();

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

        AnimatedGifEncoder encoder;
        ByteArrayOutputStream bos;
        byte[] bytes;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bos = new ByteArrayOutputStream();
            encoder = new AnimatedGifEncoder();
            encoder.start(bos);
            encoder.setDelay(speed);
            encoder.setRepeat(0);
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (Bitmap bitmap : bitmaps) {
                encoder.addFrame(bitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            encoder.finish();
            bytes = bos.toByteArray();
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(root + "/test_images/test.gif");
                outStream.write(bytes);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

}
