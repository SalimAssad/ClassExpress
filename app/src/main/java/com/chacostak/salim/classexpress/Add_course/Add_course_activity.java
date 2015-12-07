package com.chacostak.salim.classexpress.Add_course;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Info_activities.Signature_info.Signature_info_activity;


public class Add_course_activity extends ActionBarActivity {

    Fragment_add_course frag = new Fragment_add_course();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);

        if(getIntent().getExtras() != null){    //If this is true, it was called from signatures_info_activity
            Bundle arguments = new Bundle();
            arguments.putString(Signature_info_activity.SIGNATURE_NAME, getIntent().getStringExtra(Signature_info_activity.SIGNATURE_NAME));
            arguments.putString(Signature_info_activity.TEACHER, getIntent().getStringExtra(Signature_info_activity.TEACHER));
            arguments.putString(Signature_info_activity.INITIAL_DATE, getIntent().getStringExtra(Signature_info_activity.INITIAL_DATE));
            arguments.putString(Signature_info_activity.ENDING_DATE, getIntent().getStringExtra(Signature_info_activity.ENDING_DATE));
            arguments.putString(Signature_info_activity.COLOR, getIntent().getStringExtra(Signature_info_activity.COLOR));
            frag.setArguments(arguments);
        }

        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.container, frag,"add_signature").commit();
    }
}
