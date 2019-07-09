package com.mobucks.demo;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    public static final String KEY_BRIGHTNESS = "brightness";

    private static final String MOVEN_PREFERENCE = "MovenPreference";

    private SharedPreferences sPreferences;
    private static AppPreference instance;

    private AppPreference(Context context) {
        sPreferences = context.getSharedPreferences(MOVEN_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static AppPreference getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreference(context);
        }
        return instance;
    }

    public void setStringSharedPreference(String key, String value) {
        sPreferences.edit().putString(key, value).apply();
    }

    public void setBooleanSharedPreference(String key, boolean value) {
        sPreferences.edit().putBoolean(key, value).apply();
    }

    public void setLongSharedPreference(String key, long value) {
        sPreferences.edit().putLong(key, value).apply();
    }

    public void setFloatSharedPreference(String key, float value) {
        sPreferences.edit().putFloat(key, value).apply();
    }

    public String getStringSharedPreference(String key) {
        return sPreferences.getString(key, null);
    }

    public String getStringSharedPreference(String key, String defaultValue) {
        return sPreferences.getString(key, defaultValue);
    }

    public float getFloatSharedPreference(String key, float defValue) {
        return sPreferences.getFloat(key, defValue);
    }

    public long getLongSharedPreference(String key) {
        return sPreferences.getLong(key, 0);
    }

    public boolean getBooleanSharedPreference(String key) {
        return sPreferences.getBoolean(key, true);
    }

    public void clearPreferences() {
        sPreferences.edit().clear().apply();
    }
}
