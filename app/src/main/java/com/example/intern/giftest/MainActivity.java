package com.example.intern.giftest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.libvpx.LibVpxDec;
import com.google.libvpx.LibVpxEnc;
import com.google.libvpx.LibVpxEncConfig;
import com.google.libvpx.LibVpxException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialin.android.encoder.Encoder;

import java.io.File;
import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private Button makeGifButton;
    private Button shootingGifButton;
    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");

    private boolean turnOn = false;

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
                /*Intent intent = new Intent(getApplicationContext(), ShootingGifActivity.class);
                startActivity(intent);*/
                Encoder encoder=new Encoder();
                encoder.init(200,200,25,null);
                encoder.addFrame(Bitmap.createBitmap(200,200, Bitmap.Config.RGB_565),20);
                encoder.startVideoGeneration(new File(root,"vid.webm"));
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

            for (int i=0;i<15;i++){
                turnOnFlashLight();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                turnOffFlashLight();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*if (turnOn) {
                turnOffFlashLight();
                turnOn = false;
            } else {
                turnOnFlashLight();
                turnOn = true;
            }*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static native String helloLog();

    public static native int gog();

    Camera cam = null;

    public void turnOnFlashLight() {
        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception throws in turning on flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    public void turnOffFlashLight() {
        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception throws in turning off flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

}