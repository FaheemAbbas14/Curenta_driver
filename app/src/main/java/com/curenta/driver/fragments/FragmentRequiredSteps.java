package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.LoginActivity;
import com.curenta.driver.MainApplication;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentRequiredStepsBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.DriverAPIResponse;
import com.curenta.driver.utilities.Helper;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Preferences;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FragmentRequiredSteps extends Fragment {
    FragmentRequiredStepsBinding fragmentRequiredStepsBinding;
    boolean isAllComplete = false;
    String heading = "Required Steps";
    ProgressDialog dialog;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((LoginActivity) getActivity()).moveToTop();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRequiredStepsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_required_steps, container, false);

        ((LoginActivity) getActivity()).showHeading(heading);
        if (UserInfo.getInstance().firstName != null) {
            fragmentRequiredStepsBinding.txtheading.setText("Hello, " + UserInfo.getInstance().firstName);
        }
        if (UserInfo.getInstance().isPersonalInfoCompleted) {
            fragmentRequiredStepsBinding.imgPI.setImageResource(R.drawable.checked_icon);
            fragmentRequiredStepsBinding.llyPersonalInfo.setEnabled(false);
            fragmentRequiredStepsBinding.llyPersonalInfo.setEnabled(false);
        }
        if (UserInfo.getInstance().isBackgroundCheckCompleted) {
            fragmentRequiredStepsBinding.imgBG.setImageResource(R.drawable.checked_icon);
            fragmentRequiredStepsBinding.llyBGCheck.setEnabled(false);
            fragmentRequiredStepsBinding.llyBGCheck.setEnabled(false);
        }
        if (UserInfo.getInstance().isBankDetailsCompleted) {
            fragmentRequiredStepsBinding.imgBD.setImageResource(R.drawable.checked_icon);
            fragmentRequiredStepsBinding.llyBankDetails.setEnabled(false);
            fragmentRequiredStepsBinding.llyBankDetails.setEnabled(false);
        }
        if (UserInfo.getInstance().isDriveingLicenseCompleted) {
            fragmentRequiredStepsBinding.imgDL.setImageResource(R.drawable.checked_icon);
            fragmentRequiredStepsBinding.llyDrivingLicense.setEnabled(false);
            fragmentRequiredStepsBinding.llyDrivingLicense.setEnabled(false);
        }
        if (UserInfo.getInstance().isHIPAACompleted) {
            fragmentRequiredStepsBinding.imgHP.setImageResource(R.drawable.checked_icon);
            fragmentRequiredStepsBinding.llyHipa.setEnabled(false);
            fragmentRequiredStepsBinding.llyHipa.setEnabled(false);
        }
        if (UserInfo.getInstance().isVehicalInfoCompleted) {
            fragmentRequiredStepsBinding.imgVI.setImageResource(R.drawable.checked_icon);
            fragmentRequiredStepsBinding.llyVehicalInfo.setEnabled(false);
            fragmentRequiredStepsBinding.llyVehicalInfo.setEnabled(false);
        }
        if (UserInfo.getInstance().isw9Completed && UserInfo.getInstance().isi9Completed) {
            fragmentRequiredStepsBinding.imgForms.setImageResource(R.drawable.checked_icon);
            fragmentRequiredStepsBinding.llyForms.setEnabled(false);
            fragmentRequiredStepsBinding.llyForms.setEnabled(false);
        }
        fragmentRequiredStepsBinding.llyPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentPersonalInfo());
            }
        });
        fragmentRequiredStepsBinding.llyDrivingLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentDrivingLicense());
            }
        });
        fragmentRequiredStepsBinding.llyVehicalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentVehicalInfo());
            }
        });
        fragmentRequiredStepsBinding.llyBGCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentBeckgroundCheck());
            }
        });
        fragmentRequiredStepsBinding.llyBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentBankDetails());
            }
        });
        fragmentRequiredStepsBinding.llyForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentForms());
            }
        });
        fragmentRequiredStepsBinding.llyHipa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).showFragment(new FragmentHIPAA());
            }
        });
        fragmentRequiredStepsBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((LoginActivity) getActivity()).showFragment(new FragmentCongratulation());
                RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
                createRegistrationRequest();
            }
        });
        ((LoginActivity) getActivity()).moveToTop();
        checkComplete();
        return fragmentRequiredStepsBinding.getRoot();
    }

    private void checkComplete() {
        UserInfo userInfo = UserInfo.getInstance();
        if (userInfo.isPersonalInfoCompleted && userInfo.isDriveingLicenseCompleted && userInfo.isHIPAACompleted  && userInfo.isBackgroundCheckCompleted && userInfo.isVehicalInfoCompleted) {
            fragmentRequiredStepsBinding.imgNext.setImageResource(R.drawable.next_icon);
            fragmentRequiredStepsBinding.imgNext.setEnabled(true);
            isAllComplete = true;
        } else {
            fragmentRequiredStepsBinding.imgNext.setEnabled(false);
            fragmentRequiredStepsBinding.imgNext.setImageResource(R.drawable.disabled_next);
            isAllComplete = false;
        }
        if(userInfo.deviceId.equalsIgnoreCase("test")){
            String deviceId = Helper.getDeviceId(getApplicationContext());
            UserInfo.getInstance().deviceId = deviceId;
        }
        if(userInfo.fcmToken.equalsIgnoreCase("test")){
            MainApplication.setupOnseSignal();
        }
    }

    public void createRegistrationRequest() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                UserInfo user = UserInfo.getInstance();
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                Log.d("userData", user.toString());
                MultipartBody.Part profilePicbody = null;

//
//                if (user.profilePicURI != null) {
//
//                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), user.profilePicURI);
//                    profilePicbody = MultipartBody.Part.createFormData("ProfileImage", user.profilePicURI.getName(), requestFile);
//
//                }
//
//
//
//                // MultipartBody.Part is used to send also the actual file name
//
//
//                MultipartBody.Part drivingLicensebody = MultipartBody.Part.createFormData("LicenseImage", user.drivingLicensePicURI.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
//                        user.drivingLicensePicURI));
//                MultipartBody.Part carInsurancebody = MultipartBody.Part.createFormData("CarInsuranceImage", user.carInsurancePicURI.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
//                        user.drivingLicensePicURI));
//                MultipartBody.Part carRegistrationbody = MultipartBody.Part.createFormData("CarRegistrationImage", user.carRegistrationPicURI.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
//                        user.carRegistrationPicURI));
                if (user.profilePicURI != null) {
                    File picFile;
                    if (!user.profilePicCamera) {
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                        // Get the cursor
//                        Cursor cursor = getActivity().getContentResolver().query(user.profilePicURI,
//                                filePathColumn, null, null, null);
//                        // Move to first row
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String imageDecodableString = cursor.getString(columnIndex);
//                        cursor.close();
//
//                        picFile = new File(imageDecodableString);
                        picFile = new File(user.profilePicURI.getPath());
                    } else {
                        picFile = new File(user.profilePicURI.getPath());
                    }
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), picFile);
                    profilePicbody = MultipartBody.Part.createFormData("ProfileImage", picFile.getName(), requestFile);

                }

                File drivingLicenseFile;
                if (!user.drivingLicensePicCamera) {
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                    // Get the cursor
//                    Cursor cursor = getActivity().getContentResolver().query(user.drivingLicensePicURI,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String imageDecodableString = cursor.getString(columnIndex);
//                    cursor.close();
//
//                    drivingLicenseFile = new File(imageDecodableString);
                    drivingLicenseFile = new File(user.drivingLicensePicURI.getPath());
                } else {
                    drivingLicenseFile = new File(user.drivingLicensePicURI.getPath());
                }
                File carInsuranceFile;
                if (!user.carInsurancePicCamera) {
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                    // Get the cursor
//                    Cursor cursor = getActivity().getContentResolver().query(user.carInsurancePicURI,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String imageDecodableString = cursor.getString(columnIndex);
//                    cursor.close();
//
//                    carInsuranceFile = new File(imageDecodableString);
                    carInsuranceFile = new File(user.carInsurancePicURI.getPath());

                } else {
                    carInsuranceFile = new File(user.carInsurancePicURI.getPath());
                }
                File carRegistrationFile;
                if (!user.carRegistrationPicCamera) {
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                    // Get the cursor
//                    Cursor cursor = getActivity().getContentResolver().query(user.carRegistrationPicURI,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String imageDecodableString = cursor.getString(columnIndex);
//                    cursor.close();
//
//                    carRegistrationFile = new File(imageDecodableString);
                    carRegistrationFile = new File(user.carRegistrationPicURI.getPath());
                } else {
                    carRegistrationFile = new File(user.carRegistrationPicURI.getPath());
                }
                // MultipartBody.Part is used to send also the actual file name


                MultipartBody.Part drivingLicensebody = MultipartBody.Part.createFormData("LicenseImage", drivingLicenseFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                        drivingLicenseFile));
                MultipartBody.Part carInsurancebody = MultipartBody.Part.createFormData("CarInsuranceImage", carInsuranceFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                        carInsuranceFile));
                MultipartBody.Part carRegistrationbody = MultipartBody.Part.createFormData("CarRegistrationImage", carRegistrationFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                        carRegistrationFile));
                RequestBody fname = RequestBody.create(MediaType.parse("text/plain"),
                        user.firstName);
                RequestBody lname = RequestBody.create(MediaType.parse("text/plain"),
                        user.lastName);
                RequestBody email = RequestBody.create(MediaType.parse("text/plain"),
                        user.userEmail);
                RequestBody phonenNumber = RequestBody.create(MediaType.parse("text/plain"),
                        user.phoneNumber);
                RequestBody password = RequestBody.create(MediaType.parse("text/plain"),
                        user.password);
                RequestBody DOB = RequestBody.create(MediaType.parse("text/plain"),
                        user.DOB);
                RequestBody gender = RequestBody.create(MediaType.parse("text/plain"),
                        user.gender);
                RequestBody street = RequestBody.create(MediaType.parse("text/plain"),
                        user.street);
                RequestBody state = RequestBody.create(MediaType.parse("text/plain"),
                        user.state);
                RequestBody city = RequestBody.create(MediaType.parse("text/plain"),
                        user.city);
                RequestBody zipcode = RequestBody.create(MediaType.parse("text/plain"),
                        user.zipcode);
                RequestBody licenseNo = RequestBody.create(MediaType.parse("text/plain"),
                        user.drivingLicenseNo);
                RequestBody carType = RequestBody.create(MediaType.parse("text/plain"),
                        user.carType);
                RequestBody carModel = RequestBody.create(MediaType.parse("text/plain"),
                        user.carModel);
                RequestBody carColor = RequestBody.create(MediaType.parse("text/plain"),
                        user.carColor);
                RequestBody bname = RequestBody.create(MediaType.parse("text/plain"),
                        user.bankACName);
                RequestBody acNumber = RequestBody.create(MediaType.parse("text/plain"),
                        user.bankACNumber);
                RequestBody rtNumber = RequestBody.create(MediaType.parse("text/plain"),
                        user.bankRTNumber);
                RequestBody ssNumber = RequestBody.create(MediaType.parse("text/plain"),
                        user.socialSecurityCode);
                RequestBody deviceID = RequestBody.create(MediaType.parse("text/plain"),
                        user.deviceId);
                RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"),
                        "" + user.latitude);
                RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"),
                        "" + user.longitude);
                RequestBody userIdRef = RequestBody.create(MediaType.parse("text/plain"),
                        user.userIdRef);
                RequestBody fcmToken = RequestBody.create(MediaType.parse("text/plain"),
                        user.fcmToken);
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentadispatcherURL);
                RetrofitClient.getAPIClient().registerDriver(profilePicbody, drivingLicensebody, carInsurancebody, carRegistrationbody, fname, lname, email, phonenNumber, password, DOB, gender, street, city, state, zipcode, licenseNo, carModel, carColor, ssNumber, bname, acNumber, rtNumber, longitude, latitude, userIdRef, fcmToken, deviceID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DriverAPIResponse>() {
                            @Override
                            public void onSuccess(DriverAPIResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1 && response.data != null) {
                                    LoggedInUser loggedInUser = LoggedInUser.getInstance();
                                    loggedInUser.copyData(response.data);
                                    loggedInUser.password=UserInfo.getInstance().password;
                                    Gson gson = new Gson();
                                    String loggedInUserGson = gson.toJson(loggedInUser);
                                    Preferences.getInstance().setString("loggedInUser", loggedInUserGson);
                                    Log.d("registrationAPICall", "success " + response.toString());
                                    UserInfo.getInstance().instance=null;
                                    try {
                                    for (int i = 0; i <getActivity().getSupportFragmentManager().getBackStackEntryCount() ; i++) {

                                            if (getActivity()!=null && getActivity().getSupportFragmentManager() != null) {
                                                getActivity().getSupportFragmentManager().popBackStack();
                                            }

                                    }
                                    } catch(IllegalStateException ex) {

                                    }
                                    catch(Exception ex) {

                                    }
                                    if(user.w9Uri!=null){
                                        deleteForms(user.w9Uri);
                                    }
                                    if(user.i9Uri!=null){
                                        deleteForms(user.i9Uri);
                                    }
                                    if(user.profilePicURI!=null){
                                        deleteForms(user.profilePicURI);
                                    }
                                    if(user.drivingLicensePicURI!=null){
                                        deleteForms(user.drivingLicensePicURI);
                                    }
                                    if(user.carInsurancePicURI!=null){
                                        deleteForms(user.carInsurancePicURI);
                                    }
                                    if(user.carRegistrationPicURI!=null){
                                        deleteForms(user.carRegistrationPicURI);
                                    }


                                    ((LoginActivity) getActivity()).showFragment(new FragmentCongratulation());
//                                    Intent nextIntent = new Intent(getApplicationContext(), DashboardActivity.class);
//                                    startActivity(nextIntent);
//                                    getActivity().finish();
                                    //  Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();


                                } else {
                                    Log.d("registrationAPICall", "fail " + response.toString());
                                    Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                dialog.dismiss();
                                Log.d("registrationAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Sign up failed please try again later", Toast.LENGTH_SHORT).show();
            FirebaseCrashlytics.getInstance().recordException(e);
            dialog.dismiss();
            Log.d("registrationAPICall", "failed " + e.toString());
        }
    }

    private void deleteForms(Uri uri) {
        File fdelete = new File(uri.getPath());
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + uri.getPath());
            } else {
                System.out.println("file not Deleted :" + uri.getPath());
            }
        }
    }

}