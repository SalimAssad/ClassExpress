package com.chacostak.salim.classexpress.Upcoming_homework;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Upcoming_events.Fragment_upcoming_homework;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;
import com.chacostak.salim.classexpress.Utilities.Sorter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 26/04/2015.
 */
public class Fragment_next_week_homework extends Fragment{

    View v;

    DB_Homework_Manager homework_manager;
    DB_Courses_Manager sig_manager;

    Calendar calendar;
    DateValidation dateValidation;

    ArrayList<EventData> events = new ArrayList<>();
    Sorter sorter = new Sorter();

    int weekOfYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_this_week_homework, container, false);

        if(savedInstanceState == null) {
            if(getArguments() != null)
                weekOfYear = getArguments().getInt(Fragment_home.DATE) + 1;

            homework_manager = new DB_Homework_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
            sig_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

            calendar = Calendar.getInstance();
            dateValidation = new DateValidation(getActivity());

            Cursor homework = homework_manager.getAll();
            Cursor cursor_signature;
            String storedTitle;
            String storedDescription;
            String storedDate;
            String storedTimeLimit;
            String storedSignature;
            String storedColor;
            int hw_week_of_year;
            long remainingTime;
            while(homework.moveToNext()){
                storedTitle = homework.getString(homework.getColumnIndex(homework_manager.TITLE));
                storedDescription = homework.getString(homework.getColumnIndex(homework_manager.DESCRIPTION));
                storedDate = homework.getString(homework.getColumnIndex(homework_manager.DAY_LIMIT));
                storedTimeLimit = homework.getString(homework.getColumnIndex(homework_manager.TIME_LIMIT));
                storedSignature = homework.getString(homework.getColumnIndex(homework_manager.COURSE));

                cursor_signature = sig_manager.getCourseColor(storedSignature);
                cursor_signature.moveToNext();
                storedColor = cursor_signature.getString(0);

                hw_week_of_year = dateValidation.getWeekOfYear(storedDate + " " + storedTimeLimit);

                if(hw_week_of_year == weekOfYear) {
                    if (calendar.before(dateValidation.formatDateANDTimeInPm(storedDate + " " + storedTimeLimit))) {
                        remainingTime = dateValidation.getRemainingTime(dateValidation.formatDateANDTimeInPm(storedDate + " " + storedTimeLimit));
                        events.add(new EventData(storedTitle, storedDescription, storedDate, storedTimeLimit, remainingTime, 'H', storedSignature, storedColor));
                    }
                }
            }

            events = sorter.bubbleSortRemainingTime(events);
            addEvents();
        }

        return v;
    }

    public void addEvents() {
        for (int i = 0; i < events.size(); i++) {
            if(events.get(i).type == 'H'){
                Bundle arguments = new Bundle();
                Fragment_upcoming_homework frag = new Fragment_upcoming_homework();
                arguments.putString(Fragment_home.NAME, events.get(i).name);
                arguments.putString(Fragment_home.DESCRIPTION, events.get(i).description);
                arguments.putString(Fragment_home.INITIAL_TIME, events.get(i).initial_time);
                arguments.putString(Fragment_home.DATE, events.get(i).initial_date);
                arguments.putLong(Fragment_home.REMAINING_TIME, events.get(i).remainingTime);
                arguments.putString(Fragment_home.SIG_PARENT, events.get(i).sig_parent);
                arguments.putString(Fragment_home.COLOR, events.get(i).color);
                arguments.putBoolean(Fragment_home.ACTIVATE_TIMER, false);
                arguments.putBoolean(Fragment_this_week_homework.THIS_WEEK, false);
                frag.setArguments(arguments);
                getFragmentManager().beginTransaction().add(R.id.event_container, frag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
            /*
            if (!events.isEmpty())
                removeTextNoEvents();
                */
        }
    }
}
