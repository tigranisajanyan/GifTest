package com.example.intern.giftest.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intern.giftest.R;
import com.example.intern.giftest.effects.BoostEffect;
import com.example.intern.giftest.effects.EffectSelectorAdapter;
import com.example.intern.giftest.effects.EmbossEffect;
import com.example.intern.giftest.effects.EngraveEffect;
import com.example.intern.giftest.effects.GrayScaleEffect;
import com.example.intern.giftest.effects.RecyclerItemClickListener;
import com.example.intern.giftest.effects.ReflectionEffect;
import com.example.intern.giftest.effects.SnowEffect;
import com.example.intern.giftest.effects.Utils.EffectsItem;
import com.example.intern.giftest.effects.Utils.OnEffectApplyFinishedListener;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class VideoEffectsActivity extends AppCompatActivity {

    private ImageView previewImageView = null;
    private RecyclerView effectsList = null;
    private EffectSelectorAdapter adapter = null;
    private EffectsItem selectedItem = null;
    int pos;

    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.effects_activity_layout);

        byte[] byteArray = getIntent().getByteArrayExtra("bytearray");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        previewImageView = (ImageView) findViewById(R.id.frame_image_imageView);
        previewImageView.setImageBitmap(bitmap);
        effectsList = (RecyclerView) findViewById(R.id.effects_list_recyclerview);
        effectsList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new EffectSelectorAdapter();
        initEffects(adapter);
        effectsList.setAdapter(adapter);

        effectsList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                pos = position;
                selectedItem = adapter.getItem(position);
                selectedItem.getAction().applyOnOneFrame(selectedItem.getBitmap(), new OnEffectApplyFinishedListener() {
                    @Override
                    public void onFinish(Bitmap bmp) {
                        previewImageView.setImageBitmap(bmp);
                    }
                }, null);
            }
        }));

    }

    private void initEffects(EffectSelectorAdapter adapter) {
        EffectsItem item1 = new EffectsItem(bitmap, new GrayScaleEffect(VideoEffectsActivity.this));
        EffectsItem item2 = new EffectsItem(bitmap, new ReflectionEffect(VideoEffectsActivity.this));
        EffectsItem item3 = new EffectsItem(bitmap, new SnowEffect(VideoEffectsActivity.this));
        EffectsItem item4 = new EffectsItem(bitmap, new BoostEffect(VideoEffectsActivity.this));
        EffectsItem item5 = new EffectsItem(bitmap, new EngraveEffect(VideoEffectsActivity.this));
        EffectsItem item6 = new EffectsItem(bitmap, new EmbossEffect(VideoEffectsActivity.this));
        adapter.addItem(item1);
        adapter.addItem(item2);
        adapter.addItem(item3);
        adapter.addItem(item4);
        adapter.addItem(item5);
        adapter.addItem(item6);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.effects_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_apply) {
            applyEffectOnVideo();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void applyEffectOnVideo() {
        if (selectedItem != null) {
            Intent data = new Intent().putExtra("position", pos + 1);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Toast.makeText(this, "please select any effect", Toast.LENGTH_SHORT).show();
        }
    }

}
