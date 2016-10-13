package com.api.face.microsoft.microsoftfaceapi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.api.face.microsoft.microsoftfaceapi.R;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alina_Zhdanava on 10/12/2016.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    public void setmFaceDataset(List<String> mFaceDataset) {
        this.mImageDataset = mFaceDataset;
    }

    private List<String> mImageDataset;
    private Context mContext;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public ImageAdapter(List<String> myDataset, Context context) {
        if (myDataset != null)
            mImageDataset = myDataset;
        mContext = context;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_face_list, parent, false);
        ImageAdapter.ViewHolder vh = new ImageAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {
        holder.backLayout.setSelected(selectedItems.get(position, false));
        Glide.with(mContext).load(mImageDataset.get(position)).override(200, 200).into(holder.viewImage);
    }

    @Override
    public int getItemCount() {
        if (mImageDataset != null)
            return mImageDataset.size();
        return 0;
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

    public List<String> getSelectedItems() {
        List<String> items =
                new ArrayList<String>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add( mImageDataset.get(selectedItems.keyAt(i)));
        }
        return items;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView viewImage;
        public RelativeLayout backLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            backLayout = (RelativeLayout) itemView.findViewById(R.id.background);
            viewImage = (ImageView) itemView.findViewById(R.id.view_image);
            viewImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                view.setSelected(false);
            } else {
                selectedItems.put(getAdapterPosition(), true);
                view.setSelected(true);
            }
        }

    }
}