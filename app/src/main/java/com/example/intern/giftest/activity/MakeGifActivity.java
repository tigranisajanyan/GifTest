package com.example.intern.giftest.activity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import android.view.DragEvent;
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
import com.example.intern.giftest.R;
import com.example.intern.giftest.adapter.Adapter;
import com.example.intern.giftest.adapter.BitmapRecyclerViewAdapter;
import com.example.intern.giftest.clipart.Clipart;
import com.example.intern.giftest.clipart.MainView;
import com.example.intern.giftest.effects.BoostEffect;
import com.example.intern.giftest.effects.EffectSelectorAdapter;
import com.example.intern.giftest.effects.Effects;
import com.example.intern.giftest.effects.EmbossEffect;
import com.example.intern.giftest.effects.EngraveEffect;
import com.example.intern.giftest.effects.GrayScaleEffect;
import com.example.intern.giftest.effects.ReflectionEffect;
import com.example.intern.giftest.effects.SnowEffect;
import com.example.intern.giftest.effects.Utils.EffectsItem;
import com.example.intern.giftest.helper.SimpleItemTouchHelperCallback;
import com.example.intern.giftest.utils.AddEffect;
import com.example.intern.giftest.items.GalleryItem;
import com.example.intern.giftest.R;
import com.example.intern.giftest.utils.GifImitation;
import com.example.intern.giftest.utils.GifItConst;
import com.example.intern.giftest.utils.MergeTwoGifs;
import com.example.intern.giftest.utils.SaveGIFAsyncTask;
import com.example.intern.giftest.utils.SpacesItemDecoration;
import com.example.intern.giftest.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
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
    private Button addGifButton;
    private ImageView imageView;
    private GifImageView gifImageView;
    private LinearLayout clipartLayout;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;
    private MainView mainView;

    private ArrayList<GalleryItem> array = new ArrayList<>();

    private int speed = 500;
    private int displayWidth;
    private int displayHeight;
    private String videoPath;
    private boolean isHide = true;
    private GifImitation gifImitation;
    private GifDrawable gifDrawable;
	private RecyclerView actionsRecyclerView = null;
	private BitmapRecyclerViewAdapter effectsAdapter = null;

    private ViewGroup container;

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


		LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext());
		horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

		actionsRecyclerView = (RecyclerView) findViewById(R.id.actions_recycler_view);
		actionsRecyclerView.setLayoutManager(horizontalLayoutManager);

		effectsAdapter = new BitmapRecyclerViewAdapter();
		actionsRecyclerView.setAdapter(effectsAdapter);
        if (getIntent().getIntExtra(GifItConst.INDEX, 0) == GifItConst.IMAGES_TO_GIF_INDEX) {

            ArrayList<String> arrayList = getIntent().getStringArrayListExtra(GifItConst.IMAGE_PATHS);
            for (int i = 0; i < arrayList.size(); i++) {
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(GifItConst.FILE_PREFIX + arrayList.get(i));
                bitmap = Utils.scaleCenterCrop(bitmap, GifItConst.FRAME_SIZE, GifItConst.FRAME_SIZE);

                GalleryItem galleryItem = new GalleryItem(bitmap, arrayList.get(i), true, true, bitmap.getWidth(), bitmap.getHeight());
                array.add(galleryItem);
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
                    array.add(galleryItem);
                }
            }
            ByteBuffer buffer = PhotoUtils.readBufferFromFile(files[files.length - 1].getAbsolutePath(), PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
            Bitmap bitmap = PhotoUtils.fromBufferToBitmap(PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL), PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL), buffer);
            array.add(new GalleryItem(bitmap, files[files.length - 1].getAbsolutePath(), true, false, bitmap.getWidth(), bitmap.getHeight()));
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
                    array.add(galleryItem);
                }
            }
        }

        init();
        //intiCliparts();
        initEffects();


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
            if (gifImageView.getVisibility() == View.VISIBLE) {
                ArrayList<Bitmap> bitmaps = Utils.getGifFramesFromResources(MakeGifActivity.this, R.drawable.giff);
                MergeTwoGifs mergeTwoGifs = new MergeTwoGifs(array, bitmaps, MakeGifActivity.this, (int) gifImageView.getX(), (int) gifImageView.getY());
                mergeTwoGifs.mergeFrames();
                gifImageView.setVisibility(View.GONE);
            }
            new DrawClipArtOnMainFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        imageView = (ImageView) findViewById(R.id.image);
        gifImageView = (GifImageView) findViewById(R.id.gif_art);
        addClipArtButton = (Button) findViewById(R.id.add_clipart);
        addEffectButton = (Button) findViewById(R.id.add_effect);
        addGifButton = (Button) findViewById(R.id.add_gif);
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
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        gifImitation = new GifImitation(MakeGifActivity.this, imageView, array, 500);
        gifImitation.start();

        Bitmap bm = Bitmap.createBitmap(GifItConst.FRAME_SIZE, GifItConst.FRAME_SIZE, Bitmap.Config.ARGB_8888);
        bm.eraseColor(Color.TRANSPARENT);
        Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mainView = new MainView(this, mutableBitmap);
        mainView.setId(R.id.mainViewId);

        clipartLayout = (LinearLayout) findViewById(R.id.clipart_horizontal_list_container);
        clipartLayout.setVisibility(View.VISIBLE);
        clipartLayout.animate().translationY(200);

        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.giff);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    clipartLayout.animate().translationY(0);
                    container.addView(mainView);
                    isHide = false;
                } else {
                    clipartLayout.animate().translationY(200);
                    container.removeView(mainView);
                    isHide = true;
                }
            }
        });


        addEffectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                Bitmap bitmap = array.get(0).getBitmap();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//                Intent intent = new Intent(MakeGifActivity.this, VideoEffectsActivity.class);
//                intent.putExtra("bytearray", byteArray);
//                startActivityForResult(intent, 333);
				if (isHide) {
					clipartLayout.animate().translationY(0);
					container.addView(mainView);
					isHide = false;
				} else {
					clipartLayout.animate().translationY(200);
					container.removeView(mainView);
					isHide = true;
				}
            }
        });

        addGifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gifImageView.getVisibility() == View.GONE) {
                    gifImageView.setVisibility(View.VISIBLE);
                } else {
                    gifImageView.setVisibility(View.GONE);
                }

            }
        });

        gifImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                gifImageView.startDrag(ClipData.newPlainText("", ""), new View.DragShadowBuilder(gifImageView), null, 0);
                return false;
            }
        });

        container.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {

                final int action = event.getAction();
                switch (action) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;

                    case DragEvent.ACTION_DROP:
                        gifImageView.setX(event.getX() - gifImageView.getWidth() / 2);
                        gifImageView.setY(event.getY() - gifImageView.getHeight() / 2);
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 333 && resultCode == RESULT_OK) {
            int num = data.getIntExtra("position", 0);
            AddEffect addEffect = new AddEffect(array, num, adapter, MakeGifActivity.this);
            addEffect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void intiCliparts() {

        clipartLayout = (LinearLayout) findViewById(R.id.clipart_horizontal_list_container);
        clipartLayout.removeAllViews();
        clipartLayout.animate().translationY(200);

        for (int i = 0; i < clipartList.length; i++) {

            ImageView imgView = new ImageView(this);
            int size = (int) Utils.dpToPixel(35, this);
            int margin = (int) Utils.dpToPixel(7, this);
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
            clipartLayout.addView(imgView);
        }

        clipartLayout.setVisibility(View.VISIBLE);
    }

    private void initEffects() {
//		EffectsItem item1 = new EffectsItem(bitmap, new GrayScaleEffect(VideoEffectsActivity.this));
//		EffectsItem item2 = new EffectsItem(bitmap, new ReflectionEffect(VideoEffectsActivity.this));
//		EffectsItem item3 = new EffectsItem(bitmap, new SnowEffect(VideoEffectsActivity.this));
//		EffectsItem item4 = new EffectsItem(bitmap, new BoostEffect(VideoEffectsActivity.this));
//		EffectsItem item5 = new EffectsItem(bitmap, new EngraveEffect(VideoEffectsActivity.this));
//		EffectsItem item6 = new EffectsItem(bitmap, new EmbossEffect(VideoEffectsActivity.this));
//		adapter.addItem(item1);
//		adapter.addItem(item2);
//		adapter.addItem(item3);
//		adapter.addItem(item4);
//		adapter.addItem(item5);
//		adapter.addItem(item6);


		AsyncTask<Void, Void, ArrayList<Bitmap>> loadEffects = new AsyncTask<Void, Void, ArrayList<Bitmap>>() {
			@Override
			protected ArrayList<Bitmap> doInBackground(Void... params) {
				System.out.println("start effect loading");
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(array.get(0).getBitmap(), 100, 100, false);
				ArrayList<Bitmap> bitmapsArray = new ArrayList<>();
				bitmapsArray.add(Effects.snowEffect(scaledBitmap));
				bitmapsArray.add(Effects.boost(scaledBitmap, 1, 50));
				bitmapsArray.add(Effects.emboss(scaledBitmap));
				bitmapsArray.add(Effects.engrave(scaledBitmap));
				bitmapsArray.add(Effects.reflection(scaledBitmap));
				bitmapsArray.add(Effects.grayscale(scaledBitmap));
				return bitmapsArray;
			}

			@Override
			protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
				super.onPostExecute(bitmaps);
				System.out.println("count = "+bitmaps.size());
				for (Bitmap bmp : bitmaps ) {
					effectsAdapter.addItem(bmp);
				}
				effectsAdapter.notifyDataSetChanged();
			}
		};

		loadEffects.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//        clipartLayout.removeAllViews();
//        ImageView imgView = new ImageView(this);
//        int size = (int) Utils.dpToPixel(35, this);
//        int margin = (int) Utils.dpToPixel(7, this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
//        layoutParams.setMargins(margin, margin, margin, margin);
//        imgView.setLayoutParams(layoutParams);
//        imgView.setImageBitmap(Effects.snowEffect(array.get(0).getBitmap()));
//        imgView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//addClipart(position);
//			}
//		});
//        clipartLayout.addView(imgView);

    }

    private void addClipart(int position) {
        if (mainView != null) {
            mainView.addClipart(clipartList[position]);
        }
    }

    class DrawClipArtOnMainFrame extends AsyncTask<Void, Void, Void> {

        Clipart clipart = mainView.getClipartItem();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Utils.getGifFramesPath(root + "/mygif.gif");
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).isSeleted()) {
                    Bitmap resultBitmap = array.get(i).getBitmap();
                    Canvas canvas = new Canvas(resultBitmap);
                    Paint paint = new Paint();

                    if (clipart != null) {
                        Matrix transformMatrix = new Matrix();
                        transformMatrix.postRotate(clipart.getRotation(), clipart.getBitmap().getWidth() / 2, clipart.getBitmap().getHeight() / 2);
                        transformMatrix.postTranslate(clipart.getX() * Math.min(resultBitmap.getWidth(), resultBitmap.getHeight()) / Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()), clipart.getY());
                        transformMatrix.postScale(clipart.getScaleX(), clipart.getScaleY());
                        canvas.drawBitmap(resultBitmap, 0, 0, paint);
                        canvas.scale((float) Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()) / mainView.getWidth(), (float) Math.max(resultBitmap.getWidth(), resultBitmap.getHeight()) / mainView.getWidth(), 0, 0);
                        canvas.drawBitmap(clipart.getBitmap(), transformMatrix, paint);
                    } else {
                        canvas.drawBitmap(resultBitmap, 0, 0, paint);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            container.removeView(mainView);
            clipartLayout.animate().translationY(200);
            isHide = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gifImitation.stop();
    }

}
