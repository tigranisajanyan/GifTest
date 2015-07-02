package com.example.intern.giftest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class IncommingCallReceiver extends BroadcastReceiver {

    Context mContext;
    Camera cam = null;

    @Override
    public void onReceive(Context mContext, Intent intent) {
        try {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);


            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                ringing(mContext);
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                turnOffFlashLight(mContext);
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                turnOffFlashLight(mContext);

            }
        } catch (Exception e) {
        }

    }

    public void turnOnFlashLight(Context context) {
        try {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception throws in turning on flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    public void turnOffFlashLight(Context context) {
        try {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception throws in turning off flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    public void ringing(Context context){
        for (int i = 0; i < 15; i++) {
            turnOnFlashLight(context);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            turnOffFlashLight(context);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}