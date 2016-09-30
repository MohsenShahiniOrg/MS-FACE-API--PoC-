package com.api.face.microsoft.microsoftfaceapi;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;


/**
 * Created by Alina_Zhdanava on 9/29/2016.
 */

public class ImageResultReceiver extends ResultReceiver {

    private Receiver receiver;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public ImageResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }
}




