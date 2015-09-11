package com.example.intern.giftest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.example.intern.giftest.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by intern on 9/4/15.
 */
public class GifViewActivity extends ActionBarActivity {

    public static final String EXTRA_GIF_PATH = "gif_file_path_string";
    private boolean isPlaying = true;
    private GifDrawable gifFromPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gif);

        Intent intent = getIntent();
        String gifPath = intent.getStringExtra(EXTRA_GIF_PATH);

        try {
            gifFromPath = new GifDrawable(gifPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final GifImageView gifImageView = (GifImageView) findViewById(R.id.gif_img);
        if (gifFromPath != null)
            gifImageView.setImageDrawable(gifFromPath);


        findViewById(R.id.play_pause_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    gifFromPath.pause();
                    isPlaying = false;
                    findViewById(R.id.play_pause_button).setBackgroundResource(android.R.drawable.ic_media_play);
                } else {
                    gifFromPath.start();
                    isPlaying = true;
                    findViewById(R.id.play_pause_button).setBackgroundResource(android.R.drawable.ic_media_pause);
                }
            }
        });

    }
}
