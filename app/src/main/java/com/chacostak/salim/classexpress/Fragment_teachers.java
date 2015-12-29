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

import com.chacostak.salim.classexpress.Add_teacher.Add_teacher_activity;
import com.chacostak.salim.classexpress.Add_teacher.Fragment_add_teacher;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Teacher_Manager;
import com.chacostak.salim.classexpress.Info_activities.Teacher_info.Teacher_info_activity;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;

/**
 * Created by Salim on 05/04/2015.
 */
public class Fragment_teachers extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    View v;
    DB_Teacher_Manager teacher_manager;
    Cursor cursor;
    CustomAdapter adapter;
    ArrayList<EventData> data;
    ListView list;
    ActionTeach action_helper;

    int selectedIndex = 0;

    public static final String SELECTED_TEACHER = "SELECTED_TEACHER";

    final int DATA_SAVED = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_teachers, container, false);

        teacher_manager = new DB_Teacher_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        cursor = teacher_manager.getAll();

        prepareAdapter();
        prepareListView();

        v.findViewById(R.id.add_button).setOnClickListener(this);

        return v;
    }

    @Override
    public void onDestroyView() {
        teacher_manager.closeDatabase();

        if(action_helper != null) {
            action_helper.closeDatabase();
            action_helper = null;
        }

        super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(action_helper.action != null)
            action_helper.select(i);
        else {
            selectedIndex = i;
            list.setItemChecked(i, false); //If action mode isn't activated this deselect the position
            Intent intent = new Intent(getActivity(), Teacher_info_activity.class);
            intent.putExtra(SELECTED_TEACHER, ((EventData) adapterView.getItemAtPosition(i)).name);
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
        while(cursor.moveToNext()){
            data.add(new EventData());
            data.get(data.size()-1).name = cursor.getString(cursor.getColumnIndex(teacher_manager.NAME));
            data.get(data.size()-1).email = cursor.getString(cursor.getColumnIndex(teacher_manager.EMAIL));
            data.get(data.size()-1).phone = cursor.getString(cursor.getColumnIndex(teacher_manager.PHONE));
            data.get(data.size()-1).web_page = cursor.getString(cursor.getColumnIndex(teacher_manager.WEB_PAGE));
        }

        adapter = new CustomAdapter(getActivity(),R.layout.custom_list_view, data);

        cursor.close();
    }

    //Sets the listview of the signatures
    private void prepareListView() {
        list = (ListView) v.findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        action_helper = new ActionTeach(getActivity(), adapter);
    }

    @Override
    public void onClick(View v) {
        selectedIndex = -1;
        Intent intent = new Intent(getActivity(), Add_teacher_activity.class);
        startActivityForResult(intent, DATA_SAVED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        switch (requestCode) {
            case DATA_SAVED:
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {  //Gets the info that has been edited
                    boolean remove = intentData.getBooleanExtra(Fragment_add_teacher.REMOVE, false);
                    if(remove){
                        adapter.remove(adapter.getItem(selectedIndex));
                    }else {
                        String name = intentData.getStringExtra(Fragment_add_teacher.NAME);
                        String email = intentData.getStringExtra(Fragment_add_teacher.EMAIL);
                        String phone = intentData.getStringExtra(Fragment_add_teacher.PHONE);
                        String web_page = intentData.getStringExtra(Fragment_add_teacher.WEB_PAGE);

                        if (selectedIndex == -1) { //-1 is used as a constant to indicate that a course has added
                            EventData data = new EventData();
                            data.name = name;
                            data.email = email;
                            data.phone = phone;
                            data.web_page = web_page;
                            adapter.add(data);
                        } else {
                            ((EventData) adapter.getItem(selectedIndex)).name = name;
                            ((EventData) adapter.getItem(selectedIndex)).email = email;
                            ((EventData) adapter.getItem(selectedIndex)).phone = phone;
                            ((EventData) adapter.getItem(selectedIndex)).web_page = web_page;
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
