package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 16,March,2021
 */
public class ChangePasswordRequest {
    public String email;
    public String newPassword;

    public ChangePasswordRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
