package com.example.intern.giftest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.intern.giftest.R;
import com.example.intern.giftest.adapter.GalleryAdapter;
import com.example.intern.giftest.utils.GalleryItem;
import com.example.intern.giftest.utils.GifsArtConst;
import com.example.intern.giftest.utils.SpacesItemDecoration;
import com.example.intern.giftest.utils.Utils;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private static Context context;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private GalleryAdapter galleryAdapter;
    private ArrayList<GalleryItem> customGalleryArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        init();

    }

    public void init() {

        context = this;

        galleryAdapter = new GalleryAdapter(customGalleryArrayList, this, getSupportActionBar());

        recyclerView = (RecyclerView) findViewById(R.id.gallery_rec_view);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(galleryAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2));

        new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

            if (galleryAdapter.getSelected().size() > 0) {

                Intent intent = new Intent(GalleryActivity.this, MakeGifActivity.class);
                intent.putExtra(GifsArtConst.INDEX, 1);
                intent.putStringArrayListExtra(GifsArtConst.IMAGE_PATHS, galleryAdapter.getSelected());
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(getContext(), "no images selected", Toast.LENGTH_LONG).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Context getContext() {
        return context;
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            customGalleryArrayList.addAll(Utils.getGalleryPhotos(GalleryActivity.this));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            findViewById(R.id.progress).setVisibility(View.GONE);
            recyclerView.setBackgroundColor(getResources().getColor(R.color.yellow));
            galleryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
