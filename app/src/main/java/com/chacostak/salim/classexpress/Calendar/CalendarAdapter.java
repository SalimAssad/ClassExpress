package com.chacostak.salim.classexpress.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;

/**
 * Created by Salim on 20/08/2015.
 */
public class CalendarAdapter extends ArrayAdapter {

    ArrayList<EventData> data;
    String days[] = null;
    int realDay;
    int realMonth;
    int realYear;
    int showedMonth;
    int showedYear;

    public CalendarAdapter(Context context, int resource, ArrayList xdays, ArrayList xdata, int xrealDay, int xrealMonth, int xrealYear, int xshowedMonth, int xshowedYear) {
        super(context, resource, xdays);
        data = xdata;
        realDay = xrealDay;
        realMonth = xrealMonth;
        realYear = xrealYear;
        showedMonth = xshowedMonth;
        showedYear = xshowedYear;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.calendar_adapter, null);

        TextView textDay = (TextView) v.findViewById(R.id.textDay);

        if(position > 6) {
            RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.main_layout);
            int day = (int) getItem(position);
            v.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 100));
            textDay.setText(String.valueOf(day));
            if((day > 15 && position < 13) || (day < 8 && position > 30))
                layout.setBackgroundColor(Color.parseColor("#1E010101"));
            else if(day == realDay && realMonth == showedMonth && realYear == showedYear)
                layout.setBackgroundColor(Color.parseColor("#4E00648D"));

        }else{
            if(days == null)
                days = getContext().getResources().getStringArray(R.array.abrev_days);
            textDay.setText(days[position]);
        }



        return v;
    }
}
