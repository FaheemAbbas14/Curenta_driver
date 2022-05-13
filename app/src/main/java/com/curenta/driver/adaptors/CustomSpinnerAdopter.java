package com.curenta.driver.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.curenta.driver.R;

import java.util.List;

/**
 * Created by faheem on 28,April,2022
 */
public class CustomSpinnerAdopter extends ArrayAdapter<CustomSpinnerAdopter.SpinnerItem> {

    private List<SpinnerItem> objects;
    private Context context;

    public static class SpinnerItem {
        public String title;
        public boolean value;

        public SpinnerItem(String title, boolean value) {
            this.title = title;
            this.value = value;
        }
    }

    public CustomSpinnerAdopter(Context context, int resourceId,
                                List<SpinnerItem> objects) {
        super(context, resourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.cancel_spinner_item, parent, false);
        TextView label = (TextView) row.findViewById(R.id.spnText);
        View divider = (View) row.findViewById(R.id.divider);
        label.setText(objects.get(position).title);
        //if (position == 0) {//Special style for dropdown header
            divider.setVisibility(View.GONE);
       // }

        return row;
    }

}
