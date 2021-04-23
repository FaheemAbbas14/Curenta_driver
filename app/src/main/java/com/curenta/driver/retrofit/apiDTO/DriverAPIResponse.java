package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 15,February,2021
 */
public class DriverAPIResponse {
    public int responseCode;
    public String responseMessage;
    public Data data;
    public Object exceptionMessage;
    public class Data{
        public int driverId;
        public String email;
        public Object fname;
        public Object lname;
        public Object password;
        public Object mobile;
        public Object dateofBirth;
        public Object gender;
        public Object street;
        public Object city;
        public Object state;
        public Object zipcode;
        public Object profileImagePath;
        public Object licenseImagePath;
        public Object carRegistrationImagePath;
        public Object carInsuranceImagePath;
        public Object hippaComplaintLetterPath;
        public Object driverLicenseNumber;
        public Object vehicleModel;
        public Object vehiclecolor;
        public Object socialsecurityNumber;
        public Object bankname;
        public Object accountnumber;
        public Object routingnumber;
        public Object longitude;
        public Object latitude;
        public Object userIdRef;
        public Object createDate;
        public Object updateDate;
        public Object createdBy;
        public Object fcmToken;
        public Object driverStatus;
        public Object driverSelfie;
    }
}
