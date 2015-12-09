package com.chacostak.salim.classexpress.Day_courses;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;
import com.chacostak.salim.classexpress.Utilities.CourseValidation;
import com.chacostak.salim.classexpress.Utilities.Sorter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 12/04/2015.
 */
public class Fragment_day_courses extends Fragment implements AdapterView.OnItemClickListener, View.OnTouchListener {

    View v;
    Day_course_adapter adapter;
    ListView list;
    ViewFlipper flipper;

    Calendar calendar;

    String dayName = "dummy";
    int dayOfWeek;

    float firstX = 0;

    DB_Schedule_Manager schedule;
    DB_Courses_Manager sig_manager;
    Cursor cursor;

    ArrayList<EventData> data;
    Sorter sort = new Sorter();

    DateValidation dateValidation;
    CourseValidation courseValidation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_day_course, container, false);

        if(getArguments() != null) {
            dayName = getArguments().getString(Fragment_home.DATE);
            dayOfWeek = getArguments().getInt(Fragment_home.DAY_OF_WEEK);
        }

        flipper = (ViewFlipper) v.findViewById(R.id.viewFlipper);

        calendar = Calendar.getInstance();

        schedule = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        sig_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        dateValidation = new DateValidation(getActivity());
        courseValidation = new CourseValidation();

        data = new ArrayList<>();

        prepareAdapter();
        prepareListview();

        list.setOnTouchListener(this);

        getActivity().setTitle(dayName);

        return v;
    }

    //Sets up the adapter reading the values from the data base
    private void prepareAdapter(){
        data.clear();
        cursor = schedule.searchByDay(dayName);
        Cursor sig_cursor;
        String signature, starts, ends;
        while(cursor.moveToNext()){
            signature = cursor.getString(cursor.getColumnIndex(schedule.COURSE));
            sig_cursor = sig_manager.searchByName(signature);
            sig_cursor.moveToNext();
            starts = sig_cursor.getString(sig_cursor.getColumnIndex(sig_manager.START));
            ends = sig_cursor.getString(sig_cursor.getColumnIndex(sig_manager.END));
            if(courseValidation.stillInCourse(getActivity(), starts, ends)) {
                data.add(new EventData());
                data.get(data.size() - 1).name = signature;
                data.get(data.size() - 1).initial_time = cursor.getString(cursor.getColumnIndex(schedule.START));
                data.get(data.size() - 1).ending_time = cursor.getString(cursor.getColumnIndex(schedule.END));
                data.get(data.size() - 1).remainingTime = dateValidation.getRemainingTime(dateValidation.formatTimeInPm(data.get(data.size() - 1).initial_time));
                data.get(data.size() - 1).color = sig_cursor.getString(sig_cursor.getColumnIndex(sig_manager.COLOR));
            }
            sig_cursor.close();
        }
        cursor.close();
        data = sort.bubbleSortRemainingTime(data);
        adapter = new Day_course_adapter(getActivity(),R.layout.day_sig_listview, data);
    }


    //Sets up the adapter reading the values from the data base
    private void updateAdapter(int addOrSubstract){
        data.clear();
        cursor = schedule.searchByDay(dayName);
        Cursor sig_cursor;
        String signature, starts, ends;
        while(cursor.moveToNext()){
            signature = cursor.getString(cursor.getColumnIndex(schedule.COURSE));
            sig_cursor = sig_manager.searchByName(signature);
            sig_cursor.moveToNext();
            starts = sig_cursor.getString(sig_cursor.getColumnIndex(sig_manager.START));
            ends = sig_cursor.getString(sig_cursor.getColumnIndex(sig_manager.END));
            calendar.add(Calendar.DATE, addOrSubstract);
            if(courseValidation.stillInCourse(getActivity(), calendar, starts, ends)) {  //TODO: DEBUG THIS METHOD
                data.add(new EventData());
                data.get(data.size() - 1).name = signature;
                data.get(data.size() - 1).initial_time = cursor.getString(cursor.getColumnIndex(schedule.START));
                data.get(data.size() - 1).ending_time = cursor.getString(cursor.getColumnIndex(schedule.END));
                data.get(data.size() - 1).remainingTime = dateValidation.getRemainingTime(dateValidation.formatTimeInPm(data.get(data.size() - 1).initial_time));
                data.get(data.size() - 1).color = sig_cursor.getString(sig_cursor.getColumnIndex(sig_manager.COLOR));
            }
            sig_cursor.close();
        }
        cursor.close();
        data = sort.bubbleSortRemainingTime(data);
        adapter = new Day_course_adapter(getActivity(),R.layout.day_sig_listview, data);
    }

    private void prepareListview() {
        list = (ListView) v.findViewById(R.id.listView);
        list.setAdapter(adapter);

        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                firstX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float lastX = event.getX();
                if((lastX-firstX) < 40 && (lastX-firstX) > -40)
                    break;

                if(lastX < firstX){
                    if(dayOfWeek == 7)
                        dayOfWeek = 1;
                    else
                        dayOfWeek++;

                    dayName = dateValidation.getDayName(dayOfWeek);
                    updateAdapter(1);
                    list.setAdapter(adapter);

                    flipper.setInAnimation(getActivity(), R.anim.slide_in_right_to_left);
                    flipper.setOutAnimation(getActivity(), R.anim.slide_out_right_to_left);

                    flipper.showNext();
                }else{
                    if(dayOfWeek == 1)
                        dayOfWeek = 7;
                    else
                        dayOfWeek--;

                    dayName = dateValidation.getDayName(dayOfWeek);
                    updateAdapter(-1);
                    list.setAdapter(adapter);

                    flipper.setInAnimation(getActivity(), R.anim.slide_in_left_to_right);
                    flipper.setOutAnimation(getActivity(), R.anim.slide_out_left_to_right);

                    flipper.showPrevious();
                }

                getActivity().setTitle(dayName);
                break;
        }
        return true; //If false, won't trigger ACTION_UP
    }
}
