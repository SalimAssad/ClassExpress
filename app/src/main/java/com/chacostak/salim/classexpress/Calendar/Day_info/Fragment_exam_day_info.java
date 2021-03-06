package com.chacostak.salim.classexpress.Calendar.Day_info;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Fragment_exams;
import com.chacostak.salim.classexpress.Fragment_homework;
import com.chacostak.salim.classexpress.Info_activities.Exam_info.Exam_info_activity;
import com.chacostak.salim.classexpress.Info_activities.Homework_info.Homework_info_activity;
import com.chacostak.salim.classexpress.R;

/**
 * Created by Salim on 27/12/2015.
 */
public class Fragment_exam_day_info extends Fragment_day_event implements View.OnClickListener {

    View v;

    String title;
    String description;
    String date;
    String color;

    TextView textTitle;
    TextView textDescription;
    TextView textDate;

    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String DATE = "DATE";
    public static final String COLOR = "COLOR";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_exam_day_info, container, false);

        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            description = getArguments().getString(DESCRIPTION);
            date = getArguments().getString(DATE);
            color = getArguments().getString(COLOR);

            textTitle = (TextView) v.findViewById(R.id.textTitle);
            textDescription = (TextView) v.findViewById(R.id.textDescription);
            textDate = (TextView) v.findViewById(R.id.textDate);

            textTitle.setText(title);
            textDescription.setText(description);
            textDate.setText(date.split(" - ")[1]);

            textTitle.setBackgroundColor(Color.parseColor(color));
            textDate.setBackgroundColor(Color.parseColor(color));

            v.setOnClickListener(this);
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), Exam_info_activity.class);
        intent.putExtra(Fragment_exams.SELECTED_EXAM, date);
        startActivity(intent);
    }
}
