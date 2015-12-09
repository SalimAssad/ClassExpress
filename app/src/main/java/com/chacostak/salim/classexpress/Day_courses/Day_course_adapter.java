package com.chacostak.salim.classexpress.Day_courses;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;

/**
 * Created by Salim on 12/04/2015.
 */
public class Day_course_adapter extends ArrayAdapter {

    public Day_course_adapter(Context context, int resource, ArrayList<EventData> data) {
        super(context, resource, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.day_sig_listview, null);

        EventData data = (EventData) getItem(position);

        TextView initial_time = (TextView) v.findViewById(R.id.initial_time);
        TextView signature = (TextView) v.findViewById(R.id.signature);
        TextView ending_time = (TextView) v.findViewById(R.id.ending_time);

        LinearLayout main_layout = (LinearLayout) v.findViewById(R.id.main_layout);

        if(data != null) {
            initial_time.setText(data.initial_time);
            signature.setText(data.name);
            ending_time.setText(data.ending_time);
            main_layout.setBackgroundColor(Color.parseColor(data.color));
        }

        return v;
    }
}
