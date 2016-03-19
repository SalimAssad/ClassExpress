package com.chacostak.salim.classexpress;

import android.app.Activity;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.chacostak.salim.classexpress.Calendar.Calendar_activity;
import com.chacostak.salim.classexpress.Calendar.ClassExpressCalendar;
import com.chacostak.salim.classexpress.Calendar.Day_info.Fragment_day_info;

import java.util.ArrayList;

/**
 * Created by Salim on 05/04/2015.
 */
public class Fragment_calendar extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, TimePickerDialog.OnTimeSetListener {

    View v;
    ClassExpressCalendar calendar = null;
    Spinner spinnerMonths, spinnerYears;
    ArrayAdapter adapterMonths, adapterYears;

    Fragment_day_info fragmentDayInfo;

    TimePickerDialog timePickerDialog;

    String date = "";
    String time = "";

    String selectedMonth;
    int selectedYear;

    boolean openedFromCalendarActivity = false;

    boolean firstRun = true, secondRun = true;

    public static final String DATE = "DATE";
    public static final String VACATION_TITLE = "VACATION_TITLE";

    //LinearLayout day_info_container = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_calendar, container, false);

        if (savedInstanceState == null) { //TODO: EL FRAGMENTO SE CREA 2 VECES D:
            Bundle arguments = new Bundle();
            calendar = new ClassExpressCalendar(getActivity(), v);
            selectedYear = calendar.getRealYear();
            selectedMonth = calendar.getRealMonthAbbreviation();

            //calendar.loadCalendar(calendar.getShowedMonthInt(), selectedYear);

            timePickerDialog = new TimePickerDialog(getActivity(), this, 15, 0, false);

            if (getArguments() != null)
                openedFromCalendarActivity = getArguments().getBoolean(Calendar_activity.OPENED_FROM_CALENDAR_ACTIVITY, false);

            calendar.setOnItemClickListener(this);

            View menu_view = LayoutInflater.from(getActivity()).inflate(R.layout.menu_calendar_layout, null);

            spinnerMonths = (Spinner) menu_view.findViewById(R.id.spinner_months);
            adapterMonths = new ArrayAdapter(getActivity(), R.layout.simple_list_item_activated, getActivity().getResources().getStringArray(R.array.complete_months));
            spinnerMonths.setAdapter(adapterMonths);
            spinnerMonths.setSelection(calendar.getShowedMonthInt());
            spinnerMonths.setOnItemSelectedListener(this);

            spinnerYears = (Spinner) menu_view.findViewById(R.id.spinner_years);
            adapterYears = new ArrayAdapter(getActivity(), R.layout.simple_list_item_activated, getYears());
            spinnerYears.setAdapter(adapterYears);
            spinnerYears.setSelection(adapterYears.getPosition(calendar.getShowedYear()));
            spinnerYears.setOnItemSelectedListener(this);

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(menu_view);

            fragmentDayInfo = new Fragment_day_info();
            arguments.putString(DATE, calendar.getRealDate());
            arguments.putString(VACATION_TITLE, calendar.getVacationTitle(calendar.getRealDate()));
            fragmentDayInfo.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.day_info_container, fragmentDayInfo).commit();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        calendar.loadCalendar(calendar.getShowedMonthInt(), selectedYear);
    }

    private ArrayList getYears() {
        ArrayList<Integer> years = new ArrayList<>();
        int positive = selectedYear + 1;
        int negative = selectedYear - 1;
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

        return years;
    }

    @Override
    public void onDestroyView() {
        if (calendar != null)
            calendar.closeDatabases(); //If not closed, might leak connections

        if(fragmentDayInfo != null){
            getFragmentManager().beginTransaction().remove(fragmentDayInfo).commit();
            fragmentDayInfo = null;
        }

        super.onDestroyView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (firstRun)
            firstRun = false;
        else if (secondRun)
            secondRun = false;
        else {
            switch (parent.getId()) {
                case R.id.spinner_months:
                    selectedMonth = (String) spinnerMonths.getItemAtPosition(position);
                    selectedMonth = calendar.getAbrevMonthName(selectedMonth);
                    break;
                case R.id.spinner_years:
                    selectedYear = (int) spinnerYears.getItemAtPosition(position);
                    break;
            }
            calendar.loadCalendar(calendar.getMonthInt(selectedMonth), selectedYear);
            calendar.setResized(false);
        }
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

            if (openedFromCalendarActivity)
                timePickerDialog.show();

            fragmentDayInfo.updateData(date, calendar.getVacationTitle(date));
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
