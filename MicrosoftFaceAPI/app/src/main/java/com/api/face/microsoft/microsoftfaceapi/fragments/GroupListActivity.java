package com.api.face.microsoft.microsoftfaceapi.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
    private TextView groupName;
    private RecyclerView imageGroupRecyclerView;
    private ImageGroupAdapter imageGroupRecyclerAdapter;
    private RecyclerView.LayoutManager imageGroupLayoutManager;
    private List<String> allPaths;

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
        groupName = (TextView) findViewById(R.id.group_name);

        imageGroupRecyclerView = (RecyclerView) findViewById(R.id.image_group_recycler_view);
        imageGroupRecyclerView.setHasFixedSize(true);
        imageGroupLayoutManager = new LinearLayoutManager(this);
        imageGroupRecyclerView.setLayoutManager(imageGroupLayoutManager);
        File rootDirectory = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/Camera");
        allPaths = getAllImagePaths(rootDirectory);
        imageGroupRecyclerAdapter = new ImageGroupAdapter(allPaths, this);
        imageGroupRecyclerView.setAdapter(imageGroupRecyclerAdapter);
    }

    @Override
    public void onClick(View v) {
        Log.i("TAG", imageGroupRecyclerAdapter.getSelectedItemCount() + "");
        Bitmap bitmap = null;
        for (Integer index : imageGroupRecyclerAdapter.getSelectedItems()) {
//            new PrepareGroupTask().execute();
    //        new TrainGroupTask(this).execute("Group");
                new IdentificationTask().execute();
        }
    }

    private class PrepareGroupTask extends AsyncTask<Void, Void, String> {
        Bitmap bitmap = null;

        @Override
        protected String  doInBackground(Void... params) {
            List<Person> persons = new ArrayList<>();
            String groupName = "Group";
            try {
                FaceServiceRestClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
             //  faceServiceClient.createPersonGroup("113","Group","data");
               CreatePersonResult person = faceServiceClient.createPerson("113", "Man", "Man");
                Face[] faces = ImageHelper.detectURL("http://b2blogger.com/pressroom/upload_images/gps-tracker_1.JPG");
                faceServiceClient.addPersonFace("113", person.personId, "http://b2blogger.com/pressroom/upload_images/gps-tracker_1.JPG", "usedData", faces[0].faceRectangle);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
            return groupName;
        }
    }

    private class TrainGroupTask extends AsyncTask<String, Void, Void> {

        private Context activityContext;

        public TrainGroupTask(Context activityContext) {
            this.activityContext = activityContext;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                FaceServiceRestClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
                faceServiceClient.trainPersonGroup("113");
                TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus("113");
                Log.i("TAG",trainingStatus.status+ " status");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


        private class IdentificationTask extends AsyncTask<Void, Void, Void> {

            protected Void doInBackground(Void... params) {

                try {
                    Face[] faces = ImageHelper.detectURL("http://b2blogger.com/pressroom/upload_images/gps-tracker_1.JPG");
                    FaceServiceRestClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
                  //  TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus("113");
                   // Log.i("TAG",trainingStatus.status+ " status");
                  //  if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                        IdentifyResult[] result = faceServiceClient.identity("113", ImageHelper.getFacesId(faces), 1);
                        Log.i("TAG","result.length " + result.length+" result. "+ result[0].candidates.get(0).personId);
                        Log.i("TAG",faceServiceClient.getPerson("113",result[0].candidates.get(0).personId).name +"");
                  //  }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClientException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.i("TAG","завершено");
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

    private List<Bitmap> getAllImagesFfromDirectory(File rootDirectory) {
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
                allImages.addAll(getAllImagesFfromDirectory(file));
            }
        }
        return allImages;
    }
}
