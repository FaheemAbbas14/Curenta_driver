package com.curenta.driver.retrofit.apiDTO;


import java.util.List;

/**
 * Created by faheem on 26,February,2021
 */
public class GetRouteResponse {
    public int responseCode;
    public String responseMessage;
    public Data data;
    public Object exceptionMessage;
    public class RouteOrder{
        public String orderId;
        public Object patientIdRef;
        public String patientEmail;
        public Object patientGuidCurentaRef;
        public Object patientIdPioneerRxRef;
        public int nurseIdRef;
        public Object facilityIdRef;
        public String orderNumber;
        public String deliveryAddress;
        public String patientName;
        public String deliveryDate;
        public String deliveryTime;
        public double devliveryDistance;
        public String currentStatus;
        public boolean isPushedNotification;
        public boolean isConfirmedByDriver;
        public String orderCreateDate;
        public String orderUpdateDate;
        public Object updateBy;
        public Object travelRequiredByDriver;
        public Object pushDateAndTime;
        public String prescription;
        public String prescriptionImagePath;
        public String routeIdRef;
        public Object confirmOrderImagePath;
        public double longitude;
        public double latitude;
        public int dileveryOrder;
        public Object completionDate;
        public Object patient;
    }

    public class PickupAddress{
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

    public class Data{
        public String routeId;
        public String routeName;
        public Object assignedDriverIdRef;
        public String routeStatus;
        public String routeCreateDate;
        public String routeUpdateDate;
        public Object completionDate;
        public double price;
        public double pricePerMile;
        public boolean isRoundTrip;
        public double returnTripPrice;
        public int returnTripDistance;
        public int returnTripDuration;
        public int notificationsSent;
        public String sendNotificationOn;
        public List<RouteOrder> routeOrders;
        public PickupAddress pickupAddress;
        public int distance;
        public int duration;
        public Object pickupDateTime;
        public Object pickupConfirmationImagePath;
        public Object cancelledBy;
        public Object cancelledById;
        public Object cancelReason;
    }
}
