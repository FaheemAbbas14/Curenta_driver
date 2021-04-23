package com.curenta.driver.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentBeckgroundCheckBinding;
import com.curenta.driver.databinding.FragmentSecurityCodeVerificationBinding;


public class FragmentBeckgroundCheck extends Fragment {

    FragmentBeckgroundCheckBinding fragmentBeckgroundCheckBinding;
    String heading = "Background Check";
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentBeckgroundCheckBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_beckground_check, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).moveToTop();
        fragmentBeckgroundCheckBinding.btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentSocialSecurity());
            }
        });
        return fragmentBeckgroundCheckBinding.getRoot();
    }

}