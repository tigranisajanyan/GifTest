package com.example.intern.giftest;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Tigran on 6/23/15.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public static final String FILE_PREFIX = "file://";
    private ArrayList<GalleryItem> array;
    private Context context;

    public Adapter(ArrayList<GalleryItem> arr, Context c) {

        array = arr;
        context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (array.get(position).isSeleted()) {
                    array.get(position).setIsSeleted(false);

                } else {
                    array.get(position).setIsSeleted(true);
                }

                holder.selected.setSelected(array
                        .get(position).isSeleted());
            }
        });

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


                    holder.icon.setImageBitmap(Utils.scaleCenterCrop(loadedImage,400,400));
                    super.onLoadingComplete(imageUri, view, loadedImage);
                }
            });

            holder.selected
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
        private ImageView selected;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.image_item);
            selected=(ImageView)itemView.findViewById(R.id.item_selected);
            selected.setVisibility(View.VISIBLE);
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

    public ArrayList<Integer> getSel(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).isSeleted() == true) {
                arrayList.add(i);

            }
        }
        return arrayList;
    }

}
