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

import com.chacostak.salim.classexpress.Add_vacation.Add_vacation_activity;
import com.chacostak.salim.classexpress.Add_vacation.Fragment_add_vacation;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;
import com.chacostak.salim.classexpress.Info_activities.Vacation_info.Vacation_info_activity;

import java.util.ArrayList;

/**
 * Created by Salim on 31/03/2015.
 */
public class Fragment_vacations extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    View v;
    DB_Vacations_Manager vac_manager;
    Cursor cursor_vac;
    CustomAdapter adapter;
    ArrayList<EventData> data;
    ListView list;
    ActionVac action_helper;

    DateValidation dateValidation;

    int selectedIndex = 0;

    public static final String SELECTED_VACATION = "SELECTED_VACATION";

    final int DATA_SAVED = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_vacations, container, false);

        dateValidation = new DateValidation(getActivity());

        vac_manager = new DB_Vacations_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        cursor_vac = vac_manager.getAll();

        prepareAdapter();
        prepareListView();

        v.findViewById(R.id.add_button).setOnClickListener(this);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (action_helper.action != null)
            action_helper.select(i);
        else {
            selectedIndex = i;
            list.setItemChecked(i, false); //If action mode isn't activated this deselect the position
            Intent intent = new Intent(getActivity(), Vacation_info_activity.class);
            intent.putExtra(SELECTED_VACATION, ((EventData) adapterView.getItemAtPosition(i)).name);
            startActivityForResult(intent, DATA_SAVED);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (action_helper.action != null)
            return false;

        action_helper.action = getActivity().startActionMode(action_helper);
        action_helper.select(i);
        return true;
    }

    //Sets up the adapter reading the values from the data base
    private void prepareAdapter() {
        data = new ArrayList<>();
        String date1;
        String date2;
        while (cursor_vac.moveToNext()) {
            data.add(new EventData());
            data.get(data.size() - 1).name = cursor_vac.getString(cursor_vac.getColumnIndex(vac_manager.TITLE));
            date1 = cursor_vac.getString(cursor_vac.getColumnIndex(vac_manager.START));
            date2 = cursor_vac.getString(cursor_vac.getColumnIndex(vac_manager.END));
            if(date1.equals(date2))
                data.get(data.size() - 1).description = date1;
            else
                data.get(data.size() - 1).description = date1 + " - " + date2;
        }

        adapter = new CustomAdapter(getActivity(), R.layout.custom_list_view, data);

        cursor_vac.close();
    }

    //Sets the listview of the signatures
    private void prepareListView() {
        list = (ListView) v.findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        action_helper = new ActionVac(getActivity(), adapter);
    }

    @Override
    public void onClick(View v) {
        selectedIndex = -1;
        Intent intent = new Intent(getActivity(), Add_vacation_activity.class);
        startActivityForResult(intent, DATA_SAVED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        switch (requestCode) {
            case DATA_SAVED:
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {  //Gets the info that has been edited
                    boolean remove = intentData.getBooleanExtra(Fragment_add_vacation.REMOVE, false);
                    if(remove){
                        adapter.remove(adapter.getItem(selectedIndex));
                    }else {
                        String title = intentData.getStringExtra(Fragment_add_vacation.TITLE);
                        String date1 = intentData.getStringExtra(Fragment_add_vacation.INITIAL_DATE);
                        String date2 = intentData.getStringExtra(Fragment_add_vacation.ENDING_DATE);

                        if (selectedIndex == -1) { //-1 is used as a constant to indicate that a course has added
                            EventData data = new EventData();
                            data.name = title;
                            if(date1.equals(date2))
                                data.description = date1;
                            else
                                data.description = date1 + " - " + date2;

                            adapter.add(data);
                        } else {
                            ((EventData) adapter.getItem(selectedIndex)).name = title;
                            if(date1.equals(date2))
                                ((EventData) adapter.getItem(selectedIndex)).description = date1;
                            else
                                ((EventData) adapter.getItem(selectedIndex)).description = date1 + " - " + date2;
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
