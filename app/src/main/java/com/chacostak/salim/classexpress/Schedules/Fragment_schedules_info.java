package com.chacostak.salim.classexpress.Schedules;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.R;

import com.chacostak.salim.classexpress.Info_activities.Course_info.Fragment_course_info;
import com.chacostak.salim.classexpress.Utilities.Dialogs;

import java.util.ArrayList;

/**
 * Created by Salim on 26/03/2015.
 */
public class Fragment_schedules_info extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener {

    View v;
    TextView textDays, textHours;
    public String hour_begins, hour_ends;
    public String day;
    Fragment_add_schedule add_schedule;
    DB_Schedule_Manager sch_manager;

    public static ArrayList<Fragment_schedules_info> schedules_added = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {
        v = inflater.inflate(R.layout.fragment_schedules_info, container, false);

        sch_manager = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        storeArguments();

        textDays = (TextView) v.findViewById(R.id.textDays);
        textHours = (TextView) v.findViewById(R.id.textHours);

        textDays.setText(day);
        textHours.setText(hour_begins + " - " + hour_ends);

        v.findViewById(R.id.delete).setOnClickListener(this);
        v.findViewById(R.id.edit).setOnClickListener(this);
        v.findViewById(R.id.copy).setOnClickListener(this);

        return v;
    }

    private void storeArguments() {
        hour_begins = getArguments().getString(Fragment_add_schedule.BEGINS);
        hour_ends = getArguments().getString(Fragment_add_schedule.ENDS);
        day = getArguments().getString(Fragment_add_schedule.DAY);
    }

    @Override
    public void onClick(View v) {
        schedules_added.remove(this); //This instance is removed from the arraylist
        switch(v.getId()){
            case R.id.copy:
                add_schedule = new Fragment_add_schedule();
                add_schedule.setArguments(prepareCopyArguments());

                getFragmentManager().beginTransaction().add(R.id.schedule_container, add_schedule)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            case R.id.edit:
                add_schedule = new Fragment_add_schedule();
                add_schedule.setArguments(prepareArguments());

                getFragmentManager().beginTransaction().add(R.id.schedule_container, add_schedule)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).remove(this)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
                break;
            case R.id.delete:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if(sharedPreferences.getBoolean("general_confirmations", true) && sharedPreferences.getBoolean("confirmation_schedules", true))
                    new Dialogs(getActivity()).createConfirmationDialog(R.string.delete, R.string.are_you_sure, R.drawable.ic_launcher, this);
                else
                    delete();
                break;
        }
    }

    private void delete() {
        sch_manager.delete(Fragment_course_info.course_parent,day,0,hour_begins,hour_ends);
        getFragmentManager().beginTransaction().remove(this)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
    }

    private Bundle prepareArguments() {
        Bundle arguments = new Bundle();
        arguments.putString(Fragment_add_schedule.BEGINS, hour_begins);
        arguments.putString(Fragment_add_schedule.ENDS, hour_ends);
        arguments.putString(Fragment_add_schedule.DAY, day);
        return arguments;
    }

    private Bundle prepareCopyArguments() {
        Bundle arguments = new Bundle();
        arguments.putString(Fragment_add_schedule.BEGINS, hour_begins);
        arguments.putString(Fragment_add_schedule.ENDS, hour_ends);
        arguments.putString(Fragment_add_schedule.DAY, day);
        arguments.putBoolean(Fragment_add_schedule.IS_BEING_EDITED, false);
        return arguments;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == -1)
            delete();
    }
}
