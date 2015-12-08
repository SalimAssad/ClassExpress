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

import com.chacostak.salim.classexpress.Add_exam.Add_exam_activity;
import com.chacostak.salim.classexpress.Add_exam.Fragment_add_exam;
import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Info_activities.Exam_info.Exam_info_activity;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;

/**
 * Created by Salim on 26/04/2015.
 */
public class Fragment_exams extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    View v;
    DB_Exams_Manager exams_manager;
    DB_Courses_Manager course_manager;
    Cursor cursor_exam;
    CustomAdapter adapter;
    ArrayList<EventData> data;
    ListView list;
    ActionExam action_helper;

    int selectedIndex = 0;

    DateValidation dateValidation;

    public static final String SELECTED_EXAM = "SELECTED_EXAM";

    final int DATA_SAVED = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_homeworks, container, false);

        dateValidation = new DateValidation(getActivity());

        exams_manager = new DB_Exams_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        course_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        cursor_exam = exams_manager.getAll();

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
            Intent intent = new Intent(getActivity(), Exam_info_activity.class);
            intent.putExtra(SELECTED_EXAM, ((EventData) adapterView.getItemAtPosition(i)).description);
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
        while(cursor_exam.moveToNext()){
            cursor_course = course_manager.getCourseColor(cursor_exam.getString(cursor_exam.getColumnIndex(exams_manager.COURSE)));
            cursor_course.moveToNext();
            data.add(new EventData());
            data.get(data.size()-1).name = cursor_exam.getString(cursor_exam.getColumnIndex(exams_manager.COURSE));
            data.get(data.size()-1).description = cursor_exam.getString(cursor_exam.getColumnIndex(exams_manager.DAY_LIMIT))+ " - " + cursor_exam.getString(cursor_exam.getColumnIndex(exams_manager.TIME_LIMIT));
            data.get(data.size()-1).color = cursor_course.getString(0);
            data.get(data.size()-1).remainingTime = dateValidation.getRemainingTime(
                    dateValidation.formatDateANDTimeInPm(cursor_exam.getString(cursor_exam.getColumnIndex(exams_manager.DAY_LIMIT)) + " " +
                            cursor_exam.getString(cursor_exam.getColumnIndex(exams_manager.TIME_LIMIT))));

            cursor_course.close();
        }

        adapter = new CustomAdapter(getActivity(),R.layout.custom_list_view, data);

        cursor_exam.close();
    }

    //Sets the listview of the exams
    private void prepareListView() {
        list = (ListView) v.findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        action_helper = new ActionExam(getActivity(), adapter);
    }

    @Override
    public void onClick(View v) {
        Cursor cursor = course_manager.getAll();
        if(cursor.moveToNext()) {
            selectedIndex = -1;
            Intent intent = new Intent(getActivity(), Add_exam_activity.class);
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
                    boolean remove = intentData.getBooleanExtra(Fragment_add_exam.REMOVE, false);
                    if(remove){
                        adapter.remove(adapter.getItem(selectedIndex));
                    }else {
                        String course_name = intentData.getStringExtra(Fragment_add_exam.COURSE_NAME);
                        String description = intentData.getStringExtra(Fragment_add_exam.DAY_LIMIT) + " - " + intentData.getStringExtra(Fragment_add_exam.TIME_LIMIT);
                        String color = getColor(course_name);
                        String array[] = description.split(" - ");
                        long remainingTime = dateValidation.getRemainingTime(
                                dateValidation.formatDateANDTimeInPm(array[0] + " " + array[1]));

                        if (selectedIndex == -1) { //-1 is used as a constant to indicate that a course has added
                            EventData data = new EventData();
                            data.name = course_name;
                            data.description = description;
                            data.remainingTime = remainingTime;
                            data.color = color;
                            adapter.add(data);
                        } else {
                            ((EventData) adapter.getItem(selectedIndex)).name = course_name;
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
