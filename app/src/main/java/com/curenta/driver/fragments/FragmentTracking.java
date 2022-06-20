package com.curenta.driver.fragments;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentTrackingBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.interfaces.ILatLngUpdate;
import com.curenta.driver.interfaces.ILocationChange;
import com.curenta.driver.utilities.DirectionsJSONParser;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.GPSTracker;
import com.curenta.driver.utilities.Preferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentTracking extends Fragment implements ILocationChange, OnMapReadyCallback, ILatLngUpdate {

    private static final String TAG = "FragmentTRacking";
    public RideDetailListAdapter.Order order;
    public int index;
    FragmentTrackingBinding fragmentTrackingBinding;
    private GoogleMap mMap;
    static GPSTracker gpsTracker;
    Marker currentLocationMarker;
    private MarkerOptions mMarkerOptions;
    public LatLng mOrigin;
    public LatLng currentLocation;
    public LatLng mDestination;
    private Polyline mPolyline;
    View mapView;
    boolean tollRoute = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTrackingBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_tracking, container, false);
        if (index == 0) {
            fragmentTrackingBinding.btnText.setText("Order Pickup");
            fragmentTrackingBinding.txtTitle.setText("You are on way to Pharmacy");
        } else {
            fragmentTrackingBinding.btnText.setText("Deliver");
            fragmentTrackingBinding.txtTitle.setText("You are on way to Address Client " + index);
        }
        tollRoute = Preferences.getInstance().getBoolean("toolFreeRoute", true);
        if (tollRoute) {
            fragmentTrackingBinding.lltollon.setImageResource(R.drawable.tollon);

        } else {
            fragmentTrackingBinding.lltollon.setImageResource(R.drawable.tolloff);

        }
        if (order != null) {
            fragmentTrackingBinding.txtAddress.setText(order.address);
        }
        fragmentTrackingBinding.lltollon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tollRoute) {
                    Toast.makeText(getContext(), "Toll routes are disabled", Toast.LENGTH_SHORT).show();
                    tollRoute = false;
                    fragmentTrackingBinding.lltollon.setImageResource(R.drawable.tolloff);
                    drawRoute();
                } else {
                    Toast.makeText(getContext(), "Toll routes are enabled", Toast.LENGTH_SHORT).show();
                    tollRoute = true;
                    fragmentTrackingBinding.lltollon.setImageResource(R.drawable.tollon);
                    drawRoute();
                }
                Preferences.getInstance().saveBoolean("toolFreeRoute", tollRoute);
            }
        });
//        fragmentTrackingBinding.header.txtLabel.setText("Tracking");
//        fragmentTrackingBinding.header.imageView3.setVisibility(View.INVISIBLE);
        ((DashboardActivity) getActivity()).iLocationChange = this;
        fragmentTrackingBinding.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();

                    }
                } catch (IllegalStateException ex) {

                } catch (Exception ex) {

                }
            }
        });
        fragmentTrackingBinding.llReached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentConfirmDelivery fragmentConfirmDelivery = new FragmentConfirmDelivery();
                if (index == 0) {
                    fragmentConfirmDelivery.enumPictureType = EnumPictureType.ORDER_PICKUP;
                } else if (index == AppElement.sections.size() - 1) {
                    fragmentConfirmDelivery.enumPictureType = EnumPictureType.ORDER_COMPLETED;
                } else {
                    fragmentConfirmDelivery.enumPictureType = EnumPictureType.ORDER_DELIVER;
                }
                fragmentConfirmDelivery.order = order;
                fragmentConfirmDelivery.index = index;
                fragmentConfirmDelivery.routeId = AppElement.routeId;
                FragmentUtils.getInstance().addFragment(getActivity(), fragmentConfirmDelivery, R.id.fragContainer);

            }
        });
        fragmentTrackingBinding.btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDestination != null) {

                    String url = null;
                    if (!tollRoute) {
                        url = "geo:0,0?q=" + mDestination.latitude + "," + mDestination.longitude + "&dirflg=h,t";
                    } else {
                        url = "geo:0,0?q=" + mDestination.latitude + "," + mDestination.longitude;

                    }
                    Uri gmmIntentUri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    getActivity().startActivity(Intent.createChooser(intent, "Select application"));
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        getLocation();
        return fragmentTrackingBinding.getRoot();
    }

    public void getLocation() {
        gpsTracker = new GPSTracker(getContext(), this);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
        currentLocation = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }
        if (mMap != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 80);
        }
        drawRoute();
    }

    @SuppressLint("MissingPermission")
    public void updateLocation(Location location) {
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());


        }
    }


    @Override
    public void locationChanged(Location location) {
        Log.d("locationtracking", "changed");
        if (location != null) {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            updateLocation(location);
            locationChanged(currentLocation);
        }
    }

    private void drawRoute() {
        String url;
        // Getting URL to the Google Directions API
        if (currentLocation != null && mDestination != null) {
            url = getDirectionsUrl(currentLocation, mDestination);


            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

//        mMap.addMarker(new MarkerOptions()
//                .position(origin)
//                .title("A"));
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(dest)
                    .title("B"));
        }
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + mDestination.latitude + "," + mDestination.longitude;

        // Key
        String key = "key=AIzaSyDMP4GI8rW2KJ9cuTsRBpt6VoesB6oJmF4";

        // Building the parameters to the web service
        String parameters;
        if (tollRoute) {
            parameters = str_origin + "&" + str_dest + "&" + key;

        } else {
            parameters = str_origin + "&" + str_dest + "&" + key + "&dirflg=h,t";

        }
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.d("googleurl", url);
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void locationChanged(LatLng location) {
        Log.d("latlngtracking", "changed");
        if (mMap != null) {
            mMap.clear();
        }
        currentLocation = location;
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
            ;
        }
        if (mMap != null) {
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation)
                    // below line is use to add custom marker on our map.
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.caricon)));

            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
        }
        mOrigin = location;
        drawRoute();
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (result != null) {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(Color.BLUE);
                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    if (mPolyline != null) {
                        mPolyline.remove();
                    }
                    mPolyline = mMap.addPolyline(lineOptions);
                    fragmentTrackingBinding.txtdistance.setText(AppElement.distance);
                    fragmentTrackingBinding.txttime.setText(AppElement.duration);

                }
                //Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((DashboardActivity) getActivity()).iLocationChange = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((DashboardActivity) getActivity()).iLocationChange = null;
    }
}