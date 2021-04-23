package com.curenta.driver.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.MainApplication;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentMapsBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.dto.RideInfoDto;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.interfaces.ILocationChange;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.GetRouteResponse;
import com.curenta.driver.retrofit.apiDTO.RouteRequest;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.GPSTracker;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Preferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.net.URISyntaxException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.client.Socket;


public class FragmentMaps extends Fragment implements ILocationChange, OnMapReadyCallback {
    FragmentMapsBinding fragmentMapsBinding;
    private GoogleMap mMap;
    static GPSTracker gpsTracker;
    static String TAG = "SocketIO";
    private static Socket mSocket;
    RideInfoDto rideInfoDto;
    GetRouteResponse response;

    {
        try {
            mSocket = IO.socket(BuildConfig.socketIOPath);
            Log.d(TAG, "connecting socket io");
        } catch (URISyntaxException e) {
            Log.d(TAG, "connecting failed " + e.getMessage());
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMapsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_maps, container, false);
        ((DashboardActivity) getActivity()).isBackAllowed = true;
        fragmentMapsBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoggedInUser.getInstance().isSelfie) {
                    Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    fragmentMapsBinding.lltakephoto.startAnimation(animShake);
                }
                if (!LoggedInUser.getInstance().isCovidPassed) {
                    Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    fragmentMapsBinding.llcovid.startAnimation(animShake);
                }
                if (AppElement.isCameOnline) {
                    LoggedInUser.getInstance().isOnline = true;
                    Preferences.getInstance().saveBoolean("isOnline", true);
                    checkOnline();
                    AppElement.isCameOnline = false;
                }
            }
        });
        fragmentMapsBinding.fabNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((DashboardActivity) getActivity()).navigationClicked();
            }
        });
        fragmentMapsBinding.llonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentUtils.getInstance().addFragment(getActivity(), new FragmentStatus(), R.id.fragContainer);
            }
        });
        fragmentMapsBinding.llcovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.getInstance().addFragment(getActivity(), new FragmentCovid19(), R.id.fragContainer);


            }
        });
        fragmentMapsBinding.lltakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTakePhoto fragmentTakePhoto = new FragmentTakePhoto();
                fragmentTakePhoto.enumPictureType = EnumPictureType.DRIVER_SELFIE;
                FragmentUtils.getInstance().addFragment(getActivity(), fragmentTakePhoto, R.id.fragContainer);

            }
        });
        fragmentMapsBinding.llRideinprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (response != null && response.data!=null) {
                    FragmentRideDetail fragmentRideDetail = new FragmentRideDetail();
                    fragmentRideDetail.getRouteResponse = response;
                    fragmentRideDetail.routeId = response.data.routeId;
                    FragmentUtils.getInstance().addFragment(getActivity(), fragmentRideDetail, R.id.fragContainer);
                }

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        boolean isOnline = Preferences.getInstance().getBoolean("isOnline", false);

        LoggedInUser.getInstance().isOnline = isOnline;
        checkOnline();

        String rideInfoString = Preferences.getInstance().getString("rideInfoDto");
        if (!rideInfoString.equalsIgnoreCase("") && isOnline) {
            Log.d("routestatus", "inprogress");
            Gson gson = new Gson();
            rideInfoDto = gson.fromJson(rideInfoString, RideInfoDto.class);
            AppElement.routeId = rideInfoDto.routeId;
            getRouteDetails(rideInfoDto);
        }
        //launchDismissDlg();
        if (AppElement.isCameOnline) {
            launchDismissDlg();
        }
        return fragmentMapsBinding.getRoot();
    }

    @SuppressLint("ResourceType")
    public void checkOnline() {

        if (LoggedInUser.getInstance().isOnline) {
            MainApplication.enableNotifications();
            fragmentMapsBinding.llonline.setVisibility(View.VISIBLE);
            fragmentMapsBinding.llactionrequired.setVisibility(View.GONE);
            fragmentMapsBinding.llcovid.setVisibility(View.GONE);
            fragmentMapsBinding.lltakephoto.setVisibility(View.GONE);
            fragmentMapsBinding.llStatus.setBackgroundResource(R.drawable.rounded_green_button);
            fragmentMapsBinding.circularProgress.setVisibility(View.VISIBLE);
            fragmentMapsBinding.fab.setBackgroundResource(Color.TRANSPARENT);
        } else {
            MainApplication.disableNotifications();
            fragmentMapsBinding.llonline.setVisibility(View.GONE);
            fragmentMapsBinding.llactionrequired.setVisibility(View.VISIBLE);
            fragmentMapsBinding.llcovid.setVisibility(View.VISIBLE);
            fragmentMapsBinding.lltakephoto.setVisibility(View.VISIBLE);
            fragmentMapsBinding.llStatus.setBackgroundResource(R.drawable.round_grey_button);
            fragmentMapsBinding.circularProgress.setVisibility(View.INVISIBLE);
            fragmentMapsBinding.fab.setBackgroundResource(R.drawable.whitecircle);
        }
    }

    public void getLocation() {
        gpsTracker = new GPSTracker(getContext(), this);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
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
            publishLocation();
        }
    }

    public static void publishLocation() {
        String rideInfoString = Preferences.getInstance().getString("rideInfoDto");
        if (!rideInfoString.equalsIgnoreCase("") && gpsTracker != null) {
            mSocket.emit("updateDriverLocation", LoggedInUser.getInstance().driverId + "," + gpsTracker.getLatitude() + "," + gpsTracker.getLongitude());
            Log.d(TAG, "updateDriverLocation " + LoggedInUser.getInstance().driverId + "," + gpsTracker.getLatitude() + "," + gpsTracker.getLongitude() + "," + AppElement.routeId + "," + AppElement.orderId);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("actictystate", "resume");
        getLocation();
        mSocket.connect();

    }

    public void getRouteDetails(RideInfoDto rideInfoDto) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                RouteRequest requestDTO = new RouteRequest(rideInfoDto.routeId);
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
                                    if (!responseData.data.routeStatus.equalsIgnoreCase("Completed")) {

                                        fragmentMapsBinding.llRideinprogress.setVisibility(View.VISIBLE);
                                        response = responseData;
                                    } else {

                                        Preferences.getInstance().setString("rideInfoDto", "");
                                        fragmentMapsBinding.llRideinprogress.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1) {
                                        if (getActivity().getSupportFragmentManager() != null) {
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        }
                                        Log.d("getRouteCall", "fail " + responseData.toString());
                                        Toast.makeText(getActivity().getApplicationContext(), responseData.responseMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                                if (getActivity().getSupportFragmentManager() != null) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                                Log.d("getRouteCall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Please try again later", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("getRouteCall", "failed " + e.toString());
        }
    }

    private void launchDismissDlg() {

        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_ready);
        dialog.setCanceledOnTouchOutside(true);
        final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.getting_available_sound);
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
}