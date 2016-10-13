package com.api.face.microsoft.microsoftfaceapi;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.api.face.microsoft.microsoftfaceapi.fragments.DetectionFragment;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ImageLoadService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.api.face.microsoft.microsoftfaceapi.action.LOAD";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.api.face.microsoft.microsoftfaceapi.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.api.face.microsoft.microsoftfaceapi.extra.PARAM2";

    public ImageLoadService() {
        super("ImageLoadService");
    }

    private final String TAG = "IntentServiceLogs";


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent start: ");
        InputStream in = null;
        try {

            URL url = new URL(intent.getStringExtra(DetectionFragment.URL));
            in = url.openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            ResultReceiver imageReceiver = intent.getParcelableExtra(DetectionFragment.CALLBACK);

            Bundle bundle = new Bundle();
            bundle.putParcelable(DetectionFragment.RESULT_IMAGE, bitmap);
            imageReceiver.send(0, bundle);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
