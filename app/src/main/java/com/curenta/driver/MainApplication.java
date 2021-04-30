package com.curenta.driver;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.RideInfoDto;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.interfaces.IRideNotification;
import com.curenta.driver.utilities.Preferences;


import com.google.gson.Gson;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by faheem on 16,February,2021
 */
public class MainApplication extends Application {
    private String ONESIGNAL_APP_ID = "0782162b-2e6e-4ebb-9825-af0af68eadeb";
    private static Context sContext;
    static IRideNotification iRideNotification;
    private Socket locationSocket;
    private Socket notificationSocket;
    {
        try {
            IO.Options options = new IO.Options();
            locationSocket = IO.socket(BuildConfig.locationSocketIOPath,options);
            locationSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                 Log.d("sockets","location socket connected");
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("sockets","location socket disconnected");
                }
            }).on("error", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("sockets","location socket error");
                }
            });

            locationSocket.connect();

        } catch (URISyntaxException e) {
            //Log.d("sockets","location socket failure "+e.getMessage());
            throw new RuntimeException(e);
        }
    }
    {
        try {
            notificationSocket = IO.socket(BuildConfig.notificationSocketIOPath);

            // Log.d("sockets","notification socket connected");
        } catch (URISyntaxException e) {
            // Log.d("sockets","notification socket failure "+e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        Log.d("onesignalnotifications", BuildConfig.BUILD_TYPE);
        Log.d("onesignalnotifications", BuildConfig.onesignalappkey);
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        boolean isOnline = Preferences.getInstance().getBoolean("isOnline", false);
        if (isOnline) {
            enableNotifications();
            Log.d("onesignalnotifications", "notifications enabled");
        } else {
            disableNotifications();
            Log.d("onesignalnotifications", "notifications disabled");
        }
       // locationSocket.connect();
        notificationSocket.connect();

        // OneSignal Initialization
     setupOnseSignal();

        OneSignal.setNotificationOpenedHandler(result -> {


            JSONObject data = result.getNotification().getAdditionalData();
            String notoficationid = null;

            try {
                notoficationid = data.getString("NotificationId");
                AppElement.routeId = data.getString("RouteId");
                Log.d("onesignalnotifications", "notoficationid " + notoficationid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("onesignalnotifications", "opened result: " + data);
            boolean status = Preferences.getInstance().getBoolean("isOnline", false);
            Log.d("onesignalnotifications", "isOnline " + status);
            if (data != null && status ) {
                Gson gson = new Gson();
                RideInfoDto rideInfoDto = gson.fromJson(data.toString(), RideInfoDto.class);
                if (iRideNotification != null ) {
                    if(rideInfoDto.routeId != null) {
                        Log.d("onesignalnotifications", "open using interface ");
                        iRideNotification.rideNotification(rideInfoDto);
                    }
                    else{
                        if(notoficationid!=null) {
                            iRideNotification.cancelNotification(Integer.parseInt(notoficationid));
                        }
                    }
                } else {
                    Log.d("onesignalnotifications", "open using activity ");
                    Intent next = new Intent(getContext(), DashboardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("rideInfo", data.toString());
                    next.putExtras(bundle);
                    next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(next);
                }
            }
        });
        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {

            Log.d("onesignalnotifications", "NotificationWillShowInForegroundHandler fired!" +
                    " with notification event: " + notificationReceivedEvent.toString());
            OSNotification notification = notificationReceivedEvent.getNotification();
            JSONObject data = notification.getAdditionalData();
            Log.d("onesignalnotifications", "data " + data);
            notificationReceivedEvent.complete(null);
            boolean status = Preferences.getInstance().getBoolean("isOnline", false);
            Log.d("onesignalnotifications", "isOnline " + status);
            String notoficationid = null;

            try {
                notoficationid = data.getString("NotificationId");
                Log.d("onesignalnotifications", "notoficationid " + notoficationid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (data != null && status) {
                Gson gson = new Gson();
                RideInfoDto rideInfoDto = gson.fromJson(data.toString(), RideInfoDto.class);
                if (iRideNotification != null ) {
                    if(rideInfoDto.routeId != null) {
                        Log.d("onesignalnotifications", "open using interface ");
                        iRideNotification.rideNotification(rideInfoDto);
                    }
                    else{
                        if(notoficationid!=null) {
                            iRideNotification.cancelNotification(Integer.parseInt(notoficationid));
                        }
                    }
                } else {
                    Log.d("onesignalnotifications", "open using activity ");
                    Intent next = new Intent(getContext(), DashboardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("rideInfo", data.toString());
                    next.putExtras(bundle);
                    next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(next);
                }
            }
        });

        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
        OneSignal.pauseInAppMessages(true);
        OneSignal.setLocationShared(false);
        AppElement.cw=new ContextWrapper(this);

    }
    public Socket getLocationSocket(){
        return locationSocket;
    }
    public Socket getNotificationSocket(){
        return notificationSocket;
    }
    public static void setupOnseSignal() {

        OneSignal.initWithContext(getContext());
        OneSignal.setAppId(BuildConfig.onesignalappkey);
        String userId = OneSignal.getDeviceState().getUserId();
        Log.d("onesignalnotifications", "userId " + userId);
        UserInfo.getInstance().fcmToken = userId;
    }

    public static Context getContext() {
        return sContext;
    }

    public static void setRideNotifier(IRideNotification iRideNotification) {
        MainApplication.iRideNotification = iRideNotification;
    }

    public static void disableNotifications() {
        OneSignal.disablePush(true);
    }

    public static void enableNotifications() {
        OneSignal.disablePush(false);
    }
}