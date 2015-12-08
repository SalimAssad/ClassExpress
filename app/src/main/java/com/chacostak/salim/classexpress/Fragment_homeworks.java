package com.chacostak.salim.classexpress;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.chacostak.salim.classexpress.Add_homework.Add_homework_activity;
import com.chacostak.salim.classexpress.Add_homework.Fragment_add_homework;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Info_activities.Homework_info.Homework_info_activity;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;

/**
 * Created by Salim on 31/03/2015.
 */
public class Fragment_homeworks extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    View v;
    DB_Homework_Manager hw_manager;
    DB_Courses_Manager course_manager;
    Cursor cursor_hw;
    CustomAdapter adapter;
    ArrayList<EventData> data;
    ListView list;
    ActionHw action_helper;

    int selectedIndex = 0;

    DateValidation dateValidation;

    public static final String SELECTED_HOMEWORK = "SELECTED_HOMEWORK";

    final int DATA_SAVED = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_homeworks, container, false);

        dateValidation = new DateValidation(getActivity());

        hw_manager = new DB_Homework_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        course_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        cursor_hw = hw_manager.getAll();

        prepareAdapter();
        prepareListView();

        v.findViewById(R.id.add_button).setOnClickListener(this);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(action_helper.action != null)
            action_helper.select(i);
        else {
            selectedIndex = i;
            list.setItemChecked(i, false); //If action mode isn't activated this deselect the position
            Intent intent = new Intent(getActivity(), Homework_info_activity.class);
            intent.putExtra(SELECTED_HOMEWORK, ((EventData) adapterView.getItemAtPosition(i)).name);
            startActivityForResult(intent, DATA_SAVED);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(action_helper.action != null)
            return false;

        action_helper.action = getActivity().startActionMode(action_helper);
        action_helper.select(i);
        return true;
    }

    //Sets up the adapter reading the values from the data base
    private void prepareAdapter(){
        data = new ArrayList<>();
        Cursor cursor_course;
        while(cursor_hw.moveToNext()){
            cursor_course = course_manager.getCourseColor(cursor_hw.getString(cursor_hw.getColumnIndex(hw_manager.COURSE)));
            cursor_course.moveToNext();
            data.add(new EventData());
            data.get(data.size()-1).name = cursor_hw.getString(cursor_hw.getColumnIndex(hw_manager.TITLE));
            data.get(data.size()-1).description = cursor_hw.getString(cursor_hw.getColumnIndex(hw_manager.DESCRIPTION));
            data.get(data.size()-1).color = cursor_course.getString(0);
            data.get(data.size()-1).remainingTime = dateValidation.getRemainingTime(
                    dateValidation.formatDateANDTimeInPm(cursor_hw.getString(cursor_hw.getColumnIndex(hw_manager.DAY_LIMIT)) + " " +
                            cursor_hw.getString(cursor_hw.getColumnIndex(hw_manager.TIME_LIMIT))));

            cursor_course.close();
        }

        adapter = new CustomAdapter(getActivity(),R.layout.custom_list_view, data);

        cursor_hw.close();
    }

    //Sets the listview of the signatures
    private void prepareListView() {
        list = (ListView) v.findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        action_helper = new ActionHw(getActivity(), adapter);
    }

    @Override
    public void onClick(View v) {
        Cursor cursor = course_manager.getAll();
        if(cursor.moveToNext()) {
            selectedIndex = -1;
            Intent intent = new Intent(getActivity(), Add_homework_activity.class);
            startActivityForResult(intent, DATA_SAVED);
        }else
            Toast.makeText(getActivity(), getString(R.string.no_courses_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        switch (requestCode) {
            case DATA_SAVED:
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {  //Gets the info that has been edited
                    boolean remove = intentData.getBooleanExtra(Fragment_add_homework.REMOVE, false);
                    if(remove){
                        adapter.remove(adapter.getItem(selectedIndex));
                    }else {
                        String course_name = intentData.getStringExtra(Fragment_add_homework.COURSE_NAME);
                        String title = intentData.getStringExtra(Fragment_add_homework.TITLE);
                        String description = intentData.getStringExtra(Fragment_add_homework.DESCRIPTION);
                        String color = getColor(course_name);
                        String date = intentData.getStringExtra(Fragment_add_homework.DAY_LIMIT) + " - " + intentData.getStringExtra(Fragment_add_homework.TIME_LIMIT);
                        String array[] = date.split(" - ");
                        long remainingTime = dateValidation.getRemainingTime(
                                dateValidation.formatDateANDTimeInPm(array[0] + " " + array[1]));

                        if (selectedIndex == -1) { //-1 is used as a constant to indicate that a course has added
                            EventData data = new EventData();
                            data.name = title;
                            data.description = description;
                            data.remainingTime = remainingTime;
                            data.color = color;
                            adapter.add(data);
                        } else {
                            ((EventData) adapter.getItem(selectedIndex)).name = title;
                            ((EventData) adapter.getItem(selectedIndex)).description = description;
                            ((EventData) adapter.getItem(selectedIndex)).remainingTime = remainingTime;
                            ((EventData) adapter.getItem(selectedIndex)).color = color;
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private String getColor(String course_name) {
        Cursor cursor = course_manager.getCourseColor(course_name);
        if(cursor.moveToNext()){
            String color = cursor.getString(cursor.getColumnIndex(course_manager.COLOR));
            cursor.close();
            return color;
        }else{
            cursor.close();
            return null;
        }
    }
}
