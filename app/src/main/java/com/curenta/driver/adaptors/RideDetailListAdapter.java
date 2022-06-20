
package com.curenta.driver.adaptors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.R;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.fragments.FragmentThankYouAction;
import com.curenta.driver.fragments.FragmentTracking;
import com.curenta.driver.retrofit.RetrofitClient;
import com.curenta.driver.retrofit.apiDTO.OrderPickupResponse;
import com.curenta.driver.retrofit.apiDTO.RouteRequest;
import com.curenta.driver.utilities.FragmentUtils;
import com.curenta.driver.utilities.InternetChecker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * SimpleDemoAdapter, just shows demo data
 */
public class RideDetailListAdapter extends SectioningAdapter {

    static final String TAG = EarningListAdapter.class.getSimpleName();
    static final boolean USE_DEBUG_APPEARANCE = false;
    public Activity context;
    public String routeId;
    ProgressDialog dialog;


    public static class RoutStep {
        public String name;
        public String address;
        public String routeStepId;
        public ArrayList<Order> orders;
        public boolean isFocused;
        public boolean isArrived;

        public RoutStep(String name, String address, ArrayList<Order> orders, String routeStepId, boolean isFocused, boolean isArrived) {
            this.name = name;
            this.address = address;
            this.orders = orders;
            this.routeStepId = routeStepId;
            this.isFocused = isFocused;
            this.isArrived = isArrived;
        }


    }

    public static class Order {
        public String name;
        public String address;
        public String buttonText;
        public String orderId;
        public String routeStepId;
        public double latitude;
        public double longitude;
        public String phoneNumber;
        public int patientId;
        public int facilityId;
        public String deliveryNote;
        public int routeStepIndex;
        public boolean isFocused;

        public boolean isCompleted;
        public boolean isCancled = false;
        public boolean isArrived;

        public Order(String name, String address, String buttonText, boolean isCompleted, boolean isArrived, String orderId, boolean isCancled, double latitude, double longitude, String routeStepId, String phoneNumber, int patientId, int facilityId, String deliveryNote, boolean isFocused, int routeIndex) {
            this.name = name;
            this.address = address;
            this.buttonText = buttonText;
            this.orderId = orderId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.routeStepId = routeStepId;
            this.phoneNumber = phoneNumber;
            this.patientId = patientId;
            this.facilityId = facilityId;
            this.deliveryNote = deliveryNote;
            this.isFocused = isFocused;
            this.isArrived = isArrived;
            this.isCancled = isCancled;
            this.isCompleted = isCompleted;
            this.routeStepIndex = routeIndex;

        }


    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener {
        TextView heading;
        TextView description, deliveryNotes;
        TextView patientName;
        LinearLayout topLayout, llDeliveryNotes;
        RecyclerView recyclerView;


        public ItemViewHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.heading);
            description = (TextView) itemView.findViewById(R.id.txtDescription);
            patientName = (TextView) itemView.findViewById(R.id.patientName);
            deliveryNotes = (TextView) itemView.findViewById(R.id.txtDeliveryNotes);
            topLayout = (LinearLayout) itemView.findViewById(R.id.lltop);
            llDeliveryNotes = (LinearLayout) itemView.findViewById(R.id.llDeliveryNotes);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            topLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (adapterPosition >= 0) {
                RoutStep item = sections.get(adapterPosition);
                if (v == topLayout) {
                    RideDetailListAdapter.this.onAddressClick(item.orders.get(0), adapterPosition);
                }
            }
        }
    }


    public ArrayList<RoutStep> sections = new ArrayList<>();
    boolean showAdapterPositions;

    public RideDetailListAdapter(Activity context, boolean showAdapterPositions, String routeId) {
        this.showAdapterPositions = showAdapterPositions;
        this.context = context;
        this.routeId = routeId;

    }


    void onAddressClick(Order item, int sectionIndex) {
        if (sectionIndex < sections.size()) {
//        LatLng location = getLocationFromAddress(context, item.latitude,item.longitude);
//        if(location!=null) {
//            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location.latitude + "," + location.longitude);
//            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            context.startActivity(Intent.createChooser(intent, "Select application"));
//        }
            LatLng source = null;
            if (sectionIndex > 0) {
                source = getLocationFromAddress(context, sections.get(sectionIndex - 1).orders.get(0).latitude, sections.get(sectionIndex - 1).orders.get(0).longitude);
            }
            LatLng destination = getLocationFromAddress(context, item.latitude, item.longitude);
            FragmentTracking fragmentTracking = new FragmentTracking();
            fragmentTracking.mDestination = destination;
            fragmentTracking.mOrigin = source;
            fragmentTracking.order = item;
            fragmentTracking.index = sectionIndex;
            FragmentUtils.getInstance().addFragment(context, fragmentTracking, R.id.fragContainer);

//        FragmentNavigation fragmentNavigation = new FragmentNavigation();
//        fragmentNavigation.mDestination = destination;
//        fragmentNavigation.mOrigin = source;
//        fragmentNavigation.order = item;
//        fragmentNavigation.sections = sections;
//        fragmentNavigation.index = sectionIndex;
//        FragmentUtils.getInstance().addFragment(context, fragmentNavigation, R.id.fragContainer);

            //  sections.get(sectionIndex).orders.get(0).isArrived = true;
            notifyAllSectionsDataSetChanged();
        }

    }


    public void setData(ArrayList<RoutStep> sections) {
        this.sections = sections;
    }


    @Override
    public int getNumberOfSections() {
        return sections.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return sections.size();
    }


    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.ride_detail_item, parent, false);
        return new ItemViewHolder(v);
    }


    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        Log.d("ridelistadopter", "psition " + sectionIndex + " itemIndex " + itemIndex + " items " + sections.size());
        RoutStep s = sections.get(sectionIndex);
        try {
            ItemViewHolder ivh = (ItemViewHolder) viewHolder;
            if (sectionIndex == 0) {
                ivh.heading.setText(s.name);
                ivh.patientName.setVisibility(View.GONE);
            } else {
                ivh.heading.setText("Address Client " + sectionIndex);
                ivh.patientName.setText(s.name);
            }
            ivh.description.setText(s.name);

            if (!s.isFocused) {
                ivh.topLayout.setBackgroundColor(Color.WHITE);
                ivh.topLayout.setEnabled(false);

            } else {
                AppElement.orderId = s.routeStepId;
                ivh.topLayout.setEnabled(true);
                ivh.heading.setTextColor(ContextCompat.getColor(context, R.color.blue));
                ivh.topLayout.setBackgroundResource(R.color.focus);
                if (s.orders.get(0).deliveryNote != null && !s.orders.get(0).deliveryNote.contains("null")) {
                    ivh.llDeliveryNotes.setVisibility(View.VISIBLE);
                    ivh.deliveryNotes.setText(s.orders.get(0).deliveryNote);
                }
            }
            if (s.isArrived && s.isFocused) {
                Log.d("Rideadopter", "name " + s.name + " isArrived " + s.isArrived);
                ivh.heading.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));

            }
            if (!s.isFocused) {
                Log.d("Rideadopter", "name " + s.name + " focused " + s.isFocused);
                ivh.heading.setTextColor(ContextCompat.getColor(context, R.color.blue));

            }
            boolean allcompeleted = true;
            for (Order order : s.orders) {
                if (!order.isCompleted) {
                    allcompeleted = false;
                }
            }
            if (allcompeleted) {
                ivh.heading.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));
                ivh.topLayout.setEnabled(false);
            }
//            OrdersListAdopter ordersListAdopter = new OrdersListAdopter(sections, itemIndex);
//            ivh.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            ivh.recyclerView.setAdapter(ordersListAdopter);
//            ordersListAdopter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBindGhostHeaderViewHolder(SectioningAdapter.GhostHeaderViewHolder viewHolder, int sectionIndex) {
        if (USE_DEBUG_APPEARANCE) {
            viewHolder.itemView.setBackgroundColor(0xFF9999FF);
        }
    }


    private String pad(int spaces) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            b.append(' ');
        }
        return b.toString();
    }

    private void orderPicup(String routeId, int index) {
        try {
            boolean isInternetConnected = InternetChecker.isInternetAvailable();
            if (isInternetConnected) {
                ProgressDialog dialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                RouteRequest requestDto = new RouteRequest(routeId);
                Gson gson = new Gson();
                String request = gson.toJson(requestDto);
                RetrofitClient.changeApiBaseUrl(BuildConfig.curentaordertriagingURL);
                RetrofitClient.getAPIClient().orderPickup(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OrderPickupResponse>() {
                            @Override
                            public void onSuccess(OrderPickupResponse response) {
                                dialog.dismiss();
                                if (response.responseCode == 1) {
                                    // sections.get(index).isCompleted = true;
                                    if (index <= sections.size() - 1) {
                                        sections.get(index + 1).isFocused = true;
                                    }
                                    //Toast.makeText(context.getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    notifyAllSectionsDataSetChanged();
                                    FragmentThankYouAction fragmentThankYouAction = new FragmentThankYouAction();
                                    fragmentThankYouAction.enumPictureType = EnumPictureType.ORDER_PICKUP;
                                    FragmentUtils.getInstance().addFragment(context, fragmentThankYouAction, R.id.fragContainer);


                                } else {
                                    Log.d("pickupAPICall", "fail " + response);
                                    Toast.makeText(context.getApplicationContext(), response.responseMessage, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.d("pickupAPICall", "failed " + e.toString());
                                Toast.makeText(context.getApplicationContext(), "Server error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(context.getApplicationContext(), "Internet not available", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("pickupAPICall", "failed " + e.toString());
        }
    }


    public LatLng getLocationFromAddress(Context context, double latitude, double longitude) {
        LatLng p1 = new LatLng(latitude, longitude);

        return p1;
    }


}
