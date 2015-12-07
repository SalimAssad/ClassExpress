package com.chacostak.salim.classexpress.Info_activities.Signature_info;

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
public class Fragment_signature_info extends android.app.Fragment implements View.OnClickListener {

    View v;
    TextView textSignature, textTeacher, textDates, textEnds;

    Cursor cursor;
    DB_Courses_Manager sig_manager;
    DB_Schedule_Manager sch_manager;

    String signature_name;
    String teacher;
    String initial_date;
    String ending_date;
    String color;

    boolean wasEdited = false;

    public static String signature_parent;
    public static boolean openedFromSigInfo = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_signature_info, container, false);

        openedFromSigInfo = true;

        sig_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        sch_manager = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if(savedInstanceState != null)
            signature_name = savedInstanceState.getString(Signature_info_activity.SIGNATURE_NAME);
        else if(!wasEdited)
            signature_name = getArguments().getString(Fragment_courses.SELECTED_SIGNATURE);

        if (!wasEdited) {
            cursor = sig_manager.searchByName(signature_name);
            cursor.moveToNext();//If this is true, then it has not been edited
            initializeAtributtes();
        }

        initializeTexts();

        cursor = sch_manager.searchBySignature(signature_name);

        if(savedInstanceState == null) {
            showSchedule();
            Fragment_notifications_container frag = new Fragment_notifications_container();
            Bundle arguments = new Bundle();
            arguments.putString(Fragment_notifications_container.TAG, DB_Notifications_Manager.COURSE_TAG);
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.scroll_layout, frag).commit();
        }

        v.findViewById(R.id.add).setOnClickListener(this);

        getActivity().setTitle(signature_name);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(Signature_info_activity.SIGNATURE_NAME, signature_name);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy(){
        Fragment_schedules_info.schedules_added.clear();
        signature_parent = "";
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
        textSignature = (TextView) v.findViewById(R.id.showSignature);
        textTeacher = (TextView) v.findViewById(R.id.showTeacher);
        textDates = (TextView) v.findViewById(R.id.showDates);

        textSignature.setText(signature_name);
        textTeacher.setText(teacher);
        textDates.setText(initial_date + " - " + ending_date);

        textSignature.setBackgroundColor(Color.parseColor(color));
        textDates.setBackgroundColor(Color.parseColor(color));
    }

    private void initializeAtributtes() {
        signature_name = cursor.getString(cursor.getColumnIndex(sig_manager.SIGNATURE));
        signature_parent = signature_name;
        teacher = cursor.getString(cursor.getColumnIndex(sig_manager.TEACHER_NAME));
        initial_date = cursor.getString(cursor.getColumnIndex(sig_manager.START));
        ending_date = cursor.getString(cursor.getColumnIndex(sig_manager.END));
        color = cursor.getString(cursor.getColumnIndex(sig_manager.COLOR));
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
