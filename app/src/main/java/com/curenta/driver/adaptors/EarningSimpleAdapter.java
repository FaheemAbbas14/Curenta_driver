package com.curenta.driver.adaptors;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.curenta.driver.R;
import com.curenta.driver.dto.EarningDto;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;

/**
 * Created by faheem on 01,March,2021
 */
public class EarningSimpleAdapter extends RecyclerView.Adapter<EarningSimpleAdapter.ViewHolder>{
    private ArrayList<EarningDto> listdata;

    // RecyclerView recyclerView;
    public EarningSimpleAdapter(ArrayList<EarningDto> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_simple_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final EarningDto myListData = listdata.get(position);
        holder.txtDate.setText(myListData.title);
        holder.txtAmount.setText(myListData.value);

    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate;
        public TextView txtAmount;
        public ViewHolder(View itemView) {
            super(itemView);
            this.txtDate = (TextView) itemView.findViewById(R.id.textView);
            this.txtAmount = (TextView) itemView.findViewById(R.id.adapterPositionTextView);

        }
    }
}