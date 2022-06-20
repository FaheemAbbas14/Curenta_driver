package com.curenta.driver.fragments;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.CustomSpinnerAdopter;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentConfirmDeliveryDetailsBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.ConfirmOrderResponse;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.InternetChecker;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class FragmentConfirmDeliveryDetails extends Fragment {
    public int routeIndex;
    FragmentConfirmDeliveryDetailsBinding fragmentConfirmDeliveryDetailsBinding;
    public EnumPictureType enumPictureType;
    public String routeId;
    public RideDetailListAdapter.Order order;
    ArrayList<Bitmap> images;
    ArrayList<Uri> imagesURIs;
    public int index = 0;
    String whoOrder = "", relation = "";
    ProgressDialog dialog;
    ArrayList<String> whoOrder_list = new ArrayList<>();
    ArrayList<String> relation_list = new ArrayList<>();
    TimePickerDialog mTimePicker;
    private String deliveryTime;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentConfirmDeliveryDetailsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_confirm_delivery_details, container, false);
        fragmentConfirmDeliveryDetailsBinding.header.imageView3.setVisibility(View.INVISIBLE);
        fragmentConfirmDeliveryDetailsBinding.header.txtLabel.setText("Delivering the order");
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String hour = "", minute = "";
//                if (selectedHour > 12) {
//                    am_pm = "PM";
//                    selectedHour = selectedHour - 12;
//                } else {
//                    am_pm = "AM";
//                }
                if (selectedHour < 10) {
                    hour = "0" + selectedHour;
                } else {
                    hour = "" + selectedHour;
                }
                if (selectedMinute < 10) {
                    minute = "0" + selectedMinute;
                } else {
                    minute = "" + selectedMinute;
                }
                deliveryTime = hour + ":" + minute;

                fragmentConfirmDeliveryDetailsBinding.edtTime.setText(deliveryTime);
            }


        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Delivery Time");
        fragmentConfirmDeliveryDetailsBinding.edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimePicker.show();
            }
        });

        whoOrder_list.add("Select the person");
        whoOrder_list.add("Patient/ Family member");
        whoOrder_list.add("Facility employee");

        relation_list.add("Select the relationship");
        relation_list.add("The patient him self ");
        relation_list.add("Wife");
        relation_list.add("Mother");
        relation_list.add("Father");
        relation_list.add("Son");
        relation_list.add("Daughter");
        relation_list.add("Mother in-law ");
        relation_list.add("Father in-law");
        relation_list.add("Sister in-law");
        relation_list.add("Brother in-law");
        relation_list.add("Aunt");
        relation_list.add("Uncle");
        relation_list.add("Cousin");
        relation_list.add("Roommate");
        relation_list.add("Facility employee");
        relation_list.add("Nurse");
        relation_list.add("Caregiver");
        relation_list.add("Receptionist");
        relation_list.add("Neighbor");
        relation_list.add("Med. Tech");
        relation_list.add("Others");
        fragmentConfirmDeliveryDetailsBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch (IllegalStateException ex) {

                } catch (Exception ex) {

                }
            }
        });
//        if (cancelTYpe == 1) {
//            fragmentConfirmDeliveryDetailsBinding.header.txtLabel.setText("Cancel Route");
//            fragmentConfirmDeliveryDetailsBinding.radio.setVisibility(View.GONE);
//            fragmentConfirmDeliveryDetailsBinding.btnSubmit.setText("Cancel Route");
//        } else {
//            fragmentConfirmDeliveryDetailsBinding.header.txtLabel.setText("Cancel Order");
//            fragmentConfirmDeliveryDetailsBinding.radio2.setVisibility(View.INVISIBLE);
//            fragmentConfirmDeliveryDetailsBinding.btnSubmit.setText("Cancel Order");
//        }
        fragmentConfirmDeliveryDetailsBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whoOrder.contains("Select the person")) {
                    Toast.makeText(getActivity(), "Please select Person Who received the order", Toast.LENGTH_SHORT).show();
                    // dialog.dismiss();
                    return;
                }
                if (!fragmentConfirmDeliveryDetailsBinding.editText.getText().toString().equalsIgnoreCase("")) {
                    whoOrder += "-" + fragmentConfirmDeliveryDetailsBinding.editText.getText().toString();
                } else {
                    Toast.makeText(getActivity(), "Mandatory field can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (relation.equalsIgnoreCase("Select the relationship")) {
                    Toast.makeText(getActivity(), "Please select Patient relationship", Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                    return;
                }
                if (relation.equals("Others") || relation.equalsIgnoreCase("")) {
                    if (fragmentConfirmDeliveryDetailsBinding.edtRelation.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Mandatory field can not be empty", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        relation = fragmentConfirmDeliveryDetailsBinding.edtRelation.getText().toString();
                    }
                }

                deliveryTime = fragmentConfirmDeliveryDetailsBinding.edtTime.getText().toString();
                if (order != null) {
                    RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                    confirmDelivery();
                }
            }


        });
        //initReasonDropdown();
        initWhoOrderDropdown();
        initRelationDropdown();
        return fragmentConfirmDeliveryDetailsBinding.getRoot();
    }


    private void initWhoOrderDropdown() {
        // Spinner click listener
        fragmentConfirmDeliveryDetailsBinding.spnWhoRecieved.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                whoOrder = whoOrder_list.get(position);
                if (!whoOrder.equalsIgnoreCase("Select the person")) {
                    fragmentConfirmDeliveryDetailsBinding.llWhoRecieved.setVisibility(View.VISIBLE);
                    fragmentConfirmDeliveryDetailsBinding.editText.setHint("Please enter Person Who Received Order");
                } else {
                    fragmentConfirmDeliveryDetailsBinding.llWhoRecieved.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        List<CustomSpinnerAdopter.SpinnerItem> list = new ArrayList<>();
        for (String value : whoOrder_list) {
            if (whoOrder.equalsIgnoreCase(value)) {
                list.add(new CustomSpinnerAdopter.SpinnerItem(value, true));
            } else {
                list.add(new CustomSpinnerAdopter.SpinnerItem(value, false));
            }
        }
        ArrayAdapter<CustomSpinnerAdopter.SpinnerItem> dataAdapter = new CustomSpinnerAdopter(getContext(), R.layout.cancel_spinner_item, list);

        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.cancel_spinner_item);

        // attaching data adapter to spinner
        fragmentConfirmDeliveryDetailsBinding.spnWhoRecieved.setAdapter(dataAdapter);
    }

    private void initRelationDropdown() {
        // Spinner click listener
        fragmentConfirmDeliveryDetailsBinding.spnRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relation = relation_list.get(position);

                if (relation.equalsIgnoreCase("Others")) {
                    fragmentConfirmDeliveryDetailsBinding.llRelation.setVisibility(View.VISIBLE);
                } else {
                    fragmentConfirmDeliveryDetailsBinding.llRelation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        List<CustomSpinnerAdopter.SpinnerItem> list = new ArrayList<>();
        for (String value : relation_list) {
            if (relation.equalsIgnoreCase(value)) {
                list.add(new CustomSpinnerAdopter.SpinnerItem(value, true));
            } else {
                list.add(new CustomSpinnerAdopter.SpinnerItem(value, false));
            }
        }
        ArrayAdapter<CustomSpinnerAdopter.SpinnerItem> dataAdapter = new CustomSpinnerAdopter(getContext(), R.layout.cancel_spinner_item, list);

        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.cancel_spinner_item);

        // attaching data adapter to spinner
        fragmentConfirmDeliveryDetailsBinding.spnRelation.setAdapter(dataAdapter);
    }

    public void confirmDelivery() {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                LoggedInUser user = LoggedInUser.getInstance();
                RequestBody OrderId = RequestBody.create(MediaType.parse("text/plain"),
                        "" + order.orderId);
                RequestBody routeStepId = RequestBody.create(MediaType.parse("text/plain"),
                        "" + order.routeStepId);
                RequestBody routeID = RequestBody.create(MediaType.parse("text/plain"),
                        "" + routeId);
                RequestBody WhoOrder = RequestBody.create(MediaType.parse("text/plain"),
                        "" + whoOrder);
                RequestBody Relation = RequestBody.create(MediaType.parse("text/plain"),
                        "" + relation);
                RequestBody DeliveryTime = RequestBody.create(MediaType.parse("text/plain"),
                        "" + deliveryTime);
//                RequestBody orderId = RequestBody.create(MediaType.parse("text/plain"),
//                        "" + order.orderId);
                Log.d("deliveryAPICall", " routeID " + routeId + " routeStepId " + order.routeStepId+" orderid "+order.orderId);
                MultipartBody.Part[] pics = new MultipartBody.Part[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    File ConfirmDeliveryPic = new File(imagesURIs.get(i).getPath());
                    long length = ConfirmDeliveryPic.length();
                    length = length / 1024;
                    Log.d("filesize", "" + length);
//                    if(length>800){
//                        ConfirmDeliveryPic= CompressFile.getCompressedImageFile(ConfirmDeliveryPic,getContext());
//                        length = ConfirmDeliveryPic.length();
//                        length = length / 1024;
//                        Log.d("filesize modified", "" + length);
//                    }
                    MultipartBody.Part ConfirmDeliveryImage = MultipartBody.Part.createFormData("ConfirmOrderImage", ConfirmDeliveryPic.getName(), RequestBody.create(MediaType.parse("image/jpeg"),
                            ConfirmDeliveryPic));
                    pics[i] = ConfirmDeliveryImage;
                }

                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                RetrofitClient.getAPIClient().confirmDelivery(pics, routeID, routeStepId, OrderId, WhoOrder, Relation, DeliveryTime)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ConfirmOrderResponse>() {
                            @Override
                            public void onSuccess(ConfirmOrderResponse response) {
                                dialog.dismiss();
                                Log.d("deliveryAPICall", "routeIndex " + routeIndex + "index " + index + " size " + (AppElement.sections.get(routeIndex).orders.size()));
                                if (response.responseCode == 1) {
                                    Log.d("deliveryAPICall", "success " + response.toString());
                                    //  Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    FragmentThankYouAction fragmentThankYouAction = new FragmentThankYouAction();

                                    if (routeIndex < AppElement.sections.size() - 1) {

                                        if (index >= AppElement.sections.get(routeIndex).orders.size() - 1) {
                                            Log.d("deliveryAPICall", "next route focused");
                                            Log.d("deliveryAPICall", "routeIndex " + (routeIndex + 1) + "index " + index + " size " + (AppElement.sections.get(routeIndex).orders.size()));
                                            AppElement.delivered.add(routeIndex);
                                            AppElement.sections.get(routeIndex + 1).orders.get(0).isFocused = true;
                                            if(index< AppElement.sections.get(routeIndex).orders.size()) {
                                                AppElement.sections.get(routeIndex).orders.get(index).isCompleted = true;
                                            }
                                            else{
                                                AppElement.sections.get(routeIndex).orders.get(AppElement.sections.get(routeIndex).orders.size()-1).isCompleted = true;
                                            }

                                        } else {

                                            Log.d("deliveryAPICall", "next order focused");
                                            Log.d("deliveryAPICall", "routeIndex " + routeIndex + " order index " + index  + " size " + (AppElement.sections.get(routeIndex).orders.size()));
                                            if(index< AppElement.sections.get(routeIndex).orders.size()) {
                                                AppElement.sections.get(routeIndex).orders.get(index).isCompleted = true;
                                            }
                                            else{
                                                AppElement.sections.get(routeIndex).orders.get(AppElement.sections.get(routeIndex).orders.size()-1).isCompleted = true;
                                            }
                                            AppElement.sections.get(routeIndex).orders.get(index + 1).isFocused = true;
                                        }
                                    } else {
                                        if(index< AppElement.sections.get(routeIndex).orders.size()) {
                                            AppElement.sections.get(routeIndex).orders.get(index).isCompleted = true;
                                        }
                                        else{
                                            AppElement.sections.get(routeIndex).orders.get(AppElement.sections.get(routeIndex).orders.size()-1).isCompleted = true;
                                        }
                                        boolean allCompleted = true;
                                        for (RideDetailListAdapter.Order order1 : AppElement.sections.get(routeIndex).orders) {
                                            if (!order1.isCompleted) {
                                                allCompleted = false;
                                            }
                                        }
                                        if (allCompleted) {
                                            fragmentThankYouAction.isCompleted = true;
                                            enumPictureType = EnumPictureType.ORDER_COMPLETED;
                                        } else {
                                            enumPictureType = EnumPictureType.ORDER_DELIVER;
                                        }
                                    }
                                    if (routeIndex < AppElement.sections.get(routeIndex).orders.size() - 1) {
                                        AppElement.sections.get(routeIndex).orders.get(index).isCompleted = true;
                                    }

                                    if (enumPictureType == EnumPictureType.ORDER_PICKUP) {

                                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_PICKUP;
                                        FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);

                                    } else if (enumPictureType == EnumPictureType.ORDER_DELIVER) {

                                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_DELIVER;
                                        FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);

                                    } else if (enumPictureType == EnumPictureType.ORDER_COMPLETED) {
                                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_COMPLETED;
                                        FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);

                                    }
                                } else {
                                    if (response.responseMessage.equalsIgnoreCase("no order found")) {
                                        ((DashboardActivity) getActivity()).RideNewNotification(1);
                                    } else if (response.responseMessage.equalsIgnoreCase("Route does not exist")) {
                                        ((DashboardActivity) getActivity()).RideNewNotification(2);
                                    } else {
                                        Log.d("deliveryAPICall", "fail " + response.toString());
                                        Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("deliveryAPICall", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("deliveryAPICall", "failed " + e.toString());
        }
    }

}