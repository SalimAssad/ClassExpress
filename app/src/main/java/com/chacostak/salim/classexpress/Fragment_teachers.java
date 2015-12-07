package com.chacostak.salim.classexpress;

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

    public static final String SELECTED_TEACHER = "SELECTED_TEACHER";

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
    public void onResume(){
        super.onResume();
        cursor = teacher_manager.getAll();
        prepareAdapter();
        prepareListView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(action_helper.action != null)
            action_helper.select(i);
        else {
            list.setItemChecked(i, false); //If action mode isn't activated this deselect the position
            Intent intent = new Intent(getActivity(), Teacher_info_activity.class);
            intent.putExtra(SELECTED_TEACHER, ((EventData) adapterView.getItemAtPosition(i)).name);
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
            data.get(data.size()-1).name = cursor.getString(cursor.getColumnIndex(teacher_manager.NAME));
            data.get(data.size()-1).email = cursor.getString(cursor.getColumnIndex(teacher_manager.EMAIL));
            data.get(data.size()-1).phone = cursor.getString(cursor.getColumnIndex(teacher_manager.PHONE));
            data.get(data.size()-1).web_page = cursor.getString(cursor.getColumnIndex(teacher_manager.WEB_PAGE));
        }

        adapter = new CustomAdapter(getActivity(),R.layout.custom_list_view, data);
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
        Intent intent = new Intent(getActivity(), Add_teacher_activity.class);
        startActivity(intent);
    }
}
