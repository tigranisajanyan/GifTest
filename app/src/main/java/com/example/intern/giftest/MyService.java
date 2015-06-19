package com.example.intern.giftest;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;


public class MyService extends Service {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");

    private String[] paths;

    public void onCreate() {
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        paths = intent.getStringArrayExtra("paths");
        someTask(paths);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    void someTask(final String[] paths) {
        new Thread(new Runnable() {
            public void run() {

                Looper.prepare();

                for (int i = 0; i < paths.length; i++) {

                    String fname = "image_" + String.format("%03d", i) + ".jpg";

                    try {
                        File file = new File(myDir, fname);
                        Bitmap bitmap;
                        if (file.exists()) {
                            file.delete();
                        }

                        String path = paths[i];
                        bitmap = ImageLoader.getInstance().loadImageSync("file://" + path);
                        bitmap = Utils.scaleCenterCrop(bitmap, 720, 720);

                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        bitmap.recycle();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error while SaveToMemory", Toast.LENGTH_SHORT).show();
                    }
                }
                /*String fname = "image_" + String.format("%03d", paths.length) + ".jpg";
                try {
                    File file = new File(myDir, fname);
                    Bitmap bitmap;
                    if (file.exists()) {
                        file.delete();
                    }

                    bitmap=Bitmap.createBitmap(720,720, Bitmap.Config.ARGB_8888);

                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    bitmap.recycle();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error while SaveToMemory", Toast.LENGTH_SHORT).show();
                }*/
                // }
                stopSelf();
            }
        }).start();
    }

}
