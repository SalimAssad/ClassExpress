package com.chacostak.salim.classexpress.Upcoming_events;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.Fragment_homeworks;
import com.chacostak.salim.classexpress.Info_activities.Homework_info.Homework_info_activity;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Services.CountDownService;
import com.chacostak.salim.classexpress.Upcoming_homework.Fragment_this_week_homework;
import com.chacostak.salim.classexpress.Utilities.DateValidation;

import java.util.Calendar;

/**
 * Created by Salim on 10/04/2015.
 */
public class Fragment_upcoming_homework extends Fragment implements View.OnClickListener {

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

    boolean activate = true; //If is in false, it is in the "Upcoming_homework_activity"
    boolean is_this_week = true;

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
            activate = getArguments().getBoolean(Fragment_home.ACTIVATE_TIMER);
            is_this_week = getArguments().getBoolean(Fragment_this_week_homework.THIS_WEEK);

            timer_layout.setBackgroundColor(Color.parseColor(getArguments().getString(Fragment_home.COLOR)));
        }

        calendar = Calendar.getInstance();
        dateValidation = new DateValidation(getActivity());

        if(activate) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    //If the broadcasted message isn't from a homework it will ignore it
                    if(intent.getCharExtra(CountDownService.TYPE, 'X') == 'H') {
                        String broadcastedTitle = intent.getStringExtra(CountDownService.ID);
                        if (broadcastedTitle.equals(title)) {
                            timer.setText(intent.getStringExtra(CountDownService.REMAINING_TIME));
                            textUnit.setText(intent.getStringExtra(CountDownService.UNIT));
                        }
                    }
                }
            };
        }else{
            timer.setText(getRemainingDays1());
            textUnit.setText(getString(R.string.d));
        }

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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), Homework_info_activity.class);
        intent.putExtra(Fragment_homeworks.SELECTED_HOMEWORK, title);
        startActivity(intent);
    }

    //Intended for when it is in the "Upcoming_homework_activity"
    private String getRemainingDays1(){
        Calendar stored_date = dateValidation.formatDateANDTimeInPm(getArguments().getString(Fragment_home.DATE)+" "+getArguments().getString(Fragment_home.INITIAL_TIME));
        int aux;
        int remainingDays;
        if(is_this_week) {
            remainingDays = stored_date.get(Calendar.DAY_OF_WEEK) - calendar.get(Calendar.DAY_OF_WEEK);
        }else{
            aux = 7 - calendar.get(Calendar.DAY_OF_WEEK);
            remainingDays = stored_date.get(Calendar.DAY_OF_WEEK);
            remainingDays += aux;
        }

        return String.valueOf(remainingDays);
    }

    //Intended for when it is in the "Fragment_home"
    private int getRemainingDays2(){
        Calendar stored_date = dateValidation.formatDateANDTimeInPm(getArguments().getString(Fragment_home.DATE)+" "+getArguments().getString(Fragment_home.INITIAL_TIME));;
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
            if(activate)
                time = getRemainingDays2();
            else
                time = Long.parseLong(getRemainingDays1());
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
}
