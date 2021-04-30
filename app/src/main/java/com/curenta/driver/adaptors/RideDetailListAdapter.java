
package com.curenta.driver.adaptors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.curenta.driver.BuildConfig;
import com.curenta.driver.R;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.enums.EnumPictureType;
import com.curenta.driver.fragments.FragmentCancelOrder;
import com.curenta.driver.fragments.FragmentMaps;
import com.curenta.driver.fragments.FragmentTakePhoto;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static class Section {
        public ArrayList<Order> items = new ArrayList<>();


    }

    public static class Order {
        public String name;
        public String address;
        public String buttonText;
        public boolean isFocused;
        public boolean isCompleted;
        public boolean isCancled = false;
        public boolean isArrived;
        public String orderId;

        public Order(String name, String address, String buttonText, boolean isFocused, boolean isCompleted, boolean isArrived, String orderId, boolean isCancled) {
            this.name = name;
            this.address = address;
            this.buttonText = buttonText;
            this.isFocused = isFocused;
            this.isCompleted = isCompleted;
            this.isArrived = isArrived;
            this.orderId = orderId;
            this.isCancled = isCancled;
        }


    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener {
        TextView heading;
        TextView description;
        TextView patientName;
        TextView cancel;
        ImageView icon;
        Button action;
        LinearLayout topLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.heading);
            description = (TextView) itemView.findViewById(R.id.txtDescription);
            patientName = (TextView) itemView.findViewById(R.id.patientName);
            cancel = (TextView) itemView.findViewById(R.id.txtCancelOrder);
            icon = (ImageView) itemView.findViewById(R.id.imgIcon);
            action = (Button) itemView.findViewById(R.id.btnAction);
            topLayout = (LinearLayout) itemView.findViewById(R.id.lltop);
            topLayout.setOnClickListener(this);
            action.setOnClickListener(this);
            cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Order item = sections.get(0).items.get(adapterPosition);
            if (v == topLayout) {
                RideDetailListAdapter.this.onAddressClick(item, adapterPosition);
            } else if (v == action) {
                RideDetailListAdapter.this.onActionClicked(item, adapterPosition);
            } else if (v == cancel) {
                RideDetailListAdapter.this.onCancelClick(item, adapterPosition);
            }
        }
    }


    public ArrayList<Section> sections = new ArrayList<>();
    boolean showAdapterPositions;

    public RideDetailListAdapter(Activity context, boolean showAdapterPositions, String routeId) {
        this.showAdapterPositions = showAdapterPositions;
        this.context = context;
        this.routeId = routeId;

    }


    void onAddressClick(Order item, int sectionIndex) {
        LatLng location = getLocationFromAddress(context, item.address);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location.latitude + "," + location.longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        context.startActivity(Intent.createChooser(intent, "Select application"));
//        LatLng source = null;
//        if (sectionIndex > 0) {
//            source = getLocationFromAddress(context, sections.get(0).items.get(sectionIndex - 1).address);
//        }
//        LatLng destination = getLocationFromAddress(context, item.address);
//        FragmentTracking fragmentTracking = new FragmentTracking();
//        fragmentTracking.mDestination = destination;
//        fragmentTracking.mOrigin = source;
//        FragmentUtils.getInstance().addFragment(context, fragmentTracking, R.id.fragContainer);
        sections.get(0).items.get(sectionIndex).isArrived = true;
        notifyAllSectionsDataSetChanged();
    }

    void onCancelClick(Order item, int sectionIndex) {
        FragmentCancelOrder fragmentCancelOrder = new FragmentCancelOrder();
        fragmentCancelOrder.order = item;
        fragmentCancelOrder.sections = sections;
        fragmentCancelOrder.index = sectionIndex;
        fragmentCancelOrder.routeId = routeId;
        fragmentCancelOrder.cancelTYpe = 2;
        FragmentUtils.getInstance().addFragment(context, fragmentCancelOrder, R.id.fragContainer);
        notifyAllSectionsDataSetChanged();
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    void onActionClicked(Order item, int sectionIndex) {
        // Toast.makeText(MainApplication.getContext(), "Action clicked " + sectionIndex, Toast.LENGTH_SHORT).show();
        if (sectionIndex == 0) {
            FragmentTakePhoto fragmentTakePhoto = new FragmentTakePhoto();
            fragmentTakePhoto.enumPictureType = EnumPictureType.ORDER_PICKUP;
            fragmentTakePhoto.order = item;
            fragmentTakePhoto.sections = sections;
            fragmentTakePhoto.index = sectionIndex;
            fragmentTakePhoto.routeId = routeId;
            FragmentUtils.getInstance().addFragment(context, fragmentTakePhoto, R.id.fragContainer);
//            FragmentTakePhoto fragmentTakePhoto = new FragmentTakePhoto();
//            fragmentTakePhoto.enumPictureType = EnumPictureType.ORDER_PICKUP;
//            fragmentTakePhoto.order = item;
//            fragmentTakePhoto.sections = sections;
//            fragmentTakePhoto.index = sectionIndex;
//            fragmentTakePhoto.rideInfoDto = rideInfoDto;
//            FragmentUtils.getInstance().addFragment(context, fragmentTakePhoto, R.id.fragContainer);
            // orderPicup(rideInfoDto.routeId, sectionIndex);
        } else if (sectionIndex == sections.size() - 1) {
            FragmentTakePhoto fragmentTakePhoto = new FragmentTakePhoto();
            fragmentTakePhoto.enumPictureType = EnumPictureType.ORDER_COMPLETED;
            fragmentTakePhoto.order = item;
            fragmentTakePhoto.routeId = routeId;
            fragmentTakePhoto.sections = sections;
            fragmentTakePhoto.index = sectionIndex;
            FragmentUtils.getInstance().addFragment(context, fragmentTakePhoto, R.id.fragContainer);
        } else {
            FragmentTakePhoto fragmentTakePhoto = new FragmentTakePhoto();
            fragmentTakePhoto.enumPictureType = EnumPictureType.ORDER_DELIVER;
            fragmentTakePhoto.order = item;
            fragmentTakePhoto.sections = sections;
            fragmentTakePhoto.index = sectionIndex;
            fragmentTakePhoto.routeId = routeId;
            FragmentUtils.getInstance().addFragment(context, fragmentTakePhoto, R.id.fragContainer);
        }

    }

    public void setData(ArrayList<Section> sections) {
        this.sections = sections;
    }


    @Override
    public int getNumberOfSections() {
        return sections.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return sections.get(sectionIndex).items.size();
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
        Section s = sections.get(sectionIndex);
        ItemViewHolder ivh = (ItemViewHolder) viewHolder;
        if (itemIndex == 0) {
            ivh.heading.setText(s.items.get(itemIndex).name);
            ivh.patientName.setVisibility(View.GONE);
        } else {
            ivh.heading.setText("Address Client " + itemIndex);
            ivh.patientName.setText(s.items.get(itemIndex).name);
        }
        ivh.description.setText(s.items.get(itemIndex).address);

        ivh.action.setText(s.items.get(itemIndex).buttonText);
        ivh.action.setEnabled(false);
        ivh.cancel.setEnabled(false);
        if (!s.items.get(itemIndex).isFocused) {
            ivh.topLayout.setBackgroundColor(Color.WHITE);
            ivh.action.setBackgroundResource(R.drawable.grey_rounded);
            ivh.topLayout.setEnabled(false);
        } else {
            AppElement.orderId = s.items.get(itemIndex).orderId;
            ivh.topLayout.setEnabled(true);
            ivh.icon.setImageResource(R.drawable.bluenextarrow);
            ivh.heading.setTextColor(ContextCompat.getColor(context, R.color.blue));
            ivh.topLayout.setBackgroundResource(R.color.focus);
            ivh.action.setBackgroundResource(R.drawable.grey_rounded);
            FragmentMaps.publishLocation();
        }
        if (s.items.get(itemIndex).isArrived) {
            ivh.action.setBackgroundResource(R.drawable.blue_rounded);
            ivh.icon.setImageResource(R.drawable.rounded_green_button);
            ivh.action.setBackgroundResource(R.drawable.blue_rounded);
            ivh.heading.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));
            ivh.action.setEnabled(true);
            ivh.cancel.setEnabled(true);
        }
        if (s.items.get(itemIndex).isCompleted) {
            if (itemIndex == 0) {
                ivh.action.setText("Order Pickup Completed");
            } else if (s.items.get(itemIndex).isCancled) {
                ivh.action.setText("Canceled");
            } else {
                ivh.action.setText("Delivered");

            }
            ivh.heading.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));
            ivh.icon.setImageResource(R.drawable.tick_mark);
            ivh.action.setBackgroundResource(R.drawable.rounded_green);
            ivh.topLayout.setEnabled(false);
            ivh.action.setEnabled(false);
            ivh.cancel.setEnabled(false);
        }
        if (itemIndex == 0) {
            ivh.cancel.setVisibility(View.INVISIBLE);
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
                                    sections.get(0).items.get(index).isCompleted = true;
                                    if (index <= sections.get(0).items.size() - 1) {
                                        sections.get(0).items.get(index + 1).isFocused = true;
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
}
