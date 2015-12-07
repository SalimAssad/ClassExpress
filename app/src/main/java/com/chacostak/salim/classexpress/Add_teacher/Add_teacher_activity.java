package com.chacostak.salim.classexpress.Add_teacher;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.chacostak.salim.classexpress.Add_homework.Fragment_add_homework;
import com.chacostak.salim.classexpress.R;

public class Add_teacher_activity extends ActionBarActivity {

    Fragment_add_teacher frag = new Fragment_add_teacher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);

        if(getIntent().getExtras() != null){    //If this is true, it was called from signatures_info_activity
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_add_teacher.NAME, getIntent().getStringExtra(Fragment_add_teacher.NAME));
            arguments.putString(Fragment_add_teacher.PHONE, getIntent().getStringExtra(Fragment_add_teacher.PHONE));
            arguments.putString(Fragment_add_teacher.EMAIL, getIntent().getStringExtra(Fragment_add_teacher.EMAIL));
            arguments.putString(Fragment_add_teacher.WEB_PAGE, getIntent().getStringExtra(Fragment_add_teacher.WEB_PAGE));
            frag.setArguments(arguments);
        }

        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
    }
}
