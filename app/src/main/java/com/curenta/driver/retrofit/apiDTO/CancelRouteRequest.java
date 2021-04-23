package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 30,March,2021
 */
public class CancelRouteRequest {
    public String routeId;
    public String userDriverId;
    public String cancelledBy="DRIVER";
    public String cancelReason="";



    public CancelRouteRequest(String routeId, String userDriverId, String cancelReason) {
        this.routeId = routeId;
        this.userDriverId = userDriverId;
        this.cancelReason = cancelReason;
    }
}
