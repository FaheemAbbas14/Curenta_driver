package com.curenta.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentSecurityCodeVerificationBinding;


public class FragmentSecurityCodeVerification extends Fragment {
    public String verificationCode;
    FragmentSecurityCodeVerificationBinding fragmentSecurityCodeVerificationBinding;
    boolean isOTPValid;
    String heading = "Password Reset";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSecurityCodeVerificationBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_security_code_verification, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        fragmentSecurityCodeVerificationBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });
        return fragmentSecurityCodeVerificationBinding.getRoot();
    }

    public void SetValidation() {
        // Check for a valid otp.
        if (fragmentSecurityCodeVerificationBinding.edtCode.getText().toString().isEmpty()) {
            fragmentSecurityCodeVerificationBinding.txtCodeError.setText(getResources().getString(R.string.otp_error));
            isOTPValid = false;
        } else if (!fragmentSecurityCodeVerificationBinding.edtCode.getText().toString().equalsIgnoreCase(verificationCode)) {
            fragmentSecurityCodeVerificationBinding.txtCodeError.setText(getResources().getString(R.string.error_invalid_otp));
            isOTPValid = false;
        } else {
            isOTPValid = true;
            fragmentSecurityCodeVerificationBinding.txtCodeError.setText("");
        }
        if (isOTPValid) {
            ((LoginActivity) getActivity()).showFragment(new FragmentNewPassword());
        }

    }
}