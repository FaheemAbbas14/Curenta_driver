package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 21,May,2021
 */
public class ConfirmOrderResponse {
    public Data data;
    public String responseType;
    public int responseCode;
    public String responseName;
    public String responseMessage;
    public class PickupAddress{
        public String pickupAddressId;
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
    }

    public class Data{
        public String routeId;
        public Object routeName;
        public Object totalPrice;
        public Object outboundPricePerMile;
        public Object inboundPricePerMile;
        public Object isRoundTrip;
        public Object outboundDistance;
        public Object outboundDuration;
        public Object inboundDistance;
        public Object inboundDuration;
        public Object stepCount;
        public Object driverId;
        public Object driverName;
        public Object driverEmail;
        public Object pickupImage;
        public Object pickupDate;
        public Object completeDate;
        public Object createDate;
        public Object updateDate;
        public Object isEnabled;
        public Object pickupAddressId;
        public Object pickupLongitude;
        public Object pickupLatitude;
        public PickupAddress pickupAddress;
        public Object routeSteps;
        public Object routeStatus;
    }
}
