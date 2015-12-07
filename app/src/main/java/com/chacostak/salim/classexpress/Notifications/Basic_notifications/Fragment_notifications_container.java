package com.chacostak.salim.classexpress.Notifications.Basic_notifications;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Notifications_Manager;
import com.chacostak.salim.classexpress.R;

import java.util.ArrayList;

/**
 * Created by Salim on 17/06/2015.
 */
public class Fragment_notifications_container extends Fragment implements View.OnClickListener {

    View v;
    TextView title;

    public static final String TAG = "TAG";
    static final String COUNTER = "COUNTER";

    int counter = 0;

    String tag;

    DB_Notifications_Manager notifications_manager;

    static final ArrayList<Notification_data> notifications = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_notifications_container, container, false);

        notifications_manager = new DB_Notifications_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        if(getArguments() != null)
            tag = getArguments().getString(TAG);

        if(savedInstanceState == null)
            showNotifications(true);
        else
            showNotifications(false);

        v.findViewById(R.id.add_notification).setOnClickListener(this);

        if(tag.equals(DB_Notifications_Manager.COURSE_TAG) || tag.equals(DB_Notifications_Manager.EXAM_TAG)) {
            title = (TextView) v.findViewById(R.id.text_title);
            title.setText(getString(R.string.global_notifications));
        }

        return v;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        notifications.clear();
    }

    private void showNotifications(boolean flag) {
        Cursor cursor = notifications_manager.searchByTag(tag);
        while(cursor.moveToNext()){
            restoreNotifications(cursor.getInt(cursor.getColumnIndex(notifications_manager.TIME_BEFORE)), cursor.getString(cursor.getColumnIndex(notifications_manager.UNIT_TYPE)), flag);
        }
    }

    private void restoreNotifications(int time_before, String unit, boolean flag){
        notifications.add(new Notification_data());
        notifications.get(counter).tag = tag;
        notifications.get(counter).new_time = time_before;
        notifications.get(counter).new_unit = unit;

        if(flag) {
            Fragment_notifications frag = new Fragment_notifications();
            Bundle arguments = new Bundle();
            arguments.putInt(COUNTER, counter);
            frag.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.notifications_container, frag).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).commit();
        }
        counter++;
    }

    @Override
    public void onClick(View v) {
        notifications.add(new Notification_data());
        notifications.get(counter).tag = tag;

        Fragment_notifications frag = new Fragment_notifications();
        Bundle arguments = new Bundle();
        arguments.putInt(COUNTER, counter);
        frag.setArguments(arguments);
        getFragmentManager().beginTransaction().add(R.id.notifications_container, frag).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).commit();
        counter++;
    }
}
