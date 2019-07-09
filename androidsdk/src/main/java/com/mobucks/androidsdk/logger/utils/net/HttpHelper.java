package com.mobucks.androidsdk.logger.utils.net;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


import com.mobucks.androidsdk.logger.utils.net.exceptions.HttpFailureException;
import com.mobucks.androidsdk.tools.Tools;

public class HttpHelper {
    private static final int readTimeout = 10000;
    private static final int connectTimeout = 15000;
    private static final int SUCCESS_RESPONSE=204;

    /**
     * Open a HttpURLConnection on a specific url and Posts
     * a json.
     * @param requestURL
     * @param jsonArray
     * @return String
     * @throws IOException
     * @throws HttpFailureException
     */
    public static String postJsonRequest(String requestURL,
                                  JSONArray jsonArray) throws IOException, HttpFailureException {


        URL url = new URL(requestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(readTimeout);
        conn.setConnectTimeout(connectTimeout);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);


        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonArray.toString());
        writer.flush();
        writer.close();
        os.close();
        int responseCode = conn.getResponseCode();

        String response ;
        if (responseCode == SUCCESS_RESPONSE) {
            response=streamToString(conn.getInputStream());
        } else {
            throw new HttpFailureException(Tools.stringFromStream( conn.getErrorStream()),responseCode);
        }
        return response;
    }

    /**
     * Open a HttpURLConnection on a specific url and makes a Get request
     * @param requestURL
     * @return
     * @throws IOException
     */
    public static String getRequest(String requestURL) throws IOException {
        URL url = new URL(requestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(readTimeout);
        conn.setConnectTimeout(connectTimeout);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        return  streamToString(conn.getInputStream());
    }

    /**
     * Read all stream data into a string
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String streamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        return  stringBuilder.toString();
    }

}
