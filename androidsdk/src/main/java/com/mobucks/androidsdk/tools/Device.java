package com.mobucks.androidsdk.tools;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Device information interface
 */
public class Device {
    private ConnectivityManager connectivityManager = null;
    private Location location;
    private String country;
    private String networkOperatorName;
    private String advertisingId;
    private String packageName;
    private String ua;

    public Device(Context context) {
        packageName = context.getPackageName();
        findLastKnownLocation(context);
        loadNetwork(context);
        loadConnectivityManager(context);
        loadAdvertisingId(context);
        loadUa(context);
    }

    /**
     * Get device manufacturer
     * @return
     */
    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * Get device model
     * @return
     */
    public String getModel() {
        return Build.MODEL;
    }

    /**
     * Get device country
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * Get device last known location
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get device network connection
     * @return
     */
    public String getNetworkConnection() {
        NetworkInfo info =null;
        try {
            info = connectivityManager.getActiveNetworkInfo();
        }catch (SecurityException e){
        }
        if (info == null || !info.isConnected())
            return "0";
        int type = info.getType();
        if (type==ConnectivityManager.TYPE_WIFI) {
            return "2";
        } else if(type==ConnectivityManager.TYPE_MOBILE) {
            switch (info.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "4"; // Cellular Data - 2G
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "5"; // Cellular data - 3G
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "6"; // Cellular data - 4G
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return "3"; // Cellular data - Unknown Generation
            }
        } else {
            return "0";
        }
    }

    /**
     * Get network operator name
     * @return
     */
    public String getNetworkOperatorName() {
        return networkOperatorName;
    }

    /**
     * Find last known location
     * @param context
     */
    private void findLastKnownLocation(Context context) {
        try {
            FusedLocationProviderClient mFusedLocationClient =LocationServices.getFusedLocationProviderClient(context);
            mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Device.this.location = location;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load ua
     * @param context
     */
    private void loadUa(Context context) {
        if (Build.VERSION.SDK_INT >= 17) {
            ua = WebSettings.getDefaultUserAgent(context);
        }else{
            ua =  System.getProperty("http.agent");
        }
    }
    /**
     * Load country info from sim card
     * @param context
     */
    private void loadNetwork(Context context){
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try { networkOperatorName = manager.getNetworkOperatorName(); } catch (Exception e) { e.printStackTrace();}
        try { country = manager.getSimCountryIso(); } catch (Exception e) { e.printStackTrace();}
    }

    /**
     * Loads connectivity manager
     * @param context
     */
    private void loadConnectivityManager(Context context){
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Load adsertising id from google services
     * @param context
     */
    private void loadAdvertisingId(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info  advertisingInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    advertisingId = advertisingInfo.getId();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * Returns user agent
     * @return
     */
    public String getUa() {
        return ua;
    }

    /**
     * Get google advertising id
     * @return
     */
    public String getAdvertisingId() {
        return advertisingId;
    }

    /**
     * Get package name
     * @return
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Returns device data in a map .
     * @return
     */
    public Map<String,String>  getData() {
        Map<String,String> data = new HashMap<>();
        if(packageName!=null){
            data.put("packageName",packageName);
        }
        if(country!=null){
            data.put("country",country);
        }
        if(networkOperatorName!=null){
            data.put("networkOperatorName",networkOperatorName);
        }
        if(advertisingId!=null){
            data.put("advertisingId",advertisingId);
        }
        if(location!=null){
            data.put("latitude",String.valueOf(location.getLatitude()));
            data.put("longitude",String.valueOf(location.getLongitude()));
        }

        data.put("networkConnection",getNetworkConnection());
        data.put("ua",ua);
        return  data;
    }
}
