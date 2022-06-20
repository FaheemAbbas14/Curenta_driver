package com.curenta.driver.dto;

import android.content.ContextWrapper;

import com.curenta.driver.adaptors.RideDetailListAdapter;

import java.util.ArrayList;

public class AppElement {
    public static String logindevURL = "https://curentadispatcher-tests.azurewebsites.net/";
    public static String curentaordertriagingURL = "https://curentaordertriaging-test.azurewebsites.net/";
    public static String curentadispatcherURL = "https://curentadispatcher-tests.azurewebsites.net/";
    public static String productionURL = "";
    public static String token = "";
    public static String socketIOPath = "http://chat.socket.io";
    public static String routeId = "";
    public static String orderId = "";
    public static boolean isCameOnline = false;
    public static double Latitude = 0;
    public static double Longitude = 0;
    public static boolean bothImageSelection = true;
    public static ContextWrapper cw;
    public static String distance = "";
    public static String duration = "";
    public static String pharmacyContact;
    public static String patientContact;
    public static ArrayList<Integer> delivered = new ArrayList<>();
    public static boolean isPickupCompleted = false;
    public static int routeStepIndex = 0;
    public static int orderIndex = 0;
    public static ArrayList<RideDetailListAdapter.RoutStep> sections = new ArrayList<>();
}
