package com.chacostak.salim.classexpress.Calendar.Day_info;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Calendar.Data.CalendarData;
import com.chacostak.salim.classexpress.Calendar.Data.ExamData;
import com.chacostak.salim.classexpress.Calendar.Data.HomeworkData;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Salim on 14/12/2015.
 */
public class Day_info_adapter extends ArrayAdapter {

    DateValidation dateValidation;

    public Day_info_adapter(Context context, int resource, List objects) {
        super(context, resource, objects);

        dateValidation = new DateValidation();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.day_info_adapter, null);

        CalendarData calendarData = (CalendarData) getItem(position);
        TextView textTitle = (TextView) v.findViewById(R.id.textTitle);
        TextView textDescription = (TextView) v.findViewById(R.id.textDescription);
        TextView textCourse = (TextView) v.findViewById(R.id.textCourse);
        TextView textBottom = (TextView) v.findViewById(R.id.textBottom);

        if(calendarData.getType() == 'H'){
            HomeworkData data = (HomeworkData) calendarData;
            textTitle.setText(data.getTitle());
            textDescription.setText(data.getDescription());
            textCourse.setText(data.getCourse());
            textBottom.setText(getDate(data.getInitialDate()));

            textTitle.setBackgroundColor(Color.parseColor(data.getColor()));
            textBottom.setBackgroundColor(Color.parseColor(data.getColor()));
        }else if(calendarData.getType() == 'E'){
            ExamData data = (ExamData) calendarData;
            textTitle.setText(data.getTitle());
            textDescription.setText(data.getRoom());
            textCourse.setText(data.getCourse());
            textBottom.setText(getDate(data.getInitialDate()));

            textTitle.setBackgroundColor(Color.parseColor(data.getColor()));
            textBottom.setBackgroundColor(Color.parseColor(data.getColor()));
        }else{

        }

        return v;
    }

    private String getDate(Calendar cal) {
        String am_pm;
        final int AM_PM = cal.get(Calendar.AM_PM);
        if(AM_PM == 0)
            am_pm = "am";
        else
            am_pm = "pm";

        return cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR) + " - " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + " " + am_pm;
    }
}
