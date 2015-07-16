package com.example.intern.giftest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Tigran on 7/13/15.
 */
public class SimpleIntentService extends IntentService {

    private ExtractMpegFramesTest extractMpegFramesTest;

    public SimpleIntentService() {
        super("SimpleIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        String fileName = intent.getStringExtra("name");
        extractMpegFramesTest = new ExtractMpegFramesTest();
        try {
            extractMpegFramesTest.testExtractMpegFrames(fileName);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        extractMpegFramesTest.setOnFinishChangedListener(new OnFinishChangedListener() {
            @Override
            public void onFinish(boolean isDone) {

            }
        });

    }

}