package com.curenta.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentStatusBinding;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.utilities.Preferences;


public class FragmentStatus extends Fragment {
    FragmentStatusBinding fragmentStatusBinding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentStatusBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_status, container, false);

        fragmentStatusBinding.header.txtLabel.setText("Your Status");
        fragmentStatusBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).checkOnline();
                try {
                    if (getActivity()!=null && getActivity().getSupportFragmentManager() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch(IllegalStateException ex) {

                }
                catch(Exception ex) {

                }
            }
        });
        fragmentStatusBinding.chStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatus(isChecked);
                LoggedInUser.getInstance().isOnline = isChecked;
                ((DashboardActivity) getActivity()).updateDriverStatus(false);
            }
        });
        checkStatus(LoggedInUser.getInstance().isOnline);
        fragmentStatusBinding.chStatus.setChecked(LoggedInUser.getInstance().isOnline);
        return fragmentStatusBinding.getRoot();
    }

    private void checkStatus(boolean isChecked) {
        Preferences.getInstance().saveBoolean("isOnline", isChecked);
        if (isChecked) {
            fragmentStatusBinding.txtheading.setText("You are online");
            fragmentStatusBinding.txtLabel2.setText("Your status is now online so you can receive notifications and go for orders with Curenta.");
            fragmentStatusBinding.txtLabel3.setText("If you finished your working hours for today or you need to pause your work, You can make yourself offline so you will not receive any notifications untill you are back online.");
        } else {
            LoggedInUser.getInstance().isCovidPassed = false;
            LoggedInUser.getInstance().isSelfie = false;
            fragmentStatusBinding.txtheading.setText("You are offline");
            fragmentStatusBinding.txtLabel2.setText("Your status is now offline so you will not receive any notifications from Curenta untill you are back online.");
            fragmentStatusBinding.txtLabel3.setText("If you are ready to work and receive Curenta orders now, just make yourself online.");
        }
    }

}