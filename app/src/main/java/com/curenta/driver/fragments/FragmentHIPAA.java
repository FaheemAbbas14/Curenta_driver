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
import com.curenta.driver.databinding.FragmentHIPAABinding;
import com.curenta.driver.dto.UserInfo;


public class FragmentHIPAA extends Fragment {
    FragmentHIPAABinding fragmentHIPAABinding;
    String heading = "Required Steps";
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHIPAABinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_h_i_p_a_a, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).moveToTop();
        fragmentHIPAABinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo.getInstance().isHIPAACompleted=true;
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return fragmentHIPAABinding.getRoot();
    }

}