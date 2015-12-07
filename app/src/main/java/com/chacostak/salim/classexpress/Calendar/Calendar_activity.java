package com.chacostak.salim.classexpress.Calendar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.chacostak.salim.classexpress.Fragment_calendar;
import com.chacostak.salim.classexpress.R;


public class Calendar_activity extends ActionBarActivity {

    public static final String DATE = "DATE";
    public static final String TIME = "ALARM";

    public static final String OPENED_FROM_CALENDAR_ACTIVITY = "OPENED_FROM_CALENDAR_ACTIVITY";

    Fragment_calendar frag = new Fragment_calendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putBoolean(OPENED_FROM_CALENDAR_ACTIVITY, true);
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.container, frag, "frag_calendar").commit();
        }
    }
}
