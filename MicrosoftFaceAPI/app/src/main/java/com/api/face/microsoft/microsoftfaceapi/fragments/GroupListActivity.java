package com.api.face.microsoft.microsoftfaceapi.fragments;

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

import com.api.face.microsoft.microsoftfaceapi.ImageGroupAdapter;

import com.api.face.microsoft.microsoftfaceapi.R;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupListActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupListActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupListActivity extends AppCompatActivity implements View.OnClickListener {

    private List<File> fileList;
    private final int UPLOAD_IMAGE = 1;
    private Button createGroupButton;
    private TextView groupName;
    private RecyclerView imageGroupRecyclerView;
    private RecyclerView.Adapter imageGroupRecyclerAdapter;
    private RecyclerView.LayoutManager imageGroupLayoutManager;


    private OnFragmentInteractionListener mListener;

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
       List<String> allPaths = getAllImagePaths(rootDirectory);
       // File rootDirectory = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/Camera");
       // List<Bitmap> allImages = getAllImagesFfromDirectory(rootDirectory);
        imageGroupRecyclerAdapter = new ImageGroupAdapter(allPaths,this);
        imageGroupRecyclerView.setAdapter(imageGroupRecyclerAdapter);
    }

    @Override
    public void onClick(View v) {

    }

    private class IdentificationTask extends AsyncTask<Bitmap, Void, List<Person>> {
        Bitmap bitmap = null;

        @Override
        protected List<Person> doInBackground(Bitmap... params) {
            // Get an instance of face service client to detect faces in image.
            bitmap = params[0];
            List<Person> persons = new ArrayList<>();
            try {
                FaceServiceRestClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
                faceServiceClient.createPersonGroup("Group", "Man", "Man");
                faceServiceClient.createPerson("Group", "Man", "Man");


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
            return persons;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200,300, false);
                allImages.add(resizedBitmap);
            }
            if (file.isDirectory()) {
                allImages.addAll(getAllImagesFfromDirectory(file));
            }
        }
        return allImages;
    }
}
