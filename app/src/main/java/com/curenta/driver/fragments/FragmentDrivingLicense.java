package com.curenta.driver.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentDrivingLicenseBinding;
import com.curenta.driver.databinding.FragmentSecurityCodeVerificationBinding;
import com.curenta.driver.dto.Regex;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.utilities.ImageConverter;


public class FragmentDrivingLicense extends Fragment {
    FragmentDrivingLicenseBinding fragmentDrivingLicenseBinding;
    String heading = "Required Steps";
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentDrivingLicenseBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_driving_license, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).moveToTop();
        if(UserInfo.getInstance().drivingLicensePic!=null){
         //   Bitmap roundedPic= ImageConverter.getCroppedBitmap(UserInfo.getInstance().drivingLicensePic);
            fragmentDrivingLicenseBinding.imgProfilePic.setImageBitmap(UserInfo.getInstance().drivingLicensePic);
            checkComplete(false);
        }
        fragmentDrivingLicenseBinding.imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo.getInstance().drivingLicenseNo=fragmentDrivingLicenseBinding.edtLicense.getText().toString();
                FragmentPictureSelection fragmentPictureSelection = new FragmentPictureSelection();
                fragmentPictureSelection.pictureType = EnumPictureType.DRIVING_LICENSE;
                ((LoginActivity) getActivity()).showFragment(fragmentPictureSelection);
            }
        });

        fragmentDrivingLicenseBinding.edtLicense.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable et) {
                String s=et.toString();
                if((!s.equals(s.toUpperCase()) && s.length()==1)) {
                    s=s.toUpperCase();
                    fragmentDrivingLicenseBinding.edtLicense.setText(s);
                    fragmentDrivingLicenseBinding.edtLicense.setSelection(s.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String value = fragmentDrivingLicenseBinding.edtLicense.getText().toString();
                UserInfo.getInstance().drivingLicenseNo = value;
                checkComplete(false);
            }
        });
        fragmentDrivingLicenseBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String licenseNo = fragmentDrivingLicenseBinding.edtLicense.getText().toString();
                if(fragmentDrivingLicenseBinding.edtLicense.getText().toString().equalsIgnoreCase("")){
                    fragmentDrivingLicenseBinding.txtLicenseError.setText(getResources().getString(R.string.error_invalid_license));
                }
                else{
                    fragmentDrivingLicenseBinding.txtLicenseError.setText("");
                }
                if(UserInfo.getInstance().drivingLicensePicURI==null){
                    fragmentDrivingLicenseBinding.txtLicensePicError.setText("Please select a driving license picture");
                }
                else{
                    fragmentDrivingLicenseBinding.txtLicensePicError.setText("");
                }
                if(licenseNo.equalsIgnoreCase("") ){
                    fragmentDrivingLicenseBinding.txtLicenseError.setText(getResources().getString(R.string.error_invalid_license));
                }
                else{
                    checkComplete(true);
                    if (UserInfo.getInstance().isDriveingLicenseCompleted) {
                        try {
                            if (getActivity()!=null && getActivity().getSupportFragmentManager() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        } catch(IllegalStateException ex) {

                        }
                        catch(Exception ex) {

                        }
                    }
                }

            }
        });
        return fragmentDrivingLicenseBinding.getRoot();
    }

    public void checkComplete(boolean check) {

        if (!UserInfo.getInstance().drivingLicenseNo.equalsIgnoreCase("") && UserInfo.getInstance().drivingLicensePic != null) {
            fragmentDrivingLicenseBinding.imgNext.setImageResource(R.drawable.next_icon);
            fragmentDrivingLicenseBinding.imgNext.setEnabled(true);
            if(check) {
                UserInfo.getInstance().isDriveingLicenseCompleted = true;
            }
        }
        else{
            fragmentDrivingLicenseBinding.imgNext.setEnabled(true);
            if(check) {
                UserInfo.getInstance().isDriveingLicenseCompleted = false;
            }
            fragmentDrivingLicenseBinding.imgNext.setImageResource(R.drawable.next_icon);
        }
    }
}