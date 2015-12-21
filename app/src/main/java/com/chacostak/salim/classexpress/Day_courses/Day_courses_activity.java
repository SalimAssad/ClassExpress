package com.chacostak.salim.classexpress.Day_courses;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.ADmob;

public class Day_courses_activity extends ActionBarActivity {

    Fragment_day_signatures frag = new Fragment_day_signatures();

    ADmob ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_courses);

        Thread thread = null;

        if(savedInstanceState == null){
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bundle arguments = new Bundle();
                    arguments.putString(Fragment_home.DATE, getIntent().getStringExtra(Fragment_home.DATE));
                    arguments.putInt(Fragment_home.DAY_OF_WEEK, getIntent().getIntExtra(Fragment_home.DAY_OF_WEEK, -1));
                    frag.setArguments(arguments);
                    getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
                }
            });
            thread.start();
        }

        ads = new ADmob(this, "ca-app-pub-9359328777269512/2247466588");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ads.addSmartBanner((LinearLayout) findViewById(R.id.ad_container));
            }
        });

        try {
            if(thread != null)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        ads.ad.destroy();
        super.onDestroy();
    }
}
