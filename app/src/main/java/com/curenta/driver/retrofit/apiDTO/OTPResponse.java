package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 31,March,2021
 */
public class OTPResponse {
    public int responseCode;
    public String responseMessage;
    public Data data;
    public Object exceptionMessage;
    public class Data{
        public String email;
        public String otp;
    }

}
