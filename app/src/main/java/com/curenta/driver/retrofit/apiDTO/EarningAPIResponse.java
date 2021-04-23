package com.curenta.driver.retrofit.apiDTO;

import java.util.List;

/**
 * Created by faheem on 26,February,2021
 */
public class EarningAPIResponse {
    public int responseCode;
    public String responseMessage;
    public List<Earning> data;
    public Object exceptionMessage;
    public class Earning{
        public String driverEarningId;
        public float earningValue;
        public int driverRefId;
        public String routeRefId;
        public String createDate;
        public boolean isEnabled;
        public String earningType;
    }


}
