package com.api.face.microsoft.microsoftfaceapi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.api.face.microsoft.microsoftfaceapi.R;
import com.microsoft.projectoxford.face.contract.FaceList;
import com.microsoft.projectoxford.face.contract.FaceMetadata;

/**
 * Created by Alina_Zhdanava on 10/12/2016.
 */

public class FaceListAdapter extends RecyclerView.Adapter<FaceListAdapter.ViewHolder> {

    public void setmFaceDataset(FaceMetadata[] mFaceDataset) {
        this.mFaceDataset = mFaceDataset;
    }

    private FaceMetadata[] mFaceDataset;
    private Context mContext;

    public FaceListAdapter(FaceList myDataset, Context context) {
        if(myDataset !=null)
        mFaceDataset = myDataset.faces;
        mContext = context;
    }

    @Override
    public FaceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_person_list, parent, false);
        FaceListAdapter.ViewHolder vh = new FaceListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FaceListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (mFaceDataset != null)
            return mFaceDataset.length;
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public ImageView viewImage;
        public LinearLayout background;
        public TextView person_name;
        public TextView person_id;

        public ViewHolder(View itemView) {
            super(itemView);
            background = (LinearLayout) itemView.findViewById(R.id.background);
            viewImage = (ImageView) itemView.findViewById(R.id.view_image);
            person_name = (TextView) itemView.findViewById(R.id.person_name);
            person_id = (TextView) itemView.findViewById(R.id.person_id);
        }

    }
}