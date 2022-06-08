package com.curenta.driver.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.CustomSpinnerAdopter;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentCancelOrderBinding;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.dto.LoggedInUser;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.CancelOrderRequest;
import com.curenta.driver.retrofit.apiDTO.CancelRouteRequest;
import com.curenta.driver.retrofit.apiDTO.CancelRouteResponse;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.InternetChecker;
import com.curenta.driver.utilities.Preferences;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FragmentCancelOrder extends Fragment {
    public RideDetailListAdapter.Order order;
    public ArrayList<RideDetailListAdapter.RoutStep> sections;
    public int index;
    public String routeId;
    public int routeIndex;
    FragmentCancelOrderBinding fragmentCancelOrderBinding;
    String reason = "", whoOrder = "", relation = "", newAddress = "";
    ProgressDialog dialog;
    public int cancelTYpe;
    ArrayList<String> reasons_list = new ArrayList<>();
    ArrayList<String> whoOrder_list = new ArrayList<>();
    ArrayList<String> relation_list = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCancelOrderBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_cancel_order, container, false);
        fragmentCancelOrderBinding.header.imageView3.setVisibility(View.INVISIBLE);
        if (cancelTYpe == 1) {
            fragmentCancelOrderBinding.header.txtLabel.setText("Cancel Route");
        } else {
            fragmentCancelOrderBinding.header.txtLabel.setText("Cancel Order");
        }
        reasons_list.add("Select the reason");
        reasons_list.add("No one at home and Patient does not answer his phone");
        reasons_list.add("No one at home and Facility do not answer the phone");
        reasons_list.add("No one at home and Neither patient nor facility answer the phone");
        reasons_list.add("Wrong address");
        reasons_list.add("Patient is discharged from the facility");
        reasons_list.add("Patient is at the hospital");
        reasons_list.add("Patient passed away");
        reasons_list.add("Patient order to redeliver tomorrow");
        reasons_list.add("Late delivery time and patient asked to redeliver tomorrow");
        reasons_list.add("Facility asked to redeliver tomorrow");
        reasons_list.add("Others");

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
        fragmentCancelOrderBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
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
//            fragmentCancelOrderBinding.header.txtLabel.setText("Cancel Route");
//            fragmentCancelOrderBinding.radio.setVisibility(View.GONE);
//            fragmentCancelOrderBinding.btnSubmit.setText("Cancel Route");
//        } else {
//            fragmentCancelOrderBinding.header.txtLabel.setText("Cancel Order");
//            fragmentCancelOrderBinding.radio2.setVisibility(View.INVISIBLE);
//            fragmentCancelOrderBinding.btnSubmit.setText("Cancel Order");
//        }
        fragmentCancelOrderBinding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reason.equals("Others") || reason.equals("Wrong address")) {
                    if (fragmentCancelOrderBinding.editText.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Mandatory field can not be empty", Toast.LENGTH_SHORT).show();
                    } else {

                        newAddress = fragmentCancelOrderBinding.editText.getText().toString();
                        if (relation.equalsIgnoreCase("Others")) {
                            relation = fragmentCancelOrderBinding.edtRelation.getText().toString();
                        }
                        if (cancelTYpe == 1) {
                            cancelRoute(routeId);
                        } else {
                            cancelRouteOrder(routeId, order.routeStepId);
                        }
                    }
                } else {
                    if (cancelTYpe == 1) {
                        cancelRoute(routeId);
                    } else {
                        if (order != null) {
                            cancelRouteOrder(routeId, order.routeStepId);
                        }
                    }
                }
            }
        });
        initReasonDropdown();
        initWhoOrderDropdown();
        initRelationDropdown();
        return fragmentCancelOrderBinding.getRoot();
    }

    private void initReasonDropdown() {
        // Spinner click listener
        fragmentCancelOrderBinding.spnCancelReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reason = reasons_list.get(position);
                if (reason.equalsIgnoreCase("Wrong address")) {
                    fragmentCancelOrderBinding.rdoWrongAddress.setVisibility(View.VISIBLE);
                    fragmentCancelOrderBinding.llAddress.setVisibility(View.VISIBLE);
                    fragmentCancelOrderBinding.editText.setHint("Please enter the new address");
                } else if (reason.equalsIgnoreCase("Others")) {
                    fragmentCancelOrderBinding.llAddress.setVisibility(View.VISIBLE);
                    fragmentCancelOrderBinding.rdoWrongAddress.setVisibility(View.GONE);
                    fragmentCancelOrderBinding.editText.setHint("Please enter the reason");
                } else {
                    fragmentCancelOrderBinding.rdoWrongAddress.setVisibility(View.GONE);
                    fragmentCancelOrderBinding.llAddress.setVisibility(View.GONE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Creating adapter for spinner
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.cancel_spinner_item, reasons_list);
        List<CustomSpinnerAdopter.SpinnerItem> list = new ArrayList<>();
        for (String value : reasons_list) {
            if (reason.equalsIgnoreCase(value)) {
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
        fragmentCancelOrderBinding.spnCancelReason.setAdapter(dataAdapter);
    }

    private void initWhoOrderDropdown() {
        // Spinner click listener
        fragmentCancelOrderBinding.spnWhoOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                whoOrder = whoOrder_list.get(position);
                if (!whoOrder.equalsIgnoreCase("Select the person")) {
                    fragmentCancelOrderBinding.llWhoOrder.setVisibility(View.VISIBLE);
                    fragmentCancelOrderBinding.edtWhoOrder.setHint("Please enter Person Who Order to Cancel");
                } else {
                    fragmentCancelOrderBinding.llWhoOrder.setVisibility(View.GONE);
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
        fragmentCancelOrderBinding.spnWhoOrder.setAdapter(dataAdapter);
    }

    private void initRelationDropdown() {
        // Spinner click listener
        fragmentCancelOrderBinding.spnRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relation = relation_list.get(position);

                if (relation.equalsIgnoreCase("Others")) {
                    fragmentCancelOrderBinding.llRelation.setVisibility(View.VISIBLE);
                    fragmentCancelOrderBinding.edtRelation.setHint("Please enter the relation");
                } else {
                    fragmentCancelOrderBinding.llRelation.setVisibility(View.GONE);
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
        fragmentCancelOrderBinding.spnRelation.setAdapter(dataAdapter);
    }

    private void cancelRouteOrder(String routeId, String routeStepId) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                if (reason.equalsIgnoreCase("Select the reason")) {
                    Toast.makeText(getActivity(), "Please select Cancellation reason", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (reason.equalsIgnoreCase("Wrong address")) {
                    int radioButtonID = fragmentCancelOrderBinding.rdoWrongAddress.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) fragmentCancelOrderBinding.rdoWrongAddress.findViewById(radioButtonID);
                    reason += "-" + (String) radioButton.getText();
                    reason += "-" + fragmentCancelOrderBinding.editText.getText().toString();
                }
                if (reason.equals("Others")) {
                    if (fragmentCancelOrderBinding.editText.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Mandatory field can not be empty", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    } else {
                        reason = fragmentCancelOrderBinding.editText.getText().toString();
                    }
                }
                if (whoOrder.contains("Select the person")) {
                    Toast.makeText(getActivity(), "Please select Person Who Order to Cancel", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (!fragmentCancelOrderBinding.edtWhoOrder.getText().toString().equalsIgnoreCase("")) {
                    whoOrder += "-" + fragmentCancelOrderBinding.edtWhoOrder.getText().toString();
                } else {
                    Toast.makeText(getActivity(), "Mandatory field can not be empty", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (relation.equalsIgnoreCase("Select the relationship") ) {
                    Toast.makeText(getActivity(), "Please select patient relationship", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                if (relation.equals("Others") || relation.equalsIgnoreCase("")) {
                    if (fragmentCancelOrderBinding.edtRelation.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Mandatory field can not be empty", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    } else {
                        relation = fragmentCancelOrderBinding.edtRelation.getText().toString();
                    }
                }



                Log.d("cancelRoute", "routeIndex " + routeIndex + "index " + index + " size " + (sections.get(0).orders.size() - 1));

                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                CancelOrderRequest requestDTO = new CancelOrderRequest(routeId, routeStepId, LoggedInUser.getInstance().driverId, LoggedInUser.getInstance().email, reason, whoOrder, newAddress, relation, order.orderId);
                Gson gson = new Gson();
                String request = gson.toJson(requestDTO);
                RetrofitClient.getAPIClient().cancelRouteOrder(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CancelRouteResponse>() {
                            @Override
                            public void onSuccess(CancelRouteResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {

                                    sections.get(routeIndex).orders.get(index).isCompleted = true;
                                    sections.get(routeIndex).orders.get(index).isCancled = true;
                                    Log.d("cancelRoute", "routeIndex " + (routeIndex - 1) + "index " + index + " size " + (sections.get(0).orders.size() - 1));
                                    launchDismissDlg();


                                } else {
                                    Log.d("cancelRoute", "fail " + response);
                                    Toast.makeText(getContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("cancelRoute", "failed " + e.toString());
                                Toast.makeText(getContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (
                Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("cancelRoute", "failed " + e.toString());
        }

    }

    private void cancelRoute(String routeId) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                if (reason.equalsIgnoreCase("Wrong address")) {
                    int radioButtonID = fragmentCancelOrderBinding.rdoWrongAddress.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) fragmentCancelOrderBinding.rdoWrongAddress.findViewById(radioButtonID);
                    reason = (String) radioButton.getText();
                }
                if (reason.equals("Others")) {
                    reason = fragmentCancelOrderBinding.editText.getText().toString();
                }
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                CancelRouteRequest requestDTO = new CancelRouteRequest(routeId, reason, LoggedInUser.getInstance().driverId, LoggedInUser.getInstance().email, whoOrder, relation, newAddress);
                Gson gson = new Gson();
                String request = gson.toJson(requestDTO);
                RetrofitClient.getAPIClient().cancelRoute(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CancelRouteResponse>() {
                            @Override
                            public void onSuccess(CancelRouteResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {

                                    Preferences.getInstance().setString("rideInfoDto", "");
                                    launchDismissDlg();
                                    ((DashboardActivity) getActivity()).checkRide();
                                    // Toast.makeText(getActivity().getApplicationContext(), "Route canceled", Toast.LENGTH_SHORT).show();

                                } else {
                                    Log.d("cancelRoute", "fail " + response);
                                    Toast.makeText(getActivity().getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("cancelRoute", "failed " + e.toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Error while connecting to server", Toast.LENGTH_SHORT).show();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("cancelRoute", "failed " + e.toString());
        }
    }

    public void launchDismissDlg() {

        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_cancel_order);
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout btnReopenId = (LinearLayout) dialog.findViewById(R.id.llgotIt);
        TextView txtdescription = (TextView) dialog.findViewById(R.id.txtdescription);
        if (cancelTYpe == 1) {
            txtdescription.setText("Route cancelled successfully");
        } else {
            txtdescription.setText("Order cancelled successfully");
        }
        btnReopenId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cancelTYpe == 1) {
                    try {
                        for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {

                            if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }
                    } catch (IllegalStateException ex) {

                    } catch (Exception ex) {

                    }

                } else {
                    if (routeIndex < sections.size() - 1) {
                        sections.get(routeIndex + 1).isFocused = true;
                        if (sections.get(routeIndex).orders.size() == 1) {
                            AppElement.nextFocusIndex = routeIndex;
                        }
                        try {
                            if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        } catch (IllegalStateException ex) {

                        } catch (Exception ex) {

                        }
                    } else {
                        sections.get(routeIndex).orders.get(index).isCompleted = true;
                        boolean allCompleted = true;
                        for (RideDetailListAdapter.Order order1 : sections.get(routeIndex).orders) {
                            if (!order1.isCompleted) {
                                allCompleted = false;
                            }
                        }
                        if(allCompleted) {
                            AppElement.nextFocusIndex = 0;
                            AppElement.delivered.clear();
                            FragmentThankYouAction fragmentThankYouAction = new FragmentThankYouAction();
                            fragmentThankYouAction.isCompleted = true;
                            fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_COMPLETED;
                            FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);
                        }
                        else{
                            try {
                                if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            } catch (IllegalStateException ex) {

                            } catch (Exception ex) {

                            }
                        }
                    }

                }
                dialog.dismiss();
            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}