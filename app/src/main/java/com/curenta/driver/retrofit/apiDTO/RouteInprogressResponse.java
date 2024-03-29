package com.curenta.driver.retrofit.apiDTO;

import java.util.List;

/**
 * Created by faheem on 15,April,2021
 */
public class RouteInprogressResponse {
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
    public int responseCode;
    public String responseMessage;
    public Data data;
    public Object exceptionMessage;
    public class Patient{
        public int patientId;
        public String fname;
        public String lname;
        public String dob;
        public String phonenumber;
        public String email;
        public Object userIdRef;
        public String createDate;
        public String updateDate;
        public boolean status;
        public Object updateBy;
        public Object address;
        public Object zipCode;
        public Object city;
        public Object state;
        public String gender;
        public boolean isConsent;
        public Object consentDate;
        public Object profilePicPath;
        public boolean haveinsurance;
        public Object planIddesc;
        public Object planIdNumber;
        public Object planIdRef;
        public Object pharmacyName;
        public Object pharmacyAddress;
        public Object pharmacyPhoneNumber;
        public Object insCompanyIdref;
        public String patientIdCurenta;
        public String patientIdPioneerRx;
        public Object insImagePath1;
        public Object insImagePath2;
        public Object insImagePath3;
        public Object insImagePath4;
        public Object hospicesIdRef;
        public Object fax;
        public Object stateCode;
        public String comments;
        public Object medicationStatus;
        public Object actionStatus;
        public Object mainDiagnosis;
        public boolean isLtcpatient;
        public int facilityIdRef;
        public String facilityType;
        public Object residenceFacilityIdRef;
        public Object residenceType;
        public Object deliveryType;
        public Object deliveryAddress;
        public String patientType;
        public int nurseIdRef;
        public List<Object> patientAddresses;
    }

    public class RouteOrder{
        public String orderId;
        public int patientIdRef;
        public String patientEmail;
        public Object patientGuidCurentaRef;
        public Object patientIdPioneerRxRef;
        public int nurseIdRef;
        public int facilityIdRef;
        public String orderNumber;
        public String orderSource;
        public String deliveryAddress;
        public String patientName;
        public String deliveryDate;
        public String deliveryTime;
        public int devliveryDistance;
        public String currentStatus;
        public boolean isPushedNotification;
        public boolean isConfirmedByDriver;
        public String orderCreateDate;
        public String orderUpdateDate;
        public Object updateBy;
        public Object travelRequiredByDriver;
        public Object pushDateAndTime;
        public Object prescription;
        public String prescriptionImagePath;
        public String routeIdRef;
        public Object confirmOrderImagePath;
        public double longitude;
        public double latitude;
        public int dileveryOrder;
        public Object completionDate;
        public Patient patient;
        public Object mainDiagnosis;
        public Object poolGroupIdRef;
        public Object deadlineDate;
        public String cancelledBy;
        public int cancelledById;
        public String cancelReason;
        public int eta;
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

    public class RouteData{
        public String routeId;
        public String routeName;
        public int assignedDriverIdRef;
        public String routeStatus;
        public String routeCreateDate;
        public String routeUpdateDate;
        public Object completionDate;
        public double price;
        public double pricePerMile;
        public boolean isRoundTrip;
        public double returnTripPrice;
        public double returnTripDistance;
        public double returnTripDuration;
        public double notificationsSent;
        public String sendNotificationOn;
        public List<RouteOrder> routeOrders;
        public PickupAddress pickupAddress;
        public double distance;
        public double duration;
        public Object pickupDateTime;
        public Object pickupConfirmationImagePath;
        public Object cancelledBy;
        public Object cancelledById;
        public Object cancelReason;
    }
    public class Update{
        public String appVersion;
        public String updateType;
        public String platformType;
    }
    public class Data{
        public RouteData routeData;
        public List<Update> updates;
    }

}
