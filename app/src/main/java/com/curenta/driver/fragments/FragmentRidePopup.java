package com.curenta.driver.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentRidePopupBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.dto.RideInfoDto;
import com.curenta.driver.interfaces.ILocationChange;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.AcceptRideResponse;
import com.curenta.driver.retrofit.apiDTO.GetRoutesResponse;
import com.curenta.driver.retrofit.apiDTO.RideAcceptRequest;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FragmentRidePopup extends Fragment implements ILocationChange, OnMapReadyCallback {
    FragmentRidePopupBinding fragmentRidePopupBinding;
    private GoogleMap mMap;
    GPSTracker gpsTracker;
    int counter = 60;
    private Handler handler = new Handler(Looper.getMainLooper());
    boolean isRideAccepted = false;
    public RideInfoDto rideInfoDto;
    boolean isPopupactive = true;
    ProgressDialog dialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRidePopupBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_ride_popup, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        isPopupactive = true;
        fragmentRidePopupBinding.imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPopupactive = false;

                try {
                    if (getActivity().getSupportFragmentManager() != null) {
                        try {
                            if (getActivity().getSupportFragmentManager() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        } catch (IllegalStateException ex) {

                        } catch (Exception ex) {

                        }
                    }
                } catch (IllegalStateException ex) {

                }

                Log.d("totalFragments", "" + getActivity().getSupportFragmentManager().getBackStackEntryCount());
            }
        });
        fragmentRidePopupBinding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRideAccepted = true;
                if (rideInfoDto != null && rideInfoDto.routeId != null) {
                    RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);

                    // rideInfoDto.routeId = "98b0e766-1eae-4d20-a64d-e8b8861fee19";
                    acceptRide();
                    //  FragmentUtils.getInstance().addFragment(getActivity(), new FragmentRideDetail(), R.id.fragContainer);

                }
            }
        });

        if (rideInfoDto != null && rideInfoDto.routeId != null) {
            try {
                fragmentRidePopupBinding.txtEndAddress.setText(rideInfoDto.endPoint);
                fragmentRidePopupBinding.txtMiles.setText(rideInfoDto.distanceInMiles);
                fragmentRidePopupBinding.txtMinutes.setText(rideInfoDto.totalMins);
                fragmentRidePopupBinding.txtPayment.setText("$" + rideInfoDto.price);
                fragmentRidePopupBinding.txtStartAddress.setText(rideInfoDto.startingPoint);
                fragmentRidePopupBinding.txtStops.setText("" + (Integer.parseInt(rideInfoDto.stops) - 1) + " stops");
                fragmentRidePopupBinding.txtTotalStops.setText("Total Stops: " + rideInfoDto.stops + " stops");
            } catch (Exception e) {
                Toast.makeText(getContext(), "Accepting route failed.Please try again", Toast.LENGTH_SHORT).show();
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        fragmentRidePopupBinding.circularProgressbar.setProgress(0);   // Main Progress
        fragmentRidePopupBinding.circularProgressbar.setSecondaryProgress(100); // Secondary Progress
        fragmentRidePopupBinding.circularProgressbar.setMax(100); // Maximum Progress
        fragmentRidePopupBinding.circularProgressbar.setProgressDrawable(drawable);

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            public void run() {
                if(!isRideAccepted) {
                    int progress = (counter / 6) * 10;
                    fragmentRidePopupBinding.circularProgressbar.setProgress(progress);
                    fragmentRidePopupBinding.txtcounter.setText("" + counter);
                    if ((progress < 0 || counter <= 0) && isPopupactive) {
                        try {
                            if (getActivity().getSupportFragmentManager() != null) {
                                try {
                                    if (getActivity().getSupportFragmentManager() != null) {
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                } catch (IllegalStateException ex) {

                                } catch (Exception ex) {

                                }
                            }
                        } catch (IllegalStateException ex) {

                        } catch (Exception ex) {

                        }

                    }
                    counter--;
                }
                handler.postDelayed(this, delay);
            }
        }, 0);

        return fragmentRidePopupBinding.getRoot();
    }

    public void getLocation() {
        gpsTracker = new GPSTracker(getContext(), this);


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    @SuppressLint("MissingPermission")
    public void updateLocation(Location location) {
        if (location != null && mMap != null) {
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("actictystate", "resume");
        getLocation();

    }

    public void acceptRide() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                RideAcceptRequest requestDTO = new RideAcceptRequest(LoggedInUser.getInstance().driverId, rideInfoDto.routeId);
                Gson gson = new Gson();
                String request = gson.toJson(requestDTO);

                RetrofitClient.getAPIClient().acceptRide(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AcceptRideResponse>() {
                            @Override
                            public void onSuccess(AcceptRideResponse response) {

                                if (response.responseCode == 1) {
                                    Log.d("acceptRideCall", "success " + response.toString());
                                    // Toast.makeText(getActivity().getApplicationContext(), " Ride Accepted", Toast.LENGTH_SHORT).show();
                                    getRouteDetails();
                                } else {
                                    //getRouteDetails();
                                    dialog.dismiss();
                                    if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                                        try {
                                            if (getActivity().getSupportFragmentManager() != null) {
                                                try {
                                                    if (getActivity().getSupportFragmentManager() != null) {
                                                        getActivity().getSupportFragmentManager().popBackStack();
                                                    }
                                                } catch (IllegalStateException ex) {

                                                } catch (Exception ex) {

                                                }
                                            }
                                        } catch (IllegalStateException ex) {

                                        }
                                        Log.d("getRouteCall", "fail " + response.toString());
                                        Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                try {
                                    if (getActivity().getSupportFragmentManager() != null) {
                                        try {
                                            if (getActivity().getSupportFragmentManager() != null) {
                                                getActivity().getSupportFragmentManager().popBackStack();
                                            }
                                        } catch (IllegalStateException ex) {

                                        } catch (Exception ex) {

                                        }
                                    }
                                } catch (IllegalStateException ex) {

                                }
                                Log.d("acceptRideCall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("acceptRideCall", "failed " + e.toString());
        }
    }

    public void getRouteDetails() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                RetrofitClient.getAPIClient().getRoutes(rideInfoDto.routeId, true, true, true)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetRoutesResponse>() {
                            @Override
                            public void onSuccess(GetRoutesResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1 && response.data != null) {
                                    AppElement.routeId = rideInfoDto.routeId;
                                    Gson gson = new Gson();
                                    String rideInfoDtoGson = gson.toJson(rideInfoDto);
                                    Preferences.getInstance().setString("rideInfoDto", rideInfoDtoGson);
                                    Log.d("getRouteCall", "success " + response.toString());
                                    // Toast.makeText(getActivity().getApplicationContext(), "getRouteAPI Success", Toast.LENGTH_SHORT).show();
                                    FragmentRideDetail fragmentRideDetail = new FragmentRideDetail();
                                    fragmentRideDetail.getRouteResponse = response;
                                    fragmentRideDetail.routeId = rideInfoDto.routeId;
                                    try {
                                        if (getActivity().getSupportFragmentManager() != null) {

                                            if (getActivity().getSupportFragmentManager() != null) {
                                                getActivity().getSupportFragmentManager().popBackStack();
                                            }


                                        }
                                    } catch (IllegalStateException ex) {

                                    } catch (Exception ex) {

                                    }
                                    isPopupactive = false;
                                    FragmentUtils.getInstance().addFragment(getActivity(), fragmentRideDetail, R.id.fragContainer);
                                } else {
                                    dialog.dismiss();
                                    try {
                                        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                                            if (getActivity().getSupportFragmentManager() != null) {

                                                if (getActivity().getSupportFragmentManager() != null) {
                                                    getActivity().getSupportFragmentManager().popBackStack();
                                                }

                                            }
                                            Log.d("getRouteCall", "fail " + response.toString());
                                            Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IllegalStateException ex) {

                                    } catch (Exception ex) {

                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                try {
                                    if (getActivity().getSupportFragmentManager() != null) {

                                        if (getActivity().getSupportFragmentManager() != null) {
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        }

                                    }
                                } catch (IllegalStateException ex) {

                                } catch (Exception ex) {

                                }
                                Log.d("getRouteCall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("getRouteCall", "failed " + e.toString());
        }
    }
}