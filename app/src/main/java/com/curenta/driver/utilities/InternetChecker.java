package com.curenta.driver.utilities;

import java.io.IOException;


/**
 * Created by faheem on 17,February,2021
 */
public class InternetChecker {
    public static boolean isInternetAvailable() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }
}
