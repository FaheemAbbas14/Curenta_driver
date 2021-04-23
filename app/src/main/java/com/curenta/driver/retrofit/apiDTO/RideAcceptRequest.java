package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 25,February,2021
 */
public class RideAcceptRequest {

        public int driverId;
        public String routeId;

    public RideAcceptRequest(int driverId, String routeId) {
        this.driverId = driverId;
        this.routeId = routeId;
    }
}
