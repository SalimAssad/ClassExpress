package com.chacostak.salim.classexpress.Info_activities.Course_info;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Notifications_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.Fragment_courses;
import com.chacostak.salim.classexpress.Notifications.Basic_notifications.Fragment_notifications_container;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Schedules.Fragment_add_schedule;
import com.chacostak.salim.classexpress.Schedules.Fragment_schedules_info;

/**
 * Created by Salim on 27/03/2015.
 */
public class Fragment_course_info extends android.app.Fragment implements View.OnClickListener {

    View v;
    TextView textSignature, textTeacher, textDates;

    Cursor cursor;
    DB_Courses_Manager course_manager;
    DB_Schedule_Manager sch_manager;

    String course_name;
    String teacher;
    String initial_date;
    String ending_date;
    String color;

    boolean wasEdited = false;

    public static String course_parent;
    public static boolean openedFromSigInfo = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_signature_info, container, false);

        openedFromSigInfo = true;

        course_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        sch_manager = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if(savedInstanceState != null)
            course_name = savedInstanceState.getString(Course_info_activity.COURSE_NAME);
        else if(!wasEdited)
            course_name = getArguments().getString(Fragment_courses.SELECTED_COURSE);

        if (!wasEdited) {
            cursor = course_manager.searchByName(course_name);
            cursor.moveToNext();//If this is true, then it has not been edited
            initializeAtributtes();
        }

        initializeTexts();

        cursor = sch_manager.searchByCourse(course_name);

        if(savedInstanceState == null) {
            showSchedule();
            Fragment_notifications_container frag = new Fragment_notifications_container();
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_notifications_container.TAG, DB_Notifications_Manager.COURSE_TAG);
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.scroll_layout, frag).commit();
        }

        v.findViewById(R.id.add).setOnClickListener(this);

        getActivity().setTitle(course_name);

        return v;
    }

    @Override
    public void onDestroyView() {
        course_manager.closeDatabase();
        sch_manager.closeDatabase();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(Course_info_activity.COURSE_NAME, course_name);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy(){
        Fragment_schedules_info.schedules_added.clear();
        course_parent = "";
        openedFromSigInfo = false;
        super.onDestroy();
    }

    private void showSchedule() {
        while(cursor.moveToNext()){
            Fragment_schedules_info schedules_info = new Fragment_schedules_info();
            schedules_info.setArguments(prepareArguments());

            addInfo(schedules_info);
        }
    }

    public Bundle prepareArguments(){
        Bundle arguments = new Bundle();
        arguments.putString(Fragment_add_schedule.DAY, cursor.getString(cursor.getColumnIndex(sch_manager.DAY_OF_WEEK)));
        arguments.putString(Fragment_add_schedule.BEGINS, cursor.getString(cursor.getColumnIndex(sch_manager.START)));
        arguments.putString(Fragment_add_schedule.ENDS, cursor.getString(cursor.getColumnIndex(sch_manager.END)));
        return arguments;
    }

    private void addInfo(Fragment_schedules_info frag) {
        getFragmentManager().beginTransaction().add(R.id.schedule_container, frag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    public void initializeTexts() {
        textSignature = (TextView) v.findViewById(R.id.textCourse);
        textTeacher = (TextView) v.findViewById(R.id.showTeacher);
        textDates = (TextView) v.findViewById(R.id.showDates);

        textSignature.setText(course_name);
        textTeacher.setText(teacher);
        textDates.setText(initial_date + " - " + ending_date);

        textSignature.setBackgroundColor(Color.parseColor(color));
        textDates.setBackgroundColor(Color.parseColor(color));
    }

    private void initializeAtributtes() {
        course_name = cursor.getString(cursor.getColumnIndex(course_manager.SIGNATURE));
        course_parent = course_name;
        teacher = cursor.getString(cursor.getColumnIndex(course_manager.TEACHER_NAME));
        initial_date = cursor.getString(cursor.getColumnIndex(course_manager.START));
        ending_date = cursor.getString(cursor.getColumnIndex(course_manager.END));
        color = cursor.getString(cursor.getColumnIndex(course_manager.COLOR));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add:
                v.findViewById(R.id.add).setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fadein));
                getFragmentManager().beginTransaction().add(R.id.schedule_container, new Fragment_add_schedule())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
        }
    }
}
