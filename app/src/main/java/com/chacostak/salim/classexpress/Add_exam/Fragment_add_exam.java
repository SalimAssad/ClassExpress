package com.chacostak.salim.classexpress.Add_exam;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.Validate;

import java.util.Calendar;

/**
 * Created by Salim on 26/04/2015.
 */
public class Fragment_add_exam extends Fragment implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    View v;
    private DatePickerDialog day_limit_picker;
    private TimePickerDialog hour_limit_picker;
    DateValidation dateValidation;

    EditText editDayLimit;
    EditText editTimeLimit;
    EditText editRoom;
    Spinner spin_signature;

    //OLD VALUES IF IT IS BEING EDITED
    String oldSignature;
    String oldRoom;
    String oldDayLimit;
    String oldTimeLimit;

    //NEW VALUES
    String newSignature;
    String newRoom;
    String newDayLimit;
    String newTimeLimit;

    public static final String SIGNATURE_NAME = "SIGNATURE_NAME";
    public static final String NAME = "NAME";
    public static final String ROOM = "ROOM";
    public static final String DAY_LIMIT = "DAY_LIMIT";
    public static final String TIME_LIMIT = "TIME_LIMIT";

    DB_Exams_Manager exams_manager;

    boolean isBeingEdited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_add_exam, container, false);

        exams_manager = new DB_Exams_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        initializeEdits();
        setSpinners();

        dateValidation = new DateValidation(getActivity());

        setListeners();

        if(getArguments() != null){
            isBeingEdited = true;
            oldSignature = getArguments().getString(SIGNATURE_NAME);
            oldRoom = getArguments().getString(ROOM);
            oldDayLimit = getArguments().getString(DAY_LIMIT);
            oldTimeLimit = getArguments().getString(TIME_LIMIT);

            editRoom.setText(oldRoom);
            editDayLimit.setText(oldDayLimit);
            editTimeLimit.setText(oldTimeLimit);
            spin_signature.setSelection(((ArrayAdapter) spin_signature.getAdapter()).getPosition(oldSignature));

            String day_limit_array[] = editDayLimit.getText().toString().split("/");
            String time_limit_array[] = editTimeLimit.getText().toString().split(":");

            day_limit_picker = new DatePickerDialog(getActivity(), this, Integer.parseInt(day_limit_array[2]), dateValidation.getMonthInt(day_limit_array[1]), Integer.parseInt(day_limit_array[0]));
            hour_limit_picker = new TimePickerDialog(getActivity(), this, dateValidation.pmToNormalTime(Integer.parseInt(time_limit_array[0]), time_limit_array[1].split(" ")[1]), Integer.parseInt(time_limit_array[1].split(" ")[0]), false);
        }else{
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int hour = 15;

            editDayLimit.setText(day+"/"+dateValidation.getMonthName(month)+"/"+year);
            editTimeLimit.setText("3:00 pm");

            day_limit_picker = new DatePickerDialog(getActivity(), this, year, month, day);
            hour_limit_picker = new TimePickerDialog(getActivity(), this, hour, 0, false);
        }

        return v;
    }

    private void setSpinners() {
        DB_Courses_Manager sig_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_activated_1);

        Cursor cursor = sig_manager.getAll();
        while(cursor.moveToNext())
            adapter.add(cursor.getString(cursor.getColumnIndex(sig_manager.SIGNATURE)));

        spin_signature = (Spinner) v.findViewById(R.id.spin_signature);
        spin_signature.setAdapter(adapter);
    }

    private void initializeEdits() {
        editDayLimit = (EditText) v.findViewById(R.id.edit_day_limit);
        editTimeLimit = (EditText) v.findViewById(R.id.edit_time_limit);
        editRoom = (EditText) v.findViewById(R.id.edit_room);

        editDayLimit.setInputType(EditorInfo.TYPE_NULL);
        editTimeLimit.setInputType(EditorInfo.TYPE_NULL);
    }

    private void setListeners() {
        editDayLimit.setOnFocusChangeListener(this);
        editTimeLimit.setOnFocusChangeListener(this);

        editDayLimit.setOnClickListener(this);
        editTimeLimit.setOnClickListener(this);
        v.findViewById(R.id.save_button).setOnClickListener(this);
        v.findViewById(R.id.cancel_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_day_limit:
                day_limit_picker.show();
                break;
            case R.id.edit_time_limit:
                hour_limit_picker.show();
                break;
            case R.id.save_button:
                if(!isBeingEdited && !validate())
                    return;

                newRoom = editRoom.getText().toString();
                newDayLimit = editDayLimit.getText().toString();
                newTimeLimit = editTimeLimit.getText().toString();
                newSignature = String.valueOf(spin_signature.getSelectedItem());

                storeData();

                if(getArguments() != null){
                    Intent output = new Intent();
                    output.putExtra(ROOM, newRoom);
                    output.putExtra(DAY_LIMIT, newDayLimit);
                    output.putExtra(TIME_LIMIT, newTimeLimit);
                    output.putExtra(SIGNATURE_NAME, newSignature);
                    getActivity().setResult(Activity.RESULT_OK, output);
                }
                getActivity().finish();
                break;
            case R.id.cancel_button:
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        if(editDayLimit.isFocused())
            editDayLimit.setText(i3+"/"+dateValidation.getMonthName(i2)+"/"+i);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b)
            return;
        switch(view.getId()){
            case R.id.edit_day_limit:
                day_limit_picker.show();
                break;
            case R.id.edit_time_limit:
                hour_limit_picker.show();
                break;
        }
    }

    private boolean validate(){
        if(!validateInputs()) {
            Toast.makeText(getActivity(), R.string.required_fields_empty, Toast.LENGTH_LONG).show();
            return false;
        }else if(alreadyExists()) {
            Toast.makeText(getActivity(),R.string.course_exists,Toast.LENGTH_LONG).show();
            return false;
        }else
            return true;
    }

    private boolean alreadyExists(){
        Cursor cursor;
        cursor = exams_manager.search(editDayLimit.getText().toString(), editTimeLimit.getText().toString());
        if(cursor.moveToNext())
            return true;
        else
            return false;
    }

    private boolean validateInputs(){
        Validate validate = new Validate();
        if(validate.isEmpty(editDayLimit.getText().toString()))
            return false;
        else
            return true;
    }

    private void storeData(){
        if(isBeingEdited)
            exams_manager.update(oldDayLimit, oldTimeLimit, newSignature, newRoom, newDayLimit, newTimeLimit);
        else
            exams_manager.insert(newSignature, newRoom, newDayLimit, newTimeLimit);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        if(!timePicker.isShown())
            return;

        String minutes = String.valueOf(i2);
        String extra;
        if(minutes.length() == 1)
            minutes = "0"+minutes;

        extra = getExtra(i);

        if(editTimeLimit.isFocused())
            editTimeLimit.setText(dateValidation.getPmTime(i) + ":" + minutes + " " + extra);
    }

    private String getExtra(int hourOfDay) {
        if(hourOfDay >= 12)
            return "pm";
        else
            return "am";
    }
}
