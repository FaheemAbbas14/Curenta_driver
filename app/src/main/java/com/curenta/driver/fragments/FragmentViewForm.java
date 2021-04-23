package com.curenta.driver.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentViewFormBinding;
import com.curenta.driver.dto.UserInfo;


import java.util.List;


public class FragmentViewForm extends Fragment  {

    FragmentViewFormBinding fragmentViewFormBinding;

    String heading = "Forms";
    Uri formUri;
    UserInfo userInfo;
    public static final String SAMPLE_FILE = "formi9.pdf";
    Integer pageNumber = 0;
    String pdfFileName;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentViewFormBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_view_form, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        userInfo = UserInfo.getInstance();

        fragmentViewFormBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        ((LoginActivity) getActivity()).moveToTop();

        return fragmentViewFormBinding.getRoot();
    }


}