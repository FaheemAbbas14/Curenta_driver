package com.curenta.driver.fragments;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.R;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentNavigationBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.interfaces.ILocationChange;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.GPSTracker;
import com.google.android.gms.maps.model.LatLng;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class FragmentNavigation extends Fragment implements ILocationChange, OnMapReadyCallback, PermissionsListener {

    public LatLng mOrigin;
    public RideDetailListAdapter.Order order;
    public ArrayList<RideDetailListAdapter.Section> sections;
    public int index;
    private MapboxMap mapboxMap;
    FragmentNavigationBinding fragmentNavigationBinding;
    String TAG = "Navigation";
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    public LatLng mDestination;
    static GPSTracker gpsTracker;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.access_token));
        fragmentNavigationBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_navigation, container, false);
        fragmentNavigationBinding.mapView.onCreate(savedInstanceState);
        if (index == 0) {
            fragmentNavigationBinding.btnText.setText("Order Pickup");
            fragmentNavigationBinding.txtTitle.setText("You are on way to Pharmacy");
        } else {
            fragmentNavigationBinding.btnText.setText("Deliver");
            fragmentNavigationBinding.txtTitle.setText("You are on way to Address Client " + index);
        }
        if (order != null) {
            fragmentNavigationBinding.txtAddress.setText(order.address);
        }
        fragmentNavigationBinding.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        fragmentNavigationBinding.llReached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTakePhoto fragmentTakePhoto = new FragmentTakePhoto();
                if (index == 0) {
                    fragmentTakePhoto.enumPictureType = EnumPictureType.ORDER_PICKUP;
                } else if (index == sections.size() - 1) {
                    fragmentTakePhoto.enumPictureType = EnumPictureType.ORDER_COMPLETED;
                } else {
                    fragmentTakePhoto.enumPictureType = EnumPictureType.ORDER_DELIVER;
                }

                fragmentTakePhoto.order = order;
                fragmentTakePhoto.sections = sections;
                fragmentTakePhoto.index = index;
                fragmentTakePhoto.routeId = AppElement.routeId;
                FragmentUtils.getInstance().addFragment(getActivity(), fragmentTakePhoto, R.id.fragContainer);
            }
        });
        fragmentNavigationBinding.mapView.getMapAsync(this);
        getLocation();
        return fragmentNavigationBinding.getRoot();
    }

    public void getLocation() {
        gpsTracker = new GPSTracker(getContext(), this);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
        mOrigin = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

    }

    @Override
    public void locationChanged(Location location) {
        Log.d("locationtracking", "changed");
        if (location != null) {
            mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
            updatedLocation();
        }
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
//        mapboxMap.addOnMapClickListener(this);
        fragmentNavigationBinding.btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean simulateRoute = false;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();
// Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(getActivity(), options);
            }
        });
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);

            }
        });
        if (mDestination != null && mOrigin != null) {
            updatedLocation();
        }
    }

    private void updatedLocation() {
        if (mapboxMap != null && mapboxMap.getStyle() != null) {
            Point destinationPoint = Point.fromLngLat(mDestination.longitude, mDestination.latitude);
            Point originPoint = Point.fromLngLat(mOrigin.longitude,
                    mOrigin.latitude);

            GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
            if (source != null) {
                source.setGeoJson(Feature.fromGeometry(destinationPoint));
            }

            getRoute(originPoint, destinationPoint);
            fragmentNavigationBinding.btnNavigate.setEnabled(true);
            //fragmentNavigationBinding.btnNavigate.setBackgroundResource(R.color.blue);
        }
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(getContext())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);
                        double distance = (currentRoute.distance() / 1000) * 0.621371;
                        DecimalFormat df = new DecimalFormat("0.00");
                        distance = Double.parseDouble(df.format(distance));
                        //distance=Math.floor(distance);
                        if (distance > 1) {
                            fragmentNavigationBinding.txtdistance.setText(distance + " Miles");
                        } else {
                            fragmentNavigationBinding.txtdistance.setText(distance + " Mile");
                        }
                        if(currentRoute.duration()!=null) {
                            double time = currentRoute.duration() / 60;
                            time = Double.parseDouble(df.format(time));
                            time=Math.floor(time);
                            if (time > 1) {
                                fragmentNavigationBinding.txttime.setText(time + " mins");
                            } else {
                                fragmentNavigationBinding.txttime.setText(time + " min");
                            }

                        }
                        Log.d("routeData", currentRoute.distance() + "," + currentRoute.duration());
// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, fragmentNavigationBinding.mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(getContext(), loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentNavigationBinding.mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentNavigationBinding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentNavigationBinding.mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentNavigationBinding.mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fragmentNavigationBinding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentNavigationBinding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        fragmentNavigationBinding.mapView.onLowMemory();
    }
}


