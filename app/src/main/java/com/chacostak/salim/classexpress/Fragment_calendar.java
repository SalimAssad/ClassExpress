package com.chacostak.salim.classexpress;

import android.app.Activity;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.chacostak.salim.classexpress.Calendar.Calendar_activity;
import com.chacostak.salim.classexpress.Calendar.ClassExpressCalendar;

import java.util.ArrayList;

/**
 * Created by Salim on 05/04/2015.
 */
public class Fragment_calendar extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, TimePickerDialog.OnTimeSetListener {

    View v;
    ClassExpressCalendar calendar = null;
    Spinner spinnerMonths, spinnerYears;
    ArrayAdapter adapterMonths, adapterYears;

    TimePickerDialog timePickerDialog;

    String date = "";
    String time = "";

    String selectedMonth;
    int selectedYear;

    boolean openedFromCalendarActivity = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_calendar, container, false);

        if (savedInstanceState == null) {
            int positive, negative;
            ArrayList<Integer> years = new ArrayList<>();

            calendar = new ClassExpressCalendar(getActivity(), v);
            selectedYear = calendar.getRealYear();
            selectedMonth = calendar.getRealMonth();
            positive = selectedYear + 1;
            negative = selectedYear - 1;
            calendar.loadEvents(selectedMonth);
            calendar.loadCalendar(calendar.getShowedMonthInt(), selectedYear);

            spinnerMonths = (Spinner) v.findViewById(R.id.spinner_months);
            adapterMonths = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, getActivity().getResources().getStringArray(R.array.complete_months));
            spinnerMonths.setAdapter(adapterMonths);
            spinnerMonths.setSelection(calendar.getMonthInt(calendar.getRealMonth()));
            spinnerMonths.setOnItemSelectedListener(this);

            spinnerYears = (Spinner) v.findViewById(R.id.spinner_years);
            for (int i = 0; i < 22; i++) {
                if (i == 0)
                    years.add(selectedYear);
                else {
                    years.add(positive);
                    years.add(0, negative);
                    positive++;
                    negative--;
                }
            }

            adapterYears = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, years);
            spinnerYears.setAdapter(adapterYears);
            spinnerYears.setSelection(adapterYears.getPosition(calendar.getShowedYear()));
            spinnerYears.setOnItemSelectedListener(this);

            timePickerDialog = new TimePickerDialog(getActivity(), this, 15, 0, false);

            if (getArguments() != null)
                openedFromCalendarActivity = getArguments().getBoolean(Calendar_activity.OPENED_FROM_CALENDAR_ACTIVITY, false);

            calendar.setOnItemClickListener(this);
        }

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_months:
                selectedMonth = (String) spinnerMonths.getItemAtPosition(position);
                break;
            case R.id.spinner_years:
                selectedYear = (int) spinnerYears.getItemAtPosition(position);
                break;
        }

        calendar.loadEvents(selectedMonth);
        calendar.loadCalendar(calendar.getMonthInt(selectedMonth), selectedYear);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int getDay(int position) {
        return calendar.getDay(position);
    }

    public int getShowedMonthInt() {
        return calendar.getShowedMonthInt();
    }

    public int getShowedYear() {
        return calendar.getShowedYear();
    }

    public String getAbrevMonthName(int xmonth) {
        return calendar.getAbrevMonthName(xmonth);
    }

    public int getPmTime(int hourOfDay) {
        return calendar.getPmTime(hourOfDay);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (openedFromCalendarActivity)
            calendarActivityEvents(position);
    }

    private void calendarActivityEvents(int position) {
        if (position > 6) {
            int day = getDay(position);
            int month = getShowedMonthInt();
            int year = getShowedYear();
            if (position < 13 && day > 7) {
                if (month == 0) {
                    month = 11;
                    year--;
                } else
                    month--;
            } else if (position > 25 && day < 7) {
                if (month == 11) {
                    month = 0;
                    year++;
                } else
                    month++;
            }

            date = String.valueOf(day) + "/" + getAbrevMonthName(month) + "/" + String.valueOf(year);

            timePickerDialog.show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String extra;
        String minutes = String.valueOf(minute);
        if (hourOfDay >= 12)
            extra = "pm";
        else
            extra = "am";

        if (minutes.length() == 1)
            minutes = "0" + minutes;

        time = String.valueOf(getPmTime(hourOfDay)) + ":" + minutes + " " + extra;

        Intent output = new Intent();
        output.putExtra(Calendar_activity.DATE, date);
        output.putExtra(Calendar_activity.TIME, time);

        getActivity().setResult(Activity.RESULT_OK, output);
        getActivity().finish();
    }
}
