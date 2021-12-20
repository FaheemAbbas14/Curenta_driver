package com.curenta.driver.dto;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.collection.ArraySet;

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
    public static int distance = 0;
    public static int duration = 0;
    public static String durationtxt = "";
    public static String pharmacyContact;
    public static String patientContact;
    public static ArrayList<Bitmap> images= new ArrayList<>();
    public static ArrayList<Uri> imagesURIs=new ArrayList<>();
}
