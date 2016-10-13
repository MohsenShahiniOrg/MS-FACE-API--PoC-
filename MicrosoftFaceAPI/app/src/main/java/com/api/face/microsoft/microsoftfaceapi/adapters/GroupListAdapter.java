package com.api.face.microsoft.microsoftfaceapi.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.api.face.microsoft.microsoftfaceapi.activities.PersonListActivity;
import com.api.face.microsoft.microsoftfaceapi.R;
import com.api.face.microsoft.microsoftfaceapi.helper.ImageHelper;
import com.microsoft.projectoxford.face.contract.PersonGroup;
import com.microsoft.projectoxford.face.contract.TrainingStatus;

import java.util.List;

/**
 * Created by Alina_Zhdanava on 10/11/2016.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {


    public void setmGroupDataset(List<PersonGroup> mGroupDataset) {
        this.mGroupDataset = mGroupDataset;
    }

    private List<PersonGroup> mGroupDataset;
    private Context mContext;

    public GroupListAdapter(List<PersonGroup> mGroupDataset, Context context) {
        this.mGroupDataset = mGroupDataset;
        this.mContext = context;
    }

    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_group_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupListAdapter.ViewHolder holder, int position) {
        holder.groupName.setText(mGroupDataset.get(position).name);
        TrainingStatus status;

        try {
            status = new TrainSatusTask().execute(mGroupDataset.get(position).personGroupId).get();
            if (status != null) {
                holder.groupStatus.setText(status.status.toString());
            } else {
                holder.groupStatus.setText(R.string.group_not_trained);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mGroupDataset != null) return mGroupDataset.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupName;
        TextView groupStatus;
        Button trainGroup;
        LinearLayout background;

        ViewHolder(View itemView) {
            super(itemView);
            background = (LinearLayout) itemView.findViewById(R.id.background);
            groupName = (TextView) itemView.findViewById(R.id.group_name);
            groupStatus = (TextView) itemView.findViewById(R.id.group_status);
            trainGroup = (Button) itemView.findViewById(R.id.train_group_button);
            trainGroup.setOnClickListener(this);
            background.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.train_group_button) {
                String id = mGroupDataset.get(getAdapterPosition()).personGroupId;
                new TrainGroupTask().execute(id);
            }

            Intent intent = new Intent(mContext, PersonListActivity.class);
            intent.putExtra("personGroupID", mGroupDataset.get(getAdapterPosition()).personGroupId);
            mContext.startActivity(intent);
        }
    }

    private static class TrainGroupTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ImageHelper.trainGroup(params[0]);
            return null;
        }
    }

    private static class TrainSatusTask extends AsyncTask<String, Void, TrainingStatus> {

        @Override
        protected TrainingStatus doInBackground(String... params) {
           return ImageHelper.trainSatus(params[0]);
        }
    }
}
