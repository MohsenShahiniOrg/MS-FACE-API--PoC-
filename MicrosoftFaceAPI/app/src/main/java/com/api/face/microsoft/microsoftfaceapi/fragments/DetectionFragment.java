package com.api.face.microsoft.microsoftfaceapi.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
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
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DetectionFragment extends Fragment implements View.OnClickListener, ImageResultReceiver.Receiver {

    private ImageView imageView;
    private TextView resultText;
    private Button browseButton;
    private final int UPLOAD_IMAGE = 1;
    private ImageResultReceiver imageReceiver;
    public static final String URL = "url";
    public static final String CALLBACK = "receiver";
    public static final String RESULT_IMAGE = "image";
    public static final String RESULT_FACES = "faces";
    private FaceServiceClient faceServiceClient;

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
        faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
        return view;
    }

    @Override
    public void onClick(View v) {
        Bitmap bitmap = null;
        imageReceiver = new ImageResultReceiver(new Handler());
        imageReceiver.setReceiver(this);

        Intent intent = new Intent(getActivity(), ImageLoadService.class);
        intent.putExtra(URL, "http://b2blogger.com/pressroom/upload_images/gps-tracker_1.JPG");

        intent.putExtra(CALLBACK, imageReceiver);
        getActivity().startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Bitmap bitmap = resultData.getParcelable(RESULT_IMAGE);
        imageView.setImageBitmap(bitmap);
        String value = resultData.getString(RESULT_FACES);

        Toast.makeText(getActivity(), value, Toast.LENGTH_LONG);
        new DetectionTask().execute(bitmap);
    }


    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 2;
        paint.setStrokeWidth(stokeWidth);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }

    private class DetectionTask extends AsyncTask<Bitmap, Void, Face[]> {
        Face[] faces = null;
        Bitmap bitmap = null;

        @Override
        protected Face[] doInBackground(Bitmap... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            bitmap = params[0];

            try {
                faces = faceServiceClient.detect(
                        "http://b2blogger.com/pressroom/upload_images/gps-tracker_1.JPG",
                        true,       /* Whether to return face ID */
                        true,       /* Whether to return face landmarks */
                                /* Which face attributes to analyze, currently we support:
                                   age,gender,headPose,smile,facialHair */
                        new FaceServiceClient.FaceAttributeType[]{
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Gender,
                                FaceServiceClient.FaceAttributeType.Glasses,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.HeadPose
                        });
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return faces;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(Face[] result) {
            imageView.setImageBitmap(drawFaceRectanglesOnBitmap(bitmap, result));

            String face_description = "Age: " + (faces[0].faceAttributes.age) + "\n"
                    + "Gender: " + faces[0].faceAttributes.gender + "\n"
                    + "Head pose(in degree): roll(" + faces[0].faceAttributes.headPose.roll + "), "
                    + "yaw(" + faces[0].faceAttributes.headPose.yaw + ")\n"
                    + "Glasses: " + faces[0].faceAttributes.glasses + "\n"
                    + "Smile: " + faces[0].faceAttributes.smile;
            resultText.setText(face_description);

        }
    }
}
