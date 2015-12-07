package com.chacostak.salim.classexpress.Upcoming_homework;

import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.R;

public class Upcoming_homework_activity extends ActionBarActivity implements TabHost.OnTabChangeListener {

    TabHost tabHost;
    TabSpec spec1, spec2;
    int weekOfYear;

    String stab1, stab2;

    Fragment_this_week_homework frag_this_week;
    Fragment_next_week_homework frag_next_week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_homework);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        //Tab 1
        stab1 = getString(R.string.home_this_week);
        spec1 = tabHost.newTabSpec(stab1);
        spec1.setIndicator(stab1);
        spec1.setContent(R.id.tab1);

        tabHost.addTab(spec1);

        //Tab 2
        stab2 = getString(R.string.home_next_week);
        spec2 = tabHost.newTabSpec(stab2);
        spec2.setIndicator(stab2);
        spec2.setContent(R.id.tab2);

        tabHost.addTab(spec2);

        tabHost.setOnTabChangedListener(this);

        weekOfYear = getIntent().getIntExtra(Fragment_home.DATE, 0);

        Bundle arguments = new Bundle();
        frag_this_week = new Fragment_this_week_homework();
        arguments.putInt(Fragment_home.DATE, weekOfYear);
        frag_this_week.setArguments(arguments);
        getFragmentManager().beginTransaction().replace(android.R.id.tabcontent, frag_this_week).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    public void onTabChanged(String tabId) {
        Bundle arguments = new Bundle();
        arguments.putInt(Fragment_home.DATE, weekOfYear);
        if(tabId.equals(stab1)){
            frag_this_week = new Fragment_this_week_homework();
            frag_this_week.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(android.R.id.tabcontent, frag_this_week).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }else if(tabId.equals(stab2)){
            frag_next_week = new Fragment_next_week_homework();
            frag_next_week.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(android.R.id.tabcontent, frag_next_week).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }
}