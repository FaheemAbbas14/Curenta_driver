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
    public String cancelledBy;
    public String relation;
    public String newAddress;
    public CancelRouteRequest(String routeId, String cancelReason, int userId, String userEmail, String cancelledBy, String relation, String newAddress) {
        this.routeId = routeId;
        this.cancelReason = cancelReason;
        this.userId = userId;
        this.userEmail = userEmail;
        this.cancelledBy = cancelledBy;
        this.relation = relation;
        this.newAddress = newAddress;
    }
}
