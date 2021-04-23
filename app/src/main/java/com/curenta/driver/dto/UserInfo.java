package com.curenta.driver.dto;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.net.URI;

public class UserInfo {
    public static UserInfo instance;
    public String firstName;
    public String lastName;
    public String imageURL;
    public String deviceId="test";
    public String userIdRef="0";
    public String fcmToken="test";
    public String userEmail;
    public String phoneNumber;
    public String password;
    public double latitude;
    public double longitude;
    public String DOB="";
    public String gender="";
    public String street="";
    public String city="";
    public String state="";
    public String zipcode="";
    public String drivingLicenseNo="";
    public String carType="";
    public String carModel="";
    public String carColor="";
    public String bankACName="";
    public String bankACNumber="";
    public String bankRTNumber ="";
    public String socialSecurityCode="";
    public Bitmap profilePic;
    public Bitmap drivingLicensePic;
    public Bitmap carRegistrationPic;
    public Bitmap carInsurancePic;
    public boolean profilePicCamera;
    public boolean drivingLicensePicCamera;
    public boolean carRegistrationPicCamera;
    public boolean carInsurancePicCamera;
    public Uri profilePicURI;
    public Uri drivingLicensePicURI;
    public Uri carRegistrationPicURI;
    public Uri carInsurancePicURI;
    public Uri i9Uri;
    public Uri w9Uri;
    public boolean isPersonalInfoCompleted = false;
    public boolean isDriveingLicenseCompleted = false;
    public boolean isVehicalInfoCompleted = false;
    public boolean isBackgroundCheckCompleted = false;
    public boolean isBankDetailsCompleted = false;
    public boolean isHIPAACompleted = false;
    public boolean isw9Completed = false;
    public boolean isi9Completed = false;
    public UserInfo() {
    }

    public static UserInfo getInstance() {
        if (instance == null) {
            instance = new UserInfo();
        }
        return instance;

    }
}
