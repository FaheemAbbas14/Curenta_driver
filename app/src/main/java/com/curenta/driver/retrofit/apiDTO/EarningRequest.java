package com.curenta.driver.retrofit.apiDTO;

import java.util.Date;

/**
 * Created by faheem on 26,February,2021
 */
public class EarningRequest {
    public int driverId;
    public String fromDate;
    public String toDate;

    public EarningRequest(int driverId, String fromDate, String toDate) {
        this.driverId = driverId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
