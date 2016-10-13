package com.api.face.microsoft.microsoftfaceapi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.api.face.microsoft.microsoftfaceapi.R;
import com.microsoft.projectoxford.face.contract.Person;

import java.util.List;

/**
 * Created by Alina_Zhdanava on 10/5/2016.
 */

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.ViewHolder> {

    public void setmPersonDataset(List<Person> mPersonDataset) {
        this.mPersonDataset = mPersonDataset;
    }

    private List<Person> mPersonDataset;
    private Context mContext;

    public PersonListAdapter(List<Person> myDataset, Context context) {
        mPersonDataset = myDataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_person_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.personName.setText(mPersonDataset.get(position).name);
    }

    @Override
    public int getItemCount() {
        return mPersonDataset != null ? mPersonDataset.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView personName;

        ViewHolder(View itemView) {
            super(itemView);
            personName = (TextView) itemView.findViewById(R.id.person_name);
        }
    }
}


