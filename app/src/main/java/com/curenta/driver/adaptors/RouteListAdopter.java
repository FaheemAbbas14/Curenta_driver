package com.curenta.driver.adaptors;

import static com.curenta.driver.MainApplication.getContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curenta.driver.R;
import com.curenta.driver.dto.AppElement;
import com.curenta.driver.fragments.FragmentTracking;
import com.curenta.driver.utilities.FragmentUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by faheem on 22,May,2022
 */
public class RouteListAdopter extends RecyclerView.Adapter<RouteListAdopter.ViewHolder> {

    public static ArrayList<RideDetailListAdapter.RoutStep> sections = new ArrayList<>();
    public static String routeId;
    public static Context context;
    static ProgressDialog dialog;
    ;

    // RecyclerView recyclerView;
    public RouteListAdopter(Context context, String routeId, ArrayList<RideDetailListAdapter.RoutStep> sections) {
        this.sections = sections;
        this.context = context;
        this.routeId = routeId;
    }

    @Override
    public RouteListAdopter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.ride_detail_item, parent, false);
        RouteListAdopter.ViewHolder viewHolder = new RouteListAdopter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RouteListAdopter.ViewHolder ivh, int sectionIndex) {
        RideDetailListAdapter.RoutStep s = sections.get(sectionIndex);
        try {
            ivh.patientName.setText(s.name);
            //   RideDetailListAdapter.ItemViewHolder ivh = (RideDetailListAdapter.ItemViewHolder) viewHolder;
            if (sectionIndex == 0) {
                ivh.patientName.setVisibility(View.GONE);
            } else {
                ivh.heading.setText("Address Client " + sectionIndex);

            }
            ivh.description.setText(s.address);

            if (!s.isFocused) {
                ivh.topLayout.setBackgroundColor(Color.WHITE);
                ivh.topLayout.setEnabled(false);

            } else {
                AppElement.orderId = s.routeStepId;
                ivh.topLayout.setEnabled(true);
                ivh.heading.setTextColor(context.getResources().getColor(R.color.blue));
                ivh.topLayout.setBackgroundResource(R.color.focus);
                if (s.orders.get(0).deliveryNote != null && !s.orders.get(0).deliveryNote.contains("null")) {
                    ivh.llDeliveryNotes.setVisibility(View.VISIBLE);
                    ivh.deliveryNotes.setText(s.orders.get(0).deliveryNote);
                }
            }

            boolean allcompeleted = true;
            for (RideDetailListAdapter.Order order : s.orders) {
                if (!order.isCompleted) {
                    allcompeleted = false;
                }
            }
            if (allcompeleted) {
                ivh.heading.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));
                ivh.topLayout.setEnabled(false);
            }
            else{
                ivh.heading.setTextColor(context.getResources().getColor(R.color.labellightgrey));
            }
            if (s.isArrived) {
                ivh.heading.setTextColor(context.getResources().getColor(R.color.lightgreen));

            }
            Log.d("routeOrders", "sectionIndex "+sectionIndex+"" + s.name+" completed "+allcompeleted);
            boolean isPickup = false;
            if (sectionIndex == 0) {
                isPickup = true;
            }
//            if(sectionIndex>0){
//                sectionIndex=sectionIndex-1;
//            }

            OrdersListAdopter ordersListAdopter = new OrdersListAdopter(context, routeId,isPickup, s.orders, sections, sectionIndex);
            ivh.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ivh.recyclerView.setAdapter(ordersListAdopter);
            ordersListAdopter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return sections.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView heading;
        TextView description, deliveryNotes;
        TextView patientName;
        LinearLayout topLayout, llDeliveryNotes;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
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
                RideDetailListAdapter.RoutStep item = sections.get(adapterPosition);
                if (v == topLayout) {
                    RouteListAdopter.this.onAddressClick(item.orders.get(0), adapterPosition);
                }
            }
        }
    }


    void onAddressClick(RideDetailListAdapter.Order item, int sectionIndex) {
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
            fragmentTracking.sections = sections;
            fragmentTracking.index = sectionIndex;
            FragmentUtils.getInstance().addFragment(context, fragmentTracking, R.id.fragContainer);

//        FragmentNavigation fragmentNavigation = new FragmentNavigation();
//        fragmentNavigation.mDestination = destination;
//        fragmentNavigation.mOrigin = source;
//        fragmentNavigation.order = item;
//        fragmentNavigation.sections = sections;
//        fragmentNavigation.index = sectionIndex;
//        FragmentUtils.getInstance().addFragment(context, fragmentNavigation, R.id.fragContainer);

            sections.get(sectionIndex).isArrived = true;
            for (int i = 0; i < sections.get(sectionIndex).orders.size(); i++) {
                if (!sections.get(sectionIndex).orders.get(i).isCompleted) {
                    sections.get(sectionIndex).orders.get(i).isArrived = true;
                    break;
                }
            }
            notifyDataSetChanged();
        }

    }

    public LatLng getLocationFromAddress(Context context, double latitude, double longitude) {
        LatLng p1 = new LatLng(latitude, longitude);

        return p1;
    }

}

