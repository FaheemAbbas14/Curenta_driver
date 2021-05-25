package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 30,March,2021
 */
public class CancelRouteRequest {
    public String routeId;
    public String userType="DRIVER";
    public String cancelReason="";
    public int userId;
    public String userEmail;


    public CancelRouteRequest(String routeId, String cancelReason, int userId, String userEmail) {
        this.routeId = routeId;
        this.cancelReason = cancelReason;
        this.userId = userId;
        this.userEmail = userEmail;
    }
}
