package com.mobucks.androidsdk.network.tasks;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.mobucks.androidsdk.network.callbacks.NetworkCall;

import java.io.InputStream;
import java.net.URL;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    NetworkCall<Bitmap> networkCall;

    public DownloadImage(NetworkCall<Bitmap> networkCall) {
        this.networkCall = networkCall;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            networkCall.onError(e);
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        networkCall.onComplete(result);
    }
}
