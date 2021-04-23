package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 12,April,2021
 */
public class UpdateDRiverLocationRequest {
    public int driverId;
    public double longitude;
    public double latitude;

    public UpdateDRiverLocationRequest(int driverId, double longitude, double latitude) {
        this.driverId = driverId;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
