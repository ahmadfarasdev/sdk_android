package com.mobucks.androidsdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;

import com.mobucks.androidsdk.Globals;
import com.mobucks.androidsdk.R;
import com.mobucks.androidsdk.enumerations.Gender;
import com.mobucks.androidsdk.interfaces.AdListener;
import com.mobucks.androidsdk.logger.Logger;
import com.mobucks.androidsdk.models.Ad;
import com.mobucks.androidsdk.models.AdResponse;
import com.mobucks.androidsdk.network.NetworkService;
import com.mobucks.androidsdk.network.callbacks.NetworkCall;
import com.mobucks.androidsdk.tools.Device;
import com.mobucks.androidsdk.tools.Tools;
import com.mobucks.androidsdk.tools.xmlparser.AdXmlParser;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract AdView class provides default fuctionality
 * to the extends classes.
 *
 * @param <T>
 */
abstract class AdView<T> extends FrameLayout {

    public static final String TAG = "AdView";

    private static final String TARGETING_AGE = "age";
    private static final String TARGETING_GENDER = "gender";
    private static final String TARGETING_LANGUAGE = "language";
    private static final String TARGETING_EXTERNAL_UUID = "externalUuid";
    protected String placementId;
    private String uid;
    private String password;
    protected AdListener<T> adListener;
    private NetworkService networkService = new NetworkService();
    private AdXmlParser adXmlParser = new AdXmlParser();
    private Device device;
    private Map<String, Object> targetingMap = new HashMap<>();
    private boolean isConfigLoaded = false;
    private boolean isPlacementIdGroup = false;
    private boolean runOfSite = false;

    /**
     * Creates an AdView with the minimum required params
     *
     * @param placementId
     * @param uid
     * @param password
     * @param context
     */
    public AdView(@NonNull String placementId, @NonNull String uid, @NonNull String password, @NonNull Context context) {
        super(context);
        this.placementId = placementId;
        this.uid = uid;
        this.password = password;
        device = new Device(context);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     *
     * @param context
     */
    public AdView(@NonNull Context context) {
        this(context, null, 0);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     *
     * @param context
     * @param attrs
     */
    public AdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public AdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadDataFromLayoutXml(context, attrs);
        device = new Device(context);
    }

    /**
     * Loads user defined data from the xml layout
     *
     * @param context
     * @param attrs
     */
    private void loadDataFromLayoutXml(Context context, @Nullable AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.placementId);
        this.placementId = typedArray.getString(R.styleable.placementId_placementId);
        typedArray.recycle();
        if (this.placementId == null) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.placementGroupId);
            this.placementId = typedArray.getString(R.styleable.placementGroupId_placementGroupId);
            typedArray.recycle();
            if (this.placementId != null) {
                isPlacementIdGroup = true;
            }
        }
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.uid);
        this.uid = typedArray.getString(R.styleable.uid_uid);
        typedArray.recycle();
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.password);
        this.password = typedArray.getString(R.styleable.password_password);
        typedArray.recycle();

    }

    /**
     * Generates the ad request url that contains all the information required from the server.
     * - placementId
     * - uid
     * - password
     * - targeting data
     * - device and screen data
     *
     * @param placementId
     * @param uid
     * @param password
     * @return
     */
    private String createServerUrl(String placementId, String uid, String password) {
        Uri.Builder uriBuilder = Uri.parse(Globals.adServerApi)
                .buildUpon()
                .appendQueryParameter("uid", uid)
                .appendQueryParameter("passwd", password)
                .appendQueryParameter("sdkVersion", Globals.version)
                .appendQueryParameter("platform", "android")
                .appendQueryParameter(isPlacementIdGroup ? "pgid " : "plid", placementId);

        DisplayMetrics displayMetrics = Tools.getScreenSize(getContext());

        if (displayMetrics != null) {
            uriBuilder.appendQueryParameter("screenSize", displayMetrics.widthPixels + "x" + displayMetrics.heightPixels);
        }
        if (isRunOfSite()) {
            uriBuilder.appendQueryParameter("runOfSite", "1");
        }

        for (Map.Entry<String, Object> entry : targetingMap.entrySet()) {
            uriBuilder.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
        }

        for (Map.Entry<String, String> entry : device.getData().entrySet()) {
            uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        loadParams(uriBuilder);
        return uriBuilder.build().toString();
    }

    private String createSdkConfigUrl(String uid) {
        Uri.Builder uriBuilder = Uri.parse(Globals.sdkConfigApi)
                .buildUpon()
                .appendQueryParameter("uid", uid);

        return uriBuilder.build().toString();
    }

    /**
     * Loads extra params from the extended class
     *
     * @param uriBuilder
     */
    abstract void loadParams(Uri.Builder uriBuilder);

    /**
     * Returns the extended class view
     *
     * @return
     */
    abstract T getCurrentView();

    /**
     * Async request to the server for an Ad.
     * Depending on the server response this method calls one of the following methods
     * - adLoaded
     * - adNoFill
     * <p>
     * Also if there is an Adlistener attached calls
     * -  adListener.onAdloaded
     * -  adListener.onAdFailed
     */
    private void getAd() {
        String serverUrl = createServerUrl(placementId, uid, password);
        Log.i(TAG, "serverUrl: " + serverUrl);
        networkService.get(serverUrl, new NetworkCall<String>() {
            @Override
            public void onComplete(String result) {
                Log.i(TAG, "result: " + result);
                if (result == null) {
                    setRunOfSite(false);
                    if (adListener != null) {
                        adListener.onAdFailed(new RuntimeException("No valid response"));
                    }
                    return;
                }
                try {
                    Ad ad;
                    if (result.toLowerCase().contains("vast")) {
                        ad = new Ad();
                        ad.setVast(result);
                    } else {
                        AdResponse adResponse = adXmlParser.parse(new ByteArrayInputStream(result.getBytes()));
                        if (adResponse.hasError()) {
                            setRunOfSite(false);
                            if (adListener != null) {
                                adListener.onAdFailed(new RuntimeException(adResponse.getAdError().getDescription()));
                            }
                            return;
                        }

                        if (adResponse.isNotFilled()) {
                            adNoFill(adResponse.getAd());
                            return;
                        }
                        ad = adResponse.getAd();
                    }
                    adLoaded(ad);
                    if (adListener != null) {
                        adListener.onAdloaded(getCurrentView());
                    }

                } catch (Exception e) {
                    setRunOfSite(false);
                    if (adListener != null) {
                        adListener.onAdFailed(e);
                    }
                }
            }

            @Override
            public void onError(Exception error) {
                setRunOfSite(false);
                if (adListener != null) {
                    adListener.onAdFailed(error);
                }
            }
        });
    }

    protected void loadOfSite() {
        Log.i(TAG, "Loading inHouse ad");
        runOfSite = true;
        loadAd();
    }

    public void loadAd() {
        if (placementId == null || uid == null || password == null) {
            throw new RuntimeException("Invalid placementId, uid or password");
        }

        if (isConfigLoaded) {
            getAd();
            return;
        }

        String sdkConfigUrl = createSdkConfigUrl(uid);
        Log.i(TAG, "sdkConfigUrl: " + sdkConfigUrl);
        networkService.get(sdkConfigUrl, new NetworkCall<String>() {
            @Override
            public void onComplete(String result) {
                if (result == null) {
                    if (adListener != null) {
                        adListener.onAdFailed(new RuntimeException("No valid response"));
                    }
                    return;
                }
                try {
                    JSONObject response = new JSONObject(result);
                    if (!response.has("adServerApi")) {
                        throw new Exception("Invalid sdk configuration");
                    }
                    String serverApi = response.getString("adServerApi");

                    serverApi = Tools.transformFailUrl(serverApi);
                    Globals.adServerApi = serverApi;
                    Log.i(TAG, "adServerApi: " + Globals.adServerApi);
                    isConfigLoaded = true;
                    getAd();
                } catch (Exception e) {
                    if (adListener != null) {
                        adListener.onAdFailed(e);
                    }
                }
            }

            @Override
            public void onError(Exception error) {
                adListener.onAdFailed(error);
            }
        });
    }

    /**
     * Treats placementid as a placement group id
     *
     * @param flag
     */
    public void setPlacementIdTypeToGroup(boolean flag) {
        this.isPlacementIdGroup = flag;
    }

    /**
     * Called when an ad is returned from the ad server
     *
     * @param ad
     */
    abstract void adLoaded(Ad ad);

    /**
     * Called when server didn't fill the request
     *
     * @param ad
     */
    abstract void adNoFill(Ad ad);

    /**
     * Destroys the ad
     */
    public void destroy() {
        adListener = null;
        networkService = null;
        adXmlParser = null;
        device = null;
        targetingMap = null;
        Tools.removeViewFromParent(this);
    }

    /**
     * Set the placement id
     *
     * @param placementId
     */
    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    /**
     * Sets the uid
     *
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    protected boolean isRunOfSite() {
        return runOfSite;
    }

    protected void setRunOfSite(boolean runOfSite) {
        this.runOfSite = runOfSite;
    }

    /**
     * Sets the password
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Resize ad for proper container fitting
     */
    public void resize() {
    }

    /**
     * True if is Banner
     */
    public boolean isBanner() {
        return this instanceof BannerAdView;
    }

    /**
     * True if is Interstitial
     */
    public boolean isInterstitial() {
        return this instanceof InterstitialAdView;
    }

    /**
     * Sets the Ad listener
     *
     * @param adListener
     */
    public void setAdListener(AdListener<T> adListener) {
        this.adListener = adListener;
    }

    /**
     * Open the destination url of an ad to the native browser
     *
     * @param ad
     */
    protected void openToBrowser(Ad ad) {
        try {
            String url = ad.isVideoAd() ? ad.getVideoData().getClickUrl() : ad.getImageLink();
            if (!TextUtils.isEmpty(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                this.getContext().startActivity(intent);
            }
        } catch (Exception e) {
            Logger.e("Error while opening browser", e);
        }
    }

    /**
     * Sets the targeting age
     *
     * @param age
     */
    public void setTargetingAge(Integer age) {
        targetingMap.put(TARGETING_AGE, age);
    }

    /**
     * Returns the targeting age
     *
     * @return
     */
    public Integer getTargetingAge() {
        return (Integer) targetingMap.get(TARGETING_AGE);
    }

    /**
     * Sets the targeting gender
     *
     * @param gender
     */
    public void setTargetingGender(Gender gender) {
        targetingMap.put(TARGETING_GENDER, gender);
    }

    /**
     * Returns the targeting gender
     *
     * @return
     */
    public Gender getTargetingGender() {
        return (Gender) targetingMap.get(TARGETING_GENDER);
    }

    /**
     * Sets the targeting language
     *
     * @param language
     */
    public void setTargetingLanguage(String language) {
        targetingMap.put(TARGETING_LANGUAGE, language);
    }

    /**
     * Returns the targeting language
     *
     * @return
     */
    public String getTargetingLanguage() {
        return (String) targetingMap.get(TARGETING_LANGUAGE);
    }

    /**
     * Sets the targeting external uuid
     *
     * @param uuid
     */
    public void setTargetingExternalUuid(String uuid) {
        targetingMap.put(TARGETING_EXTERNAL_UUID, uuid);
    }

    /**
     * Returns the targeting external uuid
     *
     * @return
     */
    public String getTargetingExternalUuid() {
        return (String) targetingMap.get(TARGETING_EXTERNAL_UUID);
    }

    /**
     * Needs to be calls on android activity life cycle onDestroy
     */
    public void onDestroy() {
        destroy();
    }

    /**
     * Needs to be calls on android activity life cycle onPause
     */
    public void onPause() {
    }


    /**
     * Needs to be calls on android activity life cycle onResume
     */
    public void onResume() {
    }


}
