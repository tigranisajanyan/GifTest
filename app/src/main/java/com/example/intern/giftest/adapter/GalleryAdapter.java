package com.example.intern.giftest.adapter;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.intern.giftest.utils.GalleryItem;
import com.example.intern.giftest.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public static final String FILE_PREFIX = "file://";
    private ArrayList<GalleryItem> array;
    private Context context;
    private ActionBar actionBar;

    public GalleryAdapter(ArrayList<GalleryItem> arr, Context c, ActionBar actionBar) {

        this.actionBar = actionBar;
        this.array = arr;
        this.context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (array.get(position).isSeleted()) {
                    array.get(position).setIsSeleted(false);
                    actionBar.setTitle(getSelected().size() + " Selected");
                    if (getSelected().size() < 1) {
                        actionBar.setTitle("GifTest");
                    }

                } else {
                    array.get(position).setIsSeleted(true);
                    actionBar.setTitle(getSelected().size() + " Selected");
                }

                holder.select.setSelected(array
                        .get(position).isSeleted());
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(array.get(position).getWidth(), array.get(position).getHeight());
        holder.icon.setLayoutParams(layoutParams);
        try {

            ImageLoader.getInstance().displayImage(FILE_PREFIX + array.get(position).getImagePath()
                    , holder.icon, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.icon.setImageBitmap(null);
                    super.onLoadingStarted(imageUri, view);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    holder.icon.setImageBitmap(loadedImage);
                    super.onLoadingComplete(imageUri, view, loadedImage);
                }
            });

            holder.select
                    .setSelected(array.get(position).isSeleted());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private ImageView select;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.gallery_image_item);
            select = (ImageView) itemView.findViewById(R.id.gallery_item_selected);
            select.setVisibility(View.VISIBLE);
        }
    }

    public String getItem(int i) {
        return array.get(i).getImagePath();
    }

    public ArrayList<String> getSelected() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).isSeleted() == true) {
                arrayList.add(array.get(i).getImagePath());

            }
        }
        return arrayList;
    }

}
