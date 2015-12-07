package com.chacostak.salim.classexpress.Info_activities.Vacation_info;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.chacostak.salim.classexpress.Add_vacation.Add_vacation_activity;
import com.chacostak.salim.classexpress.Add_vacation.Fragment_add_vacation;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;
import com.chacostak.salim.classexpress.Fragment_vacations;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.ADmob;
import com.chacostak.salim.classexpress.Utilities.Dialogs;

public class Vacation_info_activity extends ActionBarActivity implements DialogInterface.OnClickListener {

    Fragment_vacation_info frag;

    final int DATA_SAVED = 0;

    ADmob ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_ad_layout);

        Thread thread = null;

        if(savedInstanceState == null) {
            frag = new Fragment_vacation_info();
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_vacations.SELECTED_VACATION, getIntent().getStringExtra(Fragment_vacations.SELECTED_VACATION));
            frag.setArguments(arguments);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getFragmentManager().beginTransaction().replace(R.id.container, frag, "vacation_info").commit();
                }
            });
            thread.start();
        }else
            frag = (Fragment_vacation_info) getFragmentManager().findFragmentByTag("vacation_info");

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
        getMenuInflater().inflate(R.menu.menu_vacation_info, menu);
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
                Intent intent = new Intent(this, Add_vacation_activity.class);
                intent.putExtra(Fragment_add_vacation.TITLE, frag.title);
                intent.putExtra(Fragment_add_vacation.INITIAL_DATE, frag.begin_date);
                intent.putExtra(Fragment_add_vacation.ENDING_DATE, frag.end_date);
                intent.putExtra(Fragment_add_vacation.YEARLY, frag.yearly);
                startActivityForResult(intent, DATA_SAVED);
                return true;
            case R.id.delete:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if(sharedPreferences.getBoolean("general_confirmations", true) && sharedPreferences.getBoolean("confirmation_vacations", true))
                    new Dialogs(this).createConfirmationDialog(R.string.delete, R.string.are_you_sure, R.drawable.ic_launcher, this);
                else
                    delete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        new DB_Vacations_Manager(this, DB_Helper.DB_Name, DB_Helper.DB_Version).delete(frag.title);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DATA_SAVED:
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {  //Gets the info that has been edited
                    frag.yearly = data.getBooleanExtra(Fragment_add_vacation.YEARLY, true);
                    frag.title = data.getStringExtra(Fragment_add_vacation.TITLE);
                    frag.begin_date = data.getStringExtra(Fragment_add_vacation.INITIAL_DATE);
                    frag.end_date = data.getStringExtra(Fragment_add_vacation.ENDING_DATE);

                    frag.yearlyCheckBox.setChecked(frag.yearly);
                    frag.textTitle.setText(frag.title);
                    frag.textDate.setText(frag.begin_date + " - " + frag.end_date);

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
}
