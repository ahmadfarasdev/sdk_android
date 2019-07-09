package com.mobucks.androidsdk.interfaces;

public interface AdListener<T> {
    void onAdloaded(T adView);
    void onAdFailed(Exception e);
    void onAdClicked(T adView);
}
