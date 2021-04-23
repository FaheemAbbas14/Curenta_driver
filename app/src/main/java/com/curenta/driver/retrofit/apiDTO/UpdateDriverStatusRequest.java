package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 23,April,2021
 */
public class UpdateDriverStatusRequest {
    public int driverId;
    public String status;

    public UpdateDriverStatusRequest(int driverId, String status) {
        this.driverId = driverId;
        this.status = status;
    }
}
