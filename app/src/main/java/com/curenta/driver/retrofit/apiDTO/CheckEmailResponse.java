package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 17,February,2021
 */
public class CheckEmailResponse {
    public String message;
    public User user;
    public Object exceptionMessage;


    public class User {
        public int userId;
        public String email;
        public String fname;
        public String lname;
        public Object password;
        public String mobile;
        public Object token;
        public Object createDate;
        public Object updateDate;
        public Object status;
        public Object updateBy;
        public boolean isPatient;
        public boolean isDoctor;
        public boolean isCurentaUser;
        public Object parentUserRef;
        public boolean isDriver;
        public String deviceId;
        public boolean isNurseUser;
    }

}

