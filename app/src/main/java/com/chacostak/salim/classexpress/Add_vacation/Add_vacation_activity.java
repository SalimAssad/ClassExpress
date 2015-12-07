package com.chacostak.salim.classexpress.Add_vacation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.chacostak.salim.classexpress.R;


public class Add_vacation_activity extends ActionBarActivity {

    Fragment_add_vacation frag = new Fragment_add_vacation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);

        if(getIntent().getExtras() != null){    //If this is true, it was called from signatures_info_activity
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_add_vacation.TITLE, getIntent().getStringExtra(Fragment_add_vacation.TITLE));
            arguments.putString(Fragment_add_vacation.INITIAL_DATE, getIntent().getStringExtra(Fragment_add_vacation.INITIAL_DATE));
            arguments.putString(Fragment_add_vacation.ENDING_DATE, getIntent().getStringExtra(Fragment_add_vacation.ENDING_DATE));
            arguments.putBoolean(Fragment_add_vacation.YEARLY, getIntent().getBooleanExtra(Fragment_add_vacation.YEARLY, true));
            frag.setArguments(arguments);
        }

        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.container, frag,"add_vacation").commit();
    }
}
