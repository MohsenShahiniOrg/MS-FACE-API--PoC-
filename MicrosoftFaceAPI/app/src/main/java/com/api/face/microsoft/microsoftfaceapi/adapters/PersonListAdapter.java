package com.api.face.microsoft.microsoftfaceapi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.api.face.microsoft.microsoftfaceapi.R;
import com.microsoft.projectoxford.face.contract.Person;

import java.util.List;

/**
 * Created by Alina_Zhdanava on 10/5/2016.
 */

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.ViewHolder> {

    private List<Person> mPersonDataset;
    private Context mContext;

    public PersonListAdapter(List<Person> myDataset, Context context) {
        mPersonDataset = myDataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view_folder, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mPersonDataset.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView viewImage;
        public FrameLayout background;

        public ViewHolder(View itemView) {
            super(itemView);
            background = (FrameLayout) itemView.findViewById(R.id.background);
            viewImage = (ImageView) itemView.findViewById(R.id.view_image);
            viewImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}


