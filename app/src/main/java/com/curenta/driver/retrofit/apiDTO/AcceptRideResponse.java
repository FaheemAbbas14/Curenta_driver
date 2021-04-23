package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 25,February,2021
 */
public class AcceptRideResponse {

    public int responseCode;
    public String responseMessage;
    public Data data;
    public Object exceptionMessage;

    public class Data {
        public String routeId;
        public Object routeName;
        public Object assignedDriverIdRef;
        public Object routeStatus;
        public Object routeCreateDate;
        public Object routeUpdateDate;
        public Object completionDate;
        public Object price;
        public Object routeOrders;
        public Object pickupAddress;
        public Object distance;
        public Object duration;
    }


}
