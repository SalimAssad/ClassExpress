package com.chacostak.salim.classexpress.Add_course;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Teacher_Manager;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Schedules.Fragment_add_schedule;
import com.chacostak.salim.classexpress.Schedules.Fragment_schedules_info;
import com.chacostak.salim.classexpress.Info_activities.Course_info.Fragment_course_info;
import com.chacostak.salim.classexpress.Info_activities.Course_info.Course_info_activity;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Salim on 07/02/2015.
 */
public class Fragment_add_course extends android.app.Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnFocusChangeListener {

    View v;
    private DatePickerDialog begins;
    private DatePickerDialog ends;
    DateValidation dateValidation;

    EditText editBegins;
    EditText editEnds;
    EditText editCourse;
    AutoCompleteTextView editTeacher;

    Spinner spin_color;
    ColorSpinnerAdapter adapter;

    //OLD VALUES IF IT IS BEING EDITED
    String oldSignature;
    String oldTeacher;
    String oldBegins;
    String oldEnds;
    String oldColor;

    //NEW VALUES
    String newCourse;
    String newTeacher;
    String newBegins;
    String newEnds;
    String newColor;

    DB_Courses_Manager course_manager;
    DB_Schedule_Manager sch_manager;
    DB_Teacher_Manager teacher_manager;

    boolean isBeingEdited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_add_signature, container, false);

        Fragment_course_info.openedFromSigInfo = false; //Important, will double save if not set to false

        course_manager = new DB_Courses_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        sch_manager = new DB_Schedule_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);
        teacher_manager = new DB_Teacher_Manager(getActivity(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        editBegins = (EditText) v.findViewById(R.id.edit_begins);
        editEnds = (EditText) v.findViewById(R.id.edit_ends);
        editCourse = (EditText) v.findViewById(R.id.edit_signature_name);
        editTeacher = (AutoCompleteTextView) v.findViewById(R.id.edit_teacher);
        setAutoComplete();

        editBegins.setInputType(EditorInfo.TYPE_NULL);
        editEnds.setInputType(EditorInfo.TYPE_NULL);

        prepareAdapter();
        prepareSpinner();

        setListeners();

        dateValidation = new DateValidation(getActivity());

        if (getArguments() != null) {
            isBeingEdited = true;
            oldSignature = getArguments().getString(Course_info_activity.COURSE_NAME);
            oldTeacher = getArguments().getString(Course_info_activity.TEACHER);
            oldBegins = getArguments().getString(Course_info_activity.INITIAL_DATE);
            oldEnds = getArguments().getString(Course_info_activity.ENDING_DATE);
            oldColor = getArguments().getString(Course_info_activity.COLOR);

            editCourse.setText(oldSignature);
            editTeacher.setText(oldTeacher);
            editBegins.setText(oldBegins);
            editEnds.setText(oldEnds);
            spin_color.setSelection(adapter.getItemPosition(oldColor));

            String begins_array[] = editBegins.getText().toString().split("/");
            String ends_array[] = editEnds.getText().toString().split("/");

            begins = new DatePickerDialog(getActivity(), this, Integer.parseInt(begins_array[2]), dateValidation.getMonthInt(begins_array[1]), Integer.parseInt(begins_array[0]));
            ends = new DatePickerDialog(getActivity(), this, Integer.parseInt(ends_array[2]), dateValidation.getMonthInt(ends_array[1]), Integer.parseInt(ends_array[0]));
        } else {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int auxMonth, auxYear;

            begins = new DatePickerDialog(getActivity(), this, year, month, day);
            ends = new DatePickerDialog(getActivity(), this, year, month + 4, day);

            auxMonth = month + 4;
            auxYear = year;
            if (auxMonth > 11) {
                auxMonth = auxMonth - 12;
                auxYear++;
            }

            editBegins.setText(day + "/" + dateValidation.getMonthAbbreviation(month) + "/" + year);
            editEnds.setText(day + "/" + dateValidation.getMonthAbbreviation(auxMonth) + "/" + auxYear);
        }

        return v;
    }

    private void setAutoComplete() {
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        Cursor cursor = teacher_manager.getAll();
        while (cursor.moveToNext())
            adapter.add(cursor.getString(cursor.getColumnIndex(teacher_manager.NAME)));
        editTeacher.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        course_manager.closeDatabase();
        sch_manager.closeDatabase();
        teacher_manager.closeDatabase();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Fragment_schedules_info.schedules_added.clear();
        Fragment_course_info.course_parent = "";
        super.onDestroy();
    }

    private void prepareAdapter() {
        ArrayList<String> colors = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.colors)));
        adapter = new ColorSpinnerAdapter(getActivity(), R.layout.color_spinner, colors);
    }

    private void prepareSpinner() {
        spin_color = (Spinner) v.findViewById(R.id.spinner_color);
        spin_color.setAdapter(adapter);
    }

    private void setListeners() {
        editBegins.setOnFocusChangeListener(this);
        editEnds.setOnFocusChangeListener(this);

        editBegins.setOnClickListener(this);
        editEnds.setOnClickListener(this);
        v.findViewById(R.id.save_button).setOnClickListener(this);
        v.findViewById(R.id.cancel_button).setOnClickListener(this);
        v.findViewById(R.id.add).setOnClickListener(this);

        editCourse.addTextChangedListener(new textListener());
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
                if (!isBeingEdited && !validate())
                    return;

                newCourse = editCourse.getText().toString();
                newTeacher = editTeacher.getText().toString();
                newBegins = editBegins.getText().toString();
                newEnds = editEnds.getText().toString();
                newColor = adapter.selectedItem;

                storeData();

                Intent output = new Intent();
                output.putExtra(Course_info_activity.COURSE_NAME, newCourse);
                output.putExtra(Course_info_activity.TEACHER, newTeacher);
                output.putExtra(Course_info_activity.INITIAL_DATE, newBegins);
                output.putExtra(Course_info_activity.ENDING_DATE, newEnds);
                output.putExtra(Course_info_activity.COLOR, newColor);
                getActivity().setResult(Activity.RESULT_OK, output);
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
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        if (!datePicker.isShown())
            return;

        if (editBegins.isFocused())
            editBegins.setText(i3 + "/" + dateValidation.getMonthAbbreviation(i2) + "/" + i);
        else if (editEnds.isFocused())
            editEnds.setText(i3 + "/" + dateValidation.getMonthAbbreviation(i2) + "/" + i);

        adjustEdits(i, i2, i3);
    }

    private void adjustEdits(int year, int month, int day) {
        String start = editBegins.getText().toString();
        String ending = editEnds.getText().toString();

        if (dateValidation.isAfter(start, ending)) {
            if (editBegins.isFocused()) {
                if (month == 11) {
                    month = 1;
                    year += 1;
                    editEnds.setText(day + "/" + dateValidation.getMonthAbbreviation(month) + "/" + year);
                } else {
                    month += 1;
                    editEnds.setText(day + "/" + dateValidation.getMonthAbbreviation(month) + "/" + year);
                }
                ends.updateDate(year, month, day);
            } else {
                if (month == 0) {
                    month = 11;
                    year -= 1;
                    editBegins.setText(day + "/" + dateValidation.getMonthAbbreviation(month) + "/" + year);
                } else {
                    month -= 1;
                    editBegins.setText(day + "/" + dateValidation.getMonthAbbreviation(month) + "/" + year);
                }
                begins.updateDate(year, month, day);
            }
        }
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b)
            return;
        switch (view.getId()) {
            case R.id.edit_begins:
                begins.show();
                break;
            case R.id.edit_ends:
                ends.show();
                break;
        }
    }

    private boolean validate() {
        if (!validateInputs()) {
            Toast.makeText(getActivity(), R.string.required_fields_empty, Toast.LENGTH_LONG).show();
            return false;
        } else if (alreadyExists()) {
            Toast.makeText(getActivity(), R.string.course_exists, Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

    private boolean alreadyExists() {
        String className = editCourse.getText().toString();
        Cursor cursor;
        cursor = course_manager.searchByName(className);
        if (cursor.moveToNext())
            return true;
        else
            return false;
    }

    private boolean validateInputs() {
        Validate validate = new Validate();
        if (validate.isEmpty(editCourse.getText().toString()))
            return false;
        else if (validate.isEmpty(editBegins.getText().toString()))
            return false;
        else if (validate.isEmpty(editEnds.getText().toString()))
            return false;
        else
            return true;
    }

    private void storeData() {
        if (isBeingEdited)
            course_manager.update(oldSignature, newCourse, 0.0, newBegins, newEnds, newTeacher, newColor);
        else
            course_manager.insert(newCourse, 0.0, newBegins, newEnds, newTeacher, newColor);


        if (validateTeachers())
            teacher_manager.insert(newTeacher, "", "", "");

        for (int i = 0; i < Fragment_schedules_info.schedules_added.size(); i++) {
            sch_manager.insert(newCourse, Fragment_schedules_info.schedules_added.get(i).day, 0, Fragment_schedules_info.schedules_added.get(i).hour_begins,
                    Fragment_schedules_info.schedules_added.get(i).hour_ends);
        }
    }

    private boolean validateTeachers() {
        Validate validate = new Validate();
        if (validate.isEmpty(newTeacher))
            return false;
        else if (teacher_manager.searchByName(newTeacher).moveToNext())
            return false;
        else
            return true;
    }
}
