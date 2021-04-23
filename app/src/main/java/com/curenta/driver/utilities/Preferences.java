package com.curenta.driver.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.curenta.driver.MainApplication;

/**
 * Created by faheem on 19,February,2021
 */
public class Preferences {
    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mPrefsEditor;
    public static final Preferences instance;
    private final Context context;

    static {
        instance = new Preferences();
    }

    public static Preferences getInstance() {

        return instance;

    }

    {
        context = MainApplication.getContext();
    }

    public Preferences() {

        String appSharedPrefs = "CurentaDriver";
        this.mSharedPrefs = context.getSharedPreferences(appSharedPrefs, Context.MODE_PRIVATE);
        this.mPrefsEditor = mSharedPrefs.edit();
    }

    public String getString(String key) {
        return mSharedPrefs.getString(key, ""); // Get our string from prefs or return an empty string
    }

    public String getString(String key, String defaultValue) {
        return mSharedPrefs.getString(key, defaultValue); // Get our string from prefs or return an empty string
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPrefs.getInt(key, defaultValue); // Get our string from prefs or return an empty string
    }

    public float getFloat(String key, float defaultValue) {
        return mSharedPrefs.getFloat(key, defaultValue); // Get our string from prefs or return an empty string
    }

    public void setString(String key, String value) {
        mPrefsEditor.putString(key, value);
        mPrefsEditor.commit();
    }

    public void setInt(String key, int value) {
        mPrefsEditor.putInt(key, value);
        mPrefsEditor.commit();
    }

    public void setFloat(String key, float value) {
        mPrefsEditor.putFloat(key, value);
        mPrefsEditor.commit();
    }

    public void saveBoolean(String key, Boolean value) {


        mPrefsEditor.putBoolean(key, value);
        mPrefsEditor.commit();
    }

    public Boolean getBoolean(String key) {
        return mSharedPrefs.getBoolean(key, false);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return mSharedPrefs.getBoolean(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return mSharedPrefs.getLong(key, defaultValue);
    }

    public void saveLong(String key, long value) {
        mPrefsEditor.putLong(key, value);
        mPrefsEditor.commit();
    }

}