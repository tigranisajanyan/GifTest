package com.example.intern.giftest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.intern.giftest.R;
import com.example.intern.giftest.items.GalleryItem;

import java.util.ArrayList;

/**
 * Created by Tigran on 9/11/15.
 */
public class VideoPickerAdapter extends RecyclerView.Adapter<VideoPickerAdapter.ViewHolder> {

    private ArrayList<GalleryItem> array;
    private Context context;

    private ArrayList<GalleryItem> selected = new ArrayList<>();

    public VideoPickerAdapter(ArrayList<GalleryItem> arr, Context c) {

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

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (array.get(position).isSeleted()) {
                    array.get(position).setIsSeleted(false);
                    array.get(position).setPosition(getSelected().size());
                    selected.remove(array.get(position));

                } else {
                    array.get(position).setIsSeleted(true);
                    array.get(position).setPosition(getSelected().size());
                    selected.add(array.get(position));
                }

                holder.select.setSelected(array
                        .get(position).isSeleted());

            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(array.get(position).getWidth(), array.get(position).getHeight());
        holder.image.setLayoutParams(layoutParams);
        holder.image.setImageBitmap(array.get(position).getBitmap());

        holder.select
                .setSelected(array.get(position).isSeleted());

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ImageView select;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.gallery_image_item);
            select = (ImageView) itemView.findViewById(R.id.gallery_item_selected);
            select.setVisibility(View.VISIBLE);
        }
    }

    public GalleryItem getItem(int i) {
        return array.get(i);
    }

    public ArrayList<String> getSelected() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).isSeleted()) {
                arrayList.add(selected.get(i).getImagePath());
            }
        }
        return arrayList;
    }

}
