package com.curenta.driver.retrofit.apiDTO;

import java.util.List;

/**
 * Created by faheem on 22,October,2021
 */
public class GetPatientDetailResponse {
    public String message;
    public List<Patient> patients;
    public Object exceptionMessage;
    public OperationStatus operationStatus;

    public class PatientAddress{
        public int addressId;
        public String address;
        public String street;
        public String city;
        public String state;
        public String zipCode;
        public Object addressType;
        public int patientIdRef;
        public boolean status;
        public boolean isDefault;
        public Object updateBy;
    }

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
        public Object mbi;
        public Object medicalNumb;
        public Object primaryDx;
        public Object mrnumber;
        public Object socialSecurityNumb;
        public Object ltcstatus;
        public Object street;
        public Object deliveryZipCode;
        public Object deliveryCity;
        public Object deliveryState;
        public boolean isexternalPatient;
        public Object uploadFilePath;
        public Object patientStatus;
        public Object isBp;
        public List<Object> insuranceCard;
        public List<Object> orders;
        public List<PatientAddress> patientAddresses;
        public List<Object> patientAllergies;
        public List<Object> patientFiles;
        public List<Object> refillRequestMain;
        public List<Object> transferRequest;
    }

    public class OperationStatus{

        public String _1;
    }


}
