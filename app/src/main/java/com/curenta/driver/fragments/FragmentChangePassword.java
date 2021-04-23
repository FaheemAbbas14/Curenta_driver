package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentChangePasswordBinding;
import com.curenta.driver.dto.LoggedInUser;
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


public class FragmentChangePassword extends Fragment {

    FragmentChangePasswordBinding fragmentChangePasswordBinding;
    boolean isPasswordValid,isOldPasswordValid;
    ProgressDialog dialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChangePasswordBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_change_password, container, false);
        fragmentChangePasswordBinding.header.txtLabel.setText("Password Reset");
        fragmentChangePasswordBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        fragmentChangePasswordBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });

        return fragmentChangePasswordBinding.getRoot();
    }

    public void SetValidation() {
        LoggedInUser loggedInUser = LoggedInUser.getInstance();
        // Check for a valid password.
        if (fragmentChangePasswordBinding.edtoldPassword.getText().toString().isEmpty()) {
            fragmentChangePasswordBinding.txtoldPasswordError.setText(getResources().getString(R.string.password_error));
            isOldPasswordValid = false;
        } else if (!fragmentChangePasswordBinding.edtoldPassword.getText().toString().equals(loggedInUser.password)) {
            fragmentChangePasswordBinding.txtoldPasswordError.setText("Invalid Old password");
            isOldPasswordValid = false;
        } else {
            isOldPasswordValid=true;
            fragmentChangePasswordBinding.txtoldPasswordError.setText("");
        }
        if (fragmentChangePasswordBinding.edtPassword.getText().toString().isEmpty()) {
            fragmentChangePasswordBinding.txtPasswordError.setText(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (fragmentChangePasswordBinding.edtPassword.getText().length() < 6) {
            fragmentChangePasswordBinding.txtPasswordError.setText(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else if (fragmentChangePasswordBinding.edtPassword.getText().toString().equals(fragmentChangePasswordBinding.edtoldPassword.getText().toString())) {
            fragmentChangePasswordBinding.txtPasswordError.setText(getResources().getString(R.string.error_old_password));
            isPasswordValid = false;
        } else if (!fragmentChangePasswordBinding.edtPassword.getText().toString().equals(fragmentChangePasswordBinding.edtCnfPassword.getText().toString())) {
            fragmentChangePasswordBinding.txtPasswordError.setText(getResources().getString(R.string.error_mismatch_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            fragmentChangePasswordBinding.txtPasswordError.setText("");
        }
        if (isPasswordValid && isOldPasswordValid) {
            changePassword(LoggedInUser.getInstance().email, fragmentChangePasswordBinding.edtPassword.getText().toString());
            //Toast.makeText(getActivity().getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
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
                                    LoggedInUser loggedInUser = LoggedInUser.getInstance();
                                    loggedInUser.password = password;
                                    Gson gson = new Gson();
                                    String loggedInUserGson = gson.toJson(loggedInUser);
                                    Preferences.getInstance().setString("loggedInUser", loggedInUserGson);
                                    Log.d("changePasswordAPICall", "success " + response.toString());

                                    Toast.makeText(getActivity().getApplicationContext(), "Password successfully updated", Toast.LENGTH_SHORT).show();


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