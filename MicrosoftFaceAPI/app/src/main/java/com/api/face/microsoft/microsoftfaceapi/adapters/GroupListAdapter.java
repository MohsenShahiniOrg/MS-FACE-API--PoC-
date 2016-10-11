package com.api.face.microsoft.microsoftfaceapi.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.api.face.microsoft.microsoftfaceapi.activities.GroupDetailsActivity;
import com.api.face.microsoft.microsoftfaceapi.R;
import com.microsoft.projectoxford.face.contract.PersonGroup;

import java.util.List;

/**
 * Created by Alina_Zhdanava on 10/11/2016.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<PersonGroup> mGroupDataset;
    private Context mContext;

    public GroupListAdapter(List<PersonGroup> groupDataset, Context context) {
        this.mGroupDataset = groupDataset;
        this.mContext = context;
    }

    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_group_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GroupListAdapter.ViewHolder holder, int position) {

        holder.group_name.setText(mGroupDataset.get(position).name);
        holder.group_status.setText(mGroupDataset.get(position).trainingStatus.status.toString());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView group_name;
        public TextView group_status;
        public LinearLayout background;

        public ViewHolder(View itemView) {
            super(itemView);
            background = (LinearLayout) itemView.findViewById(R.id.background);
            group_name = (TextView) itemView.findViewById(R.id.group_name);
            group_status = (TextView) itemView.findViewById(R.id.group_status);

            background.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent =  new Intent(mContext,GroupDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("personGroupID",mGroupDataset.get(getAdapterPosition()).personGroupId);
           mContext.startActivity(intent);
        }
    }
}
