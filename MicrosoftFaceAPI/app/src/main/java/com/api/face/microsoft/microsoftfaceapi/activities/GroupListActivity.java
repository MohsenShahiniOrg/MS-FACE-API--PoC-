package com.api.face.microsoft.microsoftfaceapi.activities;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.api.face.microsoft.microsoftfaceapi.R;
import com.api.face.microsoft.microsoftfaceapi.adapters.GroupListAdapter;
import com.api.face.microsoft.microsoftfaceapi.helper.ImageHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity implements View.OnClickListener {

    private Button createGroupButton;
    private TextView groupName;
    private TextView groupId;
    private TextView personName;
    private RecyclerView groupList;
    private GroupListAdapter groupRecyclerAdapter;
    private RecyclerView.LayoutManager groupLayoutManager;
    private AlertDialog.Builder trainDialog;


    public GroupListActivity() {
    }

    public static GroupListActivity newInstance() {
        GroupListActivity fragment = new GroupListActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        createGroupButton = (Button) findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(this);

        groupName = (TextView) findViewById(R.id.group_name);
        groupId = (TextView) findViewById(R.id.group_id);

        groupList = (RecyclerView) findViewById(R.id.group_recycler_view);
        groupList.setHasFixedSize(true);
        groupLayoutManager = new LinearLayoutManager(this);
        groupList.setLayoutManager(groupLayoutManager);
        groupRecyclerAdapter = new GroupListAdapter(ImageHelper.getAllGroups(),this);
        groupList.setAdapter(groupRecyclerAdapter);

        trainDialog = new AlertDialog.Builder(this);
        trainDialog.setTitle(R.string.title);
        trainDialog.setMessage(R.string.dialog_message);
        trainDialog.setPositiveButton(R.string.possitive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                new TrainGroupTask().execute(groupId.getText().toString());
            }
        });
        trainDialog.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CreateGroupTask extends AsyncTask<String, Void, Void> {
        // params[0] - String personGroupId
// params[1] - String name
        @Override
        protected Void doInBackground(String... params) {
            ImageHelper.createGroup(params[0], params[1]);
            return null;
        }
    }

    private class CreatePersonTask extends AsyncTask<String, Void, Void> {
        Bitmap bitmap = null;

        @Override
        protected Void doInBackground(String... params) {
            ImageHelper.createPerson(params[0], params[1], params[2]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            trainDialog.show();
        }
    }

    private class TrainGroupTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ImageHelper.trainGroup(params[0]);
            return null;
        }
    }

    private List<String> getAllImagePaths(File rootDirectory) {

        File[] fileArray = rootDirectory.listFiles();
        List<String> allPaths = new ArrayList<>();
        for (File file : fileArray) {
            if (file.isDirectory()) {
                allPaths.addAll(getAllImagePaths(file));
            }
            if (file.getAbsolutePath().endsWith(".jpg")) {
                String filePath = file.getAbsolutePath();
                allPaths.add(filePath);
            }
        }
        return allPaths;
    }

}
