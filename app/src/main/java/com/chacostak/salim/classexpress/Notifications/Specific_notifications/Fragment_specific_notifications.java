package com.chacostak.salim.classexpress.Notifications.Specific_notifications;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Data_Base.DB_Calendar_Notifications_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.Notifications.AlarmHandler;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.Dialogs;

import org.w3c.dom.Text;

/**
 * Created by Salim on 17/06/2015.
 */
public class Fragment_specific_notifications extends AlarmHandler implements View.OnClickListener, DialogInterface.OnClickListener {

    View v;
    ImageButton delete_button;
    TextView textDate;

    int my_position = -1;
    char type;

    DB_Calendar_Notifications_Manager notifications_manager;
    DB_Schedule_Manager schedule_manager;

    String date;
    String time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_specific_notifications, container, false);

        notifications_manager = new DB_Calendar_Notifications_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        schedule_manager = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if (getArguments() != null) {
            my_position = getArguments().getInt(Fragment_specific_notifications_container.COUNTER, -1);
            type = getArguments().getChar(Fragment_specific_notifications_container.TYPE);
        }

        delete_button = (ImageButton) v.findViewById(R.id.delete);
        delete_button.setOnClickListener(this);

        date = Fragment_specific_notifications_container.notifications.get(my_position).date;
        time = Fragment_specific_notifications_container.notifications.get(my_position).time;

        textDate = (TextView) v.findViewById(R.id.textDate);
        textDate.setText(date + " - " + time);

        return v;
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPreferences.getBoolean("general_confirmations", true) && sharedPreferences.getBoolean("confirmation_notifications", false))
            new Dialogs(getActivity()).createConfirmationDialog(R.string.delete, R.string.are_you_sure, R.drawable.ic_launcher, this);
        else
            delete();
    }

    private void delete() {
        String xtype = "";
        if(type == 'H')
            xtype = DB_Calendar_Notifications_Manager.HOMEWORK;
        else if(type == 'E')
            xtype = DB_Calendar_Notifications_Manager.EXAM;
        else if(type == 'G')
            xtype = DB_Calendar_Notifications_Manager.GENERIC;

        notifications_manager.delete(Fragment_specific_notifications_container.notifications.get(my_position).tag,
                Fragment_specific_notifications_container.notifications.get(my_position).date,
                Fragment_specific_notifications_container.notifications.get(my_position).time,
                xtype);
        getFragmentManager().beginTransaction().remove(this).setTransition(FragmentTransaction.TRANSIT_EXIT_MASK).commit();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1)
            delete();
    }
}
