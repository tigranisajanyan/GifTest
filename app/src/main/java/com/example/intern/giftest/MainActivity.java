package com.example.intern.giftest;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

public class MainActivity extends ActionBarActivity {

    private Button makeGifButton;
    private Button shootingGifButton;

    private static Context context;
    public static Context getContext() {
        return context;
    }

    static {
        System.loadLibrary("gif");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        context = this;
        Utils.craeteDir("test_images");

        makeGifButton = (Button) findViewById(R.id.make_gif_button);
        shootingGifButton = (Button) findViewById(R.id.shooting_gif_button);

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

                //EncodeByteRgbFrameExample.encodeByteRgbFrameExample(file.toString(), bitmapdata, LibVpxEnc.FOURCC_ABGR, bitmap.getWidth(), bitmap.getHeight(), 10, 1, 100, new StringBuilder());

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

            Intent msgIntent = new Intent(MainActivity.this, SimpleIntentService.class);
            startService(msgIntent);
            //Toast.makeText(MainActivity.this, helloLog() + "", Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static native int helloLog();

}