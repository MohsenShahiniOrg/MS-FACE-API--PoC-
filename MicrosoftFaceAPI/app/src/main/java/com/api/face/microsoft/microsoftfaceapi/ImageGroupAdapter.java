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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alina_Zhdanava on 10/5/2016.
 */

public class ImageGroupAdapter extends RecyclerView.Adapter<ImageGroupAdapter.ImageViewHolder> {

    private List<String> mDataset;
    private SparseBooleanArray selectedItems;
    private FrameLayout backLayout;
    private Context mContext;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    // Provide a suitable constructor (depends on the kind of dataset)


    public ImageGroupAdapter(List<String> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
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
        Glide.with(mContext).load(mDataset.get(position)).override(1200, 1200).into(holder.viewImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView viewImage;
        //  public  SparseBooleanArray selectedItems = new SparseBooleanArray();


        public ImageViewHolder(View itemView) {
            super(itemView);
            viewImage = (ImageView) itemView.findViewById(R.id.view_image);
        }

//        @Override
//        public void onClick(View view) {
//            if (selectedItems.get(getAdapterPosition(), false)) {
//                selectedItems.delete(getAdapterPosition());
//                view.setSelected(false);
//            }
//            else {
//                selectedItems.put(getAdapterPosition(), true);
//                view.setSelected(true);
//            }
//        }
    }


}


