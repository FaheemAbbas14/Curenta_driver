package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 02,March,2021
 */
public class EditProfileRequest {

        public int driverId;
        public String fname;
        public String lname;
        public String mobile;
        public String street;
        public String city;
        public String state;
        public String zipcode;
        public String vehicleModel;
        public String vehiclecolor;

    public EditProfileRequest(int driverId, String fname, String lname, String mobile, String street, String city, String state, String zipcode, String vehicleModel, String vehiclecolor) {
        this.driverId = driverId;
        this.fname = fname;
        this.lname = lname;
        this.mobile = mobile;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.vehicleModel = vehicleModel;
        this.vehiclecolor = vehiclecolor;
    }
}
