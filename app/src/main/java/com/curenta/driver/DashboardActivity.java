package com.curenta.driver;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.curenta.driver.databinding.ActivityDashboardBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.dto.RideInfoDto;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.fragments.FragmentContactUs;
import com.curenta.driver.fragments.FragmentCovid19;
import com.curenta.driver.fragments.FragmentEarningSimpleLIst;
import com.curenta.driver.fragments.FragmentMyAccount;
import com.curenta.driver.fragments.FragmentNavigation;
import com.curenta.driver.fragments.FragmentRideDetail;
import com.curenta.driver.fragments.FragmentRidePopup;
import com.curenta.driver.fragments.FragmentStatus;
import com.curenta.driver.fragments.FragmentTakePhoto;
import com.curenta.driver.interfaces.ILatLngUpdate;
import com.curenta.driver.interfaces.ILocationChange;
import com.curenta.driver.interfaces.IRideNotification;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.GetRouteResponse;
import com.curenta.driver.retrofit.apiDTO.RouteInprogressResponse;
import com.curenta.driver.retrofit.apiDTO.RouteRequest;
import com.curenta.driver.retrofit.apiDTO.UpdateDRiverLocationRequest;
import com.curenta.driver.retrofit.apiDTO.UpdateDriverStatusRequest;
import com.curenta.driver.retrofit.apiDTO.UpdateDriverStatusResponse;
import com.curenta.driver.retrofit.apiDTO.UpdateLocationResponse;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.GPSTracker;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Preferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.curenta.driver.MainApplication.getContext;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IRideNotification, ILocationChange, OnMapReadyCallback {


    ActivityDashboardBinding activityDashboardBinding;
    public static boolean isBackAllowed = true;
    ImageView profilePic;
    private GoogleMap mMap;
    static GPSTracker gpsTracker;
    static String TAG = "SocketIO";
    private Socket locationSocket;
    private Socket notificationSocket;
    RideInfoDto rideInfoDto;
    GetRouteResponse response;
    ScheduledExecutorService mscheduler;

    public ILatLngUpdate iLocationChange;
    int version;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        activityDashboardBinding.navView.setNavigationItemSelectedListener(this);
        View headerView = activityDashboardBinding.navView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.txtUsername);
        profilePic = (ImageView) headerView.findViewById(R.id.imgUserPhoto);
        MainApplication app = (MainApplication) getApplication();
        locationSocket = app.getLocationSocket();
        ;
        notificationSocket = app.getNotificationSocket();

        if (locationSocket.connected()) {
            Log.d("sockets", "location socket connected");
        }
        if (notificationSocket.connected()) {
            Log.d("sockets", "notification socket connected");
        }
        notificationSocket.on("DriverNotifications", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                Log.d("sockets", "data recieved " + data.toString());

            }
        });
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String appversion = pInfo.versionName;
            appversion = appversion.replace(".", "");
            version = Integer.parseInt(appversion);
            Log.d("version", "version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mscheduler =
                Executors.newSingleThreadScheduledExecutor();

        mscheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        publishLocation();
                    }
                }, 5, 10, TimeUnit.SECONDS);
        String logedInUser = Preferences.getInstance().getString("loggedInUser");
        Gson gson = new Gson();
        LoggedInUser loggedInUser = LoggedInUser.getInstance();
        LoggedInUser userDTO = gson.fromJson(logedInUser, LoggedInUser.class);
        if(userDTO!=null) {
            loggedInUser.setInstance(userDTO);
        }
        navUsername.setText(loggedInUser.fname + " " + loggedInUser.lname);

        loadProfilePic();
        // FragmentUtils.getInstance().addFragment(DashboardActivity.this, new FragmentMaps(), R.id.fragContainer);
        MainApplication.setRideNotifier(this);
        Intent intent = getIntent();
        if (intent.hasExtra("rideInfo")) {
            String rideInfo = intent.getExtras().getString("rideInfo");
            RideInfoDto rideInfoDto = gson.fromJson(rideInfo, RideInfoDto.class);
            if (rideInfoDto.routeId != null) {

                FirebaseCrashlytics.getInstance().log(rideInfo);
                FragmentRidePopup fragmentRidePopup = new FragmentRidePopup();
                fragmentRidePopup.rideInfoDto = rideInfoDto;
                FragmentUtils.getInstance().addFragment(DashboardActivity.this, fragmentRidePopup, R.id.fragContainer);
            }
        }

        activityDashboardBinding.appBarMain.contentMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoggedInUser.getInstance().isSelfie) {
                    Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    activityDashboardBinding.appBarMain.contentMain.lltakephoto.startAnimation(animShake);
                }
                if (!LoggedInUser.getInstance().isCovidPassed) {
                    Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    activityDashboardBinding.appBarMain.contentMain.llcovid.startAnimation(animShake);
                }
//                if (AppElement.isCameOnline) {
//                    LoggedInUser.getInstance().isOnline = true;
//                    Preferences.getInstance().saveBoolean("isOnline", true);
//                    checkOnline();
//                    AppElement.isCameOnline = false;
//                    checkRide();
//                    MainApplication.enableNotifications();
//                }
            }
        });
        activityDashboardBinding.appBarMain.contentMain.fabNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigationClicked();
            }
        });
        activityDashboardBinding.appBarMain.contentMain.llonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentUtils.getInstance().addFragment(DashboardActivity.this, new FragmentStatus(), R.id.fragContainer);
            }
        });
        activityDashboardBinding.appBarMain.contentMain.llcovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.getInstance().addFragment(DashboardActivity.this, new FragmentCovid19(), R.id.fragContainer);


            }
        });
        activityDashboardBinding.appBarMain.contentMain.lltakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTakePhoto fragmentTakePhoto = new FragmentTakePhoto();
                fragmentTakePhoto.enumPictureType = EnumPictureType.DRIVER_SELFIE;
                FragmentUtils.getInstance().addFragment(DashboardActivity.this, fragmentTakePhoto, R.id.fragContainer);

            }
        });
        activityDashboardBinding.appBarMain.contentMain.llRideinprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (response != null && response.data != null) {
                    FragmentRideDetail fragmentRideDetail = new FragmentRideDetail();
                    fragmentRideDetail.getRouteResponse = response;
                    fragmentRideDetail.routeId = response.data.routeId;
                    FragmentUtils.getInstance().addFragment(DashboardActivity.this, fragmentRideDetail, R.id.fragContainer);
                }

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        boolean isOnline = Preferences.getInstance().getBoolean("isOnline", false);

        LoggedInUser.getInstance().isOnline = isOnline;
        checkOnline();

        checkRide();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String version = pInfo.versionName;
            activityDashboardBinding.txtAppversion.setText("Version: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        updateDriverStatus(false);

//        Intent locationService = new Intent(getApplicationContext(), GoogleService.class);
//        startService(locationService);
        RouteInprogressAPICall();
        //launchDismissDlg();
//        if (AppElement.isCameOnline) {
//            launchDismissDlg();
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    public void checkRide() {
        boolean isOnline = Preferences.getInstance().getBoolean("isOnline", false);
        String rideInfoString = Preferences.getInstance().getString("rideInfoDto");
        if (!rideInfoString.equalsIgnoreCase("") && isOnline) {
            Log.d("routestatus", "inprogress");
            Gson gson = new Gson();
            rideInfoDto = gson.fromJson(rideInfoString, RideInfoDto.class);
            AppElement.routeId = rideInfoDto.routeId;
          //  getRouteDetails(rideInfoDto.routeId, false, false, false);
            activityDashboardBinding.appBarMain.contentMain.llonline.setEnabled(false);
        } else {
//            if (LoggedInUser.getInstance().isOnline) {
//                RouteInprogressAPICall();
//            }
            activityDashboardBinding.appBarMain.contentMain.llonline.setEnabled(true);
            activityDashboardBinding.appBarMain.contentMain.llRideinprogress.setVisibility(View.GONE);
        }
    }

    public void navigationClicked() {
        if (activityDashboardBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            activityDashboardBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            activityDashboardBinding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        int itemId = menuItem.getItemId(); // get selected menu item's id
// check selected menu item's id and replace a Fragment Accordingly
        if (itemId == R.id.covid19) {
            FragmentUtils.getInstance().addFragment(DashboardActivity.this, new FragmentCovid19(), R.id.fragContainer);
           // FragmentUtils.getInstance().addFragment(DashboardActivity.this, new FragmentNavigation(), R.id.fragContainer);

        } else if (itemId == R.id.contactus) {
            FragmentUtils.getInstance().addFragment(DashboardActivity.this, new FragmentContactUs(), R.id.fragContainer);

        } else if (itemId == R.id.logout) {
            LoggedInUser.instance = null;
            UserInfo.instance = null;
            Preferences.getInstance().saveBoolean("isOnline", false);
            Preferences.getInstance().setString("loggedInUser", "");
            Intent next = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", "Login");
            next.putExtras(bundle);
            startActivity(next);
            finish();

        } else if (itemId == R.id.earning) {
            FragmentUtils.getInstance().addFragment(DashboardActivity.this, new FragmentEarningSimpleLIst(), R.id.fragContainer);

        } else if (itemId == R.id.my_account) {
            FragmentUtils.getInstance().addFragment(DashboardActivity.this, new FragmentMyAccount(), R.id.fragContainer);

        }

        activityDashboardBinding.drawerLayout.closeDrawers(); // close the all open Drawer Views
        return true;


    }

    @Override
    public void onBackPressed() {

        Log.d("totalFragments", "" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            UserInfo.instance = null;
            finish();
        } else {
            checkRide();
            getSupportFragmentManager().popBackStack();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            //System.out.println("@#@");
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void rideNotification(RideInfoDto rideInfoDto) {

        getRouteDetails(rideInfoDto.routeId, true, false, false);

    }

    @Override
    public void cancelNotification(int id) {
        Log.d("cancel", "id " + id);
        if (id == 2 || id == 3) {

            Preferences.getInstance().setString("rideInfoDto", "");
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        launchCancelDismissDlg();
                        checkRide();
                    }
                });

            } else {
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    getSupportFragmentManager().popBackStack();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkRide();
                        launchCancelDismissDlg();
                    }
                });
            }


        } else {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            if (id == 4) {
                RouteInprogressAPICall();
            } else if (id == 5) {
                getRouteDetails(AppElement.routeId, true, false, false);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DashboardActivity.this, "Route update received from Curenta", Toast.LENGTH_SHORT).show();

                    }
                });
                getRouteDetails(AppElement.routeId, false, true, false);
            }

        }
    }


    public void loadProfilePic() {
        if (LoggedInUser.getInstance().profileImagePath != null) {
            Glide.with(this).load(LoggedInUser.getInstance().profileImagePath).circleCrop().into(profilePic);
        }
    }

    @SuppressLint("ResourceType")
    public void checkOnline() {

        if (LoggedInUser.getInstance().isOnline) {
            MainApplication.enableNotifications();
            activityDashboardBinding.appBarMain.contentMain.llonline.setVisibility(View.VISIBLE);
            activityDashboardBinding.appBarMain.contentMain.llactionrequired.setVisibility(View.GONE);
            activityDashboardBinding.appBarMain.contentMain.llcovid.setVisibility(View.GONE);
            activityDashboardBinding.appBarMain.contentMain.lltakephoto.setVisibility(View.GONE);
            activityDashboardBinding.appBarMain.contentMain.llStatus.setBackgroundResource(R.drawable.rounded_green_button);
            activityDashboardBinding.appBarMain.contentMain.circularProgress.setVisibility(View.VISIBLE);
            activityDashboardBinding.appBarMain.contentMain.fab.setBackgroundResource(Color.TRANSPARENT);
        } else {
            MainApplication.disableNotifications();
            activityDashboardBinding.appBarMain.contentMain.llonline.setVisibility(View.GONE);
            activityDashboardBinding.appBarMain.contentMain.llactionrequired.setVisibility(View.VISIBLE);
            activityDashboardBinding.appBarMain.contentMain.llcovid.setVisibility(View.VISIBLE);
            activityDashboardBinding.appBarMain.contentMain.lltakephoto.setVisibility(View.VISIBLE);
            activityDashboardBinding.appBarMain.contentMain.llStatus.setBackgroundResource(R.drawable.round_grey_button);
            activityDashboardBinding.appBarMain.contentMain.circularProgress.setVisibility(View.INVISIBLE);
            activityDashboardBinding.appBarMain.contentMain.fab.setBackgroundResource(R.drawable.whitecircle);
        }
    }

    public void getLocation() {
        gpsTracker = new GPSTracker(DashboardActivity.this, this);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);
    }

    @SuppressLint("MissingPermission")
    public void updateLocation(Location location) {
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));

        }
    }

    @Override
    public void locationChanged(Location location) {
        Log.d("location main", "changed");
        if (location != null) {
            updateLocation(location);
            if (AppElement.Latitude != location.getLatitude() && AppElement.Longitude != location.getLongitude()) {
                AppElement.Latitude = gpsTracker.getLatitude();
                AppElement.Longitude = gpsTracker.getLongitude();
                if (iLocationChange != null) {
                    iLocationChange.locationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
            //publishLocation();
        }
    }

    public void publishMessage(String message) {
        try {
            JSONObject poisonObject = new JSONObject();
            try {

                poisonObject.put("updateDriverLocation", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            locationSocket.emit("updateDriverLocation", poisonObject);
            Log.d(TAG, "updateDriverLocation socket success");
        } catch (Exception e) {
            Log.d(TAG, "updateDriverLocation socket failure3 " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void publishLocation() {
        gpsTracker.getLocation();
        String rideInfoString = Preferences.getInstance().getString("rideInfoDto");

        if ((!rideInfoString.equalsIgnoreCase("") || AppElement.routeId != null) && gpsTracker != null && AppElement.Latitude != gpsTracker.getLatitude() && AppElement.Longitude != gpsTracker.getLongitude()) {

            AppElement.Latitude = gpsTracker.getLatitude();
            AppElement.Longitude = gpsTracker.getLongitude();

            String message = LoggedInUser.getInstance().driverId + "," + gpsTracker.getLongitude() + "," + gpsTracker.getLatitude();
            publishMessage(message);
            publishAPICall();
            Log.d(TAG, "updateDriverLocation " + LoggedInUser.getInstance().driverId + "," + gpsTracker.getLongitude() + "," + gpsTracker.getLatitude() + "," + AppElement.routeId + "," + AppElement.orderId);

        }
    }

    public void publishLocation(double latitude, double longitude) {

        String rideInfoString = Preferences.getInstance().getString("rideInfoDto");

        if ((!rideInfoString.equalsIgnoreCase("") || AppElement.routeId != null) && gpsTracker != null && AppElement.Latitude != latitude && AppElement.Longitude != longitude) {

            AppElement.Latitude = latitude;
            AppElement.Longitude = latitude;
            String message = LoggedInUser.getInstance().driverId + "," + longitude + "," + latitude + "," + AppElement.routeId + "," + AppElement.orderId;
            publishMessage(message);
            publishAPICall();
            Log.d(TAG, "updateDriverLocation " + LoggedInUser.getInstance().driverId + "," + longitude + "," + latitude + "," + AppElement.routeId + "," + AppElement.orderId);

        }
    }

    public static void publishAPICall() {
        RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
        UpdateDRiverLocationRequest requestDto = new UpdateDRiverLocationRequest(LoggedInUser.getInstance().driverId, gpsTracker.getLongitude(), gpsTracker.getLatitude());
        Gson gson = new Gson();
        String request = gson.toJson(requestDto);
        Log.d(TAG, "updateDriverLocation " + request);

        RetrofitClient.getAPIClient().updateDriverLOcation(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UpdateLocationResponse>() {
                    @Override
                    public void onSuccess(UpdateLocationResponse response) {
                        //dialog.dismiss();
                        if (response.responseCode == 1) {

                            Log.d("updateDriverLocation", "success " + response.responseCode);


                        } else {
                            Log.d("updateDriverLocation", "fail " + response);
                            //Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //  dialog.dismiss();
                        Log.d("updateDriverLocation", "failed " + e.toString());
                        //  Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void RouteInprogressAPICall() {
        RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);


        RetrofitClient.getAPIClient().checkInprogressRoute(LoggedInUser.getInstance().driverId, LoggedInUser.getInstance().fcmToken, LoggedInUser.getInstance().fcmToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<RouteInprogressResponse>() {
                    @Override
                    public void onSuccess(RouteInprogressResponse responseData) {
                        Log.d("Routeinprogress", "success ");
                        if (responseData.data != null) {
                            for (RouteInprogressResponse.Update update : responseData.data.updates) {
                                if(update.platformType.equalsIgnoreCase("ANDRIOD")){
                                   String appCloudVersion=update.appVersion.replace(".","");
                                    int appVersion = Integer.parseInt(appCloudVersion);
                                    if (appVersion > version) {
                                        boolean forceUpdate = false;
                                        if (update.updateType.equalsIgnoreCase("FORCE")) {
                                            forceUpdate = true;
                                        }
                                        if (!update.updateType.equalsIgnoreCase("NORMAL")) {
                                            launchUpdateDismissDlg(forceUpdate);
                                        }
                                    }
                                }
                            }

                        }
                        if (responseData.responseCode == 1) {

                            if (responseData.data != null) {
                                if (responseData.data.routeData != null) {
                                    AppElement.routeId = responseData.data.routeData.routeId;
                                    getRouteDetails(responseData.data.routeData.routeId, false, false, true);
                                }

                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //  dialog.dismiss();
                        Log.d("Routeinprogress", "failed " + e.toString());
                        //  Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("actictystate", "resume");
        // registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
        getLocation();
        // Toast.makeText(this, "on resume", Toast.LENGTH_SHORT).show();
//        if (AppElement.isCameOnline) {
//            launchDismissDlg();
//        }
        checkOnline();
    }

    public void getRouteDetails(String routeId, boolean isRide, boolean isRouteUpdated, boolean isDriverAssigned) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                RouteRequest requestDTO = new RouteRequest(routeId);
                Gson gson = new Gson();
                String request = gson.toJson(requestDTO);
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                RetrofitClient.getAPIClient().getRoute(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetRouteResponse>() {
                            @Override
                            public void onSuccess(GetRouteResponse responseData) {

                                if (responseData.responseCode == 1) {
                                    Log.d("getRouteCall", "success " + responseData.toString());
                                    if (!responseData.data.routeStatus.equalsIgnoreCase("Completed") && responseData.data.routeOrders.size() > 0) {
                                        response = responseData;
                                        if (rideInfoDto == null) {
                                            rideInfoDto = new RideInfoDto();
                                        }
                                        DecimalFormat df = new DecimalFormat("0.00");
//                                        double totalDistance = responseData.data.distance + responseData.data.returnTripDistance;
//                                        double totalDuration = responseData.data.duration + responseData.data.returnTripDuration;
                                        double totalDistance = responseData.data.distance;
                                        double totalDuration = responseData.data.duration;
                                        double distance = (totalDistance / 1000) * 0.621371;
                                        double duration = totalDuration / 60;
                                        distance = Double.parseDouble(df.format(distance));
                                        duration = Double.parseDouble(df.format(duration));
                                        rideInfoDto.routeId = responseData.data.routeId;
                                        rideInfoDto.distanceInMiles = "" + distance;
                                        if (responseData.data.routeOrders.size() > 0) {
                                            rideInfoDto.endPoint = responseData.data.routeOrders.get(responseData.data.routeOrders.size() - 1).deliveryAddress;
                                        }
                                        rideInfoDto.startingPoint = "" + responseData.data.pickupAddress.name;
                                        rideInfoDto.price = "" + responseData.data.price;
                                        rideInfoDto.stops = "" + responseData.data.routeOrders.size();
                                        rideInfoDto.totalMins = "" + duration;
                                        rideInfoDto.routeName = "" + responseData.data.routeName;
                                        if (isDriverAssigned) {
                                            Gson gson = new Gson();
                                            String rideInfoDtoGson = gson.toJson(rideInfoDto);
                                            Preferences.getInstance().setString("rideInfoDto", rideInfoDtoGson);
                                        }
                                        if (isRide) {

                                            FragmentRidePopup fragmentRidePopup = new FragmentRidePopup();
                                            fragmentRidePopup.rideInfoDto = rideInfoDto;
                                            FragmentUtils.getInstance().addFragment(DashboardActivity.this, fragmentRidePopup, R.id.fragContainer);
                                            Log.d("totalFragments", "" + getSupportFragmentManager().getBackStackEntryCount());

                                        } else if (isRouteUpdated) {
                                            FragmentRideDetail fragmentRideDetail = new FragmentRideDetail();
                                            fragmentRideDetail.getRouteResponse = response;
                                            fragmentRideDetail.routeId = response.data.routeId;
                                            FragmentUtils.getInstance().addFragment(DashboardActivity.this, fragmentRideDetail, R.id.fragContainer);
                                        } else {
                                            activityDashboardBinding.appBarMain.contentMain.llRideinprogress.setVisibility(View.VISIBLE);

                                        }
                                        checkRide();
                                    } else {

                                        Preferences.getInstance().setString("rideInfoDto", "");
                                        activityDashboardBinding.appBarMain.contentMain.llRideinprogress.setVisibility(View.GONE);
                                        checkRide();
                                    }
                                } else {
                                    if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                                        try {
                                            if (getSupportFragmentManager() != null) {
                                                getSupportFragmentManager().popBackStack();
                                            }
                                        } catch(IllegalStateException ex) {

                                        }
                                        Log.d("getRouteCall", "fail " + responseData.toString());
                                        Toast.makeText(getApplicationContext(), responseData.responseMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                try {
                                    if (getSupportFragmentManager() != null) {
                                        getSupportFragmentManager().popBackStack();
                                    }
                                } catch(IllegalStateException ex) {

                                }
                                Log.d("getRouteCall", "failed " + e.toString());
                                Toast.makeText(getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please try again later", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("getRouteCall", "failed " + e.toString());
        }
    }

    public void launchDismissDlg() {

        Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_ready);
        dialog.setCanceledOnTouchOutside(true);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.getting_available_sound);
        mp.start();
        LinearLayout btnReopenId = (LinearLayout) dialog.findViewById(R.id.llgotIt);


        btnReopenId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void launchCancelDismissDlg() {

        Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_ready);
        dialog.setCanceledOnTouchOutside(true);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.getting_available_sound);
        mp.start();
        LinearLayout btnReopenId = (LinearLayout) dialog.findViewById(R.id.llgotIt);
        TextView title = (TextView) dialog.findViewById(R.id.txtTitle);
        TextView description = (TextView) dialog.findViewById(R.id.txtDescription);
        title.setText("Route cancelled!");
        description.setText("Your assigned route has been cancelled by Curenta");
        btnReopenId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void updateDriverStatus(boolean force) {
        String rideInfoString = Preferences.getInstance().getString("rideInfoDto");
        if (rideInfoString.equalsIgnoreCase("") || force) {
            RetrofitClient.changeApiBaseUrl(BuildConfig.curentadispatcherURL);
            String status;
            if (!rideInfoString.equalsIgnoreCase("") && force) {
                status = "Busy";
                checkRide();
            }
            else if (LoggedInUser.getInstance().isOnline) {
                status = "Active";
            } else {
                status = "Inactive";
            }
            UpdateDriverStatusRequest requestDTO = new UpdateDriverStatusRequest(LoggedInUser.getInstance().driverId, status);
            Gson gson = new Gson();
            String request = gson.toJson(requestDTO);
            Log.d("UpdateDriverStatus", "request " + request);
            RetrofitClient.getAPIClient().updateDriverStatus(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<UpdateDriverStatusResponse>() {
                        @Override
                        public void onSuccess(UpdateDriverStatusResponse responseData) {

                            if (responseData.responseCode == 1) {
                                Log.d("UpdateDriverStatus", "success ");

                            } else {
                                Log.d("UpdateDriverStatus", "failure " + responseData.responseMessage);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            //  dialog.dismiss();
                            Log.d("UpdateDriverStatus", "failed " + e.toString());
                            //  Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            double latitude = Double.valueOf(intent.getStringExtra("latutide"));
//            double longitude = Double.valueOf(intent.getStringExtra("longitude"));
//            publishLocation(latitude,longitude);
//
//
//        }
//    };

    @Override
    protected void onPause() {
        super.onPause();
        // unregisterReceiver(broadcastReceiver);
    }

    @SuppressLint("ResourceAsColor")
    public void launchUpdateDismissDlg(boolean forceUpdate) {

        Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forec_update_popup);
        dialog.setCanceledOnTouchOutside(true);
        TextView txtUpdate = (TextView) dialog.findViewById(R.id.txtupdate);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        if (forceUpdate) {
            txtCancel.setEnabled(false);
            txtCancel.setTextColor(R.color.labelgrey);
        }
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}