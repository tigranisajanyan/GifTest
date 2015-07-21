package com.example.intern.giftest;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Tigran on 7/13/15.
 */
public class SimpleIntentService extends IntentService {

    private ExtractMpegFrames extractMpegFrames;

    public SimpleIntentService() {
        super("SimpleIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        String fileName = intent.getStringExtra("name");
        /*extractMpegFrames = new ExtractMpegFrames();
        try {
            extractMpegFrames.extractMpegFrames(fileName);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        extractMpegFrames.setOnDecodeFinishedListener(new OnFinishChangedListener() {
            @Override
            public void onFinish(boolean isDone) {

            }
        });*/

    }

}