package com.chacostak.salim.classexpress.Schedules;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Info_activities.Signature_info.Fragment_signature_info;
import com.chacostak.salim.classexpress.Utilities.DateValidation;

/**
 * Created by Salim on 25/03/2015.
 */
public class Fragment_add_schedule extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, View.OnFocusChangeListener {

    View v;
    EditText editBegins, editEnds;
    Spinner days;
    ArrayAdapter spinner_adapter;
    ImageButton save, delete;
    TimePickerDialog begins;
    TimePickerDialog ends;
    DateValidation dateValidation;
    Boolean isBeingEdited = false;
    public static final String BEGINS = "BEGINS";
    public static final String ENDS = "ENDS";
    public static final String DAY = "DAY";
    public static final String IS_BEING_EDITED = "IS_BEING_EDITED";

    String selected_day;
    String time_begins;
    String time_ends;
    int initial_hour;
    String initial_minutes;
    int ending_hour;
    String ending_minutes;

    DB_Schedule_Manager sch_manager;

    int lastPos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_add_schedule, container, false);

        setEdits();
        setSpinner();
        setImagebuttons();


        dateValidation = new DateValidation();

        if(getArguments() != null){
            initializeVariables();
            days.setSelection(spinner_adapter.getPosition(selected_day));
            editBegins.setText(getArguments().getString(BEGINS));
            editEnds.setText(getArguments().getString(ENDS));
            isBeingEdited = getArguments().getBoolean(IS_BEING_EDITED, true);

            setTimeVariables();

            begins = new TimePickerDialog(getActivity(), this, initial_hour, Integer.parseInt(initial_minutes), false);
            ends = new TimePickerDialog(getActivity(), this, ending_hour, Integer.parseInt(ending_minutes), false);
        }else
            setTimePickers();

        sch_manager = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        return v;
    }

    private void setTimeVariables() {
        String array[] = editBegins.getText().toString().split(":");
        initial_hour = dateValidation.pmToNormalTime(Integer.parseInt(array[0]), array[1].split(" ")[1]);
        initial_minutes = array[1].split(" ")[0];

        array = editEnds.getText().toString().split(":");
        ending_hour = dateValidation.pmToNormalTime(Integer.parseInt(array[0]), array[1].split(" ")[1]);;
        ending_minutes = array[1].split(" ")[0];
    }

    private void initializeVariables() {
        selected_day = getArguments().getString(DAY);
        time_begins = getArguments().getString(BEGINS);
        time_ends = getArguments().getString(ENDS);
    }

    private void setTimePickers() {
        int hour_begins = 15;
        int hour_ends = 16;
        begins = new TimePickerDialog(getActivity(), this, hour_begins, 0, false);
        ends = new TimePickerDialog(getActivity(), this, hour_ends, 0, false);

        storeInitialTime(hour_begins, "00", "pm");
        storeEndingTime(hour_ends, "00", "pm");

        editBegins.setText("3:00 pm");
        editEnds.setText("4:00 pm");
    }

    private void setImagebuttons() {
        save = (ImageButton) v.findViewById(R.id.save);
        delete = (ImageButton) v.findViewById(R.id.delete);

        save.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    private void setSpinner() {
        spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.days, R.layout.support_simple_spinner_dropdown_item);
        days = (Spinner) v.findViewById(R.id.spinnerDays);
        days.setAdapter(spinner_adapter);
    }

    private void setEdits() {
        editBegins = (EditText) v.findViewById(R.id.edit_begins);
        editEnds = (EditText) v.findViewById(R.id.edit_ends);

        editBegins.setInputType(EditorInfo.TYPE_NULL);
        editEnds.setInputType(EditorInfo.TYPE_NULL);

        editBegins.setOnClickListener(this);
        editEnds.setOnClickListener(this);

        editBegins.setOnFocusChangeListener(this);
        editEnds.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_begins:
                begins.show();
                break;
            case R.id.edit_ends:
                ends.show();
                break;
            case R.id.save:
                boolean error = false;
                if(Fragment_signature_info.openedFromSigInfo) {
                    if (isBeingEdited) {
                        if(!sch_manager.update(Fragment_signature_info.signature_parent, selected_day, 0, time_begins, time_ends, String.valueOf(days.getSelectedItem()), 0, editBegins.getText().toString(), editEnds.getText().toString()))
                            error = true;
                    }else {
                        if(!sch_manager.insert(Fragment_signature_info.signature_parent, String.valueOf(days.getSelectedItem()), 0, editBegins.getText().toString(), editEnds.getText().toString()))
                            error = true;
                    }
                }

                if(error)
                    Toast.makeText(getActivity(), getActivity().getString(R.string.schedule_error1), Toast.LENGTH_LONG).show();
                else {
                    selected_day = String.valueOf(days.getSelectedItem());
                    time_begins = editBegins.getText().toString();
                    time_ends = editEnds.getText().toString();

                    addFragToArray();

                    removeThis();
                    addInfo();
                }
                break;
            case R.id.delete:
                if(isBeingEdited) {
                    addFragToArray();
                    addInfo();
                }

                removeThis();
                break;
        }
    }

    private void addFragToArray() {
        Fragment_schedules_info.schedules_added.add(new Fragment_schedules_info());
        lastPos = Fragment_schedules_info.schedules_added.size()-1;
        Fragment_schedules_info.schedules_added.get(lastPos)
                .setArguments(prepareArguments());
    }

    private void removeThis() {
        getFragmentManager().beginTransaction().remove(this).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
    }

    private void addInfo() {
        getFragmentManager().beginTransaction().add(R.id.schedule_container, Fragment_schedules_info.schedules_added.get(lastPos))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }



    private Bundle prepareArguments() {
        Bundle arguments = new Bundle();
        arguments.putString(BEGINS, time_begins);
        arguments.putString(ENDS, time_ends);
        arguments.putString(DAY, selected_day);
        return arguments;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(!view.isShown())
            return;

        String minutes = String.valueOf(minute);
        String extra;
        if(minutes.length() == 1)
            minutes = "0"+minutes;

        extra = getExtra(hourOfDay);

        if(editBegins.isFocused()) {
            editBegins.setText(dateValidation.getPmTime(hourOfDay) + ":" + minutes + " " + extra);
            storeInitialTime(hourOfDay, minutes, extra);
        }else if(editEnds.isFocused()) {
            editEnds.setText(dateValidation.getPmTime(hourOfDay) + ":" + minutes + " " + extra);
            storeEndingTime(hourOfDay, minutes, extra);
        }

        adjustEdits(hourOfDay, minutes);
    }

    private String getExtra(int hourOfDay) {
        if(hourOfDay >= 12)
            return "pm";
        else
            return "am";
    }

    private void adjustEdits(int hour, String minutes) {
        if (dateValidation.timeIsAfter(initial_hour+":"+initial_minutes, ending_hour+":"+ending_minutes)) {
            if(editBegins.isFocused()) {
                if (hour == 23)
                    editEnds.setText(dateValidation.getPmTime(hour) + ":" + minutes + " " + getExtra(hour));
                else {
                    hour += 1;
                    editEnds.setText(dateValidation.getPmTime(hour) + ":" + minutes + " " + getExtra(hour));
                }
                storeEndingTime(hour, minutes, getExtra(hour));
                ends.updateTime(hour, Integer.parseInt(minutes));
            }else{
                if (hour == 0) {
                    if(initial_hour == 23)
                        hour = 23;
                    else
                        editBegins.setText(dateValidation.getPmTime(hour) + ":" + minutes + " " + getExtra(hour));
                }else {
                    hour -= 1;
                    editBegins.setText(dateValidation.getPmTime(hour) + ":" + minutes + " " + getExtra(hour));
                }
                storeInitialTime(hour, minutes, getExtra(hour));
                begins.updateTime(hour, Integer.parseInt(minutes));
            }
        }
    }

    public void storeInitialTime(int hourOfDay, String minutes, String extra){
        initial_hour = hourOfDay;
        initial_minutes = minutes;
    }

    public void storeEndingTime(int hourOfDay, String minutes, String extra){
        ending_hour = hourOfDay;
        ending_minutes = minutes;
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
}

