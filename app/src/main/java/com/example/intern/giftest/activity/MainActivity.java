package com.example.intern.giftest.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.decoder.VideoDecoder;
import com.example.intern.giftest.R;
import com.example.intern.giftest.clipart.MainView;
import com.example.intern.giftest.utils.GifItConst;
import com.example.intern.giftest.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private TextView makeGifButton;
    private TextView shootingGifButton;
    private TextView videoToGifButton;

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        context = this;
        Utils.craeteDir(GifItConst.MY_DIR);

        makeGifButton = (TextView) findViewById(R.id.make_gif_button);
        shootingGifButton = (TextView) findViewById(R.id.shooting_gif_button);
        videoToGifButton = (TextView) findViewById(R.id.video_to_gif_button);

        makeGifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        shootingGifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShootingGifActivity.class);
                startActivity(intent);
            }
        });

        videoToGifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Please pick video no longer then 30 seconds", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(GifItConst.VIDEO_TYPE);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.clearDir(new File(root, GifItConst.MY_DIR));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            /*Intent i = new Intent();
            i.setAction(Intent.ACTION_CAMERA_BUTTON);
            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CAMERA));
            sendOrderedBroadcast(i, null);*/

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {

            if (data != null) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(Utils.getRealPathFromURI(getApplicationContext(), data.getData()));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInmillisec = Long.parseLong(time);
                long seconds = timeInmillisec / 1000;
                if (seconds > GifItConst.VIDEO_MAX_SECONDS) {
                    Toast.makeText(MainActivity.getContext(), "Video is too long", Toast.LENGTH_LONG).show();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Generating Frames");
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    VideoDecoder videoDecoder = new VideoDecoder(MainActivity.this, Utils.getRealPathFromURI(getApplicationContext(), data.getData()), Integer.MAX_VALUE, VideoDecoder.FrameSize.NORMAL, root + "/" + GifItConst.MY_DIR);
                    videoDecoder.extractVideoFrames();
                    videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                        @Override
                        public void onFinish(boolean isDone) {
                            Intent intent = new Intent(MainActivity.this, MakeGifActivity.class);
                            intent.putExtra(GifItConst.INDEX, GifItConst.VIDEO_TO_GIF_INDEX);
                            intent.putExtra(GifItConst.VIDEO_PATH, Utils.getRealPathFromURI(getApplicationContext(), data.getData()));
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }
    }

}