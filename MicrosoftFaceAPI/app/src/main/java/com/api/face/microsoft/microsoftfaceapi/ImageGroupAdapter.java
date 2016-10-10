package com.api.face.microsoft.microsoftfaceapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alina_Zhdanava on 10/5/2016.
 */

public class ImageGroupAdapter extends RecyclerView.Adapter<ImageGroupAdapter.ImageViewHolder> {

    private List<String> mDataset;
    private Context mContext;
    public SparseBooleanArray selectedItems ;

    public ImageGroupAdapter(List<String> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view_folder, parent, false);
        ImageViewHolder vh = new ImageViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        //holder.viewImage.setImageBitmap(mDataset.get(position));
        holder.background.setSelected(selectedItems.get(position, false));
        Glide.with(mContext).load(mDataset.get(position)).override(1200, 1200).into(holder.viewImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<String> getSelectedItems() {
        List<String> items =
                new ArrayList<String>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(mDataset.get(selectedItems.keyAt(i)));
        }
        return items;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView viewImage;
        public FrameLayout background;

        public ImageViewHolder(View itemView) {
            super(itemView);
            background = (FrameLayout) itemView.findViewById(R.id.background);
            viewImage = (ImageView) itemView.findViewById(R.id.view_image);
            viewImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                background.setSelected(false);
            } else {
                selectedItems.put(getAdapterPosition(), true);
                background.setSelected(true);
            }
        }
    }


}


