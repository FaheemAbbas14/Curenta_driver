package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentNewPasswordBinding;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.ChangePasswordRequest;
import com.curenta.driver.retrofit.apiDTO.ChangePasswordResponse;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Preferences;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FragmentNewPassword extends Fragment {

    FragmentNewPasswordBinding fragmentNewPasswordBinding;
    boolean isPasswordValid;
    String heading = "Password Reset";
    ProgressDialog dialog;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNewPasswordBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_new_password, container, false);
        ((LoginActivity) getActivity()).showHeading(heading);
        fragmentNewPasswordBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });

        return fragmentNewPasswordBinding.getRoot();
    }

    public void SetValidation() {
        // Check for a valid password.
        if (fragmentNewPasswordBinding.edtPassword.getText().toString().isEmpty()) {
            fragmentNewPasswordBinding.txtPasswordError.setText(getResources().getString(R.string.password_error));
            fragmentNewPasswordBinding.txtPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        } else if (fragmentNewPasswordBinding.edtPassword.getText().length() < 6) {
            fragmentNewPasswordBinding.txtPasswordError.setText(getResources().getString(R.string.error_invalid_password));
            fragmentNewPasswordBinding.txtPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        } else {
            fragmentNewPasswordBinding.txtPasswordError.setText("");
        }
        if (fragmentNewPasswordBinding.edtCnfPassword.getText().toString().isEmpty()) {
            fragmentNewPasswordBinding.txtcNFPasswordError.setText(getResources().getString(R.string.password_error));
            fragmentNewPasswordBinding.txtcNFPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        } else if (fragmentNewPasswordBinding.edtCnfPassword.getText().length() < 6) {
            fragmentNewPasswordBinding.txtcNFPasswordError.setText(getResources().getString(R.string.error_invalid_password));
            fragmentNewPasswordBinding.txtcNFPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            fragmentNewPasswordBinding.txtcNFPasswordError.setText("");
        }
        if ( isPasswordValid && !fragmentNewPasswordBinding.edtPassword.getText().toString().equals(fragmentNewPasswordBinding.edtCnfPassword.getText().toString())) {
            fragmentNewPasswordBinding.txtcNFPasswordError.setText(getResources().getString(R.string.error_mismatch_password));
            fragmentNewPasswordBinding.txtcNFPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        }
        if (isPasswordValid) {
            changePassword(UserInfo.getInstance().userEmail,fragmentNewPasswordBinding.edtPassword.getText().toString());

        }

    }
    private void changePassword(String email, String password) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
                ChangePasswordRequest requestDTO = new ChangePasswordRequest(email, password);
                Gson gson = new Gson();
                String request = gson.toJson(requestDTO);
                RetrofitClient.getAPIClient().changePassword(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ChangePasswordResponse>() {
                            @Override
                            public void onSuccess(ChangePasswordResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {
                                    FragmentThankYou fragmentThankYou = new FragmentThankYou();
                                    fragmentThankYou.pictureType = EnumPictureType.RESET_PASSWORD;
                                    ((LoginActivity) getActivity()).showFragment(fragmentThankYou);
                                   // Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();


                                } else {
                                    Log.d("changePasswordAPICall", "fail " + response);
                                    Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("changePasswordAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("changePasswordAPICall", "failed " + e.toString());
        }
    }
}