package com.chacostak.salim.classexpress.Info_activities.Signature_info;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.chacostak.salim.classexpress.Add_course.Add_course_activity;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Fragment_courses;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.Dialogs;

public class Signature_info_activity extends ActionBarActivity implements DialogInterface.OnClickListener {

    public static final String SIGNATURE_NAME = "SIGNATURE_NAME";
    public static final String TEACHER = "TEACHER";
    public static final String INITIAL_DATE = "INITIAL_DATE";
    public static final String ENDING_DATE = "ENDING_DATE";
    public static final String COLOR = "COLOR";
    Fragment_signature_info frag;

    final int DATA_SAVED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        if (savedInstanceState == null) {
            frag = new Fragment_signature_info();
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_courses.SELECTED_SIGNATURE, getIntent().getStringExtra(Fragment_courses.SELECTED_SIGNATURE));
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.container, frag, "frag_signature_info").commit();
        }else
            frag = (Fragment_signature_info) getFragmentManager().findFragmentByTag("frag_signature_info");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signature_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.edit:
                Intent intent = new Intent(this, Add_course_activity.class);
                intent.putExtra(SIGNATURE_NAME, frag.signature_name);
                intent.putExtra(TEACHER, frag.teacher);
                intent.putExtra(INITIAL_DATE, frag.initial_date);
                intent.putExtra(ENDING_DATE, frag.ending_date);
                intent.putExtra(COLOR, frag.color);
                startActivityForResult(intent, DATA_SAVED);
                return true;
            case R.id.delete:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if(sharedPreferences.getBoolean("general_confirmations", true) && sharedPreferences.getBoolean("confirmation_signatures", true))
                    new Dialogs(this).createConfirmationDialog(R.string.delete, R.string.are_you_sure, R.drawable.ic_launcher, this);
                else
                    delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delete() {
        new DB_Courses_Manager(this, DB_Helper.DB_Name, DB_Helper.DB_Version).deleteSignature(frag.signature_name);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DATA_SAVED:
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {  //Gets the info that has been edited
                    frag.signature_name = data.getStringExtra(SIGNATURE_NAME);
                    frag.teacher = data.getStringExtra(TEACHER);
                    frag.initial_date = data.getStringExtra(INITIAL_DATE);
                    frag.ending_date = data.getStringExtra(ENDING_DATE);
                    frag.color = data.getStringExtra(COLOR);

                    frag.textSignature.setText(frag.signature_name);
                    frag.textTeacher.setText(frag.teacher);
                    frag.textDates.setText(frag.initial_date + " - " + frag.ending_date);

                    frag.textSignature.setBackgroundColor(Color.parseColor(frag.color));
                    frag.textDates.setBackgroundColor(Color.parseColor(frag.color));

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
