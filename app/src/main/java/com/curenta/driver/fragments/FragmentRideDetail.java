package com.curenta.driver.fragments;

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
import com.curenta.driver.adaptors.RouteListAdopter;
import com.curenta.driver.databinding.FragmentRideDetailBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.retrofit.apiDTO.GetRoutesResponse;
import com.curenta.driver.utilities.FragmentUtils;

import java.util.ArrayList;


public class FragmentRideDetail extends Fragment {

    FragmentRideDetailBinding fragmentRideDetailBinding;
    public static final boolean SHOW_ADAPTER_POSITIONS = true;
    public GetRoutesResponse getRouteResponse;
    public String routeId;
    boolean isAnyFocused = false;

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

                        if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
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
                    FragmentCancleRoute fragmentCancleRoute = new FragmentCancleRoute();
                    fragmentCancleRoute.routeId = routeId;
                    fragmentCancleRoute.cancelTYpe = 1;
                    FragmentUtils.getInstance().addFragment(getActivity(), fragmentCancleRoute, R.id.fragContainer);

                }
            }
        });
        if (AppElement.sections.size() == 0) {
            if (getRouteResponse != null && getRouteResponse.data != null) {
                appendSection(getRouteResponse);
            }
        }
        Log.d("ridelistadopter", "total orders " + AppElement.sections.size());

        ((DashboardActivity) getActivity()).isBackAllowed = false;
//        fragmentRideDetailBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        rideDetailListAdopter = new RideDetailListAdapter(getActivity(), SHOW_ADAPTER_POSITIONS, routeId);
//        rideDetailListAdopter.setData(sections);
//        fragmentRideDetailBinding.recyclerView.setAdapter(rideDetailListAdopter);

        //   rideDetailListAdopter.notifyAllSectionsDataSetChanged();
        int focusIndex = 0;
        boolean allCompleted = true;
        for (RideDetailListAdapter.RoutStep section : AppElement.sections) {
            boolean isCompleted = true;
            for (int i = 0; i < section.orders.size(); i++) {
                Log.d("deliveryAPICall", "" + section.orders.get(i).name + " completed " + section.orders.get(i).isCompleted);

                if (!section.orders.get(i).isCompleted) {
                    isCompleted = false;
                    allCompleted = false;
                    AppElement.orderIndex = i;
                    break;
                }
            }

            if (isCompleted) {
                focusIndex++;
                Log.d("deliveryAPICall", "" + section.orders.get(0).routeStepIndex + " focusIndex " + focusIndex);
            } else {
                break;
            }

        }
        if (focusIndex < AppElement.sections.size()) {

            AppElement.sections.get(focusIndex).isFocused = true;
            AppElement.sections.get(focusIndex).isArrived = true;
            for (int i = 0; i < AppElement.sections.get(focusIndex).orders.size(); i++) {
                if (!AppElement.sections.get(focusIndex).orders.get(i).isCompleted) {
                    AppElement.sections.get(focusIndex).orders.get(i).isArrived = true;
                    break;
                }
            }
        }
//        if (AppElement.nextFocusIndex < focusIndex) {
//            AppElement.nextFocusIndex = focusIndex;
//        }
//        if (!allCompleted && AppElement.nextFocusIndex >= sections.size()) {
//            AppElement.nextFocusIndex = AppElement.nextFocusIndex - 1;
//        }
//        if (!AppElement.isPickupCompleted) {
//            AppElement.nextFocusIndex = 0;
//        }
//        if (AppElement.nextFocusIndex > 0) {
//            for (int i = 0; i < AppElement.nextFocusIndex; i++) {
//                sections.get(i).isArrived = false;
//                sections.get(i).isFocused = false;
//                if (AppElement.delivered.contains(i)) {
//                    sections.get(i).orders.get(0).isCompleted = true;
//                }
//            }
//            if (AppElement.nextFocusIndex < sections.size()) {
//                sections.get(AppElement.nextFocusIndex).isFocused = true;
//                sections.get(AppElement.nextFocusIndex).isArrived = true;
//                if (sections.get(AppElement.nextFocusIndex).orders.size() == 1) {
//                    sections.get(AppElement.nextFocusIndex).orders.get(0).isCompleted = false;
//                }
//                for (int i = 0; i < sections.get(AppElement.nextFocusIndex).orders.size(); i++) {
//                    if (!sections.get(AppElement.nextFocusIndex).orders.get(i).isCompleted) {
//                        sections.get(AppElement.nextFocusIndex).orders.get(i).isArrived = true;
//                        break;
//                    }
//                }
//                Log.d("deliveryAPICall", "" + AppElement.nextFocusIndex + " arrived " + sections.get(AppElement.nextFocusIndex).isArrived);
//
//            }
//        }
        AppElement.routeStepIndex = focusIndex;
        ArrayList<RideDetailListAdapter.RoutStep> section = AppElement.sections;
        RouteListAdopter routeListAdopter = new RouteListAdopter(getContext(), routeId, AppElement.sections);
        fragmentRideDetailBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentRideDetailBinding.recyclerView.setAdapter(routeListAdopter);
        routeListAdopter.notifyDataSetChanged();
        Log.d("deliveryAPICall", "adopter set ");

        return fragmentRideDetailBinding.getRoot();
    }

    public void appendSection(GetRoutesResponse data) {
        //  RideDetailListAdapter.RoutStep section = new RideDetailListAdapter.RoutStep();
        //apending pickup
        // boolean isPickupCompleted = false;
        ArrayList<RideDetailListAdapter.RoutStep> sections = new ArrayList<>();
        boolean isPickupFocused = true;
        isAnyFocused = true;
        String pickText = "Order Pickup";
        int index = 0;
        if (data.data.get(0).routeStatus.equalsIgnoreCase("InRoute")) {
            AppElement.isPickupCompleted = true;
            isPickupFocused = false;
            isAnyFocused = false;
            pickText = "Order Pickup Completed";

        } else {
            AppElement.isPickupCompleted = false;
        }
        AppElement.pharmacyContact = data.data.get(0).pickupAddress.phoneNumberPrimary;
        ArrayList<RideDetailListAdapter.Order> pickuporders = new ArrayList<>();
        pickuporders.add(new RideDetailListAdapter.Order(data.data.get(0).pickupAddress.name, data.data.get(0).pickupAddress.fullAddress, pickText, AppElement.isPickupCompleted, false, data.data.get(0).pickupAddress.pickupAddressId, false, data.data.get(0).pickupAddress.latitude, data.data.get(0).pickupAddress.longitude, "", "", 0, 0, null, isPickupFocused, index));
        RideDetailListAdapter.RoutStep routeStepDto = new RideDetailListAdapter.RoutStep(data.data.get(0).pickupAddress.name, data.data.get(0).pickupAddress.fullAddress, pickuporders, data.data.get(0).routeSteps.get(0).routeStepsId, isPickupFocused, false);
        sections.add(routeStepDto);
        int client = 0;

        for (int i = 0; i < data.data.size(); i++) {
            Log.d("ridelistadopter", "Order no " + i + " steps " + data.data.get(i).routeSteps.size());
//            for (int j=0;j<data.data.get(i).routeSteps.size();j++){
            String name = null, address = null;

            for (GetRoutesResponse.RouteStep routeStep : data.data.get(i).routeSteps) {
                index++;
                Log.d("routeStepindex", "" + index + " name " + routeStep.routeStepsId);
                boolean isOrderfocused = false;

                ArrayList<RideDetailListAdapter.Order> orders = new ArrayList<>();
                if (routeStep.orders.size() > 0) {

                    // Log.d("ridelistadopter", "routeStep " + routeStep.routeStepsId + " orders " + routeStep.orders.size());

                    for (GetRoutesResponse.Order order : routeStep.orders) {
                        boolean isCancelled = false;
                        boolean isOrdercompleted = false;
                        name = order.patientName;
                        address = order.deliveryAddress;

                        Log.d("routestatus", order.patientName + " status " + order.orderStatus);
                        client++;

                        String buttonText = "Deliver";
                        if (order.orderStatus.equalsIgnoreCase("Delivered")) {
                            isOrdercompleted = true;
                            buttonText = "Delivered";

                        }
                        if (order.orderStatus.equalsIgnoreCase("UNDELIVERED") || order.orderStatus.equalsIgnoreCase("Returned")) {
                            isOrdercompleted = true;
                            isCancelled = true;
                            buttonText = "Canceled";

                        }
                        if (AppElement.isPickupCompleted != false && !isOrdercompleted && !isAnyFocused) {
                            isOrderfocused = true;
                            isAnyFocused = true;
                        }
                        orders.add(new RideDetailListAdapter.Order(order.patientName, order.deliveryAddress, buttonText, isOrdercompleted, false, order.orderId, isCancelled, order.latitude, order.longitude, routeStep.routeStepsId, "", order.patientId, order.facilityId, order.deliveryNote, isOrderfocused, index));

                    }

                }
                Log.d("ridelistadopter", "routeStep " + routeStep.routeStepsId + " orders " + orders.size());
                RideDetailListAdapter.RoutStep routeStepdata = new RideDetailListAdapter.RoutStep(name, address, orders, routeStep.routeStepsId, isOrderfocused, false);
                sections.add(routeStepdata);

            }


            //}
        }
        AppElement.sections = sections;
        Log.d("deliveryAPICall", "append set ");
        //  sections.add(section);
    }


}