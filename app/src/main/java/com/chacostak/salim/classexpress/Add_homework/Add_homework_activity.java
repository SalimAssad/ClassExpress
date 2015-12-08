package com.chacostak.salim.classexpress.Add_homework;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.chacostak.salim.classexpress.R;

public class Add_homework_activity extends ActionBarActivity {

    Fragment_add_homework frag = new Fragment_add_homework();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);

        if(getIntent().getExtras() != null){    //If this is true, it was called from signatures_info_activity
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_add_homework.TITLE, getIntent().getStringExtra(Fragment_add_homework.TITLE));
            arguments.putString(Fragment_add_homework.DESCRIPTION, getIntent().getStringExtra(Fragment_add_homework.DESCRIPTION));
            arguments.putString(Fragment_add_homework.DAY_LIMIT, getIntent().getStringExtra(Fragment_add_homework.DAY_LIMIT));
            arguments.putString(Fragment_add_homework.TIME_LIMIT, getIntent().getStringExtra(Fragment_add_homework.TIME_LIMIT));
            arguments.putString(Fragment_add_homework.COURSE_NAME, getIntent().getStringExtra(Fragment_add_homework.COURSE_NAME));
            frag.setArguments(arguments);
        }

        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
    }
}
