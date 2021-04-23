package com.curenta.driver.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.DashboardActivity;
import com.curenta.driver.MainApplication;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentCovid19Binding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.utilities.Preferences;


public class FragmentCovid19 extends Fragment {
    FragmentCovid19Binding fragmentCovid19Binding;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCovid19Binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_covid19, container, false);
        fragmentCovid19Binding.header.txtLabel.setText("Covid - 19");
        fragmentCovid19Binding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        fragmentCovid19Binding.btnReadyDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                LoggedInUser.getInstance().isCovidPassed = true;
                if (LoggedInUser.getInstance().isSelfie && LoggedInUser.getInstance().isCovidPassed) {
                    AppElement.isCameOnline = true;
                    LoggedInUser.getInstance().isOnline = true;
                    Preferences.getInstance().saveBoolean("isOnline", true);
                    ((DashboardActivity) getActivity()).checkOnline();
                    AppElement.isCameOnline = false;
                    MainApplication.enableNotifications();
                    //((DashboardActivity) getActivity()).launchDismissDlg();
                }
            }
        });
        fragmentCovid19Binding.textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cdc.gov/coronavirus/2019-ncov/symptoms-testing/symptoms.html"));
                startActivity(browserIntent);
            }
        });
        fragmentCovid19Binding.chkMask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                      @Override
                                                                      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                          checkValidation();
                                                                      }
                                                                  }
        );
        fragmentCovid19Binding.chkCovidDrive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                            @Override
                                                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                                checkValidation();
                                                                            }
                                                                        }
        );

        fragmentCovid19Binding.chkDisinfect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                           @Override
                                                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                               checkValidation();
                                                                           }
                                                                       }
        );

        fragmentCovid19Binding.chkSanitize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                          @Override
                                                                          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                              checkValidation();
                                                                          }
                                                                      }
        );
        fragmentCovid19Binding.chkPrecautationPage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                                          @Override
                                                                          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                              checkValidation();
                                                                          }
                                                                      }
        );
        return fragmentCovid19Binding.getRoot();
    }

    private void checkValidation() {
        if (fragmentCovid19Binding.chkPrecautationPage.isChecked()  && fragmentCovid19Binding.chkMask.isChecked() && fragmentCovid19Binding.chkDisinfect.isChecked() && fragmentCovid19Binding.chkCovidDrive.isChecked() && fragmentCovid19Binding.chkSanitize.isChecked()) {
            fragmentCovid19Binding.btnReadyDrive.setBackgroundResource(R.drawable.blue_rounded);
            fragmentCovid19Binding.btnReadyDrive.setEnabled(true);
        } else {
            fragmentCovid19Binding.btnReadyDrive.setBackgroundResource(R.drawable.grey_rounded);
            fragmentCovid19Binding.btnReadyDrive.setEnabled(false);
        }
    }


}