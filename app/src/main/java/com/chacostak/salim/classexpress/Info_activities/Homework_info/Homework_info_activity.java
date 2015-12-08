package com.chacostak.salim.classexpress.Info_activities.Homework_info;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.chacostak.salim.classexpress.Add_homework.Add_homework_activity;
import com.chacostak.salim.classexpress.Add_homework.Fragment_add_homework;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Fragment_homeworks;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.ADmob;
import com.chacostak.salim.classexpress.Utilities.Dialogs;

public class Homework_info_activity extends ActionBarActivity implements DialogInterface.OnClickListener {

    Fragment_homework_info frag;

    final int DATA_SAVED = 0;

    ADmob ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_ad_layout);

        Thread thread = null;

        if(savedInstanceState == null) {
            frag = new Fragment_homework_info();
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_homeworks.SELECTED_HOMEWORK, getIntent().getStringExtra(Fragment_homeworks.SELECTED_HOMEWORK));
            frag.setArguments(arguments);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getFragmentManager().beginTransaction().replace(R.id.container, frag, "homework_info").commit();
                }
            });
            thread.start();
        }else
            frag = (Fragment_homework_info) getFragmentManager().findFragmentByTag("homework_info");

        ads = new ADmob(this, "ca-app-pub-9359328777269512/2247466588");
        ads.addSmartBanner((LinearLayout) findViewById(R.id.ad_container));

        try{
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_homework_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.edit:
                Intent intent = new Intent(this, Add_homework_activity.class);
                intent.putExtra(Fragment_add_homework.TITLE, frag.title);
                intent.putExtra(Fragment_add_homework.DESCRIPTION, frag.description);
                intent.putExtra(Fragment_add_homework.DAY_LIMIT, frag.day_limit);
                intent.putExtra(Fragment_add_homework.TIME_LIMIT, frag.time_limit);
                intent.putExtra(Fragment_add_homework.COURSE_NAME, frag.course_name);
                startActivityForResult(intent, DATA_SAVED);
                return true;
            case R.id.delete:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if(sharedPreferences.getBoolean("general_confirmations", true) && sharedPreferences.getBoolean("confirmation_homework", true))
                    new Dialogs(this).createConfirmationDialog(R.string.delete, R.string.are_you_sure, R.drawable.ic_launcher, this);
                else
                    delete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        new DB_Homework_Manager(this, DB_Helper.DB_Name, DB_Helper.DB_Version).deleteByTitle(frag.title);
        prepareResult(true);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DATA_SAVED:
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {  //Gets the info that has been edited
                    frag.course_name = data.getStringExtra(Fragment_add_homework.COURSE_NAME);
                    frag.title = data.getStringExtra(Fragment_add_homework.TITLE);
                    frag.description = data.getStringExtra(Fragment_add_homework.DESCRIPTION);
                    frag.day_limit = data.getStringExtra(Fragment_add_homework.DAY_LIMIT);
                    frag.time_limit = data.getStringExtra(Fragment_add_homework.TIME_LIMIT);

                    frag.textSignature.setText(frag.course_name);
                    frag.textTitle.setText(frag.title);
                    frag.textDescription.setText(frag.description);
                    frag.textDayLimit.setText(frag.day_limit + " - " + frag.time_limit);

                    frag.wasEdited = true;
                }
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == -1)
            delete();
    }

    @Override
    public void onBackPressed() {
        prepareResult(false);
        super.onBackPressed();
    }

    private void prepareResult(boolean removeFlag){
        Intent output = new Intent();
        output.putExtra(Fragment_add_homework.TITLE, frag.title);
        output.putExtra(Fragment_add_homework.COURSE_NAME, frag.course_name);
        output.putExtra(Fragment_add_homework.DESCRIPTION, frag.description);
        output.putExtra(Fragment_add_homework.DAY_LIMIT, frag.day_limit);
        output.putExtra(Fragment_add_homework.TIME_LIMIT, frag.time_limit);
        output.putExtra(Fragment_add_homework.REMOVE, removeFlag);
        setResult(Activity.RESULT_OK, output);
    }
}
