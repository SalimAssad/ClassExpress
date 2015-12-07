package com.chacostak.salim.classexpress.Notifications.Basic_notifications;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Notifications_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.Notifications.AlarmHandler;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.Dialogs;

import java.util.Arrays;

/**
 * Created by Salim on 17/06/2015.
 */
public class Fragment_notifications extends AlarmHandler implements View.OnClickListener, AdapterView.OnItemSelectedListener, DialogInterface.OnClickListener {

    View v;
    Spinner spinner;
    ArrayAdapter adapter;
    ImageButton delete_button;

    int my_position = -1;

    boolean already_exists = false;
    boolean new_notification = false;

    DB_Notifications_Manager notifications_manager;
    DB_Schedule_Manager schedule_manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String array[];

        v = inflater.inflate(R.layout.fragment_notifications, container, false);

        notifications_manager = new DB_Notifications_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        schedule_manager = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        delete_button = (ImageButton) v.findViewById(R.id.delete);
        delete_button.setOnClickListener(this);

        if (getArguments() != null) {
            my_position = getArguments().getInt(Fragment_notifications_container.COUNTER, -1);

            if (Fragment_notifications_container.notifications.get(my_position).tag.equals(DB_Notifications_Manager.COURSE_TAG))
                array = Arrays.copyOfRange(getResources().getStringArray(R.array.time_before), 0, 4);
            else
                array = getResources().getStringArray(R.array.time_before);

            spinner = (Spinner) v.findViewById(R.id.time_spinner);
            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, array);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            if (Fragment_notifications_container.notifications.get(my_position).new_time == -1 || Fragment_notifications_container.notifications.get(my_position).new_unit == null) {
                spinner.setSelection(1);
                new_notification = true;
            } else {
                spinner.setSelection(adapter.getPosition(Fragment_notifications_container.notifications.get(my_position).new_time + " "
                        + Fragment_notifications_container.notifications.get(my_position).new_unit));
            }
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(sharedPreferences.getBoolean("general_confirmations", false) && sharedPreferences.getBoolean("confirmation_notifications", false))
            new Dialogs(getActivity()).createConfirmationDialog(R.string.delete, R.string.are_you_sure, R.drawable.ic_launcher, this);
        else
            delete();
    }

    private void delete() {
        int t = Fragment_notifications_container.notifications.get(my_position).new_time;
        String u = Fragment_notifications_container.notifications.get(my_position).new_unit;
        if (!checkOtherNotifications(t, u)) {
            notifications_manager.delete(Fragment_notifications_container.notifications.get(my_position).tag,
                    Fragment_notifications_container.notifications.get(my_position).new_time,
                    Fragment_notifications_container.notifications.get(my_position).new_unit);
        }
        Fragment_notifications_container.notifications.get(my_position).new_time = -1;
        Fragment_notifications_container.notifications.get(my_position).new_unit = null;
        getFragmentManager().beginTransaction().remove(this).setTransition(FragmentTransaction.TRANSIT_EXIT_MASK).commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected_item = (String) spinner.getItemAtPosition(position);
        String array[] = selected_item.split(" ");
        already_exists = checkOtherNotifications(Integer.parseInt(array[0]), array[1]);

        if (new_notification) {
            if (!already_exists) {
                notifications_manager.insert(
                        Fragment_notifications_container.notifications.get(my_position).tag,
                        Integer.parseInt(array[0]),
                        array[1]);
                new_notification = false;
            }
        } else if (!already_exists) {
            notifications_manager.update(Fragment_notifications_container.notifications.get(my_position).tag,
                    Fragment_notifications_container.notifications.get(my_position).new_time,
                    Fragment_notifications_container.notifications.get(my_position).new_unit,
                    Fragment_notifications_container.notifications.get(my_position).tag,
                    Integer.parseInt(array[0]),
                    array[1]);
        }

        Fragment_notifications_container.notifications.get(my_position).old_time = Fragment_notifications_container.notifications.get(my_position).new_time;
        Fragment_notifications_container.notifications.get(my_position).old_unit = Fragment_notifications_container.notifications.get(my_position).new_unit;

        Fragment_notifications_container.notifications.get(my_position).new_time = Integer.parseInt(array[0]);
        Fragment_notifications_container.notifications.get(my_position).new_unit = array[1];
    }

    private boolean checkOtherNotifications(int xtime, String xunit) {
        for (int i = 0; i < Fragment_notifications_container.notifications.size(); i++) {
            int t = Fragment_notifications_container.notifications.get(i).new_time;
            String u = Fragment_notifications_container.notifications.get(i).new_unit;
            if (i != my_position && t == xtime && u.equals(xunit))
                return true;
        }
        return false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == -1)
            delete();
    }
}
