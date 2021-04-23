package com.curenta.driver.retrofit.apiDTO;

import java.util.List;

/**
 * Created by faheem on 26,February,2021
 */
public class OrderPickupResponse {
    public int responseCode;
    public String responseMessage;
    public Data data;
    public Object exceptionMessage;

    public class Data{
        public String name;
        public String fullAddress;
        public String city;
        public String state;
        public String country;
        public String locallyKnownName;
        public double longitude;
        public double latitude;
        public String phoneNumberPrimary;
        public String phoneNumberSecondary;
        public String email;
        public String pickupUpdateDate;
        public String pickupCreateDate;
        public boolean isEnabled;
        public String slotStartTime;
        public String slotEndTime;
        public String pickupAddressId;
    }
}
