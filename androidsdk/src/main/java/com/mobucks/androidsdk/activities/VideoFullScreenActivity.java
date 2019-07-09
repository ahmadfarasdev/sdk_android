package com.mobucks.androidsdk.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import com.mobucks.androidsdk.models.Ad;
import com.mobucks.androidsdk.views.VideoAdView;

import java.lang.ref.WeakReference;

/**
 * Fullscreen video activity
 */
public class VideoFullScreenActivity extends Activity {
    public static WeakReference<Ad> ad;
    public static WeakReference<VideoAdView> videoAdView;
    public static WeakReference<VideoAdView> innerVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ad.get() == null ){
            finish();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        videoAdView = new WeakReference<>(new VideoAdView(this));
        setContentView(videoAdView.get(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


    }



    @Override
    protected void onResume() {
        super.onResume();
        if(videoAdView!=null && videoAdView.get()!=null){
            videoAdView.get().loadVideoFromAd(ad.get());
            videoAdView.get().play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoAdView!=null && videoAdView.get()!=null){
            videoAdView.get().onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(innerVideoView !=null &&  innerVideoView.get()!=null && videoAdView!=null && videoAdView.get()!=null && ad !=null && ad.get()!=null){
            ad.get().getVideoData().setCurrentPosition(videoAdView.get().getCurrentPosition());
            ad.get().getVideoData().setFullscreen(false);
            innerVideoView.get().loadVideoFromAd(ad.get());
            innerVideoView.get().play();
        }

        if(videoAdView!=null && videoAdView.get()!=null){
            videoAdView.get().onDestroy();
        }
    }

}
