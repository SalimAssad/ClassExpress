package com.chacostak.salim.classexpress.Add_course;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chacostak.salim.classexpress.R;

import java.util.ArrayList;

/**
 * Created by Salim on 13/04/2015.
 */
public class ColorSpinnerAdapter extends BaseAdapter {

    int resource;
    Context activity;
    ArrayList<String> colors;
    String selectedItem;

    public ColorSpinnerAdapter(Context context, int xresource, ArrayList<String> data) {
        activity = context;
        resource = xresource;
        colors = data;
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int position) {
        return colors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View rootView, ViewGroup parent){
        View v = rootView;

        if(v == null)
            v = LayoutInflater.from(activity).inflate(R.layout.color_spinner, null);

        selectedItem = String.valueOf(getItem(position));

        if(selectedItem != null) {
            TextView text = (TextView) v.findViewById(R.id.textColor);
            text.setBackgroundColor(Color.parseColor(selectedItem));
        }

        return v;
    }

    public int getItemPosition(String oldColor) {
        for(int i = 0; i < colors.size(); i++){
            if(oldColor.equals(colors.get(i)))
                return i;
        }
        return 0;
    }
}
