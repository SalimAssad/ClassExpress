package com.chacostak.salim.classexpress.Upcoming_events;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Info_activities.Exam_info.Exam_info_activity;
import com.chacostak.salim.classexpress.Fragment_exams;
import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Services.CountDownService;
import com.chacostak.salim.classexpress.Utilities.DateValidation;

import java.util.Calendar;

/**
 * Created by Salim on 29/04/2015.
 */
public class Fragment_upcoming_exam extends Fragment implements View.OnClickListener {

    View v;
    TextView textTitle, textMedium, textSmall;
    TextView timer;
    TextView textUnit;

    LinearLayout timer_layout;

    BroadcastReceiver broadcastReceiver;

    Calendar calendar;
    DateValidation dateValidation;

    long remainingTime = 0;
    String title;
    private LinearLayout main_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_upcoming_homework, container, false);

        textTitle = (TextView) v.findViewById(R.id.text_title);
        textMedium = (TextView) v.findViewById(R.id.text_medium);
        textSmall = (TextView) v.findViewById(R.id.text_small);

        timer = (TextView) v.findViewById(R.id.timer);
        textUnit = (TextView) v.findViewById(R.id.textUnitType);

        timer_layout = (LinearLayout) v.findViewById(R.id.timer_layout);

        if(getArguments() != null){
            title = getArguments().getString(Fragment_home.NAME);
            textTitle.setText(title);
            textMedium.setText(getArguments().getString(Fragment_home.DESCRIPTION));
            textSmall.setText(getArguments().getString(Fragment_home.DATE)+" - "+getArguments().getString(Fragment_home.INITIAL_TIME));
            remainingTime = getArguments().getLong(Fragment_home.REMAINING_TIME);

            timer_layout.setBackgroundColor(Color.parseColor(getArguments().getString(Fragment_home.COLOR)));
        }

        calendar = Calendar.getInstance();
        dateValidation = new DateValidation(getActivity());

        timer.setText(String.valueOf(remainingTime/1000));

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //If the broadcasted message isn't from a exam it will ignore it
                if(intent.getCharExtra(CountDownService.TYPE, 'X') == 'E') {
                    String broadcastedTitle = intent.getStringExtra(CountDownService.ID);
                    if (broadcastedTitle.equals(textSmall.getText().toString())) {
                        timer.setText(intent.getStringExtra(CountDownService.REMAINING_TIME));
                        textUnit.setText(intent.getStringExtra(CountDownService.UNIT));
                    }
                }
            }
        };

        main_layout = (LinearLayout) v.findViewById(R.id.main_layout);
        main_layout.setOnClickListener(this);

        return v;
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        }catch (Exception e){

        }
    }

    @Override
    public void onResume(){
        super.onResume();
        restoreRemainingTime(remainingTime);
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(CountDownService.COUNTDOWN));
    }

    private int getRemainingDays(){
        Calendar stored_date = dateValidation.formatDateANDTimeInPm(getArguments().getString(Fragment_home.DATE)+" "+getArguments().getString(Fragment_home.INITIAL_TIME));
        int aux;
        int remainingDays = stored_date.get(Calendar.DAY_OF_WEEK) - calendar.get(Calendar.DAY_OF_WEEK);
        if(remainingDays < 0){
            aux = 7 - calendar.get(Calendar.DAY_OF_WEEK);
            remainingDays = stored_date.get(Calendar.DAY_OF_WEEK);
            remainingDays += aux;
        }

        return remainingDays;
    }

    private void restoreRemainingTime(long millisUntilFinished){
        long time = 0;
        long oneDay = 86400000;
        long hourAndHalf = 5400000;
        String unit = "";
        if(millisUntilFinished > oneDay){
            unit = getString(R.string.d);
            time = getRemainingDays();
        } else if(millisUntilFinished > hourAndHalf){
            unit = getString(R.string.h);
            time = ((millisUntilFinished/1000)/60)/60;
        } else if(millisUntilFinished <= hourAndHalf){
            unit = getString(R.string.m);
            time = (millisUntilFinished/1000)/60;
        }

        timer.setText(String.valueOf(time));
        textUnit.setText(unit);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), Exam_info_activity.class);
        intent.putExtra(Fragment_exams.SELECTED_EXAM, getArguments().getString(Fragment_home.DATE)+" - "+getArguments().getString(Fragment_home.INITIAL_TIME));
        startActivity(intent);
    }
}
