package com.mobucks.androidsdk.views;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import com.appnexus.opensdk.AdListener;
import com.appnexus.opensdk.NativeAdResponse;
import com.appnexus.opensdk.ResultCode;
import com.mobucks.androidsdk.exceptions.NoAdException;
import com.mobucks.androidsdk.models.Ad;
import com.mobucks.androidsdk.models.AdSize;
import com.mobucks.androidsdk.network.callbacks.NetworkCall;
import com.mobucks.androidsdk.network.tasks.CheckInternet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AdView for Banner display
 */
public class BannerAdView extends AdView<BannerAdView> {
    private Handler mHandler = new Handler();

    private Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (ad != null && (!ad.isValid() || ad.isClicked())) {
                loadAd();
            }
            mHandler.postDelayed(mHandlerTask, autoRefreshInterval);
        }
    };
    private long autoRefreshInterval = 0;
    private List<AdSize> adSizes = new ArrayList<>();
    private AdSize maxSize;
    private boolean shouldReloadOnResume = false;
    private boolean expandToFitScreenWidth = true;
    private boolean resizeWebViewToFitContainer = true;
    private WebView webView;
    private com.appnexus.opensdk.BannerAdView appNexusBannerAdView;
    private Ad ad;
    private boolean hasBeenPaused = false;
    private boolean isAppNexusAd = false;

    /**
     * Creates an BannerAdView with the minimum required params
     *
     * @param placementId
     * @param uid
     * @param password
     * @param context
     */
    public BannerAdView(@NonNull String placementId, @NonNull String uid, @NonNull String password, @NonNull Context context) {
        super(placementId, uid, password, context);
        createUi(context);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     *
     * @param context
     */
    public BannerAdView(@NonNull Context context) {
        this(context, null, 0, 0);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     *
     * @param context
     * @param attrs
     */
    public BannerAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public BannerAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public BannerAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        createUi(context);
    }

    /**
     * Loads extra params for BannerView
     *
     * @param uriBuilder
     */
    @Override
    void loadParams(Uri.Builder uriBuilder) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (AdSize adSize : adSizes) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(",");
            }
            stringBuilder.append(adSize.toString());
        }
        if (adSizes.size() > 0) {
            uriBuilder.appendQueryParameter("adSizes", stringBuilder.toString());
        }
        if (maxSize != null) {
            uriBuilder.appendQueryParameter("maxSize", maxSize.toString());
        }

    }

    /**
     * Returns the BannerAd view
     *
     * @return
     */
    @Override
    BannerAdView getCurrentView() {
        return this;
    }

    /**
     * Creates the banner ui
     *
     * @param context
     */
    private void createUi(Context context) {
        webView = new WebView(context);
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    adClicked(ad);
                }
                return false;
            }
        });

        appNexusBannerAdView = new com.appnexus.opensdk.BannerAdView(context);
        appNexusBannerAdView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        appNexusBannerAdView.setAutoRefreshInterval(0);
        appNexusBannerAdView.setAllowVideoDemand(false);
        appNexusBannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded(com.appnexus.opensdk.AdView adView) {
                if (adListener != null) {
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
                if (adListener != null) {
                    adListener.onAdClicked(getCurrentView());
                }
            }

            @Override
            public void onAdClicked(com.appnexus.opensdk.AdView adView, String s) {
                if (adListener != null) {
                    adListener.onAdClicked(getCurrentView());
                }
            }
        });
        this.addView(appNexusBannerAdView);
        this.addView(webView);
        webView.setVisibility(GONE);
        appNexusBannerAdView.setVisibility(GONE);

    }

    /**
     * Switch to  appNexus mode
     */
    private void appNexusMode() {
        isAppNexusAd = true;
        appNexusBannerAdView.setVisibility(VISIBLE);
        webView.setVisibility(GONE);
    }

    /**
     * Switch to  mobucks  mode
     */
    private void mobucksMode() {
        isAppNexusAd = false;
        appNexusBannerAdView.setVisibility(GONE);
        webView.setVisibility(VISIBLE);
    }

    /**
     * Destroys the ad
     */
    @Override
    public void destroy() {
        super.destroy();
        mHandler = null;
        mHandlerTask = null;
        adSizes = null;
        webView = null;
        ad = null;
        appNexusBannerAdView.destroy();
        appNexusBannerAdView = null;
    }

    /**
     * Callback called when the ad is loaded
     *
     * @param ad
     */
    @Override
    void adLoaded(Ad ad) {
        this.ad = ad;
        mobucksMode();
        webView.clearCache(false);
        String htmlBody = (ad.getImageUrl() == null || ad.getImageUrl().isEmpty()) ? ad.getImageText() : "<img src=\"" + ad.getImageUrl() + "\"  width=\"100%\"/>";
        setRunOfSite(false);
        webView.loadData(htmlBody, "text/html; charset=utf-8", "UTF-8");
    }

    /**
     * Callback called when there is no fill
     *
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

        if (tagId == null && !isRunOfSite()) {
            loadOfSite();
            return;
        }
        CheckInternet checkInternet = new CheckInternet(getContext(), new NetworkCall<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                if (result) {
                    Log.i(TAG, "Loading AppNexus ad");
                    appNexusMode();
                    appNexusBannerAdView.setPlacementID(tagId);
                    appNexusBannerAdView.loadAd();
                } else {
                    loadOfSite();
                }
            }

            @Override
            public void onError(Exception error) {
                loadOfSite();
            }
        });
        checkInternet.execute("http://ib.adnxs.com/getuid?http://ib.mobucks.vodacom.co.za/?id=$UID&a=$AppleID&b=$AndrID");
    }

    /**
     * Callback called  when the ad is clicked
     *
     * @param ad
     */
    private void adClicked(Ad ad) {
        if (ad != null) {
            openToBrowser(ad);
            if (adListener != null) {
                adListener.onAdClicked(this);
            }
        }
    }

    /**
     * Set an auto rerfresh ad policy
     *
     * @param autoRefreshInterval
     */
    public void setAutoRefreshInterval(long autoRefreshInterval) {
        this.autoRefreshInterval = autoRefreshInterval;
        mHandler.removeCallbacks(mHandlerTask);
        if (autoRefreshInterval > 0) {
            mHandler.postDelayed(mHandlerTask, autoRefreshInterval);
        }
    }

    /**
     * Ad size for the requested ad
     *
     * @param width
     * @param height
     */
    public void setAdSize(int width, int height) {
        adSizes.clear();
        adSizes.add(new AdSize(width, height));
        appNexusBannerAdView.setAdSize(width, height);
    }

    /**
     * Many exceptable ad sizes for the requested ad
     *
     * @param adSizesInput
     */
    public void setAdSizes(AdSize... adSizesInput) {
        adSizes.clear();
        adSizes.addAll(Arrays.asList(adSizesInput));
        ArrayList<com.appnexus.opensdk.AdSize> appNexusAddSizes = new ArrayList<>();
        for (AdSize adSize : adSizes) {
            appNexusAddSizes.add(new com.appnexus.opensdk.AdSize(adSize.getWidth(), adSize.getHeight()));
        }
        appNexusBannerAdView.setAdSizes(appNexusAddSizes);
    }

    /**
     * Max size of the requested ad
     *
     * @param width
     * @param height
     */
    public void setMaxSize(int width, int height) {
        maxSize = new AdSize(width, height);
        appNexusBannerAdView.setMaxSize(width, height);
    }

    /**
     * If true the ad will refresh on activity resume
     *
     * @param shouldReloadOnResume
     */
    public void setShouldReloadOnResume(boolean shouldReloadOnResume) {
        this.shouldReloadOnResume = shouldReloadOnResume;
    }

    /**
     * Expand view to fit screen size
     *
     * @param expandToFitScreenWidth
     */
    public void setExpandToFitScreenWidth(boolean expandToFitScreenWidth) {
        this.expandToFitScreenWidth = expandToFitScreenWidth;
        appNexusBannerAdView.setExpandsToFitScreenWidth(true);
    }

    /**
     * Resize webview to fit container
     *
     * @param resizeWebViewToFitContainer
     */
    public void setResizeWebViewToFitContainer(boolean resizeWebViewToFitContainer) {
        this.resizeWebViewToFitContainer = resizeWebViewToFitContainer;
        appNexusBannerAdView.setResizeAdToFitContainer(resizeWebViewToFitContainer);
    }


    //Android lifecycle

    /**
     * Needs to be calls on android activity life cycle onResume
     */
    @Override
    public void onResume() {
        super.onResume();
        appNexusBannerAdView.activityOnResume();
        setAutoRefreshInterval(autoRefreshInterval);
        if (shouldReloadOnResume && hasBeenPaused) {
            loadAd();
        }
    }

    /**
     * Needs to be calls on android activity life cycle onPause
     */
    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mHandlerTask);
        appNexusBannerAdView.activityOnPause();
        hasBeenPaused = true;
    }

    /**
     * Needs to be calls on android activity life cycle onDestroy
     */
    @Override
    public void onDestroy() {
        appNexusBannerAdView.activityOnDestroy();
        super.onDestroy();
    }

}
