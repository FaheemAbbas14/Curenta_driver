package com.curenta.driver.adaptors;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.R;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.fragments.FragmentCancelOrder;
import com.curenta.driver.fragments.FragmentConfirmDelivery;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.GetFacilityDetails;
import com.curenta.driver.retrofit.apiDTO.GetPatientDetailResponse;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.InternetChecker;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by faheem on 19,May,2022
 */
public class OrdersListAdopter extends RecyclerView.Adapter<OrdersListAdopter.ViewHolder> {
    public static ArrayList<RideDetailListAdapter.RoutStep> sections = new ArrayList<>();
    public static ArrayList<RideDetailListAdapter.Order> orders = new ArrayList<>();
    public static String routeId;
    public static Context context;
    static ProgressDialog dialog;
    public static int position;
    boolean isPickup = false;

    // RecyclerView recyclerView;
    public OrdersListAdopter(Context context, String routeId, boolean isPickup, ArrayList<RideDetailListAdapter.Order> orders, ArrayList<RideDetailListAdapter.RoutStep> sections, int position) {
        this.context = context;
        this.isPickup = isPickup;
        this.orders = orders;
        this.position = position;
        this.sections = sections;
        this.routeId = routeId;
    }

    @Override
    public OrdersListAdopter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.orders_lits_item, parent, false);
        OrdersListAdopter.ViewHolder viewHolder = new OrdersListAdopter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrdersListAdopter.ViewHolder ivh, int itemIndex) {
        final RideDetailListAdapter.Order order = orders.get(itemIndex);
        Log.d("routeOrders", "name " + order.name + " isArrived " + order.isArrived + " position " + position);
        ivh.action.setText(order.buttonText);
        ivh.action.setEnabled(false);
        ivh.cancel.setEnabled(false);
        ivh.call.setEnabled(false);
        ivh.message.setEnabled(false);
        if (!order.isFocused) {
            ivh.action.setBackgroundResource(R.drawable.grey_rounded);

        } else {
            AppElement.orderId = order.orderId;
            //  ivh.icon.setImageResource(R.drawable.bluenextarrow);
            ivh.action.setBackgroundResource(R.drawable.grey_rounded);
            ivh.call.setVisibility(View.VISIBLE);
            ivh.cancel.setVisibility(View.VISIBLE);
        }
        if (order.isArrived) {
            ivh.action.setBackgroundResource(R.drawable.blue_rounded);
            //  ivh.icon.setImageResource(R.drawable.rounded_green_button);
            ivh.action.setBackgroundResource(R.drawable.blue_rounded);
            ivh.action.setEnabled(true);
            ivh.cancel.setEnabled(true);
            ivh.call.setEnabled(true);
            ivh.message.setEnabled(true);
            ivh.cancel.setVisibility(View.VISIBLE);
            ivh.call.setVisibility(View.VISIBLE);
            ivh.message.setVisibility(View.INVISIBLE);
        }
        if (order.routeStepIndex == 0) {
            ivh.cancel.setVisibility(View.INVISIBLE);
            ivh.call.setVisibility(View.INVISIBLE);
            ivh.message.setVisibility(View.INVISIBLE);
        }
        if (order.isCompleted) {
            if (position == 0) {

                ivh.action.setBackgroundResource(R.drawable.rounded_green);
                ivh.action.setText("Order Pickup Completed");
            } else if (order.isCancled) {

                ivh.action.setBackgroundResource(R.drawable.rounded_red);
                ivh.action.setText("Canceled");
            } else {

                ivh.action.setBackgroundResource(R.drawable.rounded_green);
                ivh.action.setText("Delivered");

            }
            ivh.cancel.setVisibility(View.INVISIBLE);
            ivh.call.setVisibility(View.INVISIBLE);
            ivh.message.setVisibility(View.INVISIBLE);
            // ivh.icon.setImageResource(R.drawable.tick_mark);
            ivh.action.setEnabled(false);
            ivh.cancel.setEnabled(false);
            ivh.call.setEnabled(false);
            ivh.message.setEnabled(false);
        }


    }


    @Override
    public int getItemCount() {
        return orders.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cancel;
        Button action;
        // ImageView icon;
        ImageView call, message;

        public ViewHolder(View itemView) {
            super(itemView);
            //   icon = (ImageView) itemView.findViewById(R.id.imgIcon);
            call = (ImageView) itemView.findViewById(R.id.imgCall);
            message = (ImageView) itemView.findViewById(R.id.imgMessage);
            cancel = (TextView) itemView.findViewById(R.id.txtCancelOrder);
            action = (Button) itemView.findViewById(R.id.btnAction);
            action.setOnClickListener(this);
            cancel.setOnClickListener(this);
            call.setOnClickListener(this);
            message.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Log.d("routePostion", "" + adapterPosition + " position " + position + " adapterPosition " + adapterPosition);
            if (AppElement.nextFocusIndex >= 0) {
                position = AppElement.nextFocusIndex;
            } else {
                position = 0;
            }
            RideDetailListAdapter.Order item = sections.get(position).orders.get(adapterPosition);
            Log.d("routePostion", "" + adapterPosition + " position " + position + " name " + item.name + " adapterPosition " + adapterPosition);
            if (v == action) {
                onActionClicked(item, adapterPosition);
            } else if (v == cancel) {
                onCancelClick(item, adapterPosition);
            } else if (v == call) {
                onCallClick(item, adapterPosition);
            } else if (v == message) {
                onMessageClick(item, adapterPosition);
            }

        }

    }

    static void onCancelClick(RideDetailListAdapter.Order item, int sectionIndex) {
        FragmentCancelOrder fragmentCancelOrder = new FragmentCancelOrder();
        fragmentCancelOrder.order = item;
        fragmentCancelOrder.sections = sections;
        fragmentCancelOrder.index = sectionIndex;
        fragmentCancelOrder.routeId = routeId;
        fragmentCancelOrder.routeIndex = AppElement.nextFocusIndex;
        fragmentCancelOrder.cancelTYpe = 2;
        FragmentUtils.getInstance().addFragment(context, fragmentCancelOrder, R.id.fragContainer);

    }

    static void onCallClick(RideDetailListAdapter.Order item, int sectionIndex) {
        getPatient(item.patientId, item.facilityId);
    }

    static void onMessageClick(RideDetailListAdapter.Order item, int sectionIndex) {
//        ChatFragment chatFragment=new ChatFragment();
//        FragmentUtils.getInstance().addFragment(context, chatFragment, R.id.fragContainer);

    }


    static void onActionClicked(RideDetailListAdapter.Order item, int sectionIndex) {
        Log.d("deliveryAPICall", "name " + item.name + " route index " + item.routeStepIndex + " order index " + sectionIndex);
        // Toast.makeText(MainApplication.getContext(), "Action clicked " + sectionIndex, Toast.LENGTH_SHORT).show();
        if (AppElement.nextFocusIndex <= 0) {
            FragmentConfirmDelivery fragmentConfirmDelivery = new FragmentConfirmDelivery();
            fragmentConfirmDelivery.enumPictureType = EnumPictureType.ORDER_PICKUP;
            fragmentConfirmDelivery.order = item;
            fragmentConfirmDelivery.routeId = routeId;
            fragmentConfirmDelivery.sections = sections;
            fragmentConfirmDelivery.routeIndex = AppElement.nextFocusIndex;
            fragmentConfirmDelivery.index = sectionIndex;
            FragmentUtils.getInstance().addFragment(context, fragmentConfirmDelivery, R.id.fragContainer);
        } else if (item.routeStepIndex == sections.size() - 1) {
            FragmentConfirmDelivery fragmentConfirmDelivery = new FragmentConfirmDelivery();
            fragmentConfirmDelivery.enumPictureType = EnumPictureType.ORDER_COMPLETED;
            fragmentConfirmDelivery.order = item;
            fragmentConfirmDelivery.routeId = routeId;
            fragmentConfirmDelivery.sections = sections;
            fragmentConfirmDelivery.routeIndex = AppElement.nextFocusIndex;
            fragmentConfirmDelivery.index = sectionIndex;
            FragmentUtils.getInstance().addFragment(context, fragmentConfirmDelivery, R.id.fragContainer);
        } else {
            FragmentConfirmDelivery fragmentConfirmDelivery = new FragmentConfirmDelivery();
            fragmentConfirmDelivery.enumPictureType = EnumPictureType.ORDER_DELIVER;
            fragmentConfirmDelivery.order = item;
            fragmentConfirmDelivery.sections = sections;
            fragmentConfirmDelivery.index = sectionIndex;
            fragmentConfirmDelivery.routeIndex = AppElement.nextFocusIndex;
            fragmentConfirmDelivery.routeId = routeId;
            FragmentUtils.getInstance().addFragment(context, fragmentConfirmDelivery, R.id.fragContainer);
        }

    }

    private static void getPatient(int patientId, int facilityId) {
        dialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        try {

            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaPatientUrl);
                RetrofitClient.getAPIClient().getPatientDetails(patientId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetPatientDetailResponse>() {
                            @Override
                            public void onSuccess(GetPatientDetailResponse response) {
                                Log.d("getPatientAPICall", "success " + response.toString());

                                if (response.patients != null && response.patients.size() > 0) {
                                    AppElement.patientContact = response.patients.get(0).phonenumber;
                                    getFacility(facilityId);
                                } else {

                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                                Log.d("getPatientAPICall", "failed " + e.toString());
                                dialog.dismiss();
                            }
                        });
            } else {
                dialog.dismiss();
                Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            dialog.dismiss();
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("getPatientAPICall", "failed " + e.toString());
        }
    }

    private static void getFacility(int facilityId) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {

                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaFacilityUrl);
                RetrofitClient.getAPIClient().getFacilityDetails(facilityId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetFacilityDetails>() {
                            @Override
                            public void onSuccess(GetFacilityDetails response) {
                                Log.d("getFacilityAPICall", "success " + response.toString());

                                if (response.data != null) {
                                    AppElement.pharmacyContact = (String) response.data.phone;
                                    if (AppElement.pharmacyContact != null && !AppElement.pharmacyContact.contains("+1")) {
                                        AppElement.pharmacyContact = "+1" + AppElement.pharmacyContact;
                                    }
                                    if (AppElement.patientContact != null && !AppElement.patientContact.contains("+1")) {
                                        AppElement.patientContact = "+1" + AppElement.patientContact;
                                    }
                                    launchCallDlg(AppElement.pharmacyContact, AppElement.patientContact);
                                }

                                dialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {

                                Log.d("getFacilityAPICall", "failed " + e.toString());
                                dialog.dismiss();
                            }
                        });
            } else {
                Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            dialog.dismiss();

            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("getFacilityAPICall", "failed " + e.toString());
        }
    }

    public static void launchCallDlg(String facilityNumber, String customerNumber) {

        Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.call_dailog);
        //  dialog.setCanceledOnTouchOutside(true);
        TextView callFacility = (TextView) dialog.findViewById(R.id.txtCallFacility);
        TextView callCustomer = (TextView) dialog.findViewById(R.id.txtCallCustomer);
        ImageView cancle = (ImageView) dialog.findViewById(R.id.imgCross);
        if (customerNumber.equalsIgnoreCase("")) {
            callCustomer.setEnabled(false);
            callCustomer.setClickable(false);
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        callFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facilityNumber != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + facilityNumber));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                }
                dialog.dismiss();
            }
        });
        callCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customerNumber != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);

                    callIntent.setData(Uri.parse("tel:" + customerNumber));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                }
                dialog.dismiss();
            }
        });

        //  dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}
