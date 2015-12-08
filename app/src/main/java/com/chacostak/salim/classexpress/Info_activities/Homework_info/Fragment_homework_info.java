package com.chacostak.salim.classexpress.Info_activities.Homework_info;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Add_homework.Fragment_add_homework;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Fragment_homeworks;
import com.chacostak.salim.classexpress.Notifications.Specific_notifications.Fragment_specific_notifications_container;
import com.chacostak.salim.classexpress.R;

/**
 * Created by Salim on 04/04/2015.
 */
public class Fragment_homework_info extends Fragment {

    View v;
    TextView textSignature, textTitle, textDescription, textDayLimit;

    Cursor cursor;
    DB_Homework_Manager hw_manager;

    String title;
    String description;
    String day_limit;
    String time_limit;
    String course_name;

    boolean wasEdited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_homework_info, container, false);

        hw_manager = new DB_Homework_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if(savedInstanceState != null)
            title = savedInstanceState.getString(Fragment_add_homework.TITLE);
        else if(!wasEdited)
            title = getArguments().getString(Fragment_homeworks.SELECTED_HOMEWORK);

        if(savedInstanceState == null){
            Fragment_specific_notifications_container frag = new Fragment_specific_notifications_container();
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_specific_notifications_container.TAG, title);
            arguments.putChar(Fragment_specific_notifications_container.TYPE, 'H');
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.scroll_layout, frag).commit();
        }

        if (!wasEdited) {
            cursor = hw_manager.searchByTitle(title);
            cursor.moveToNext();//If this is true, then it has not been edited
            initializeAtributtes();
        }

        initializeTexts();
        setColors();

        getActivity().setTitle(title);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(Fragment_add_homework.TITLE, title);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void initializeTexts() {
        textTitle = (TextView) v.findViewById(R.id.showTitle);
        textDescription = (TextView) v.findViewById(R.id.showDescription);
        textDayLimit = (TextView) v.findViewById(R.id.showDayLimit);
        textSignature = (TextView) v.findViewById(R.id.showSignature);

        textTitle.setText(title);
        if(!description.equals(""))
            textDescription.setText(description);
        textDayLimit.setText(day_limit + " - " + time_limit);
        textSignature.setText(course_name);
    }

    private void setColors() {
        Cursor course = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version).getCourseColor(course_name);
        course.moveToNext();
        String color = course.getString(0);
        textTitle.setBackgroundColor(Color.parseColor(color));
        textDayLimit.setBackgroundColor(Color.parseColor(color));

        course.close();
    }

    private void initializeAtributtes() {
        title = cursor.getString(cursor.getColumnIndex(hw_manager.TITLE));
        description = cursor.getString(cursor.getColumnIndex(hw_manager.DESCRIPTION));
        day_limit = cursor.getString(cursor.getColumnIndex(hw_manager.DAY_LIMIT));
        time_limit = cursor.getString(cursor.getColumnIndex(hw_manager.TIME_LIMIT));
        course_name = cursor.getString(cursor.getColumnIndex(hw_manager.COURSE));
    }
}
