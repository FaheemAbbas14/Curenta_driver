package com.curenta.driver.utilities;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.io.InputStream;

public class JsonLoader {
    public static  String loadJSONFromAsset(Activity activiy,String fileName) {
        String json = null;
        try {
            InputStream is = activiy.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
