package com.curenta.driver.utilities;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by faheem on 29,March,2021
 */
public class Helper {
    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }
        Log.d("deviceid", deviceId);
        return deviceId;
    }
}
