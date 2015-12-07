package com.chacostak.salim.classexpress.Add_course;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Info_activities.Course_info.Course_info_activity;


public class Add_course_activity extends ActionBarActivity {

    Fragment_add_course frag = new Fragment_add_course();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);

        if(getIntent().getExtras() != null){    //If this is true, it was called from signatures_info_activity
            Bundle arguments = new Bundle();
            arguments.putString(Course_info_activity.COURSE_NAME, getIntent().getStringExtra(Course_info_activity.COURSE_NAME));
            arguments.putString(Course_info_activity.TEACHER, getIntent().getStringExtra(Course_info_activity.TEACHER));
            arguments.putString(Course_info_activity.INITIAL_DATE, getIntent().getStringExtra(Course_info_activity.INITIAL_DATE));
            arguments.putString(Course_info_activity.ENDING_DATE, getIntent().getStringExtra(Course_info_activity.ENDING_DATE));
            arguments.putString(Course_info_activity.COLOR, getIntent().getStringExtra(Course_info_activity.COLOR));
            frag.setArguments(arguments);
        }

        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.container, frag,"add_signature").commit();
    }
}
