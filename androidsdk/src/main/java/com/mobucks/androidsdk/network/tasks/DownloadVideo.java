package com.mobucks.androidsdk.network.tasks;


import android.os.AsyncTask;

import com.mobucks.androidsdk.exceptions.NetworkException;
import com.mobucks.androidsdk.network.callbacks.NetworkCall;
import com.mobucks.androidsdk.tools.Tools;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadVideo extends AsyncTask<String, Void, String> {
    private NetworkCall<String> networkCall;
    private long maxVideoSize;
    private File videoFolder;

    public DownloadVideo(NetworkCall<String> networkCall, File videoFolder,long maxVideoSize) {
        this.networkCall = networkCall;
        this.maxVideoSize = maxVideoSize;
        this.videoFolder = videoFolder;
    }

    public DownloadVideo(NetworkCall<String> networkCall, File videoFolder) {
        //Default max video size 5mb
        this(networkCall,videoFolder,5242880 );
    }

    protected String doInBackground(String... urls) {
        String url = urls[0];
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            int contentLength = connection.getContentLength();

            if (contentLength > maxVideoSize) {
                throw new NetworkException("Video file exceeds max file size!");
            }
            if(!videoFolder.exists()){
               videoFolder.mkdirs();
            }
            File videoFile = new File(videoFolder, Tools.toMd5(url)+".video");
            System.out.println("theokir Downloaded at "+videoFile.getAbsolutePath());
            Tools.streamToFile(connection.getInputStream(),videoFile);
            return  videoFile.getAbsolutePath();
        } catch (Exception e) {
            networkCall.onError(e);
        }
        return null;
    }

    protected void onPostExecute(String result) {
        networkCall.onComplete(result);
    }
}
