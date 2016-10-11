package com.api.face.microsoft.microsoftfaceapi.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.api.face.microsoft.microsoftfaceapi.R;
import com.api.face.microsoft.microsoftfaceapi.adapters.PersonListAdapter;
import com.api.face.microsoft.microsoftfaceapi.helper.ImageHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroupDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addPersonButton;
    private TextView groupName;
    private TextView groupId;
    private RecyclerView personRecyclerView;
    private PersonListAdapter personRecyclerAdapter;
    private RecyclerView.LayoutManager personLayoutManager;

    public GroupDetailsActivity() {
    }

    public static GroupDetailsActivity newInstance() {
        GroupDetailsActivity fragment = new GroupDetailsActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String personGroupID = bundle.getString("personGroupID");

        addPersonButton = (Button) findViewById(R.id.create_group_button);
        addPersonButton.setOnClickListener(this);

        groupName = (TextView) findViewById(R.id.group_name);
        groupId = (TextView) findViewById(R.id.group_id);

        personRecyclerView = (RecyclerView) findViewById(R.id.group_recycler_view);
        personRecyclerView.setHasFixedSize(true);
        personLayoutManager = new GridLayoutManager(this, 4);
        personRecyclerView.setLayoutManager(personLayoutManager);
        personRecyclerAdapter = new PersonListAdapter(ImageHelper.getAllGroupPersons(personGroupID), this);
        personRecyclerView.setAdapter(personRecyclerAdapter);

    }

    @Override
    public void onClick(View v) {

    }

    @Override    public boolean onOptionsItemSelected(MenuItem item) {
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

    private List<Bitmap> getAllImagesFromDirectory(File rootDirectory) {
        File[] fileArray = rootDirectory.listFiles();
        List<Bitmap> allImages = new ArrayList<>();
        BitmapFactory.Options options = new BitmapFactory.Options();
        for (File file : fileArray) {
            if (file.getAbsolutePath().endsWith(".jpg")) {
                String filePath = file.getAbsolutePath();

                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, options);
                Log.i("TAG", options.outHeight + " /" + options.outWidth);
                options.inJustDecodeBounds = false;
                options.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 300, false);
                allImages.add(resizedBitmap);
            }
            if (file.isDirectory()) {
                allImages.addAll(getAllImagesFromDirectory(file));
            }
        }
        return allImages;
    }
}
