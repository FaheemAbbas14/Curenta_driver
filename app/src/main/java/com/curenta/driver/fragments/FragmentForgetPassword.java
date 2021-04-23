package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentForgetPasswordBinding;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.OTPRequest;
import com.curenta.driver.retrofit.apiDTO.OTPResponse;
import com.curenta.driver.utilities.InternetChecker;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FragmentForgetPassword extends Fragment {
    FragmentForgetPasswordBinding fragmentForgetPasswordBinding;
    boolean isPhoneValid;
    String heading = "Password Reset";
    ProgressDialog dialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentForgetPasswordBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_forget_password, container, false);

        fragmentForgetPasswordBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });
//        fragmentForgetPasswordBinding.edtPhoneEmail.addTextChangedListener(new TextWatcher() {
//            int length_before = 0;
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                length_before = s.length();
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (length_before < s.length()) {
//
//                    if (s.length() > 0) {
//                        if (Character.isDigit(s.charAt(0)))
//                            s.insert(0, "(");
//                    }
//                    if (s.length() > 4) {
//                        if (Character.isDigit(s.charAt(4)))
//                            s.insert(4, ") ");
//                    }
//                    if (s.length() > 9) {
//                        if (Character.isDigit(s.charAt(9)))
//                            s.insert(9, "-");
//                    }
//                }
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//
//        });
        ((LoginActivity) getActivity()).showHeading(heading);
        return fragmentForgetPasswordBinding.getRoot();
    }

    public void SetValidation() {
        // Check for a valid phone.
        String input = fragmentForgetPasswordBinding.edtPhoneEmail.getText().toString();

        if (fragmentForgetPasswordBinding.edtPhoneEmail.getText().toString().isEmpty()) {
            fragmentForgetPasswordBinding.txtPhoneError.setText(getResources().getString(R.string.email_error));
            isPhoneValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(fragmentForgetPasswordBinding.edtPhoneEmail.getText().toString()).matches()) {
            fragmentForgetPasswordBinding.txtPhoneError.setText(getResources().getString(R.string.email_error));
            isPhoneValid = false;
        } else {
            UserInfo.getInstance().userEmail = input;
            isPhoneValid = true;
            fragmentForgetPasswordBinding.txtPhoneError.setText("");
        }


        if (isPhoneValid) {
            getVerificationCode(input);
        }

    }

    private void getVerificationCode(String email) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);

                dialog.show();
                RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
                OTPRequest requestDto = new OTPRequest(email);
                Gson gson = new Gson();
                String request = gson.toJson(requestDto);
                RetrofitClient.getAPIClient().getVerificationCode(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OTPResponse>() {
                            @Override
                            public void onSuccess(OTPResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {
                                    String verificationCode = response.data.otp;
                                    Log.d("verificationCodeAPICall", "verificationCode " + verificationCode);
                                    FragmentSecurityCodeVerification fragmentSecurityCodeVerification = new FragmentSecurityCodeVerification();
                                    fragmentSecurityCodeVerification.verificationCode = verificationCode;
                                    ((LoginActivity) getActivity()).showFragment(fragmentSecurityCodeVerification);
                                    // Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();


                                } else {
                                    Log.d("verificationCodeAPICall", "fail " + response);
                                    Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("verificationCodeAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("verificationCodeAPICall", "failed " + e.toString());
        }
    }
}