package com.mobucks.androidsdk.network.tasks;

import android.os.AsyncTask;

import com.mobucks.androidsdk.logger.utils.net.exceptions.HttpFailureException;
import com.mobucks.androidsdk.network.callbacks.NetworkCall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetRequest extends AsyncTask<String, Void, String> {
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 5000;
    public static final int CONNECTION_TIMEOUT = 5000;
    private NetworkCall networkCall = null;

    public HttpGetRequest(NetworkCall<String> networkCall) {
        this.networkCall = networkCall;
    }

    @Override
    protected String doInBackground(String... params){
        String stringUrl = params[0];
        String result="";
        String inputLine;
        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);
            //Create a connection
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestProperty("User-Agent","android-sdk");
            //Connect to our url
            connection.connect();
            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            int httpCode = connection.getResponseCode();
            if(httpCode>=400){
                throw  new HttpFailureException(httpCode);
            }
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        }
        catch(Exception e){
            networkCall.onError(e);
            result = null;
        }
        return result;
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
        if(result!=null){
            networkCall.onComplete(result);
        }

    }
}