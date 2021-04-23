package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentRideDetailBinding;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.CancelRouteRequest;
import com.curenta.driver.retrofit.apiDTO.CancelRouteResponse;
import com.curenta.driver.retrofit.apiDTO.GetRouteResponse;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Preferences;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FragmentRideDetail extends Fragment {

    FragmentRideDetailBinding fragmentRideDetailBinding;
    RideDetailListAdapter rideDetailListAdopter;
    public static final boolean SHOW_ADAPTER_POSITIONS = true;
    ArrayList<RideDetailListAdapter.Section> sections = new ArrayList<>();
    public GetRouteResponse getRouteResponse;
    public String routeId;
    boolean isAnyFocused = false;
    ProgressDialog dialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRideDetailBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_ride_detail, container, false);
        fragmentRideDetailBinding.header.txtLabel.setText("Route");
        fragmentRideDetailBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                ((DashboardActivity) getActivity()).checkRide();
            }
        });
        fragmentRideDetailBinding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (routeId != null) {
                    FragmentCancelOrder fragmentCancelOrder = new FragmentCancelOrder();
                    fragmentCancelOrder.sections = sections;
                    fragmentCancelOrder.routeId = routeId;
                    fragmentCancelOrder.cancelTYpe=1;
                    FragmentUtils.getInstance().addFragment(getActivity(), fragmentCancelOrder, R.id.fragContainer);

                }
            }
        });
        if (sections.size() == 0) {
            if(getRouteResponse.data!=null) {
                appendSection(getRouteResponse);
            }
        }
        ((DashboardActivity) getActivity()).isBackAllowed = false;
        fragmentRideDetailBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideDetailListAdopter = new RideDetailListAdapter(getActivity(), SHOW_ADAPTER_POSITIONS, routeId);
        rideDetailListAdopter.setData(sections);
        fragmentRideDetailBinding.recyclerView.setAdapter(rideDetailListAdopter);
        rideDetailListAdopter.notifyAllSectionsDataSetChanged();
        return fragmentRideDetailBinding.getRoot();
    }

    public void appendSection(GetRouteResponse data) {
        RideDetailListAdapter.Section section = new RideDetailListAdapter.Section();
        //apending pickup
        boolean isPickupCompleted = false;
        boolean isPickupFocused = true;
        isAnyFocused = true;
        String pickText = "Order Pickup";
        if (data.data.routeStatus.equalsIgnoreCase("PickedUp")) {
            isPickupCompleted = true;
            isPickupFocused = false;
            isAnyFocused = false;
            pickText = "Order Pickup Completed";

        }
        section.items.add(new RideDetailListAdapter.Order(data.data.pickupAddress.name, data.data.pickupAddress.fullAddress, pickText, isPickupFocused, isPickupCompleted, false, data.data.pickupAddress.pickupAddressId,false));
        int client = 0;
        for (GetRouteResponse.RouteOrder routeOrder : data.data.routeOrders) {
            client++;
            boolean isOrdercompleted = false;
            boolean isOrderfocused = false;
            boolean isCancelled = false;
            String buttonText = "Deliver";
            if (routeOrder.currentStatus.equalsIgnoreCase("Delivered")) {
                isOrdercompleted = true;
                buttonText = "Delivered";

            }
            if (routeOrder.currentStatus.equalsIgnoreCase("UNDELIVERED")) {
                isOrdercompleted = true;
                isCancelled=true;
                buttonText = "Canceled";

            }
            if (isPickupCompleted != false && !isOrdercompleted && !isAnyFocused) {
                isOrderfocused = true;
                isAnyFocused = true;
            }
            section.items.add(new RideDetailListAdapter.Order(routeOrder.patientName, routeOrder.deliveryAddress, buttonText, isOrderfocused, isOrdercompleted, false, routeOrder.orderId,isCancelled));

        }

        sections.add(section);
    }


}