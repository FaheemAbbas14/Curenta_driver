package com.curenta.driver.dto;

import com.curenta.driver.retrofit.apiDTO.DriverAPIResponse;

/**
 * Created by faheem on 16,February,2021
 */
public class LoggedInUser {
    public static LoggedInUser instance;
    public int driverId;
    public String email;
    public String fname;
    public String lname;
    public String password;
    public String mobile;
    public String dateofBirth;
    public String gender;
    public String street;
    public String city;
    public String state;
    public String zipcode;
    public String profileImagePath;
    public String licenseImagePath;
    public String carRegistrationImagePath;
    public String carInsuranceImagePath;
    public String hippaComplaintLetterPath;
    public String driverLicenseNumber;
    public String vehicleModel;
    public String vehiclecolor;
    public String socialsecurityNumber;
    public String bankname;
    public String accountnumber;
    public String routingnumber;
    public double longitude;
    public double latitude;
    public double userIdRef;
    public String createDate;
    public String updateDate;
    public String createdBy;
    public String fcmToken;
    public String driverStatus;
    public String driverSelfie;
    public boolean isOnline = false;
    public boolean isCovidPassed = false;
    public boolean isSelfie = false;

    public LoggedInUser() {
    }

    static {

        instance = new LoggedInUser();


    }

    public LoggedInUser(int driverId, String email, String fname, String lname, String password, String mobile, String dateofBirth, String gender, String street, String city, String state, String zipcode, String profileImagePath, String licenseImagePath, String carRegistrationImagePath, String carInsuranceImagePath, String hippaComplaintLetterPath, String driverLicenseNumber, String vehicleModel, String vehiclecolor, String socialsecurityNumber, String bankname, String accountnumber, String routingnumber, double longitude, double latitude, double userIdRef, String createDate, String updateDate, String createdBy, String fcmToken, String driverStatus, String driverSelfie) {
        this.driverId = driverId;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.password = password;
        this.mobile = mobile;
        this.dateofBirth = dateofBirth;
        this.gender = gender;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.profileImagePath = profileImagePath;
        this.licenseImagePath = licenseImagePath;
        this.carRegistrationImagePath = carRegistrationImagePath;
        this.carInsuranceImagePath = carInsuranceImagePath;
        this.hippaComplaintLetterPath = hippaComplaintLetterPath;
        this.driverLicenseNumber = driverLicenseNumber;
        this.vehicleModel = vehicleModel;
        this.vehiclecolor = vehiclecolor;
        this.socialsecurityNumber = socialsecurityNumber;
        this.bankname = bankname;
        this.accountnumber = accountnumber;
        this.routingnumber = routingnumber;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userIdRef = userIdRef;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.createdBy = createdBy;
        this.fcmToken = fcmToken;
        this.driverStatus = driverStatus;
        this.driverSelfie = driverSelfie;
    }

    public static LoggedInUser getInstance() {
        if (instance == null) {
            instance = new LoggedInUser();
        }
        return instance;
    }

    public void setInstance(LoggedInUser loggedInUser) {

        this.driverId = loggedInUser.driverId;
        this.email = loggedInUser.email;
        this.fname = loggedInUser.fname;
        this.lname = loggedInUser.lname;
        this.password = loggedInUser.password;
        this.mobile = loggedInUser.mobile;
        this.dateofBirth = loggedInUser.dateofBirth;
        this.gender = loggedInUser.gender;
        this.street = loggedInUser.street;
        this.city = loggedInUser.city;
        this.state = loggedInUser.state;
        this.zipcode = loggedInUser.zipcode;
        this.profileImagePath = loggedInUser.profileImagePath;
        this.licenseImagePath = loggedInUser.licenseImagePath;
        this.carRegistrationImagePath = loggedInUser.carRegistrationImagePath;
        this.carInsuranceImagePath = loggedInUser.carInsuranceImagePath;
        this.hippaComplaintLetterPath = loggedInUser.hippaComplaintLetterPath;
        this.driverLicenseNumber = loggedInUser.driverLicenseNumber;
        this.vehicleModel = loggedInUser.vehicleModel;
        this.vehiclecolor = loggedInUser.vehiclecolor;
        this.socialsecurityNumber = loggedInUser.socialsecurityNumber;
        this.bankname = loggedInUser.bankname;
        this.accountnumber = loggedInUser.accountnumber;
        this.routingnumber = loggedInUser.routingnumber;
        this.longitude = loggedInUser.longitude;
        this.latitude = loggedInUser.latitude;
        this.userIdRef = loggedInUser.userIdRef;
        this.createDate = loggedInUser.createDate;
        this.updateDate = loggedInUser.updateDate;
        this.createdBy = loggedInUser.createdBy;
        this.fcmToken = loggedInUser.fcmToken;
        this.driverStatus = loggedInUser.driverStatus;
        this.driverSelfie = loggedInUser.driverSelfie;

    }

    public void copyData(DriverAPIResponse.Data data) {
        this.driverId = data.driverId;
        this.email = data.email;
        this.fname = (String) data.fname;
        this.lname = (String) data.lname;
        this.password = (String) data.password;
        this.mobile = (String) data.mobile;
        this.dateofBirth = (String) data.dateofBirth;
        this.gender = (String) data.gender;
        this.street = (String) data.street;
        this.city = (String) data.city;
        this.state = (String) data.state;
        this.zipcode = (String) data.zipcode;
        this.profileImagePath = (String) data.profileImagePath;
        this.licenseImagePath = (String) data.licenseImagePath;
        this.carRegistrationImagePath = (String) data.carRegistrationImagePath;
        this.carInsuranceImagePath = (String) data.carInsuranceImagePath;
        this.hippaComplaintLetterPath = (String) data.hippaComplaintLetterPath;
        this.driverLicenseNumber = (String) data.driverLicenseNumber;
        this.vehicleModel = (String) data.vehicleModel;
        this.vehiclecolor = (String) data.vehiclecolor;
        this.socialsecurityNumber = (String) data.socialsecurityNumber;
        this.bankname = (String) data.bankname;
        this.accountnumber = (String) data.accountnumber;
        this.routingnumber = (String) data.routingnumber;
        this.longitude = (double) data.longitude;
        this.latitude = (double) data.latitude;
        this.userIdRef = (double) data.userIdRef;
        this.createDate = (String) data.createDate;
        this.updateDate = (String) data.updateDate;
        this.createdBy = (String) data.createdBy;
        this.fcmToken = (String) data.fcmToken;
        this.driverStatus = (String) data.driverStatus;
        this.driverSelfie = (String) data.driverSelfie;
    }
}
