package com.curenta.driver.retrofit.apiDTO;

import java.util.List;

/**
 * Created by faheem on 21,May,2021
 */
public class GetRoutesResponse {
    public int pageSize;
    public int currentPage;
    public int totalItemCount;
    public List<Datum> data;
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

    public class Order{
        public String orderId;
        public String etapickup;
        public String etabefore;
        public String etadate;
        public int patientId;
        public String patientName;
        public String patientEmail;
        public Object patientGuidCurentaRef;
        public Object patientIdPioneerRxRef;
        public int nurseId;
        public int facilityId;
        public String orderNumber;
        public String deliveryAddress;
        public String city;
        public String state;
        public String deliveryDate;
        public String deliveryNote;
        public String deliveryTime;
        public boolean isStat;
        public float distanceFromPickup;
        public float durationFromPickup;
        public Object isConfirmedByDriver;
        public Object note;
        public String prescription;
        public Object prescriptionImagePath;
        public int numberOfRxs;
        public Object confirmOrderImagePath;
        public double longitude;
        public double latitude;
        public Object completionDatetime;
        public String routeStepId;
        public String orderSource;
        public String orderStatus;
        public String processingStatus;
        public String station;
        public String stationStatus;
        public Object handlerEmployeeId;
        public Object handlerEmployeeName;
        public String createDate;
        public int createdBy;
        public String updateDate;
        public boolean isEnabled;
        public Object updateBy;
        public List<Object> orderCancelLogs;
        public List<Object> comments;
        public List<Object> confirmOrderImages;
    }

    public class RouteStep{
        public String routeStepsId;
        public String routeId;
        public Object orderId;
        public Object pickupAddressId;
        public int routeStepOrderNumber;
        public float distanceFromPickup;
        public float durationFromPickup;
        public float toDistance;
        public float toDuration;
        public double latitude;
        public double longitude;
        public String createDate;
        public String updateDate;
        public boolean isEnabled;
        public List<Order> orders;
        public Object pickupAddress;
        public String routeStepType;
    }

    public class Datum{
        public String routeId;
        public String routeName;
        public double totalPrice;
        public float outboundPricePerMile;
        public float inboundPricePerMile;
        public boolean isRoundTrip;
        public float outboundDistance;
        public float outboundDuration;
        public float inboundDistance;
        public float inboundDuration;
        public int stepCount;
        public Object driverId;
        public Object driverName;
        public Object driverEmail;
        public Object pickupImage;
        public Object pickupDate;
        public Object completeDate;
        public String createDate;
        public String updateDate;
        public boolean isEnabled;
        public String pickupAddressId;
        public double pickupLongitude;
        public double pickupLatitude;
        public PickupAddress pickupAddress;
        public List<RouteStep> routeSteps;
        public String routeStatus;
    }

}
