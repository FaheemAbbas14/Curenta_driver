package com.curenta.driver.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentThankYouBinding;
import com.curenta.driver.enums.EnumPictureType;


public class FragmentThankYou extends Fragment {
    FragmentThankYouBinding fragmentThankYouBinding;
    public EnumPictureType pictureType;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentThankYouBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_thank_you, container, false);
        ((LoginActivity) getActivity()).makeOrignalBackground();
        ((LoginActivity) getActivity()).moveToTop();
        if (pictureType == EnumPictureType.PROFILEPIC) {
            ((LoginActivity) getActivity()).showHeading("Required Steps");
            fragmentThankYouBinding.txtDescription.setText("Thanks for your Photo");
        } else if (pictureType == EnumPictureType.DRIVING_LICENSE) {
            ((LoginActivity) getActivity()).showHeading("Driver's License");
            fragmentThankYouBinding.txtDescription.setText("Thanks for providing your\n" +
                    "        Driver's License");
        } else if (pictureType == EnumPictureType.CAR_INSURANCE) {
            ((LoginActivity) getActivity()).showHeading("Car Insurance");
            fragmentThankYouBinding.txtDescription.setText("Thanks for providing your\n" +
                    "Car Insurance");
        } else if (pictureType == EnumPictureType.CAR_REGISTRATION) {
            ((LoginActivity) getActivity()).showHeading("Car Registration");
            fragmentThankYouBinding.txtDescription.setText("Thanks for providing your\n" +
                    "Car Registration");
        }
        else if (pictureType == EnumPictureType.RESET_PASSWORD) {
            ((LoginActivity) getActivity()).showHeading("Password Reset");
            fragmentThankYouBinding.txtDescription.setText("Your password updated\n" +
                    "succsessfuly !");
        }
        else{
            ((LoginActivity) getActivity()).showHeading("Background Check");
            fragmentThankYouBinding.txtDescription.setText("Thanks for your informations");
        }
        fragmentThankYouBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getActivity().getSupportFragmentManager() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch(IllegalStateException ex) {

                }
                catch(Exception ex) {

                }

                if(pictureType==EnumPictureType.RESET_PASSWORD){
                    try {
                        if (getActivity().getSupportFragmentManager() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    } catch(IllegalStateException ex) {

                    }
                    catch(Exception ex) {

                    }
                }
                if(pictureType==null){
                    try {
                        if (getActivity().getSupportFragmentManager() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();

                        }
                    } catch(IllegalStateException ex) {

                    }
                    catch(Exception ex) {

                    }
                }
            }
        });
        return fragmentThankYouBinding.getRoot();
    }

}