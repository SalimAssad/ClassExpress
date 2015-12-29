package com.chacostak.salim.classexpress.Notifications.Specific_notifications;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chacostak.salim.classexpress.Calendar.Calendar_activity;
import com.chacostak.salim.classexpress.Data_Base.DB_Calendar_Notifications_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Notifications_Manager;
import com.chacostak.salim.classexpress.Notifications.AlarmHandler;
import com.chacostak.salim.classexpress.Notifications.AlarmEvents;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 17/06/2015.
 */
public class Fragment_specific_notifications_container extends Fragment implements View.OnClickListener {

    View v;

    public static final String TAG = "TAG";
    public static final String TYPE = "TYPE";
    static final String COUNTER = "COUNTER";

    AlarmHandler alarmHandler = new AlarmHandler();

    int counter = 0;

    String tag;
    char type;

    DB_Calendar_Notifications_Manager notifications_manager;

    static final ArrayList<Specific_notification_data> notifications = new ArrayList<>();

    final int DATA_SAVED = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_notifications_container, container, false);

        notifications_manager = new DB_Calendar_Notifications_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if (getArguments() != null) {
            tag = getArguments().getString(TAG);
            type = getArguments().getChar(TYPE);
        }

        if (savedInstanceState == null)
            showNotifications(true);
        else
            showNotifications(false);

        v.findViewById(R.id.add_notification).setOnClickListener(this);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notifications.clear();
    }

    private void showNotifications(boolean flag) {
        Cursor cursor = notifications_manager.searchByTag(tag);
        while (cursor.moveToNext()) {
            restoreNotifications(cursor.getString(cursor.getColumnIndex(notifications_manager.DATE)), cursor.getString(cursor.getColumnIndex(notifications_manager.TIME)), flag);
        }
    }

    private void restoreNotifications(String date, String time, boolean flag) {
        notifications.add(new Specific_notification_data());
        notifications.get(counter).tag = tag;
        notifications.get(counter).time = time;
        notifications.get(counter).date = date;

        if (flag) {
            Fragment_specific_notifications frag = new Fragment_specific_notifications();
            Bundle arguments = new Bundle();
            arguments.putInt(COUNTER, counter);
            arguments.putChar(TYPE, type);
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.notifications_container, frag).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).commit();
        }
        counter++;
    }

    @Override
    public void onClick(View v) {
        notifications.add(new Specific_notification_data());
        notifications.get(counter).tag = tag;

        Intent intent = new Intent(getActivity(), Calendar_activity.class);
        startActivityForResult(intent, DATA_SAVED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DATA_SAVED:
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {  //Gets the info that has been edited
                    notifications.get(counter).date = data.getStringExtra(Calendar_activity.DATE);
                    notifications.get(counter).time = data.getStringExtra(Calendar_activity.TIME);

                    Cursor cursor = notifications_manager.search(notifications.get(counter).tag, notifications.get(counter).date, notifications.get(counter).time);
                    if(cursor.moveToNext())
                        Toast.makeText(getActivity(), R.string.notification_exists, Toast.LENGTH_LONG).show();
                    else {
                        String t = "";
                        if(type == 'H')
                            t = DB_Calendar_Notifications_Manager.HOMEWORK;
                        else if(type == 'E')
                            t = DB_Calendar_Notifications_Manager.EXAM;
                        else if(type == 'G')
                            t = DB_Calendar_Notifications_Manager.GENERIC;

                        notifications_manager.insert(notifications.get(counter).tag, notifications.get(counter).date, notifications.get(counter).time, t);

                        EventData eventData;
                        String dateArray[];
                        String timeArray[];
                        Calendar calendar = Calendar.getInstance();
                        DateValidation dateValidation = new DateValidation(getActivity());

                        dateArray = notifications.get(counter).date.split("/");
                        timeArray = notifications.get(counter).time.split(":");

                        calendar.set(Integer.parseInt(dateArray[2]), dateValidation.getMonthInt(dateArray[1]), Integer.parseInt(dateArray[0]),
                                dateValidation.pmToNormalTime(Integer.parseInt(timeArray[0]), timeArray[1].split(" ")[1]), Integer.parseInt(timeArray[1].split(" ")[0]));

                        eventData = AlarmEvents.getData(t, getActivity(), tag);

                        if(alarmIsNext(calendar))
                            alarmHandler.setEventAlarm(getActivity(), calendar, eventData.name, eventData.description, notifications.get(counter).date, notifications.get(counter).time, type);

                        Fragment_specific_notifications frag = new Fragment_specific_notifications();
                        Bundle arguments = new Bundle();
                        arguments.putInt(COUNTER, counter);
                        arguments.putChar(TYPE, type);
                        frag.setArguments(arguments);
                        getFragmentManager().beginTransaction().add(R.id.notifications_container, frag).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).commit();
                        counter++;
                    }

                    cursor.close();
                }
                break;
        }
    }

    //Validates if this alarm is sooner than all the other alarms
    private boolean alarmIsNext(Calendar calendar) {
        DB_Calendar_Notifications_Manager calendar_manager = new DB_Calendar_Notifications_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        Cursor cursor = calendar_manager.getAll();
        String dateArray[];
        String timeArray[];
        Calendar c1;
        DateValidation dateValidation = new DateValidation(getActivity());

        while (cursor.moveToNext()){
            c1 = Calendar.getInstance();
            dateArray = cursor.getString(cursor.getColumnIndex(calendar_manager.DATE)).split("/");
            timeArray = cursor.getString(cursor.getColumnIndex(calendar_manager.TIME)).split(":");

            c1.set(Integer.parseInt(dateArray[2]), dateValidation.getMonthInt(dateArray[1]), Integer.parseInt(dateArray[0]),
                    dateValidation.pmToNormalTime(Integer.parseInt(timeArray[0]), timeArray[1].split(" ")[1]), Integer.parseInt(timeArray[1].split(" ")[0]));

            if(Calendar.getInstance().before(c1) && c1.before(calendar)) //If true, then there is already other notification sooner than the new one.
                return false;
        }

        cursor.close();
        calendar_manager.closeDatabase();

        return true;
    }
}
