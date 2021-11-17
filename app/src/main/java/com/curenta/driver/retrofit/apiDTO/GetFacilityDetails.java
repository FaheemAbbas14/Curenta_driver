package com.curenta.driver.retrofit.apiDTO;

/**
 * Created by faheem on 22,October,2021
 */
public class GetFacilityDetails {
    public Code code;
    public String message;
    public Data data;
    public Object exceptionMessage;
    public class Code{

        public String _1;
    }

    public class Data{
        public int facilityId;
        public String facilityName;
        public Object facilityLicenseNumber;
        public Object address;
        public Object email;
        public Object phone;
        public Object fax;
        public Object city;
        public Object contactPerson;
        public Object contactPersonRole;
        public Object adminEmail;
        public Object npi;
        public Object stateCode;
        public Object zipCode;
        public int facilityTypeRefId;
        public Object createDate;
        public Object updateDate;
        public boolean status;
        public Object updateByUser;
        public Object curentaOpsUserRefId;
        public Object curentaOpsSsouserRefId;
        public String facilityWing;
        public Object cellphone;
        public Object acquisitionDate;
        public Object assignedTo;
        public Object street;
        public Object contactPersonPhone;
        public Object facilityTypeRef;
    }
}
