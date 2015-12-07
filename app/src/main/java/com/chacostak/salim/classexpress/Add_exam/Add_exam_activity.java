package com.chacostak.salim.classexpress.Add_exam;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import com.chacostak.salim.classexpress.R;

public class Add_exam_activity extends ActionBarActivity {

    Fragment_add_exam frag = new Fragment_add_exam();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);

        if(getIntent().getExtras() != null){    //If this is true, it was called from signatures_info_activity
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_add_exam.NAME, getIntent().getStringExtra(Fragment_add_exam.NAME));
            arguments.putString(Fragment_add_exam.ROOM, getIntent().getStringExtra(Fragment_add_exam.ROOM));
            arguments.putString(Fragment_add_exam.DAY_LIMIT, getIntent().getStringExtra(Fragment_add_exam.DAY_LIMIT));
            arguments.putString(Fragment_add_exam.TIME_LIMIT, getIntent().getStringExtra(Fragment_add_exam.TIME_LIMIT));
            arguments.putString(Fragment_add_exam.SIGNATURE_NAME, getIntent().getStringExtra(Fragment_add_exam.SIGNATURE_NAME));
            frag.setArguments(arguments);
        }

        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
    }
}
