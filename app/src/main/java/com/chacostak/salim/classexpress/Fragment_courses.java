package com.chacostak.salim.classexpress;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chacostak.salim.classexpress.Add_course.Add_course_activity;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Info_activities.Signature_info.Signature_info_activity;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;

public class Fragment_courses extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    View v;
    DB_Courses_Manager sig_manager;
    Cursor cursor;
    CustomAdapter adapter;
    ArrayList<EventData> data;
    ListView list;
    ActionSig action_helper;

    public static String SELECTED_SIGNATURE = "SELECTED_SIGNATURE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_signatures,container,false);

        sig_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        cursor = sig_manager.getAll();

        prepareAdapter();
        prepareListView();

        v.findViewById(R.id.add_button).setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        cursor = sig_manager.getAll();
        prepareAdapter();
        prepareListView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(action_helper.action != null)
            action_helper.select(i);
        else {
            list.setItemChecked(i, false); //If action mode isn't activated this deselect the position
            Intent intent = new Intent(getActivity(), Signature_info_activity.class);
            intent.putExtra(SELECTED_SIGNATURE, ((EventData) adapterView.getItemAtPosition(i)).name);
            startActivity(intent);
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
        while(cursor.moveToNext()){
            data.add(new EventData());
            data.get(data.size()-1).name = cursor.getString(cursor.getColumnIndex(sig_manager.SIGNATURE));
            data.get(data.size()-1).teacher = cursor.getString(cursor.getColumnIndex(sig_manager.TEACHER_NAME));
            data.get(data.size()-1).color = cursor.getString(cursor.getColumnIndex(sig_manager.COLOR));
        }

        adapter = new CustomAdapter(getActivity(),R.layout.custom_list_view, data);
    }

    //Sets the listview of the signatures
    private void prepareListView() {
        list = (ListView) v.findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        action_helper = new ActionSig(getActivity(), adapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), Add_course_activity.class);
        startActivity(intent);
    }
}
