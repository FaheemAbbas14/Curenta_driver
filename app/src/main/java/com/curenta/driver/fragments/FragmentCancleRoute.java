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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.DashboardActivity;
import com.curenta.driver.R;
import com.curenta.driver.adaptors.RideDetailListAdapter;
import com.curenta.driver.databinding.FragmentCancleRouteBinding;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FragmentCancleRoute extends Fragment {
    public RideDetailListAdapter.Order order;
    public int index;
    public String routeId;
    FragmentCancleRouteBinding fragmentCancleRouteBinding;
    String reason = "";
    ProgressDialog dialog;
    public int cancelTYpe;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCancleRouteBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_cancle_route, container, false);
        fragmentCancleRouteBinding.header.imageView3.setVisibility(View.INVISIBLE);

        fragmentCancleRouteBinding.header.imgBackButton.setOnClickListener(new View.OnClickListener() {
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
        if (cancelTYpe == 1) {
            fragmentCancleRouteBinding.header.txtLabel.setText("Cancel Route");
            fragmentCancleRouteBinding.radio.setVisibility(View.GONE);
            fragmentCancleRouteBinding.btnSubmit.setText("Cancel Route");
        } else {
            fragmentCancleRouteBinding.header.txtLabel.setText("Cancel Order");
            fragmentCancleRouteBinding.radio2.setVisibility(View.INVISIBLE);
            fragmentCancleRouteBinding.btnSubmit.setText("Cancel Order");
        }
        fragmentCancleRouteBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reason.equals("Other")) {
                    if (fragmentCancleRouteBinding.editText.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Please enter reason", Toast.LENGTH_SHORT).show();
                    } else {
                        reason = fragmentCancleRouteBinding.editText.getText().toString();
                        if (cancelTYpe == 1) {
                            cancelRoute(routeId);
                        } else {
                            cancelRoute(routeId, order.routeStepId);
                        }
                    }
                } else {
                    if (cancelTYpe == 1) {
                        cancelRoute(routeId);
                    } else {
                        if (order != null) {
                            cancelRoute(routeId, order.routeStepId);
                        }
                    }
                }
            }
        });
        fragmentCancleRouteBinding.radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    reason = checkedRadioButton.getText().toString();
                    if (reason.equals("Other")) {
                        fragmentCancleRouteBinding.editText.setEnabled(true);
                    } else {
                        fragmentCancleRouteBinding.editText.setText("");
                        fragmentCancleRouteBinding.editText.setEnabled(false);
                    }


                }
            }
        });
        fragmentCancleRouteBinding.radio2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    reason = checkedRadioButton.getText().toString();
                    if (reason.equals("Other")) {
                        fragmentCancleRouteBinding.editText.setEnabled(true);
                    } else {
                        fragmentCancleRouteBinding.editText.setText("");
                        fragmentCancleRouteBinding.editText.setEnabled(false);
                    }


                }
            }
        });
        return fragmentCancleRouteBinding.getRoot();
    }

    private void cancelRoute(String routeId, String routeStepId) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                int radioButtonID = fragmentCancleRouteBinding.radio.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) fragmentCancleRouteBinding.radio.findViewById(radioButtonID);
                reason = (String) radioButton.getText();
                if (reason.equals("Other")) {
                    reason = fragmentCancleRouteBinding.editText.getText().toString();
                }
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                CancelOrderRequest requestDTO = new CancelOrderRequest(routeId, routeStepId, LoggedInUser.getInstance().driverId, LoggedInUser.getInstance().email, reason, "", "", "", order.orderId);
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
                                    for (int i = 0; i < AppElement.sections.get(index).orders.size(); i++) {
                                        AppElement.sections.get(index).orders.get(i).isCompleted = true;
                                        AppElement.sections.get(index).orders.get(i).isCancled = true;
                                    }
                                    AppElement.delivered.clear();
                                    AppElement.sections.clear();

                                    //   Log.d("cancelRoute", "index " + index + " size " + (sections.get(0).items.size() - 1));
                                    launchDismissDlg();


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

    private void cancelRoute(String routeId) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                int radioButtonID = fragmentCancleRouteBinding.radio2.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) fragmentCancleRouteBinding.radio2.findViewById(radioButtonID);
                reason = (String) radioButton.getText();
                if (reason.equals("Other")) {
                    reason = fragmentCancleRouteBinding.editText.getText().toString();
                }
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                CancelRouteRequest requestDTO = new CancelRouteRequest(routeId, reason, LoggedInUser.getInstance().driverId, LoggedInUser.getInstance().email, "", "", "");
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
                    if (index < AppElement.sections.size() - 1) {
                        AppElement.sections.get(0).isFocused = true;
                        try {
                            if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        } catch (IllegalStateException ex) {

                        } catch (Exception ex) {

                        }
                    } else {

                        FragmentThankYouAction fragmentThankYouAction = new FragmentThankYouAction();
                        fragmentThankYouAction.isCompleted = true;
                        fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_COMPLETED;
                        FragmentUtils.getInstance().addFragment(getActivity(), fragmentThankYouAction, R.id.fragContainer);

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