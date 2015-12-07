package com.chacostak.salim.classexpress.Add_vacation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Schedules.Fragment_add_schedule;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.Validate;

import java.util.Calendar;

/**
 * Created by Salim on 07/02/2015.
 */
public class Fragment_add_vacation extends android.app.Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {

    View v;
    private DatePickerDialog begins;
    private DatePickerDialog ends;
    DateValidation dateValidation;

    Calendar calendar = Calendar.getInstance();

    EditText editBegins;
    EditText editEnds;
    EditText editTitle;
    CheckBox yearlyCheckBox;

    public static final String TITLE = "TITLE";
    public static final String INITIAL_DATE = "INITIAL_DATE";
    public static final String ENDING_DATE = "ENDING_DATE";
    public static final String YEARLY = "YEARLY";

    //OLD VALUES IF IT IS BEING EDITED
    String oldTitle;
    String oldBegins;
    String oldEnds;
    boolean oldYearly = true;

    //NEW VALUES
    String newTitle;
    String newBegins;
    String newEnds;
    boolean newYearly;

    DB_Vacations_Manager vac_manager;

    boolean isBeingEdited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_add_vacation,container,false);

        vac_manager = new DB_Vacations_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        editBegins = (EditText) v.findViewById(R.id.edit_begins);
        editEnds = (EditText) v.findViewById(R.id.edit_ends);
        editTitle = (EditText) v.findViewById(R.id.edit_title);

        yearlyCheckBox = (CheckBox) v.findViewById(R.id.checkBox);
        yearlyCheckBox.setOnCheckedChangeListener(this);

        editBegins.setInputType(EditorInfo.TYPE_NULL);
        editEnds.setInputType(EditorInfo.TYPE_NULL);

        setListeners();

        dateValidation = new DateValidation(getActivity());

        if(getArguments() != null){
            isBeingEdited = true;
            oldTitle = getArguments().getString(TITLE);
            oldBegins = getArguments().getString(INITIAL_DATE);
            oldEnds = getArguments().getString(ENDING_DATE);
            oldYearly = getArguments().getBoolean(YEARLY, true);

            editTitle.setText(oldTitle);
            editBegins.setText(oldBegins);
            editEnds.setText(oldEnds);
            yearlyCheckBox.setChecked(oldYearly);

            String begins_array[] = editBegins.getText().toString().split("/");
            String ends_array[] = editEnds.getText().toString().split("/");

            if(oldYearly) {
                begins = new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), dateValidation.getMonthInt(begins_array[1]), Integer.parseInt(begins_array[0]));
                ends = new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), dateValidation.getMonthInt(ends_array[1]), Integer.parseInt(ends_array[0]));
            }else{
                begins = new DatePickerDialog(getActivity(), this, Integer.parseInt(begins_array[2]), dateValidation.getMonthInt(begins_array[1]), Integer.parseInt(begins_array[0]));
                ends = new DatePickerDialog(getActivity(), this, Integer.parseInt(ends_array[2]), dateValidation.getMonthInt(ends_array[1]), Integer.parseInt(ends_array[0]));
            }
        }else {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            begins = new DatePickerDialog(getActivity(), this, year, month, day);
            ends = new DatePickerDialog(getActivity(), this, year, month, day);

            if(oldYearly) {
                oldBegins = day + "/" + dateValidation.getMonthName(month);
                oldEnds = day + "/" + dateValidation.getMonthName(month);
            }else{
                oldBegins = day + "/" + dateValidation.getMonthName(month) + "/" + year;
                oldEnds = day + "/" + dateValidation.getMonthName(month) + "/" + year;
            }

            editBegins.setText(oldBegins);
            editEnds.setText(oldEnds);
        }

        return v;
    }

    private void setListeners() {
        editBegins.setOnFocusChangeListener(this);
        editEnds.setOnFocusChangeListener(this);

        editBegins.setOnClickListener(this);
        editEnds.setOnClickListener(this);
        v.findViewById(R.id.save_button).setOnClickListener(this);
        v.findViewById(R.id.cancel_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_begins:
                begins.show();
                break;
            case R.id.edit_ends:
                ends.show();
                break;
            case R.id.save_button:
                if(!isBeingEdited && !validate())
                    return;

                newTitle = editTitle.getText().toString();
                newBegins = editBegins.getText().toString();
                newEnds = editEnds.getText().toString();
                newYearly = yearlyCheckBox.isChecked();

                storeData();

                if(getArguments() != null){
                    Intent output = new Intent();
                    output.putExtra(Fragment_add_vacation.TITLE, newTitle);
                    output.putExtra(Fragment_add_vacation.INITIAL_DATE, newBegins);
                    output.putExtra(Fragment_add_vacation.ENDING_DATE, newEnds);
                    output.putExtra(Fragment_add_vacation.YEARLY, newYearly);
                    getActivity().setResult(Activity.RESULT_OK, output);
                }
                getActivity().finish();
                break;
            case R.id.cancel_button:
                getActivity().finish();
                break;
            case R.id.add:
                v.findViewById(R.id.add).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fadein));
                getFragmentManager().beginTransaction().add(R.id.schedule_container, new Fragment_add_schedule(), "schedule")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if(!datePicker.isShown())
            return;

        if(oldYearly) {
            setEditDates(month, day);
            adjustEdits(month, day);
        }else {
            setEditDates(year, month, day);
            adjustEdits(year, month, day);
        }
    }

    private void setEditDates(int year, int month, int day) {
        if(editBegins.isFocused())
            editBegins.setText(day + "/" + dateValidation.getMonthName(month) + "/" + year);
        else if(editEnds.isFocused())
            editEnds.setText(day + "/" + dateValidation.getMonthName(month) + "/" + year);
    }

    private void setEditDates(int month, int day) {
        if(editBegins.isFocused())
            editBegins.setText(day + "/" + dateValidation.getMonthName(month));
        else if(editEnds.isFocused())
            editEnds.setText(day + "/" + dateValidation.getMonthName(month));
    }

    private void adjustEdits(int year, int month, int day) {
        String start = editBegins.getText().toString();
        String ending = editEnds.getText().toString();

        if (dateValidation.isAfter(start, ending)) {
            if(editBegins.isFocused()) {
                editEnds.setText(day + "/" + dateValidation.getMonthName(month) + "/" + year);
                ends.updateDate(year, month, day);
            }else{
                editBegins.setText(day + "/" + dateValidation.getMonthName(month) + "/" + year);
                begins.updateDate(year, month, day);
            }
        }
    }

    private void adjustEdits(int month, int day) {
        String start = editBegins.getText().toString();
        String ending = editEnds.getText().toString();

        String arr1[] = start.split("/");
        String arr2[] = ending.split("/");

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.set(calendar.get(Calendar.YEAR), dateValidation.getMonthInt(arr1[1]), Integer.parseInt(arr1[0]));
        date2.set(calendar.get(Calendar.YEAR), dateValidation.getMonthInt(arr2[1]), Integer.parseInt(arr2[0]));

        if (date1.after(date2)) {
            if(editBegins.isFocused()) {
                editEnds.setText(day + "/" + dateValidation.getMonthName(month));
                ends.updateDate(calendar.get(Calendar.YEAR), month, day);
            }else{
                editBegins.setText(day + "/" + dateValidation.getMonthName(month));
                begins.updateDate(calendar.get(Calendar.YEAR), month, day);
            }
        }
    }



    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b)
            return;
        switch(view.getId()){
            case R.id.edit_begins:
                begins.show();
            break;
            case R.id.edit_ends:
                ends.show();
            break;
        }
    }

    private boolean validate(){
        if(!validateInputs()) {
            Toast.makeText(getActivity(),R.string.required_fields_empty,Toast.LENGTH_LONG).show();
            return false;
        }else if(alreadyExists()) {
            Toast.makeText(getActivity(),R.string.vacation_exists,Toast.LENGTH_LONG).show();
            return false;
        }else
            return true;
    }

    private boolean alreadyExists(){
        String xtitle = editTitle.getText().toString();
        Cursor cursor;
        cursor = vac_manager.searchByTitle(xtitle);
        if(cursor.moveToNext())
            return true;
        else
            return false;
    }

    private boolean validateInputs(){
        Validate validate = new Validate();
        if(validate.isEmpty(editTitle.getText().toString()))
            return false;
        else if(validate.isEmpty(editBegins.getText().toString()))
            return false;
        else if(validate.isEmpty(editEnds.getText().toString()))
            return false;
        else
            return true;
    }

    private void storeData(){
        int xyearly;
        if(newYearly)
            xyearly = 1;
        else
            xyearly = 0;

        if(isBeingEdited)
            vac_manager.update(oldTitle, newTitle, newBegins, newEnds, xyearly);
        else
            vac_manager.insert(newTitle, newBegins, newEnds, xyearly);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        oldYearly = isChecked;
        String date1[] = oldBegins.split("/");
        String date2[] = oldEnds.split("/");
        oldBegins = date1[0] + "/" + date1[1];
        oldEnds = date2[0] + "/" + date2[1];
        if(!isChecked){
            int year = calendar.get(Calendar.YEAR);
            oldBegins += "/" + year;
            oldEnds += "/" + year;
        }
        editBegins.setText(oldBegins);
        editEnds.setText(oldEnds);
    }
}
