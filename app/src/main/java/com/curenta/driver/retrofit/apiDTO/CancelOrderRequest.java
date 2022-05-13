package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 30,March,2021
 */
public class CancelOrderRequest {
    public String routeId;
    public String routeStepId;
    public int userId;
    public String userEmail;
    public String cancelReason;
    public String userType="DRIVER";
    public String cancelledBy;
    public String relation;
    public String newAddress;
    public CancelOrderRequest(String routeId, String routeStepId, int userId, String userEmail, String cancelReason, String cancelledBy, String newAddress, String relation) {
        this.routeId = routeId;
        this.routeStepId = routeStepId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.cancelReason = cancelReason;
        this.cancelledBy = cancelledBy;
        this.newAddress = newAddress;
        this.relation = relation;
    }
}
