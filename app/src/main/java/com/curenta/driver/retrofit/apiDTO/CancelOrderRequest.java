package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 30,March,2021
 */
public class CancelOrderRequest {

    public String routeId;
    public String orderId;
    public String userDriverId;
    public String cancelledBy="DRIVER";
    public String cancelReason="";

    public CancelOrderRequest(String routeId, String orderId, String userDriverId, String cancelReason) {
        this.routeId = routeId;
        this.orderId = orderId;
        this.userDriverId = userDriverId;
        this.cancelReason = cancelReason;
    }
}
