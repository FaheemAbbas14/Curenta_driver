package com.curenta.driver.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentContactUsBinding;
import com.curenta.driver.databinding.FragmentSecurityCodeVerificationBinding;


public class FragmentContactUs extends Fragment {

    FragmentContactUsBinding fragmentContactUsBinding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentContactUsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_contact_us, container, false);
        fragmentContactUsBinding.header.txtLabel.setText("Contact us");
        fragmentContactUsBinding.header.imageView3.setVisibility(View.INVISIBLE);
        fragmentContactUsBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getActivity().getSupportFragmentManager() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch(IllegalStateException ex) {

                }
                catch(Exception ex) {

                }
            }
        });
        return fragmentContactUsBinding.getRoot();
    }

}