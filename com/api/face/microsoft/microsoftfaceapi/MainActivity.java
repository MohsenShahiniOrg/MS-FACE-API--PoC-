package com.api.face.microsoft.microsoftfaceapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ImageResultReceiver.Receiver {

    private ImageView imageView;
    private Button browseButton;
    private final int UPLOAD_IMAGE =  1;
    private ImageResultReceiver imageReceiver;
    public static final String URL = "url";
    public static final String CALLBACK = "receiver";
    public static final String RESULT_IMAGE = "image";
    private FaceServiceClient faceServiceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.view_image);
        browseButton = (Button) findViewById(R.id.browse_button);
        browseButton.setOnClickListener(this);
        faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
    }


    @Override
    public void onClick(View v) {
        Bitmap bitmap = null;
        imageReceiver = new ImageResultReceiver (new Handler());
        imageReceiver.setReceiver(this);

            Intent intent = new Intent(this,ImageLoadService.class);
            intent.putExtra(URL,"http://b2blogger.com/pressroom/upload_images/gps-tracker_1.JPG");

            intent.putExtra(CALLBACK, imageReceiver);
            startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Bitmap bitmap = resultData.getParcelable(RESULT_IMAGE);
        imageView.setImageBitmap(bitmap);
        detectAndFrame(bitmap);
    }

    private void detectAndFrame(final Bitmap imageBitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null           // returnFaceAttributes: a string like "age, gender"
                            );
                            if (result == null)
                            {
                                publishProgress("Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(
                                    String.format("Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            return null;
                        }
                    }
                    @Override
                    protected void onPreExecute() {
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {

                    }
                    @Override
                    protected void onPostExecute(Face[] result) {

                        if (result == null) return;
                        ImageView imageView = (ImageView)findViewById(R.id.view_image);
                        imageView.setImageBitmap(drawFaceRectanglesOnBitmap(imageBitmap, result));
                        imageBitmap.recycle();
                    }
                };
        detectTask.execute(inputStream);
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

}
