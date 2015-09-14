package com.example.intern.giftest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.example.intern.giftest.R;
import com.example.intern.giftest.adapter.Adapter;
import com.example.intern.giftest.adapter.ClipartsPreviewAdapter;
import com.example.intern.giftest.adapter.EffectsPreviewAdapter;
import com.example.intern.giftest.adapter.GiphyAdapter;
import com.example.intern.giftest.clipart.Clipart;
import com.example.intern.giftest.clipart.MainView;
import com.example.intern.giftest.effects.BoostEffect;
import com.example.intern.giftest.effects.EffectItem;
import com.example.intern.giftest.effects.Effects;
import com.example.intern.giftest.effects.EmbossEffect;
import com.example.intern.giftest.effects.EngraveEffect;
import com.example.intern.giftest.effects.GrayScaleEffect;
import com.example.intern.giftest.effects.RecyclerItemClickListener;
import com.example.intern.giftest.effects.ReflectionEffect;
import com.example.intern.giftest.effects.SnowEffect;
import com.example.intern.giftest.helper.SimpleItemTouchHelperCallback;
import com.example.intern.giftest.items.ClipArtItem;
import com.example.intern.giftest.items.GiphyItem;
import com.example.intern.giftest.utils.AddEffect;
import com.example.intern.giftest.items.GalleryItem;
import com.example.intern.giftest.utils.DrawClipArtOnMainFrames;
import com.example.intern.giftest.utils.GifImitation;
import com.example.intern.giftest.utils.GifItConst;
import com.example.intern.giftest.utils.SaveGIFAsyncTask;
import com.example.intern.giftest.utils.SpacesItemDecoration;
import com.example.intern.giftest.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;


public class MakeGifActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private SeekBar speedSeekBar;
    private ImageView imageView;
    private SimpleDraweeView gifImageView;
    private VideoView videoView;
    private LinearLayout clipartLayout;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;
    private MainView mainView;

    private ArrayList<GalleryItem> galleryItemArrayList = new ArrayList<>();
    private ArrayList<String> giphyItemUrls = new ArrayList<>();

    private int speed = 500;
    private int displayWidth;
    private int displayHeight;
    private String videoPath;
    private boolean isHide = true;
    private GifImitation gifImitation;
    private GifDrawable gifDrawable;
    private RecyclerView actionsRecyclerView = null;
    private EffectsPreviewAdapter effectsAdapter = null;
    private ClipartsPreviewAdapter clipartsAdapter = null;
    private GiphyAdapter giphyAdapter = null;

    private ViewGroup container;

    public static final int ACTION_ADD_EFFECT = 10;
    public static final int ACTION_ADD_CLIPART = 11;
    public static final int ACTION_ADD_GIF = 12;

    public int selectedAction = 0;

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

        if (getIntent().getIntExtra(GifItConst.INDEX, 0) == GifItConst.IMAGES_TO_GIF_INDEX) {

            ArrayList<String> arrayList = getIntent().getStringArrayListExtra(GifItConst.IMAGE_PATHS);
            for (int i = 0; i < arrayList.size(); i++) {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifItConst.FILE_PREFIX + arrayList.get(i));
                bitmap = Utils.scaleCenterCrop(bitmap, GifItConst.FRAME_SIZE, GifItConst.FRAME_SIZE);

                GalleryItem galleryItem = new GalleryItem(bitmap, arrayList.get(i), true, true, bitmap.getWidth(), bitmap.getHeight());
                galleryItemArrayList.add(galleryItem);
            }

        } else if (getIntent().getIntExtra(GifItConst.INDEX, 0) == GifItConst.SHOOT_GIF_INDEX) {

            videoPath = getIntent().getStringExtra(GifItConst.VIDEO_PATH);
            File file = new File(root, GifItConst.MY_DIR);
            File[] files = file.listFiles();
            int x = files.length / GifItConst.GENERATED_FRAMES_MAX_COUNT + 1;
            for (int i = 0; i < files.length; i++) {
                if (i % x == 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);

                    GalleryItem galleryItem = new GalleryItem(bitmap, files[i].getAbsolutePath(), true, false, bitmap.getWidth(), bitmap.getHeight());
                    galleryItemArrayList.add(galleryItem);
                }
            }
            ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[files.length - 1].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
            Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);
            galleryItemArrayList.add(new GalleryItem(bitmap, files[files.length - 1].getAbsolutePath(), true, false, bitmap.getWidth(), bitmap.getHeight()));
        } else {

            videoPath = getIntent().getStringExtra(GifItConst.VIDEO_PATH);
            File file = new File(root, GifItConst.MY_DIR);
            File[] files = file.listFiles();
            int x = files.length / GifItConst.GENERATED_FRAMES_MAX_COUNT + 1;
            for (int i = 0; i < files.length; i++) {
                if (i % x == 0) {
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[i].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);

                    GalleryItem galleryItem = new GalleryItem(bitmap, files[i].getAbsolutePath(), true, false, bitmap.getWidth(), bitmap.getHeight());
                    galleryItemArrayList.add(galleryItem);
                }
            }
        }

        init();
        initCliparts();
        initEffects();
        initGipfy();

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
            SaveGIFAsyncTask saveGIFAsyncTask = new SaveGIFAsyncTask(root + GifItConst.SLASH + GifItConst.MY_DIR + GifItConst.SLASH + GifItConst.GIF_NAME, galleryItemArrayList, speed, adapter, MakeGifActivity.this);
            saveGIFAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (id == R.id.action_save) {
            DrawClipArtOnMainFrames drawClipArtOnMainFrames = new DrawClipArtOnMainFrames(MakeGifActivity.this, adapter, mainView, galleryItemArrayList, container, clipartLayout, isHide);
            drawClipArtOnMainFrames.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return true;
    }

    private void init() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        displayWidth = metrics.widthPixels;
        displayHeight = metrics.heightPixels;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(displayWidth, displayWidth);
        container = (ViewGroup) findViewById(R.id.main_view_container);
        container.setLayoutParams(layoutParams);

        speedSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        imageView = (ImageView) findViewById(R.id.image);
        gifImageView = (SimpleDraweeView) findViewById(R.id.gif_art);
        videoView = (VideoView) findViewById(R.id.video_view);
        recyclerView = (RecyclerView) findViewById(R.id.rec_view);

        adapter = new Adapter(galleryItemArrayList, this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        gifImitation = new GifImitation(MakeGifActivity.this, imageView, galleryItemArrayList, 500);
        gifImitation.start();

        Bitmap bm = Bitmap.createBitmap(GifItConst.FRAME_SIZE, GifItConst.FRAME_SIZE, Bitmap.Config.ARGB_8888);
        bm.eraseColor(Color.TRANSPARENT);
        Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mainView = new MainView(this, mutableBitmap);
        mainView.setId(R.id.mainViewId);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext());
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        actionsRecyclerView = (RecyclerView) findViewById(R.id.actions_recycler_view);
        actionsRecyclerView.setLayoutManager(horizontalLayoutManager);
        actionsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (selectedAction) {
                    case ACTION_ADD_CLIPART:
                        addClipart(position);
                        break;
                    case ACTION_ADD_EFFECT:
                        EffectItem selectedItem = effectsAdapter.getItem(position);
                        selectedItem.getAction().startAction(galleryItemArrayList);
                        break;
                    case ACTION_ADD_GIF:
                        Uri uri = Uri.parse(giphyItemUrls.get(position));
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setUri(uri)
                                .setAutoPlayAnimations(true).build();
                        gifImageView.setController(controller);
                        break;

                }
            }
        }));

        effectsAdapter = new EffectsPreviewAdapter();
        clipartsAdapter = new ClipartsPreviewAdapter();
        giphyAdapter = new GiphyAdapter();
        actionsRecyclerView.setAdapter(effectsAdapter);

        clipartLayout = (LinearLayout) findViewById(R.id.clipart_horizontal_list_container);
        clipartLayout.setVisibility(View.VISIBLE);
        clipartLayout.animate().translationY(200);

        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.giff);
        } catch (IOException e) {
            e.printStackTrace();
        }

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        findViewById(R.id.add_clipart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAction = ACTION_ADD_CLIPART;
                actionsRecyclerView.setAdapter(clipartsAdapter);
                if (isHide) {
                    clipartLayout.animate().translationY(0);
                    container.addView(mainView);
                    isHide = false;
                } else if (selectedAction == ACTION_ADD_CLIPART) {
                    clipartLayout.animate().translationY(200);
                    container.removeView(mainView);
                    isHide = true;
                }
                /*boolean actionChanged = selectedAction == ACTION_ADD_CLIPART;
                if (isHide) {
                    clipartLayout.animate().translationY(0);
                    container.addView(mainView);
                    isHide = false;
                } else if (actionChanged) {
                    clipartLayout.animate().translationY(200);
                    container.removeView(mainView);
                    isHide = true;
                }*/
            }
        });

        findViewById(R.id.add_effect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAction = ACTION_ADD_EFFECT;
                actionsRecyclerView.setAdapter(effectsAdapter);
                if (isHide) {
                    clipartLayout.animate().translationY(0);
                    container.addView(mainView);
                    isHide = false;
                } else if (selectedAction == ACTION_ADD_EFFECT) {
                    clipartLayout.animate().translationY(200);
                    container.removeView(mainView);
                    isHide = true;
                }
                /*boolean actionChanged = selectedAction == ACTION_ADD_EFFECT;
                if (isHide) {
                    clipartLayout.animate().translationY(0);
                    container.addView(mainView);
                    isHide = false;
                } else if (actionChanged) {
                    clipartLayout.animate().translationY(200);
                    container.removeView(mainView);
                    isHide = true;
                }*/
            }
        });

        findViewById(R.id.add_gif_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionsRecyclerView.setAdapter(giphyAdapter);
                boolean actionChanged = selectedAction == ACTION_ADD_GIF;
                selectedAction = ACTION_ADD_GIF;
                if (isHide) {
                    clipartLayout.animate().translationY(0);
                    container.addView(mainView);
                    isHide = false;
                } else if (actionChanged) {
                    clipartLayout.animate().translationY(200);
                    container.removeView(mainView);
                    isHide = true;
                }
                clipartLayout.animate().translationY(0);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 333 && resultCode == RESULT_OK) {
            int num = data.getIntExtra("position", 0);
            AddEffect addEffect = new AddEffect(galleryItemArrayList, num, adapter, MakeGifActivity.this);
            addEffect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void initCliparts() {
        for (int i = 0; i < clipartList.length; i++) {
            Bitmap clipartBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), clipartList[i]), 70, 70, false);
            ClipArtItem clipartItem = new ClipArtItem(clipartBitmap);
            clipartsAdapter.addItem(clipartItem);
        }

    }

    private void initEffects() {

        AsyncTask<Void, Void, ArrayList<EffectItem>> loadEffects = new AsyncTask<Void, Void, ArrayList<EffectItem>>() {
            @Override
            protected ArrayList<EffectItem> doInBackground(Void... params) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(galleryItemArrayList.get(0).getBitmap(), 70, 70, false);
                ArrayList<EffectItem> bitmapsArray = new ArrayList<>();
                bitmapsArray.add(new EffectItem(new SnowEffect(MakeGifActivity.this), Effects.snowEffect(scaledBitmap)));
                bitmapsArray.add(new EffectItem(new BoostEffect(MakeGifActivity.this), Effects.boost(scaledBitmap, 1, 50)));
                bitmapsArray.add(new EffectItem(new EmbossEffect(MakeGifActivity.this), Effects.emboss(scaledBitmap)));
                bitmapsArray.add(new EffectItem(new EngraveEffect(MakeGifActivity.this), Effects.engrave(scaledBitmap)));
                bitmapsArray.add(new EffectItem(new ReflectionEffect(MakeGifActivity.this), Effects.reflection(scaledBitmap)));
                bitmapsArray.add(new EffectItem(new GrayScaleEffect(MakeGifActivity.this), Effects.grayscale(scaledBitmap)));
                return bitmapsArray;
            }

            @Override
            protected void onPostExecute(ArrayList<EffectItem> effects) {
                super.onPostExecute(effects);
                for (EffectItem effect : effects) {
                    effectsAdapter.addItem(effect);
                }
                effectsAdapter.notifyDataSetChanged();
            }
        };

        loadEffects.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void initGipfy() {
        RequestQueue queue = Volley.newRequestQueue(MakeGifActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GifItConst.GIPHY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        JSONArray jsonArray = null;
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                giphyItemUrls.add(jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject(GifItConst.GIPHY_SIZE_PREVIEW).getString("url"));
                                giphyAdapter.addItem(new GiphyItem(giphyItemUrls.get(i), 0, 0));
                                giphyAdapter.notifyDataSetChanged();
                                Log.d(GifItConst.GIFIT_LOG, giphyItemUrls.get(i) + "");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(GifItConst.GIFIT_LOG, error + "");
            }
        });
        queue.add(stringRequest);
    }

    private void addClipart(int position) {
        if (mainView != null) {
            mainView.addClipart(clipartList[position]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gifImitation.stop();
    }

}
