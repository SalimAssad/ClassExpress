package com.chacostak.salim.classexpress;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Utilities.Dialogs;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;

/**
 * Created by Salim on 26/04/2015.
 */
public class ActionExam implements ActionMode.Callback, DialogInterface.OnClickListener {

    DB_Exams_Manager exams_manager;

    ActionMode action;
    ArrayList<EventData> selected = new ArrayList<>();

    CustomAdapter adapter;

    public ActionExam(Context activity, CustomAdapter xadapter){
        exams_manager = new DB_Exams_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        adapter = xadapter;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.action_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.action_delete:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(adapter.getContext());
                if(sharedPreferences.getBoolean("general_confirmations", true) && sharedPreferences.getBoolean("confirmation_exams", true))
                    new Dialogs(adapter.getContext()).createConfirmationDialog(R.string.delete, R.string.are_you_sure, R.drawable.ic_launcher, this);
                else
                    delete();
                return true;
            default:
                return false;
        }
    }

    private void delete() {
        for(int i = 0; i < selected.size(); i++){
            adapter.remove(selected.get(i));
            exams_manager.delete(selected.get(i).description.split(" - ")[0], selected.get(i).description.split(" - ")[1]);
        }
        adapter.notifyDataSetChanged();

        action.finish();
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        action = null;
        deselectAll();
    }

    public void finish(){
        action.finish();
    }

    //Determines the action that must be done when something is selected in action mode
    public void select(int pos){
        if(isAlreadySelected(pos)) {
            deselect(pos);
        }else{
            selected.add((EventData) adapter.getItem(pos));
            adapter.setSelection(pos);
        }
    }

    //Verifies if the selected item was already selected
    private boolean isAlreadySelected(int pos){
        for(int i = 0; i < selected.size(); i++) {
            if(adapter.getPosition(selected.get(i)) == pos)
                return true;
        }
        return false;
    }

    //Deselects the position, and removes it from the arraylist
    public void deselect(int pos){
        adapter.deSelect(pos);
        selected.remove(adapter.getItem(pos));
        if(selected.size() == 0)
            finish();
    }

    //Deselects everything and clears the arraylist
    public void deselectAll(){
        for(int i = 0; i < adapter.getCount(); i++)
            adapter.deSelect(i);
        selected.clear();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == -1)
            delete();
    }

    public void closeDatabase() {
        exams_manager.closeDatabase();
    }
}
