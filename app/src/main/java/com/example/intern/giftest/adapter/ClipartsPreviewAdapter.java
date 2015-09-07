package com.example.intern.giftest.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.intern.giftest.R;
import com.example.intern.giftest.effects.EffectItem;
import com.example.intern.giftest.items.ClipArtItem;

import java.util.ArrayList;


/**
 * Created by AramNazaryan on 7/24/15.
 */
public class ClipartsPreviewAdapter extends RecyclerView.Adapter<ClipartsPreviewAdapter.ViewHolder> {

    private ArrayList<ClipArtItem> items = new ArrayList<>();
    private LayoutInflater inflater = null;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.effect_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Bitmap item = items.get(position).getClipartBitmap();
        holder.imageView.setImageBitmap(item);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ClipArtItem item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    public ClipArtItem getItem(int position) {
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView = null;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }

}
