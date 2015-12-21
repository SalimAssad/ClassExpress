package com.chacostak.salim.classexpress.Info_activities.Exam_info;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Add_exam.Fragment_add_exam;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Notifications.Specific_notifications.Fragment_specific_notifications_container;
import com.chacostak.salim.classexpress.R;

/**
 * Created by Salim on 27/04/2015.
 */
public class Fragment_exam_info extends Fragment {

    View v;
    TextView textCourse, textRoom, textDayLimit;

    Cursor cursor;
    DB_Exams_Manager exams_manager;

    String room;
    String day_limit;
    String time_limit;
    String course_name;

    boolean wasEdited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_exam_info, container, false);

        exams_manager = new DB_Exams_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if(savedInstanceState != null) {
            day_limit = savedInstanceState.getString(Fragment_add_exam.DAY_LIMIT);
            time_limit = savedInstanceState.getString(Fragment_add_exam.TIME_LIMIT);
        }else if(!wasEdited) {
            day_limit = getArguments().getString(Fragment_add_exam.DAY_LIMIT);
            time_limit = getArguments().getString(Fragment_add_exam.TIME_LIMIT);
        }

        if(savedInstanceState == null){
            Fragment_specific_notifications_container frag = new Fragment_specific_notifications_container();
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_specific_notifications_container.TAG, day_limit + " - " + time_limit);
            arguments.putChar(Fragment_specific_notifications_container.TYPE, 'E');
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.scroll_layout, frag).commit();
        }

        if (!wasEdited) {
            cursor = exams_manager.search(day_limit, time_limit);
            cursor.moveToNext();//If this is true, then it has not been edited
            initializeAtributtes();
        }

        initializeTexts();
        setColor();

        return v;
    }

    private void setColor() {
        Cursor cursor = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version).getCourseColor(course_name);
        cursor.moveToNext();
        String color = cursor.getString(0);
        textCourse.setBackgroundColor(Color.parseColor(color));
        textDayLimit.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(Fragment_add_exam.DAY_LIMIT, day_limit);
        savedInstanceState.putString(Fragment_add_exam.TIME_LIMIT, time_limit);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void initializeTexts() {
        textRoom = (TextView) v.findViewById(R.id.showRoom);
        textDayLimit = (TextView) v.findViewById(R.id.showDayLimit);
        textCourse = (TextView) v.findViewById(R.id.textCourse);

        textRoom.setText(getString(R.string.room) + " " + room);
        textDayLimit.setText(day_limit + " - " + time_limit);
        textCourse.setText(getString(R.string.exam) + " - " + course_name);
    }

    private void initializeAtributtes() {
        room = cursor.getString(cursor.getColumnIndex(exams_manager.ROOM));
        day_limit = cursor.getString(cursor.getColumnIndex(exams_manager.DAY_LIMIT));
        time_limit = cursor.getString(cursor.getColumnIndex(exams_manager.TIME_LIMIT));
        course_name = cursor.getString(cursor.getColumnIndex(exams_manager.COURSE));
    }
}
