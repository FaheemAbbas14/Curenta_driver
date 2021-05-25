package com.curenta.driver.fragments;

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
import com.curenta.driver.databinding.FragmentSecurityCodeVerificationBinding;
import com.curenta.driver.databinding.FragmentVehicalInfoBinding;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.enums.EnumPictureType;


public class FragmentVehicalInfo extends Fragment {
    FragmentVehicalInfoBinding fragmentVehicalInfoBinding;
    String heading = "Required Steps";
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentVehicalInfoBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_vehical_info, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        ((LoginActivity) getActivity()).moveToTop();
//        fragmentVehicalInfoBinding.imgNext.setEnabled(false);
//        fragmentVehicalInfoBinding.imgNext.setEnabled(false);
        if (UserInfo.getInstance().carRegistrationPic != null) {
            //   Bitmap roundedPic= ImageConverter.getCroppedBitmap(UserInfo.getInstance().drivingLicensePic);
            fragmentVehicalInfoBinding.imgCarReg.setImageBitmap(UserInfo.getInstance().carRegistrationPic);
            checkComplete(false);
        }
        if (UserInfo.getInstance().carInsurancePic != null) {
            //   Bitmap roundedPic= ImageConverter.getCroppedBitmap(UserInfo.getInstance().drivingLicensePic);
            fragmentVehicalInfoBinding.imgCarIns.setImageBitmap(UserInfo.getInstance().carInsurancePic);
            checkComplete(false);
        }
        fragmentVehicalInfoBinding.imgCarReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentPictureSelection fragmentPictureSelection = new FragmentPictureSelection();
                fragmentPictureSelection.pictureType = EnumPictureType.CAR_REGISTRATION;
                ((LoginActivity) getActivity()).showFragment(fragmentPictureSelection);
                checkFields();
            }
        });
        fragmentVehicalInfoBinding.imgCarIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentPictureSelection fragmentPictureSelection = new FragmentPictureSelection();
                fragmentPictureSelection.pictureType = EnumPictureType.CAR_INSURANCE;
                ((LoginActivity) getActivity()).showFragment(fragmentPictureSelection);
                checkFields();
            }
        });
        fragmentVehicalInfoBinding.edtVehicalType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String carType = fragmentVehicalInfoBinding.edtVehicalType.getText().toString();
                    UserInfo.getInstance().carType = carType;
                    if (!carType.equalsIgnoreCase("")) {
                        fragmentVehicalInfoBinding.txtTypeError.setText("");
                    }
                    checkComplete(false);
                }
            }
        });
        fragmentVehicalInfoBinding.edtVehicalType.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String value = fragmentVehicalInfoBinding.edtVehicalType.getText().toString();
                UserInfo.getInstance().carType = value;
                checkComplete(false);
            }
        });
        fragmentVehicalInfoBinding.edtVehicalModel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String carModel = fragmentVehicalInfoBinding.edtVehicalModel.getText().toString();
                    UserInfo.getInstance().carModel = carModel;
                    if (!carModel.equalsIgnoreCase("")) {
                        fragmentVehicalInfoBinding.txtModelError.setText("");
                    }
                    checkComplete(false);
                }
            }
        });
        fragmentVehicalInfoBinding.edtVehicalModel.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String value = fragmentVehicalInfoBinding.edtVehicalModel.getText().toString();
                UserInfo.getInstance().carModel = value;
                checkComplete(false);
            }
        });
        fragmentVehicalInfoBinding.edtVehicalColor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String carColor = fragmentVehicalInfoBinding.edtVehicalColor.getText().toString();
                    UserInfo.getInstance().carColor = carColor;
                    if (!carColor.equalsIgnoreCase("")) {
                        fragmentVehicalInfoBinding.txtColorError.setText("");
                    }
                    checkComplete(false);
                }
            }
        });
        fragmentVehicalInfoBinding.edtVehicalColor.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String value = fragmentVehicalInfoBinding.edtVehicalColor.getText().toString();
                UserInfo.getInstance().carColor = value;
                checkComplete(false);
            }
        });
        fragmentVehicalInfoBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carType = fragmentVehicalInfoBinding.edtVehicalType.getText().toString();
                String carmodel = fragmentVehicalInfoBinding.edtVehicalModel.getText().toString();
                String carColor = fragmentVehicalInfoBinding.edtVehicalColor.getText().toString();
                if (carType.equalsIgnoreCase("")) {
                    fragmentVehicalInfoBinding.txtTypeError.setText(getResources().getString(R.string.error_invalid_cartype));
                } else {
                    fragmentVehicalInfoBinding.txtTypeError.setText("");
                }
                if (carmodel.equalsIgnoreCase("")) {
                    fragmentVehicalInfoBinding.txtModelError.setText(getResources().getString(R.string.error_invalid_carmodel));
                } else {
                    fragmentVehicalInfoBinding.txtModelError.setText("");
                }
                if (carColor.equalsIgnoreCase("")) {
                    fragmentVehicalInfoBinding.txtColorError.setText(getResources().getString(R.string.error_invalid_carcolor));
                } else {
                    fragmentVehicalInfoBinding.txtColorError.setText("");
                }
                if (UserInfo.getInstance().carRegistrationPicURI==null) {
                    fragmentVehicalInfoBinding.txtcarregError.setText("Please select a car registration picture");
                } else {
                    fragmentVehicalInfoBinding.txtcarregError.setText("");
                }
                if (UserInfo.getInstance().carInsurancePicURI==null) {
                    fragmentVehicalInfoBinding.txtcarinsuranceError.setText("Please select a car insurance picture");
                } else {
                    fragmentVehicalInfoBinding.txtcarinsuranceError.setText("");
                }
                checkComplete(true);
                if (UserInfo.getInstance().isVehicalInfoCompleted) {
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
        return fragmentVehicalInfoBinding.getRoot();
    }

    private void checkFields() {
        UserInfo userInfo = UserInfo.getInstance();
        String carType = fragmentVehicalInfoBinding.edtVehicalType.getText().toString();
        String carmodel = fragmentVehicalInfoBinding.edtVehicalModel.getText().toString();
        String carColor = fragmentVehicalInfoBinding.edtVehicalColor.getText().toString();
        userInfo.carType = carType;
        userInfo.carModel = carmodel;
        userInfo.carColor = carColor;
    }

    private void checkComplete(boolean check) {
        UserInfo userInfo = UserInfo.getInstance();
        if (!userInfo.carType.equalsIgnoreCase("") && !userInfo.carModel.equalsIgnoreCase("") && !userInfo.carColor.equalsIgnoreCase("") && userInfo.carInsurancePic != null && userInfo.carRegistrationPic != null) {
            fragmentVehicalInfoBinding.imgNext.setImageResource(R.drawable.next_icon);
            fragmentVehicalInfoBinding.imgNext.setEnabled(true);
            if(check) {
                UserInfo.getInstance().isVehicalInfoCompleted = true;
            }
        } else {
            fragmentVehicalInfoBinding.imgNext.setEnabled(true);
            if(check) {
                UserInfo.getInstance().isVehicalInfoCompleted = false;
            }
            fragmentVehicalInfoBinding.imgNext.setImageResource(R.drawable.next_icon);
        }
    }

}