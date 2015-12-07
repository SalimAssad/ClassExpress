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
import com.chacostak.salim.classexpress.Fragment_courses;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Services.CourseService;
import com.chacostak.salim.classexpress.Info_activities.Course_info.Course_info_activity;

/**
 * Created by Salim on 08/04/2015.
 */
public class Fragment_upcoming_course extends Fragment implements View.OnClickListener {

    View v;
    TextView textTitle, textMedium, textSmall;
    TextView timer;
    LinearLayout timer_layout;

    BroadcastReceiver broadcastReceiver;

    long remainingTime = 0;
    String signature;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_upcoming_signature, container, false);

        textTitle = (TextView) v.findViewById(R.id.text_title);
        textMedium = (TextView) v.findViewById(R.id.text_medium);
        textSmall = (TextView) v.findViewById(R.id.text_small);

        timer = (TextView) v.findViewById(R.id.timer);

        timer_layout = (LinearLayout) v.findViewById(R.id.timer_layout);

        if (getArguments() != null) {
            signature = getArguments().getString(Fragment_home.NAME);
            textTitle.setText(signature);
            textMedium.setText(getArguments().getString(Fragment_home.TEACHER));
            textSmall.setText(getArguments().getString(Fragment_home.INITIAL_TIME) + " - " + getArguments().getString(Fragment_home.ENDING_TIME));
            remainingTime = getArguments().getLong(Fragment_home.REMAINING_TIME);
            timer.setText(String.valueOf(remainingTime / 1000));

            timer_layout.setBackgroundColor(Color.parseColor(getArguments().getString(Fragment_home.COLOR)));
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //If the broadcasted message isn't from a exam it will ignore it
                if(intent.getCharExtra(CourseService.TYPE, 'X') == 'S') {
                    String broadcastedTitle = intent.getStringExtra(CourseService.ID);
                    if (broadcastedTitle.equals(textTitle.getText().toString()))
                        timer.setText(intent.getStringExtra(CourseService.REMAINING_TIME));
                }
            }
        };

        v.findViewById(R.id.main_layout).setOnClickListener(this);

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
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(CourseService.COUNTDOWN));
    }

    private void restoreRemainingTime(long millisUntilFinished){
        long time = 0;
        long hourAndHalf = 5400000;
        if(millisUntilFinished <= hourAndHalf)
            time = (millisUntilFinished/1000)/60;
        timer.setText(String.valueOf(time));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), Course_info_activity.class);
        intent.putExtra(Fragment_courses.SELECTED_COURSE, signature);
        startActivity(intent);
    }
}
