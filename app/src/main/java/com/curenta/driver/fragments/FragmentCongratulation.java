package com.curenta.driver.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curenta.driver.DashboardActivity;
import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentCongratulationBinding;
import com.curenta.driver.databinding.FragmentThankYouBinding;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.utilities.Preferences;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentCongratulation extends Fragment {
    FragmentCongratulationBinding fragmentCongratulationBinding;
    String heading = "Congratulations";
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCongratulationBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_congratulation, container, false);
        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).moveToTop();
        ((LoginActivity) getActivity()).makeOrignalBackground();
        fragmentCongratulationBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isTutorialShown= Preferences.getInstance().getBoolean("tutorialShown");
//                if(!isTutorialShown){
                    ((LoginActivity) getActivity()).showFragment(new FragmentTutorial());
//                }
//                else {
//                    Intent nextIntent = new Intent(getApplicationContext(), DashboardActivity.class);
//                    startActivity(nextIntent);
//                    getActivity().finish();
//                }

            }
        });
        return fragmentCongratulationBinding.getRoot();
    }


}