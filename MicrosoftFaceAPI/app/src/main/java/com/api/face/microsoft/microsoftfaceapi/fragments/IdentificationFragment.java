package com.api.face.microsoft.microsoftfaceapi.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.api.face.microsoft.microsoftfaceapi.ImageLoadService;
import com.api.face.microsoft.microsoftfaceapi.ImageResultReceiver;
import com.api.face.microsoft.microsoftfaceapi.R;
import com.api.face.microsoft.microsoftfaceapi.helper.ImageHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class IdentificationFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private TextView resultText;
    private Button browseButton;
    private final int UPLOAD_IMAGE =  1;
    private ImageResultReceiver imageReceiver;
    public static final String URL = "url";
    public static final String CALLBACK = "receiver";
    public static final String RESULT_IMAGE = "image";
    public static final String RESULT_FACES = "faces";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detection, container, false);

        imageView = (ImageView) view.findViewById(R.id.view_image);
        browseButton = (Button) view.findViewById(R.id.browse_button);
        resultText = (TextView) view.findViewById(R.id.result_text);

        browseButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, UPLOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
            new IdentificationTask().execute(bitmap);
        }
    }

    private class IdentificationTask extends AsyncTask<Bitmap, Void, List<Person>> {
        Bitmap bitmap = null;

        @Override
        protected List<Person>  doInBackground(Bitmap... params) {
            // Get an instance of face service client to detect faces in image.
            bitmap = params[0];
            List<Person> persons =  new ArrayList<>();
            try {

                Face[] faces = ImageHelper.detect(bitmap);
                ImageHelper.addPersonToTheGroup("",faces,"Джи","информаия");
                 persons = ImageHelper.identify(faces);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
            return  persons;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(List<Person>  result) {

            resultText.setText(result.toString());

        }
    }
}
