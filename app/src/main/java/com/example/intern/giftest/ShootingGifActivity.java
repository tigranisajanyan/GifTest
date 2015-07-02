package com.example.intern.giftest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;


public class ShootingGifActivity extends ActionBarActivity {

    private Camera camera;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;
    private Button capture, switchCamera;
    private Context context;
    private LinearLayout cameraPreviewLayout;
    private TextView secondsText;
    private TextView frameText;
    private boolean cameraFront = false;

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");

    private int currentCapturedTime;
    private int capturedTime;
    private Thread myThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting_gif);
        context = this;
        init();
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(context)) {
            Toast toast = Toast.makeText(context, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (camera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {

                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }
            camera = Camera.open(findBackFacingCamera());
            cameraPreview.refreshCamera(camera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void init() {

        cameraPreviewLayout = (LinearLayout) findViewById(R.id.camera_preview);

        cameraPreviewLayout.setLayoutParams(new LinearLayout.LayoutParams(450, 800));

        cameraPreview = new CameraPreview(context, camera);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);

        cameraPreviewLayout.addView(cameraPreview);

        secondsText = (TextView) findViewById(R.id.text_seconds);
        frameText = (TextView) findViewById(R.id.text_frames);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            /*final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Generating Frames");
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();

            loadFFMpegBinary();

            String uri = root + "/myvideo1.mp4";

            final String[] dd = new String[]{" -i " + root + "/test_images/" + "frame_%03d.jpg" + " -vf transpose=1 -strict -2 " + root + "/test_images/" + "frame_%03d.jpg"};
            final String[] videoToFrame = new String[]{" -i " + uri + " -r 2 -an -f image2 " + root + "/test_images/" + "frame_%03d.jpg"};

            final FFmpeg fFmpeg = new FFmpeg(ShootingGifActivity.this);
            try {
                fFmpeg.execute(videoToFrame, new FFmpegExecuteResponseHandler() {
                    @Override
                    public void onSuccess(String message) {

                        try {
                            fFmpeg.execute(dd, new FFmpegExecuteResponseHandler() {
                                @Override
                                public void onSuccess(String message) {
                                    ArrayList<String> strings = new ArrayList<>();
                                    File[] files = new File(root + "/test_images").listFiles();
                                    for (int i = 0; i < files.length; i++) {
                                        strings.add(files[i].getAbsolutePath());
                                    }
                                    Intent intent = new Intent(getApplicationContext(), MakeGifActivity.class);
                                    intent.putStringArrayListExtra("image_paths", strings);
                                    intent.putExtra("frame_count", frameText.getText());
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onProgress(String message) {

                                }

                                @Override
                                public void onFailure(String message) {

                                }

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFinish() {

                                }
                            });
                        } catch (FFmpegCommandAlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onProgress(String message) {
                        Log.d("gagag", message);
                    }

                    @Override
                    public void onFailure(String message) {

                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
            }*/

            //new MyTask().execute();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));


        mediaRecorder.setOrientationHint(90);

        File file = new File(root, "myvideo1.mp4");
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setMaxDuration(900000); // Set max duration 90 sec.
        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }


    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                camera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                cameraPreview.refreshCamera(camera);
            }
        }
    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(context, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        }
    };


    boolean recording = false;
    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                // stop recording and release camera
                myThread.interrupt();

                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(ShootingGifActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                recording = false;
            } else {
                if (!prepareMediaRecorder()) {
                    Toast.makeText(ShootingGifActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }

                currentCapturedTime = 0;

                Runnable myRunnableThread = new CountDownRunner();
                myThread = new Thread(myRunnableThread);
                myThread.start();

                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table
                        try {
                            mediaRecorder.start();


                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });

                recording = true;
            }
        }
    };

    /*private void loadFFMpegBinary() {
        try {

            new FFmpeg(ShootingGifActivity.this).loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {

        }
    }*/


    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {

                    secondsText.setText("sec: " + currentCapturedTime / 10.0);
                    frameText.setText("frame: " + currentCapturedTime / 5);

                    currentCapturedTime++;
                    if (currentCapturedTime == capturedTime) {
                        captrureListener.onClick(capture);
                    }

                } catch (Exception e) {
                }
            }
        });
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        final ProgressDialog progressDialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Generating Frames");
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(root + "/myvideo1.mp4");
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            long timeInmillisec = Long.parseLong(time);
            long duration = timeInmillisec / 1000;
            long hours = duration / 3600;
            long minutes = (duration - hours * 3600) / 60;
            long seconds = duration - (hours * 3600 + minutes * 60);
            Log.d("gagaga", "" + timeInmillisec);
            for (int i = 0; i <= 10; i++) {
                try {
                    File file = new File(myDir, "gag" + i + ".jpg");
                    Bitmap bitmap = retriever.getFrameAtTime(50000 * i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    if (file.exists()) {
                        file.delete();
                    }

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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ArrayList<String> strings = new ArrayList<>();
            File[] files = new File(root + "/test_images").listFiles();
            for (int i = 0; i < files.length; i++) {
                strings.add(files[i].getAbsolutePath());
            }
            Intent intent = new Intent(getApplicationContext(), MakeGifActivity.class);
            intent.putStringArrayListExtra("image_paths", strings);
            intent.putExtra("frame_count", frameText.getText());
            startActivity(intent);
            progressDialog.dismiss();
            finish();

        }
    }

}
