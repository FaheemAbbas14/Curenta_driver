package com.curenta.driver.interfaces;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by faheem on 27,April,2021
 */
public interface ILatLngUpdate {
    void locationChanged(LatLng location);
}
