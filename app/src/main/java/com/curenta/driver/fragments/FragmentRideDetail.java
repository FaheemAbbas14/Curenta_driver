package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentRideDetailBinding;
import com.curenta.driver.retrofit.apiDTO.GetRoutesResponse;
import com.curenta.driver.utilities.FragmentUtils;

import java.util.ArrayList;


public class FragmentRideDetail extends Fragment {

    FragmentRideDetailBinding fragmentRideDetailBinding;
    RideDetailListAdapter rideDetailListAdopter;
    public static final boolean SHOW_ADAPTER_POSITIONS = true;
    ArrayList<RideDetailListAdapter.Section> sections = new ArrayList<>();
    public GetRoutesResponse getRouteResponse;
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
                try {
                    for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {

                        if (getActivity()!=null && getActivity().getSupportFragmentManager() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                    ((DashboardActivity) getActivity()).checkOnline();
                } catch (IllegalStateException ex) {

                } catch (Exception ex) {

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
                    fragmentCancelOrder.cancelTYpe = 1;
                    FragmentUtils.getInstance().addFragment(getActivity(), fragmentCancelOrder, R.id.fragContainer);

                }
            }
        });
        if (sections.size() == 0) {
            if (getRouteResponse != null && getRouteResponse.data != null) {
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

    public void appendSection(GetRoutesResponse data) {
        RideDetailListAdapter.Section section = new RideDetailListAdapter.Section();
        //apending pickup
        boolean isPickupCompleted = false;
        boolean isPickupFocused = true;
        isAnyFocused = true;
        String pickText = "Order Pickup";
        if (data.data.get(0).routeStatus.equalsIgnoreCase("InRoute")) {
            isPickupCompleted = true;
            isPickupFocused = false;
            isAnyFocused = false;
            pickText = "Order Pickup Completed";

        }
        section.items.add(new RideDetailListAdapter.Order(data.data.get(0).pickupAddress.name, data.data.get(0).pickupAddress.fullAddress, pickText, isPickupFocused, isPickupCompleted, false, data.data.get(0).pickupAddress.pickupAddressId, false, data.data.get(0).pickupAddress.latitude, data.data.get(0).pickupAddress.longitude, ""));
        int client = 0;
        for (int i = 0; i < data.data.size(); i++) {

//            for (int j=0;j<data.data.get(i).routeSteps.size();j++){
            for (GetRoutesResponse.RouteStep routeStep : data.data.get(i).routeSteps) {
                if(routeStep.orders.size()>0) {
                    Log.d("routestatus", routeStep.orders.get(0).patientName + " status " + routeStep.orders.get(0).orderStatus);
                    client++;
                    boolean isOrdercompleted = false;
                    boolean isOrderfocused = false;
                    boolean isCancelled = false;
                    String buttonText = "Deliver";
                    if (routeStep.orders.get(0).orderStatus.equalsIgnoreCase("Delivered")) {
                        isOrdercompleted = true;
                        buttonText = "Delivered";

                    }
                    if (routeStep.orders.get(0).orderStatus.equalsIgnoreCase("UNDELIVERED") || routeStep.orders.get(0).orderStatus.equalsIgnoreCase("Returned")) {
                        isOrdercompleted = true;
                        isCancelled = true;
                        buttonText = "Canceled";

                    }
                    if (isPickupCompleted != false && !isOrdercompleted && !isAnyFocused) {
                        isOrderfocused = true;
                        isAnyFocused = true;
                    }
                    section.items.add(new RideDetailListAdapter.Order(routeStep.orders.get(0).patientName, routeStep.orders.get(0).deliveryAddress, buttonText, isOrderfocused, isOrdercompleted, false, routeStep.orders.get(0).orderId, isCancelled, routeStep.orders.get(0).latitude, routeStep.orders.get(0).longitude, routeStep.routeStepsId));
                }
            }

            //}
        }

        sections.add(section);
    }


}