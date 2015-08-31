package com.example.intern.giftest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.example.intern.giftest.adapter.Adapter;
import com.example.intern.giftest.clipart.Clipart;
import com.example.intern.giftest.clipart.MainView;
import com.example.intern.giftest.clipart.Util;
import com.example.intern.giftest.utils.GalleryItem;
import com.example.intern.giftest.R;
import com.example.intern.giftest.utils.GifImitation;
import com.example.intern.giftest.utils.GifsArtConst;
import com.example.intern.giftest.utils.MergeTwoGifs;
import com.example.intern.giftest.utils.SaveGIFAsyncTask;
import com.example.intern.giftest.utils.SpacesItemDecoration;
import com.example.intern.giftest.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MakeGifActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private SeekBar seekBar;
    private Button addClipArtButton;
    private Button addEffectButton;
    private ImageView imageView;
    private GifImageView gifImageView;
    private LinearLayout container;

    private Intent intent;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;
    private MainView mainView;

    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<GalleryItem> array = new ArrayList<>();

    private int index = 0;
    private int speed = 500;
    private int displayWidth;
    private String videoPath;
    private boolean isHide = true;
    private GifImitation gifImitation;
    private GifDrawable gifDrawable;

    ViewGroup container1;

    private final int[] clipartList = new int[]{
            R.drawable.clipart_1,
            R.drawable.clipart_2,
            R.drawable.clipart_3,
            R.drawable.clipart_4,
            R.drawable.clipart_5,
            R.drawable.clipart_6,
            R.drawable.clipart_7,
            R.drawable.clipart_8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_gif);

        intent = getIntent();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        displayWidth = metrics.widthPixels;

        if (intent.getIntExtra(GifsArtConst.INDEX, 0) == 1) {

            index = 1;
            arrayList = intent.getStringArrayListExtra(GifsArtConst.IMAGE_PATHS);
            for (int i = 0; i < arrayList.size(); i++) {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifsArtConst.FILE_PREFIX + arrayList.get(i), DisplayImageOptions.createSimple());
                bitmap = Utils.scaleCenterCrop(bitmap, GifsArtConst.FRAME_SIZE, GifsArtConst.FRAME_SIZE);

                GalleryItem galleryItem = new GalleryItem(bitmap, arrayList.get(i), true, true, bitmap.getWidth(), bitmap.getHeight());
                array.add(galleryItem);
            }
        } else if (intent.getIntExtra(GifsArtConst.INDEX, 0) == 2) {

            index = 2;
            videoPath = intent.getStringExtra(GifsArtConst.VIDEO_PATH);
            File file = new File(root, GifsArtConst.MY_DIR);
            File[] files = file.listFiles();
            int x = files.length / 15 + 1;
            for (int i = 0; i < files.length; i++) {
                if (i % x == 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);

                    GalleryItem galleryItem = new GalleryItem(bitmap, files[i].getAbsolutePath(), true, false, bitmap.getWidth(), bitmap.getHeight());
                    array.add(galleryItem);
                }
            }
            ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[files.length - 1].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
            Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);
            array.add(new GalleryItem(bitmap, files[files.length - 1].getAbsolutePath(), true, false, bitmap.getWidth(), bitmap.getHeight()));
        } else {

            index = 3;
            videoPath = intent.getStringExtra(GifsArtConst.VIDEO_PATH);
            File file = new File(root, GifsArtConst.MY_DIR);
            File[] files = file.listFiles();
            int x = files.length / 15 + 1;
            for (int i = 0; i < files.length; i++) {
                if (i % x == 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);

                    GalleryItem galleryItem = new GalleryItem(bitmap, files[i].getAbsolutePath(), true, false, bitmap.getWidth(), bitmap.getHeight());
                    array.add(galleryItem);
                }
            }
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(displayWidth, displayWidth);
        findViewById(R.id.main_view_container).setLayoutParams(layoutParams);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        imageView = (ImageView) findViewById(R.id.image);
        gifImageView = (GifImageView) findViewById(R.id.gif_art);
        addClipArtButton = (Button) findViewById(R.id.add_clipart);
        addEffectButton = (Button) findViewById(R.id.add_effect);
        recyclerView = (RecyclerView) findViewById(R.id.rec_view);


        adapter = new Adapter(array, this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));

        gifImitation = new GifImitation(MakeGifActivity.this, imageView, array, 500);
        gifImitation.start();

        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.giff);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initView();
        intiCliparts();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = 100 + (progress * 50);
                gifImitation.changeDuration(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        addClipArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHide) {
                    container.animate().translationY(0);
                    isHide = false;
                } else {
                    container.animate().translationY(200);
                    //container1.removeAllViews();
                    isHide = true;
                }
            }
        });


        addEffectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AddEffect addEffect = new AddEffect(array, 0, adapter, MakeGifActivity.this);
                addEffect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

                ArrayList<Bitmap> bitmaps = Utils.getGifFramesFromResources(MakeGifActivity.this, R.drawable.giff);
                for (int i = 0; i < bitmaps.size(); i++) {
                    Bitmap bitmap = bitmaps.get(i);
                    Log.d("gagagaga", "" + bitmap.getHeight());
                }

                MergeTwoGifs mergeTwoGifs = new MergeTwoGifs(array, bitmaps, MakeGifActivity.this);
                mergeTwoGifs.mergeFrames();

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

        if (id == R.id.action_export) {
            SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + "/test_images/test.gif", array, speed, adapter, MakeGifActivity.this);
            saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (id == R.id.action_save) {
            new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return true;
    }

    private void initView() {

        Bitmap bm = Bitmap.createBitmap(GifsArtConst.FRAME_SIZE, GifsArtConst.FRAME_SIZE, Bitmap.Config.ARGB_8888);
        bm.eraseColor(Color.TRANSPARENT);
        Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mainView = new MainView(this, mutableBitmap);
        mainView.setId(R.id.mainViewId);
        container1 = (ViewGroup) findViewById(R.id.main_view_container);
        container1.addView(mainView);
    }

    private void intiCliparts() {

        container = (LinearLayout) findViewById(R.id.clipart_horizontal_list_container);
        container.removeAllViews();
        container.animate().translationY(200);

        for (int i = 0; i < clipartList.length; i++) {
            //TODO replace to LinearHorizontalRecyclerView
            ImageView imgView = new ImageView(this);
            int size = (int) Util.dpToPixel(35, this);
            int margin = (int) Util.dpToPixel(7, this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.setMargins(margin, margin, margin, margin);
            imgView.setLayoutParams(layoutParams);
            imgView.setBackgroundResource(clipartList[i]);
            final int position = i;
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addClipart(position);
                }
            });
            container.addView(imgView);
        }

        container.setVisibility(View.VISIBLE);
    }

    private void addClipart(int position) {
        if (mainView != null) {
            mainView.addClipart(clipartList[position]);
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        Clipart clipart = mainView.getClipartItem();
        //ArrayList<Bitmap> list = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //list = Utils.getGifFramesPath(root + "/mygif.gif");
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < array.size(); i++) {
                Bitmap resultBitmap = array.get(i).getBitmap();
                Canvas canvas = new Canvas(resultBitmap);
                Paint paint = new Paint();

                if (clipart != null) {
                    Matrix transformMatrix = new Matrix();
                    transformMatrix.postRotate(clipart.getRotation(), clipart.getBitmap().getWidth() / 2, clipart.getBitmap().getHeight() / 2);
                    transformMatrix.postTranslate(clipart.getX(), clipart.getY());
                    transformMatrix.postScale(clipart.getScaleX(), clipart.getScaleY());
                    canvas.drawBitmap(resultBitmap, 0, 0, paint);
                    canvas.scale(360f / mainView.getWidth(), 640f / mainView.getWidth(), 0, 0);
                    canvas.drawBitmap(clipart.getBitmap(), transformMatrix, paint);
                } else {
                    canvas.drawBitmap(resultBitmap, 0, 0, paint);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gifImitation.stop();
        //Utils.clearDir(new File(root + "/" + GifsArtConst.MY_DIR));
    }

}
