package com.curenta.driver.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.DashboardActivity;
import com.curenta.driver.MainApplication;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentThankYouActionBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.utilities.Preferences;


public class FragmentThankYouAction extends Fragment {

    FragmentThankYouActionBinding fragmentThankYouActionBinding;
    public EnumPictureType enumPictureType;
    boolean isCompleted = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentThankYouActionBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_thank_you_action, container, false);

        fragmentThankYouActionBinding.header.txtLabel.setVisibility(View.INVISIBLE);
        fragmentThankYouActionBinding.header.imageView3.setVisibility(View.INVISIBLE);
        if (enumPictureType != EnumPictureType.DRIVER_SELFIE) {
            fragmentThankYouActionBinding.header.imgBackButton.setVisibility(View.INVISIBLE);
        }
        if (enumPictureType == EnumPictureType.ORDER_PICKUP) {
            fragmentThankYouActionBinding.txtLabel2.setText("You picked up orders");
            fragmentThankYouActionBinding.txtLabel3.setText("Our clients are waiting for you");
        } else if (enumPictureType == EnumPictureType.ORDER_DELIVER) {
            fragmentThankYouActionBinding.txtLabel2.setText("The receipt submitted Successfully, Thank you");
            fragmentThankYouActionBinding.txtLabel3.setText("The rest of the Clients are waiting for you ");
        } else if (enumPictureType == EnumPictureType.ORDER_COMPLETED) {
            fragmentThankYouActionBinding.txtLabel2.setText("Our clients are waiting for you");
            fragmentThankYouActionBinding.txtLabel3.setText("");
            fragmentThankYouActionBinding.txtLabel4.setText("You finished your route");
            fragmentThankYouActionBinding.txtLabel4.setVisibility(View.VISIBLE);
            fragmentThankYouActionBinding.btnSubmit.setText("Get more orders!");
        }
        fragmentThankYouActionBinding.header.imgBackButton.setImageResource(R.drawable.blue_back);
        fragmentThankYouActionBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        fragmentThankYouActionBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enumPictureType == EnumPictureType.ORDER_DELIVER) {
                    for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount()-1; i++) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                    if (isCompleted) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else if (enumPictureType == EnumPictureType.ORDER_COMPLETED) {

                    Preferences.getInstance().setString("rideInfoDto", "");
                    Log.d("fragmentCount", "" + getActivity().getSupportFragmentManager().getBackStackEntryCount());
                    for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                    ((DashboardActivity) getActivity()).checkRide();

                } else if (enumPictureType == EnumPictureType.ORDER_PICKUP) {
                    for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount()-1; i++) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                    //getActivity().getSupportFragmentManager().popBackStack();
                    LoggedInUser.getInstance().isSelfie = true;
                    if (LoggedInUser.getInstance().isSelfie && LoggedInUser.getInstance().isCovidPassed) {
                        AppElement.isCameOnline = true;
                        // ((DashboardActivity) getActivity()).launchDismissDlg();
                        AppElement.isCameOnline = true;
                        LoggedInUser.getInstance().isOnline = true;
                        Preferences.getInstance().saveBoolean("isOnline", true);
                        ((DashboardActivity) getActivity()).checkOnline();
                        AppElement.isCameOnline = false;
                        MainApplication.enableNotifications();
                        ((DashboardActivity) getActivity()).updateDriverStatus(true);
                    }
                }
            }
        });

        return fragmentThankYouActionBinding.getRoot();
    }
}