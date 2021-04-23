package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 26,February,2021
 */
public class ConfirmDeliveryResponse {
    public int responseCode;
    public String responseMessage;
    public Data data;
    public Object exceptionMessage;
    public class PickupAddress{
        public Object name;
        public Object fullAddress;
        public Object city;
        public Object state;
        public Object country;
        public Object locallyKnownName;
        public Object longitude;
        public Object latitude;
        public Object phoneNumberPrimary;
        public Object phoneNumberSecondary;
        public Object email;
        public Object pickupUpdateDate;
        public Object pickupCreateDate;
        public Object isEnabled;
        public Object slotStartTime;
        public Object slotEndTime;
        public Object pickupAddressId;
    }

    public class Data{
        public String routeId;
        public Object routeName;
        public Object assignedDriverIdRef;
        public Object routeStatus;
        public Object routeCreateDate;
        public Object routeUpdateDate;
        public Object completionDate;
        public Object price;
        public Object routeOrders;
        public PickupAddress pickupAddress;
        public Object distance;
        public Object duration;
    }




}
