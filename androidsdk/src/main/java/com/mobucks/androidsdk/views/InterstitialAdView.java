package com.mobucks.androidsdk.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.ResultCode;
import com.mobucks.androidsdk.exceptions.NoAdException;
import com.mobucks.androidsdk.models.Ad;
import com.mobucks.androidsdk.network.callbacks.NetworkCall;
import com.mobucks.androidsdk.network.tasks.CheckInternet;
import com.mobucks.androidsdk.views.fragments.InterstitialFragment;
import java.util.ArrayDeque;
import java.util.Queue;

public class InterstitialAdView extends AdView<InterstitialAdView> {
    private Handler mHandler = new Handler();
    private com.appnexus.opensdk.InterstitialAdView appNexusInterstitialAdView;
    private static final String FRAGEMENT_ID = "MobucksFragment";
    private WebView webView;
    private Queue<Ad> adQueue = new ArrayDeque<>();
    private Ad currentAd = null;
    private boolean isAppNexusAd = false;

    /**
     * Creates an InterstitialAdView with the minimum required params
     * @param placementId
     * @param uid
     * @param password
     * @param context
     */
    public InterstitialAdView(@NonNull String placementId, @NonNull String uid, @NonNull String password, @NonNull Context context) {
        super(placementId,uid,password,context);
        createUi(context);
    }
    /**
     * Constructor expects placementId,uid and password from layout xml
     * @param context
     */
    public InterstitialAdView(@NonNull Context context) {
        this(context,null);
    }
    /**
     * Constructor expects placementId,uid and password from layout xml
     * @param context
     * @param attrs
     */
    public InterstitialAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    /**
     * Constructor expects placementId,uid and password from layout xml
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public InterstitialAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }
    /**
     * Constructor expects placementId,uid and password from layout xml
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public InterstitialAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        createUi(context);
    }

    /**
     *  Creates the interstitial ui
     * @param context
     */
    private void createUi(Context context){

        webView = new WebView(context);
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    adClicked(currentAd);
                }
                return false;
            }
        });

        appNexusInterstitialAdView = new com.appnexus.opensdk.InterstitialAdView(context);
        appNexusInterstitialAdView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        appNexusInterstitialAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded(com.appnexus.opensdk.AdView adView) {
                isAppNexusAd =true;
                if(adListener!=null){
                    adListener.onAdloaded(getCurrentView());
                }
            }

            @Override
            public void onAdLoaded(NativeAdResponse nativeAdResponse) {
                //No support for native
                if (adListener != null) {
                    adListener.onAdFailed(new NoAdException("No ad available"));
                }
            }

            @Override
            public void onAdRequestFailed(com.appnexus.opensdk.AdView adView, ResultCode resultCode) {
                loadOfSite();
            }

            @Override
            public void onAdExpanded(com.appnexus.opensdk.AdView adView) {

            }

            @Override
            public void onAdCollapsed(com.appnexus.opensdk.AdView adView) {

            }

            @Override
            public void onAdClicked(com.appnexus.opensdk.AdView adView) {
                if(adListener!=null){
                    adListener.onAdClicked(getCurrentView());
                }
            }

            @Override
            public void onAdClicked(com.appnexus.opensdk.AdView adView, String s) {
                if(adListener!=null){
                    adListener.onAdClicked(getCurrentView());
                }
            }
        });
        this.addView(webView);
    }

    /**
     * Returns the InterstitialAdView view
     * @return
     */
    @Override
    InterstitialAdView getCurrentView() {
        return this;
    }

    /**
     * Callback called when the ad is loaded
     * @param ad
     */
    @Override
    void adLoaded(Ad ad) {
        adQueue.add(ad);
        setRunOfSite(false);
    }

    /**
     * Callback called when there is no fill
     * @param ad
     */
    @Override
    void adNoFill(Ad ad) {
        final String tagId = ad.getTagId();

        if (isRunOfSite()) {
            if (adListener != null) {
                adListener.onAdFailed(new NoAdException("No ad available"));
            }
            setRunOfSite(false);
            return;
        }

        if(tagId == null && !isRunOfSite()){
            loadOfSite();
            return;
        }

        CheckInternet checkInternet = new CheckInternet(getContext(), new NetworkCall<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                if(result) {
                    appNexusInterstitialAdView.setPlacementID(tagId);
                    appNexusInterstitialAdView.loadAd();
                }else{
                    loadOfSite();
                }
            }

            @Override
            public void onError(Exception error) {
                loadOfSite();
            }
        });

        checkInternet.execute("8.8.8.8");
    }

    /**
     * Show the interstitial if there is available ad in queue
     */
    public void show() {
        if(isReady()){

            if(isAppNexusAd){
                appNexusInterstitialAdView.show();
                return;
            }

            ViewGroup parent = (ViewGroup)this.getParent();
            if(parent!=null){
                parent.removeView(this);
            }
            currentAd =  adQueue.poll();
            webView.clearCache(false);
            String htmlBody = (currentAd.getImageUrl()== null || currentAd.getImageUrl().isEmpty())? currentAd.getImageText():"<img src=\""+currentAd.getImageUrl()+"\"  width=\"100%\"/>";

            webView.loadData(htmlBody, "text/html; charset=utf-8", "UTF-8");
            this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            InterstitialFragment interstitialFragment = new InterstitialFragment(this);
            ((Activity)getContext()).getFragmentManager().beginTransaction().add(android.R.id.content,interstitialFragment , FRAGEMENT_ID).commit();
        }
    }

    /**
     *  Loads extra params for InterstitialView
     * @param uriBuilder
     */
    @Override
    void loadParams(Uri.Builder uriBuilder) {

    }

    /**
     * Hides the InterstitialView
     */
    public void dismiss() {
        if(isAppNexusAd){
            appNexusInterstitialAdView.destroy();
            return;
        }

        FragmentManager fm = ((Activity)getContext()).getFragmentManager();
        Fragment interstitialFragment = fm.findFragmentByTag(FRAGEMENT_ID);
        if(interstitialFragment!=null){
            fm.beginTransaction().remove(interstitialFragment).commit();
        }
    }

    /**
     * Shows the InterstitialView and hides it after time millis.
     * @param time
     */
    public void showWithAutoDismissDelay(int time){
        if(isAppNexusAd){
            appNexusInterstitialAdView.showWithAutoDismissDelay(time);
            return;
        }

        show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        },time);
    }

    /**
     * Callback called  when the ad is clicked
     * @param ad
     */
    private void adClicked(Ad ad) {
        if(ad!=null ){
            openToBrowser(ad);
            if(adListener!=null){
                adListener.onAdClicked(this);
            }
        }
    }

    /**
     * Returns true if there are available ads to show
     * @return
     */
    public  boolean isReady() {
        if(isAppNexusAd){
            return  appNexusInterstitialAdView.isReady();
        }

        Ad ad = adQueue.peek();

        while(ad!=null && !ad.isValid()){
            adQueue.remove();
            ad = adQueue.peek();
        }
        return  ad != null;
    }

    /**
     * Destroys the ad
     */
    @Override
    public void destroy() {
        adQueue = null;
        webView = null;
        currentAd = null;
        appNexusInterstitialAdView.destroy();
        appNexusInterstitialAdView = null;
        super.destroy();
    }
}
