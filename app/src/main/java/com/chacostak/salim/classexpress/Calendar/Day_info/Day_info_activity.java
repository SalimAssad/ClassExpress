package com.chacostak.salim.classexpress.Calendar.Day_info;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.chacostak.salim.classexpress.R;

public class Day_info_activity extends AppCompatActivity {

    Fragment_day_info frag = new Fragment_day_info();

    public static String DATE = "DATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_ad_layout);

        if(savedInstanceState == null){
            String date = getIntent().getStringExtra(DATE);
            Bundle arguments = new Bundle();
            arguments.putString(DATE, date);
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global, menu);
        return true;
    }
}
