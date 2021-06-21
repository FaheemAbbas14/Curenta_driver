package com.curenta.driver.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentContactUsBinding;
import com.curenta.driver.databinding.FragmentSettingBinding;
import com.curenta.driver.utilities.Preferences;


public class FragmentSetting extends Fragment {

    FragmentSettingBinding fragmentSettingBinding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSettingBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_setting, container, false);
        fragmentSettingBinding.header.txtLabel.setText("Settings");
        fragmentSettingBinding.header.imageView3.setVisibility(View.INVISIBLE);
        fragmentSettingBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        boolean tollFreeRoute = Preferences.getInstance().getBoolean("toolFreeRoute", true);
        fragmentSettingBinding.simpleSwitch.setChecked(tollFreeRoute);
        fragmentSettingBinding.simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Preferences.getInstance().saveBoolean("toolFreeRoute", isChecked);
            }
        });
        return fragmentSettingBinding.getRoot();
    }

}