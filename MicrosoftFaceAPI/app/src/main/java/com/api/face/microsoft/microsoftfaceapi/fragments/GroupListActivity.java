package com.api.face.microsoft.microsoftfaceapi.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.api.face.microsoft.microsoftfaceapi.ImageGroupAdapter;

import com.api.face.microsoft.microsoftfaceapi.R;
import com.api.face.microsoft.microsoftfaceapi.helper.ImageHelper;
import com.bumptech.glide.Glide;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GroupListActivity extends AppCompatActivity implements View.OnClickListener {

    private List<File> fileList;
    private final int UPLOAD_IMAGE = 1;
    private Button createGroupButton;
    private Button createPersonButton;
    private TextView groupName;
    private TextView groupId;
    private TextView personName;
    private RecyclerView imageGroupRecyclerView;
    private ImageGroupAdapter imageGroupRecyclerAdapter;
    private RecyclerView.LayoutManager imageGroupLayoutManager;
    private List<String> allPaths;
    private ImageHelper helper;
    private AlertDialog.Builder trainDialog;

    public GroupListActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupListActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupListActivity newInstance() {
        GroupListActivity fragment = new GroupListActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        createGroupButton = (Button) findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(this);

        createPersonButton = (Button) findViewById(R.id.create_person_button);
        createPersonButton.setOnClickListener(this);

        groupName = (TextView) findViewById(R.id.group_name);
        personName = (TextView) findViewById(R.id.person_name);
        groupId = (TextView) findViewById(R.id.group_id);

        imageGroupRecyclerView = (RecyclerView) findViewById(R.id.image_group_recycler_view);
        imageGroupRecyclerView.setHasFixedSize(true);
        imageGroupLayoutManager = new LinearLayoutManager(this);
        imageGroupRecyclerView.setLayoutManager(imageGroupLayoutManager);


        File rootDirectory = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/Camera");
        allPaths = getAllImagePaths(rootDirectory);

        imageGroupRecyclerAdapter = new ImageGroupAdapter(allPaths, this);
        imageGroupRecyclerView.setAdapter(imageGroupRecyclerAdapter);
        helper = new ImageHelper(this);

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

        Log.i("TAG", imageGroupRecyclerAdapter.getSelectedItemCount() + "");

        switch (v.getId()) {
            case R.id.create_group_button: {

                new CreateGroupTask().execute(groupId.getText().toString(),groupName.getText().toString());
                break;
            }
            case R.id.create_person_button: {
                for (String path : imageGroupRecyclerAdapter.getSelectedItems()) {

                    new CreatePersonTask().execute(groupId.getText().toString(), personName.getText().toString(), path);
                }

                break;
            }
        }
    }

    private class CreateGroupTask extends AsyncTask<String, Void, Void> {
// params[0] - String personGroupId
//
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
