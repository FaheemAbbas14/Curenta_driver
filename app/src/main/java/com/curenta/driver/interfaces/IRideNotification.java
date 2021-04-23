package com.curenta.driver.interfaces;

import com.curenta.driver.dto.RideInfoDto;

/**
 * Created by faheem on 24,February,2021
 */
public interface IRideNotification {
    void rideNotification(RideInfoDto rideInfoDto);
    void cancelNotification(int id);
}
