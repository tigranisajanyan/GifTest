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
import com.example.intern.giftest.utils.SaveGIFAsyncTask;
import com.example.intern.giftest.utils.SpacesItemDecoration;
import com.example.intern.giftest.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MakeGifActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private SeekBar seekBar;
    private Button addClipArtButton;
    private Button applyButton;
    private ImageView imageView;
    private LinearLayout container;

    private Intent intent;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;
    private MainView mainView;

    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<GalleryItem> array = new ArrayList<>();
    private ArrayList<Bitmap> selected = new ArrayList<>();


    private int index = 0;
    private int speed = 500;
    private int displayWidth;
    private String videoPath;
    public static final float min = 0.1f;
    private boolean isHide = true;

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
                GalleryItem galleryItem = new GalleryItem();
                galleryItem.setImagePath(arrayList.get(i));
                galleryItem.setBitmap(bitmap);
                galleryItem.setIsSeleted(true);
                array.add(galleryItem);
            }
        } else if (intent.getIntExtra(GifsArtConst.INDEX, 0) == 2) {

            index = 2;
            videoPath = intent.getStringExtra(GifsArtConst.VIDEO_PATH);
            File file = new File(root, GifsArtConst.MY_DIR);
            File[] files = file.listFiles();
            int x = files.length / 10 + 1;
            for (int i = 0; i < files.length; i++) {
                if (i % x == 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);
                    Matrix m = new Matrix();
                    m.preScale(-1, 1);
                    Bitmap dst = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
                    dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                    GalleryItem galleryItem = new GalleryItem();
                    galleryItem.setBitmap(dst);
                    galleryItem.setIsSeleted(true);
                    array.add(galleryItem);
                }
            }
        } else {

            index = 3;
            videoPath = intent.getStringExtra(GifsArtConst.VIDEO_PATH);
            File file = new File(root, GifsArtConst.MY_DIR);
            File[] files = file.listFiles();
            int x = files.length / 10 + 1;
            for (int i = 0; i < files.length; i++) {
                if (i % x == 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);
                    Matrix m = new Matrix();
                    m.preScale(-1, 1);
                    Bitmap dst = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
                    dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
                    GalleryItem galleryItem = new GalleryItem();
                    galleryItem.setBitmap(dst);
                    galleryItem.setIsSeleted(true);
                    array.add(galleryItem);
                }
            }
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(displayWidth, displayWidth);
        findViewById(R.id.main_view_container).setLayoutParams(layoutParams);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        imageView = (ImageView) findViewById(R.id.image);
        addClipArtButton = (Button) findViewById(R.id.add_clipart);
        applyButton = (Button) findViewById(R.id.apply);
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

        final GifImitation gifImitation = new GifImitation(MakeGifActivity.this, imageView, array, 500);
        gifImitation.start();

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
                    isHide = true;
                }
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int corePoolSize = 60;
                int maximumPoolSize = 80;
                int keepAliveTime = 10;
                BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(maximumPoolSize);
                Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
                new MyTask().executeOnExecutor(threadPoolExecutor);
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

            int corePoolSize = 60;
            int maximumPoolSize = 80;
            int keepAliveTime = 10;
            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(maximumPoolSize);
            Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

            SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + "/test_images/test.gif", array, speed, MakeGifActivity.this);
            saveGIFAsyncTask.executeOnExecutor(threadPoolExecutor);

            /*GifEncoder gifEncoder = new GifEncoder();
            gifEncoder.init(root + "/test_images/test.gif", 640, 640, 256, 100, 100);
            int[] pixels = new int[640 * 640];
            for (int i = 0; i < bitmaps.size(); i++) {
                bitmaps.get(i).getPixels(pixels, 0, 640, 0, 0, 640, 640);
                gifEncoder.addFrame(pixels);
            }
            gifEncoder.close();*/

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        Bitmap bm = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        bm.eraseColor(Color.TRANSPARENT);
        Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mainView = new MainView(this, mutableBitmap);
        mainView.setId(R.id.mainViewId);
        ViewGroup container = (ViewGroup) findViewById(R.id.main_view_container);
        container.addView(mainView);
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < array.size(); i++) {
                Bitmap resultBitmap = array.get(i).getBitmap();
                Canvas canvas = new Canvas(resultBitmap);
                Paint paint = new Paint();

                canvas.drawBitmap(resultBitmap, 0, 0, paint);
                canvas.drawBitmap(clipart.getBitmap(), clipart.getX(), clipart.getY(), paint);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
        }
    }

}
