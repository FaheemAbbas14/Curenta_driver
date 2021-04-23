package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 15,February,2021
 */
public class LoginRequest {

    public String email;

    public String password;

    public String fcmToken;

    public String deviceId;

    public LoginRequest(String email, String password, String fcmToken, String deviceId) {
        this.email = email;
        this.password = password;
        this.fcmToken = fcmToken;
        this.deviceId = deviceId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
