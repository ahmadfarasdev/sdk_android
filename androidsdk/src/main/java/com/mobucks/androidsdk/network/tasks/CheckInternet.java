package com.mobucks.androidsdk.network.tasks;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.mobucks.androidsdk.network.callbacks.NetworkCall;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckInternet extends AsyncTask<String, Void, Boolean> {
    NetworkCall<Boolean> networkCall;
    Context context;

    public CheckInternet(Context context, NetworkCall<Boolean> networkCall) {
        this.networkCall = networkCall;
        this.context = context;
    }

    protected Boolean doInBackground(String... urls) {
        String url = urls[0];
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager == null) {
            return false;
        }

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return false;
        }

        try {
            URL urlObject = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
            conn.connect();
            int httpResult = conn.getResponseCode();
            if (httpResult == 200) {
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        networkCall.onComplete(result);
    }
}
