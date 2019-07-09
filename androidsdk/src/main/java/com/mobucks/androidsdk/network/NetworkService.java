package com.mobucks.androidsdk.network;


import com.mobucks.androidsdk.network.callbacks.NetworkCall;
import com.mobucks.androidsdk.network.tasks.HttpGetRequest;

public class NetworkService {
    public void get(String url, NetworkCall networkCall){
        HttpGetRequest httpGetRequest = new HttpGetRequest(networkCall);
        httpGetRequest.execute(url);
    }
}
