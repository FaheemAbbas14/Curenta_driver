package com.curenta.driver.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.databinding.FragmentMyAccountBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.dto.UserInfo;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.DriverAPIResponse;
import com.curenta.driver.retrofit.apiDTO.EditProfileRequest;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.ImageConverter;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.JsonLoader;
import com.curenta.driver.utilities.Preferences;
import com.curenta.driver.utilities.Utility;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class FragmentMyAccount extends Fragment {
    FragmentMyAccountBinding fragmentMyAccountBinding;
    HashMap<String, ArrayList<String>> states_hashmap = new HashMap<>();
    ArrayList<String> states_list = new ArrayList<>();
    private static final int CAPTURE_PICCODE = 989;
    Bitmap bitmap;
    Uri imageUri;
    ProgressDialog dialog;
    boolean isFromCamera;
    String state;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMyAccountBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_my_account, container, false);
        fragmentMyAccountBinding.header.txtLabel.setText("My Account");
        dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        LoggedInUser user = LoggedInUser.getInstance();
        if (user.profileImagePath != null) {
            Glide.with(this).load(user.profileImagePath).circleCrop().into(fragmentMyAccountBinding.imgProfilePic);
        }
        fragmentMyAccountBinding.txtfName.setText(user.fname);
        fragmentMyAccountBinding.txtlastName.setText(user.lname);
        fragmentMyAccountBinding.txtPhone.setText(user.mobile);
        fragmentMyAccountBinding.txtEmail.setText(user.email);
        fragmentMyAccountBinding.txtStreet.setText(user.street);
        fragmentMyAccountBinding.txtZipcode.setText(user.zipcode);
        fragmentMyAccountBinding.txtVehicleModel.setText(user.vehicleModel);
        fragmentMyAccountBinding.txtVehicleColor.setText(user.vehiclecolor);
        fragmentMyAccountBinding.txtCity.setText(user.city);
        processCities();
        fragmentMyAccountBinding.txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.getInstance().addFragment(getActivity(), new FragmentChangePassword(), R.id.fragContainer);
            }
        });
        fragmentMyAccountBinding.txtfName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkChange();
            }
        });

        fragmentMyAccountBinding.txtlastName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkChange();
            }
        });
        fragmentMyAccountBinding.txtPhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkChange();
            }
        });
        fragmentMyAccountBinding.txtStreet.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkChange();
            }
        });
        fragmentMyAccountBinding.txtZipcode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkChange();
            }
        });
        fragmentMyAccountBinding.txtVehicleModel.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkChange();
            }
        });
        fragmentMyAccountBinding.txtVehicleColor.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkChange();
            }
        });
        fragmentMyAccountBinding.txtCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkChange();
            }
        });
        fragmentMyAccountBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getActivity().getSupportFragmentManager() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch(IllegalStateException ex) {

                }
                catch(Exception ex) {

                }
            }
        });
        fragmentMyAccountBinding.llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                //cameraIntent();
            }
        });
        fragmentMyAccountBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = fragmentMyAccountBinding.txtPhone.getText().toString();
                phoneNumber = phoneNumber.replaceAll("\\)", "\\) ");
                UserInfo.getInstance().firstName = fragmentMyAccountBinding.txtfName.getText().toString();
                UserInfo.getInstance().lastName = fragmentMyAccountBinding.txtlastName.getText().toString();
                UserInfo.getInstance().phoneNumber = phoneNumber;
                UserInfo.getInstance().street = fragmentMyAccountBinding.txtStreet.getText().toString();
                UserInfo.getInstance().zipcode = fragmentMyAccountBinding.txtZipcode.getText().toString();
                UserInfo.getInstance().carModel = fragmentMyAccountBinding.txtVehicleModel.getText().toString();
                UserInfo.getInstance().carColor = fragmentMyAccountBinding.txtVehicleColor.getText().toString();
                UserInfo.getInstance().city = fragmentMyAccountBinding.txtCity.getText().toString();
                if (imageUri != null) {
                    dialog.show();
                    uploadProfilePic();
                } else {
                    dialog.show();
                    updateProfile();
                }
            }
        });
        return fragmentMyAccountBinding.getRoot();
    }

    private void processCities() {
        states_list.clear();
        states_hashmap.clear();
        try {
            JSONArray data = new JSONArray(JsonLoader.loadJSONFromAsset(getActivity(), "us_states_and_cities.json"));
            for (int i = 0; i < data.length(); i++) {
                JSONObject state = data.getJSONObject(i);
                String state_name = state.getString("state");
                states_list.add(state_name);
                JSONArray citiesArray = state.getJSONArray("cities");
                ArrayList<String> cities = new ArrayList<>();
                for (int c = 0; c < citiesArray.length(); c++) {
                    cities.add((String) citiesArray.get(c));
                }
                states_hashmap.put(state_name, cities);

            }
            initStatesDropdown();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initStatesDropdown() {
        // Spinner click listener
        fragmentMyAccountBinding.txtState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = parent.getItemAtPosition(position).toString();
                checkChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinneritem_main, states_list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        fragmentMyAccountBinding.txtState.setAdapter(dataAdapter);
        int index = dataAdapter.getPosition(LoggedInUser.getInstance().state);
        fragmentMyAccountBinding.txtState.setSelection(index);
    }

//    private void initCitiesDropdown(String state) {
//        // Spinner click listener
//        fragmentMyAccountBinding.txtCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = parent.getItemAtPosition(position).toString();
//                UserInfo.getInstance().city = item;
//
//            }
//
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        // Creating adapter for spinner
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinneritem_main, states_hashmap.get(state));
//
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
//
//        // attaching data adapter to spinner
//        fragmentMyAccountBinding.txtCity.setAdapter(dataAdapter);
//        int index = dataAdapter.getPosition(LoggedInUser.getInstance().city);
//        fragmentMyAccountBinding.txtCity.setSelection(index);
//    }

    private void cameraIntent() {
        boolean result = Utility.checkPermission(getContext());
        if (result) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,
                    CAPTURE_PICCODE);
        }


    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Upload Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    isFromCamera = true;
                    cameraIntent();
                } else if (options[item].equals("Choose from Gallery")) {
                    isFromCamera = false;
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_PICCODE) {
                if (resultCode == Activity.RESULT_OK) {
                    dialog.show();
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    //imageUri = getImageUri(getContext(), bmp);
                    saveBitmap(bmp);


                }
            } else if (requestCode == 2) {
                dialog.show();
                Uri imageUritemp = data.getData();

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUritemp);
                if (bitmap != null) {
                    saveBitmap(bitmap);


                }


            }
        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(getContext(), "Please select a image", Toast.LENGTH_SHORT).show();
        }
    }


    public void uploadProfilePic() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
                LoggedInUser user = LoggedInUser.getInstance();

                File selfiePic = new File(imageUri.getPath());

                MultipartBody.Part selfiebody = MultipartBody.Part.createFormData("ProfilePicture", selfiePic.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                        selfiePic));
                RequestBody driverId = RequestBody.create(MediaType.parse("text/plain"),
                        "" + user.driverId);
                Log.d("profilrPicAPICall", " driver id " + user.driverId);
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentadispatcherURL);
                RetrofitClient.getAPIClient().uploadProfilePic(selfiebody, driverId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DriverAPIResponse>() {
                            @Override
                            public void onSuccess(DriverAPIResponse response) {

                                if (response.responseCode == 1) {
                                    Log.d("profilrPicAPICall", "success " + response.toString());
                                    //  Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    LoggedInUser.getInstance().profileImagePath = (String) response.data.profileImagePath;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.with(getContext()).load(user.profileImagePath).circleCrop().into(fragmentMyAccountBinding.imgProfilePic);
                                            //Toast.makeText(getActivity().getApplicationContext(), "Profile Image Changed", Toast.LENGTH_SHORT).show();
                                            ((DashboardActivity) getActivity()).loadProfilePic();}
                                    });

                                    Gson gson = new Gson();
                                    String loggedInUserGson = gson.toJson(LoggedInUser.getInstance());
                                    Preferences.getInstance().setString("loggedInUser", loggedInUserGson);
                                    updateProfile();
                                } else {
                                    dialog.dismiss();
                                    Log.d("profilrPicAPICall", "fail " + response.toString());
                                    Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("profilrPicAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                dialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            dialog.dismiss();

            Log.d("profilrPicAPICall", "failed " + e.toString());
        }
    }

    private void updateProfile() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                RetrofitClient.changeApiBaseUrl(BuildConfig.logindevURL);
                EditProfileRequest requestdto = new EditProfileRequest(LoggedInUser.getInstance().driverId, UserInfo.getInstance().firstName, UserInfo.getInstance().lastName, UserInfo.getInstance().phoneNumber, UserInfo.getInstance().street, UserInfo.getInstance().city, UserInfo.getInstance().state, UserInfo.getInstance().zipcode, UserInfo.getInstance().carModel, UserInfo.getInstance().carColor);
                Gson gson = new Gson();
                String request = gson.toJson(requestdto);
                RetrofitClient.getAPIClient().updateProfile(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DriverAPIResponse>() {
                            @Override
                            public void onSuccess(DriverAPIResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {
                                    Log.d("updateProfileAPICall", "success " + response);
                                    LoggedInUser.getInstance().fname = UserInfo.getInstance().firstName;
                                    LoggedInUser.getInstance().lname = UserInfo.getInstance().lastName;
                                    LoggedInUser.getInstance().mobile = UserInfo.getInstance().phoneNumber;
                                    LoggedInUser.getInstance().street = UserInfo.getInstance().street;
                                    LoggedInUser.getInstance().zipcode = UserInfo.getInstance().zipcode;
                                    LoggedInUser.getInstance().vehicleModel = UserInfo.getInstance().carModel;
                                    LoggedInUser.getInstance().vehiclecolor = UserInfo.getInstance().carColor;
                                    LoggedInUser.getInstance().city = UserInfo.getInstance().city;
                                    LoggedInUser.getInstance().state = UserInfo.getInstance().state;
                                    LoggedInUser.getInstance().profileImagePath = (String) response.data.profileImagePath;
                                    Gson gson = new Gson();
                                    String loggedInUserGson = gson.toJson(LoggedInUser.getInstance());
                                    Preferences.getInstance().setString("loggedInUser", loggedInUserGson);
                                    Toast.makeText(getActivity().getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                    UserInfo.instance = null;

                                } else {
                                    Log.d("updateProfileAPICall", "fail " + response);
                                    Toast.makeText(getActivity().getApplicationContext(), "Updating profile failed", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("updateProfileAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                dialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            dialog.dismiss();
            Log.d("updateProfileAPICall", "failed " + e.toString());
        }
    }

    public void checkChange() {
        boolean isAnyChange = false;
        LoggedInUser user=LoggedInUser.getInstance();
        String phoneNumber = LoggedInUser.getInstance().mobile;
        phoneNumber = phoneNumber.replaceAll("\\) ", "\\)");
        if (!LoggedInUser.getInstance().fname.equalsIgnoreCase(fragmentMyAccountBinding.txtfName.getText().toString())) {
            isAnyChange = true;
        }
        if (!LoggedInUser.getInstance().lname.equalsIgnoreCase(fragmentMyAccountBinding.txtlastName.getText().toString())) {
            isAnyChange = true;
        }
        if (!phoneNumber.equalsIgnoreCase(fragmentMyAccountBinding.txtPhone.getText().toString())) {
            isAnyChange = true;
        }
        if (!LoggedInUser.getInstance().street.equalsIgnoreCase(fragmentMyAccountBinding.txtStreet.getText().toString())) {
            isAnyChange = true;
        }
        if (!user.state.equalsIgnoreCase(state)) {
            UserInfo.getInstance().state = state;
            isAnyChange = true;
        }
        if (!LoggedInUser.getInstance().city.equalsIgnoreCase(fragmentMyAccountBinding.txtCity.getText().toString())) {
            isAnyChange = true;
        }
        if (!LoggedInUser.getInstance().zipcode.equalsIgnoreCase(fragmentMyAccountBinding.txtZipcode.getText().toString())) {
            isAnyChange = true;
        }
        if (!LoggedInUser.getInstance().vehicleModel.equalsIgnoreCase(fragmentMyAccountBinding.txtVehicleModel.getText().toString())) {
            isAnyChange = true;
        }
        if (!LoggedInUser.getInstance().vehiclecolor.equalsIgnoreCase(fragmentMyAccountBinding.txtVehicleColor.getText().toString())) {
            isAnyChange = true;
        }
        if (imageUri != null) {
            isAnyChange = true;
        }
        if (isAnyChange) {
            fragmentMyAccountBinding.btnSave.setBackgroundResource(R.drawable.blue_rounded);
            fragmentMyAccountBinding.btnSave.setEnabled(true);
        } else {
            fragmentMyAccountBinding.btnSave.setBackgroundResource(R.drawable.grey_rounded);
            fragmentMyAccountBinding.btnSave.setEnabled(false);
        }
    }
    private void saveBitmap(Bitmap bmp) {

        DisposableObserver<Object> over = Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return new Object();
            }
        }).observeOn(Schedulers.io()).subscribeWith(new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                try {

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();


                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                            byteArray.length);
                    if (AppElement.cw == null) {
                        AppElement.cw = new ContextWrapper(getActivity());
                    }
                    File directory = AppElement.cw.getDir("imageDir", Context.MODE_PRIVATE);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
                    String timeStamp = dateFormat.format(new Date());
                    String imageFileName = "picture_" + timeStamp + ".jpg";
                    File file = new File(directory, imageFileName);
                    imageUri = Uri.fromFile(file);
                    if (!file.exists()) {
                        Log.d("path", file.toString());
                        FileOutputStream fos = null;

                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        fos.flush();
                        fos.close();

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkChange();
                            Bitmap circularBitmap = ImageConverter.getCroppedBitmap(bitmap);
                            fragmentMyAccountBinding.imgProfilePic.setImageBitmap(circularBitmap);
                        }
                    });
                    dialog.dismiss();
                } catch (Exception e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();

            }

            @Override
            public void onComplete() {

            }
        });

    }
}