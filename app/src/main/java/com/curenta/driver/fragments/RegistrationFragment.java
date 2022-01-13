package com.curenta.driver.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.LoginActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentRegistrationBinding;
import com.curenta.driver.dto.Regex;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.interfaces.ILocationChange;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.CheckEmailResponse;
import com.curenta.driver.utilities.GPSTracker;
import com.curenta.driver.utilities.InternetChecker;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;


public class RegistrationFragment extends Fragment {
    FragmentRegistrationBinding fragmentRegistrationBinding;
    boolean isNameValid, isEmailValid, isPhoneValid, isPasswordValid, isDuplicateEmail = false;
    String heading = "Registration";
    ProgressDialog dialog;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();
        getLocation();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRegistrationBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_registration, container, false);
        ((LoginActivity) getActivity()).showHeading(heading);
        initData();
        fragmentRegistrationBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentRegistrationBinding.edtEmail.getText().toString().length() > 0) {
                    RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
                    doEmailCheck(fragmentRegistrationBinding.edtEmail.getText().toString());
                } else {
                    SetValidation();
                }
            }
        });
        fragmentRegistrationBinding.edtPhone.addTextChangedListener(new TextWatcher() {
            int length_before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length_before = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length_before < s.length()) {

                    if (s.length() > 0) {
                        if (Character.isDigit(s.charAt(0)))
                            s.insert(0, "(");
                    }
                    if (s.length() > 4) {
                        if (Character.isDigit(s.charAt(4)))
                            s.insert(4, ") ");
                    }
                    if (s.length() > 9) {
                        if (Character.isDigit(s.charAt(9)))
                            s.insert(9, "-");
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


        });
        fragmentRegistrationBinding.edtFName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable et) {
                String s=et.toString();
                if((!s.equals(s.toUpperCase()) && s.length()==1)) {
                    s=s.toUpperCase();
                    fragmentRegistrationBinding.edtFName.setText(s);
                    fragmentRegistrationBinding.edtFName.setSelection(s.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        fragmentRegistrationBinding.edtLName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable et) {
                String s=et.toString();
                if((!s.equals(s.toUpperCase()) && s.length()==1)) {
                    s=s.toUpperCase();
                    fragmentRegistrationBinding.edtLName.setText(s);
                    fragmentRegistrationBinding.edtLName.setSelection(s.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        // Inflate the layout for this fragment
        return fragmentRegistrationBinding.getRoot();
    }

    private void initData() {
        if (UserInfo.getInstance().firstName != null) {
            fragmentRegistrationBinding.edtFName.setText(UserInfo.getInstance().firstName);
        }
        if (UserInfo.getInstance().lastName != null) {
            fragmentRegistrationBinding.edtLName.setText(UserInfo.getInstance().lastName);
        }
        if (UserInfo.getInstance().userEmail != null) {
            fragmentRegistrationBinding.edtEmail.setText(UserInfo.getInstance().userEmail);
        }
        initGPS();
    }

    public void SetValidation() {
        // Check for a valid name.
        String phoneNumber = fragmentRegistrationBinding.edtPhone.getText().toString();
        if (fragmentRegistrationBinding.edtFName.getText().toString().isEmpty()) {
            fragmentRegistrationBinding.txtFNameError.setText(getResources().getString(R.string.fname_error));
            isNameValid = false;
        } else if (!Regex.isValid(fragmentRegistrationBinding.edtFName.getText().toString(), Regex.nameRegex)) {
            fragmentRegistrationBinding.txtFNameError.setText(getResources().getString(R.string.error_invalid_fname));
            isNameValid = false;
        } else {
            isNameValid = true;
            fragmentRegistrationBinding.txtFNameError.setText("");
        }

        if (fragmentRegistrationBinding.edtLName.getText().toString().isEmpty()) {
            fragmentRegistrationBinding.txtLNameError.setText(getResources().getString(R.string.lname_error));
            isNameValid = false;
        } else if (!Regex.isValid(fragmentRegistrationBinding.edtLName.getText().toString(), Regex.nameRegex)) {
            fragmentRegistrationBinding.txtLNameError.setText(getResources().getString(R.string.error_invalid_lname));
            isNameValid = false;
        } else {
            isNameValid = true;
            fragmentRegistrationBinding.txtLNameError.setText("");
        }
        // Check for a valid email address.
        if (fragmentRegistrationBinding.edtEmail.getText().toString().isEmpty()) {
            fragmentRegistrationBinding.txtEmailError.setText(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(fragmentRegistrationBinding.edtEmail.getText().toString()).matches()) {
            fragmentRegistrationBinding.txtEmailError.setText(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else if (isDuplicateEmail) {
            isEmailValid = false;
            fragmentRegistrationBinding.txtEmailError.setText("Email already registered");
        } else {
            isEmailValid = true;
            fragmentRegistrationBinding.txtEmailError.setText("");
        }

        // Check for a valid phone.
        if (fragmentRegistrationBinding.edtPhone.getText().toString().isEmpty()) {
            fragmentRegistrationBinding.txtPhoneError.setText(getResources().getString(R.string.phone_error));
            isPhoneValid = false;
        } else if (!Regex.isValid(phoneNumber, Regex.phoneNumber)) {
            fragmentRegistrationBinding.txtPhoneError.setText(getResources().getString(R.string.error_invalid_phone));
            isPhoneValid = false;
        } else {
            isPhoneValid = true;
            fragmentRegistrationBinding.txtPhoneError.setText("");
        }

        // Check for a valid password.
        if (fragmentRegistrationBinding.edtPassword.getText().toString().isEmpty()) {
            fragmentRegistrationBinding.txtPasswordError.setText(getResources().getString(R.string.password_error));
            fragmentRegistrationBinding.txtPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        } else if (fragmentRegistrationBinding.edtPassword.getText().length() < 6) {
            fragmentRegistrationBinding.txtPasswordError.setText(getResources().getString(R.string.error_invalid_password));
            fragmentRegistrationBinding.txtPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        }
        if (fragmentRegistrationBinding.edtCnfPassword.getText().toString().isEmpty()) {
            fragmentRegistrationBinding.txtcNFPasswordError.setText(getResources().getString(R.string.password_error));
            fragmentRegistrationBinding.txtcNFPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        } else if (fragmentRegistrationBinding.edtCnfPassword.getText().length() < 6) {
            fragmentRegistrationBinding.txtcNFPasswordError.setText(getResources().getString(R.string.error_invalid_password));
            fragmentRegistrationBinding.txtcNFPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        } else if (!fragmentRegistrationBinding.edtPassword.getText().toString().equals(fragmentRegistrationBinding.edtCnfPassword.getText().toString())) {
            fragmentRegistrationBinding.txtPasswordError.setText(getResources().getString(R.string.error_mismatch_password));
            fragmentRegistrationBinding.txtPasswordError.setTextColor(Color.parseColor("#ff3366"));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            fragmentRegistrationBinding.txtPasswordError.setText("");
        }
//        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//        if (telephonyManager != null) {
//            UserInfo.getInstance().deviceId = telephonyManager.getDeviceId();
//        }
        getLocation();
        if (isEmailValid && isPasswordValid && isNameValid && isPhoneValid) {
            UserInfo.getInstance().firstName = fragmentRegistrationBinding.edtFName.getText().toString();
            UserInfo.getInstance().lastName = fragmentRegistrationBinding.edtLName.getText().toString();
            UserInfo.getInstance().userEmail = fragmentRegistrationBinding.edtEmail.getText().toString();
            UserInfo.getInstance().phoneNumber = phoneNumber;
            UserInfo.getInstance().password = fragmentRegistrationBinding.edtPassword.getText().toString();
            ((LoginActivity) getActivity()).showFragment(new FragmentRequiredSteps());
        }
   //   ((LoginActivity) getActivity()).showFragment(new FragmentRequiredSteps());
    }

    public void initGPS() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 101);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    public void getLocation() {
        GPSTracker gpsTracker = new GPSTracker(getActivity(), new ILocationChange() {
            @Override
            public void locationChanged(Location location) {
                Log.d("location register", "changed");
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                UserInfo.getInstance().latitude = latitude;
                UserInfo.getInstance().longitude = longitude;
            }
        });
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }

    }

    private void doEmailCheck(String email) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentadispatcherURL);
                RetrofitClient.getAPIClient().CheckEmailExists(email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CheckEmailResponse>() {
                            @Override
                            public void onSuccess(CheckEmailResponse response) {

                                if (response.user != null) {

                                    Log.d("loginAPICall", "success " + response);
                                    isDuplicateEmail = true;

                                } else {
                                    isDuplicateEmail = false;
                                    Log.d("loginAPICall", "fail " + response);

                                }
                                SetValidation();
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                isDuplicateEmail = false;
                                SetValidation();
                                Log.d("loginAPICall", "failed " + e.toString());
                                dialog.dismiss();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("loginAPICall", "failed " + e.toString());
        }
    }
}