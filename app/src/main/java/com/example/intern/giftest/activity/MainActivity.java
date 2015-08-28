package com.example.intern.giftest.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.decoder.VideoDecoder;
import com.example.intern.giftest.R;
import com.example.intern.giftest.utils.GifsArtConst;
import com.example.intern.giftest.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;


public class MainActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();

    private Button makeGifButton;
    private Button shootingGifButton;
    private Button videoToGifButton;

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        context = this;
        Utils.craeteDir(GifsArtConst.MY_DIR);

        makeGifButton = (Button) findViewById(R.id.make_gif_button);
        shootingGifButton = (Button) findViewById(R.id.shooting_gif_button);
        videoToGifButton = (Button) findViewById(R.id.video_to_gif_button);

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
                intent.setType(GifsArtConst.VIDEO_TYPE);
                startActivityForResult(intent, 100);
            }
        });

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

            Intent i = new Intent();
            i.setAction(Intent.ACTION_CAMERA_BUTTON);
            i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CAMERA));
            sendOrderedBroadcast(i, null);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(Utils.getRealPathFromURI(getApplicationContext(), data.getData()));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInmillisec = Long.parseLong(time);
            long seconds = timeInmillisec / 1000;
            if (seconds > 30) {
                Toast.makeText(MainActivity.getContext(), "Video is too long", Toast.LENGTH_LONG).show();
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Generating Frames");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
                VideoDecoder videoDecoder = new VideoDecoder(MainActivity.this, Utils.getRealPathFromURI(getApplicationContext(), data.getData()), Integer.MAX_VALUE, VideoDecoder.FrameSize.NORMAL, root + "/" + GifsArtConst.MY_DIR);
                videoDecoder.extractVideoFrames();
                videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                    @Override
                    public void onFinish(boolean isDone) {
                        Intent intent = new Intent(MainActivity.this, MakeGifActivity.class);
                        intent.putExtra(GifsArtConst.INDEX, 3);
                        intent.putExtra(GifsArtConst.VIDEO_PATH, Utils.getRealPathFromURI(getApplicationContext(), data.getData()));
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                    }
                });
            }
        }
    }

}