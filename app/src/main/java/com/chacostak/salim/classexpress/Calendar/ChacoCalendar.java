package com.chacostak.salim.classexpress.Calendar;

import android.content.Context;
import android.view.View;
import android.widget.GridView;

import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 05/04/2015.
 */
public class ChacoCalendar {

    Context context;
    GridView gridView;
    ArrayList<Integer> days;
    Calendar calendar;
    CalendarAdapter adapter;

    ArrayList<EventData> data;

    DateValidation dateValidation;

    int RdayOfMonth;
    int Rmonth;
    String RmonthName;
    int RlastDayOfMonth;
    int Ryear;

    int showedMonth;
    int showedYear;

    public ChacoCalendar(Context xcontext, View v) {
        context = xcontext;
        gridView = (GridView) v.findViewById(R.id.calendarGridView);
        days = new ArrayList<>();
        data = new ArrayList<>();
        calendar = Calendar.getInstance();

        dateValidation = new DateValidation(xcontext);

        RdayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Rmonth = calendar.get(Calendar.MONTH);
        RmonthName = dateValidation.getMonthName(Rmonth);
        RlastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Ryear = calendar.get(Calendar.YEAR);
    }

    private void calculateDays(int xmonth, int xyear) {
        int dayOfWeek;
        showedMonth = xmonth;
        showedYear = xyear;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, xmonth);
        calendar.set(Calendar.YEAR, xyear);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        storePreviousDays(dayOfWeek);
        storeThisMonth();

        //Now to set the calendar to the real date
        calendar.set(Calendar.MONTH, Rmonth);
        calendar.set(Calendar.DAY_OF_MONTH, RdayOfMonth);
        calendar.set(Calendar.YEAR, Ryear);
    }

    //Stores the previous days from the actual Rmonth
    public void storePreviousDays(int previousDays) {
        for(int i = 0; i < (previousDays+7)-1; i++) //Creates the space
            days.add(0);

        Calendar copy = (Calendar) calendar.clone();
        for (int i = (previousDays+7)-2; i >= 0; i--) { //Stores the days
            copy.add(Calendar.DATE, -1);
            days.set(i, copy.get(Calendar.DAY_OF_MONTH));
        }
    }

    private void storeThisMonth() {
        boolean firstTime = false;
        boolean secondTime = false;
        while(true){
            if(secondTime && calendar.get(Calendar.DAY_OF_WEEK) == 1)
                break;
            else{
                if(calendar.get(Calendar.DAY_OF_MONTH) == 1){
                    if(firstTime)
                        secondTime = true;
                    else
                        firstTime = true;
                }
                days.add(calendar.get(Calendar.DAY_OF_MONTH));
                calendar.add(Calendar.DATE, 1);
            }
        }
    }

    public String getRealMonth(){
        return dateValidation.getMonthName(Rmonth);
    }

    public int getRealYear() {
        return Ryear;
    }

    public int getShowedMonthInt(){
        return showedMonth;
    }

    public int getShowedYear(){
        return showedYear;
    }

    public int getDay(int position){
        return days.get(position);
    }

    public void setOnItemClickListener(GridView.OnItemClickListener listener){
        gridView.setOnItemClickListener(listener);
    }

    public void setOnLongItemClickListener(GridView.OnLongClickListener listener){
        gridView.setOnLongClickListener(listener);
    }

    public String getAbrevMonthName(int xmonth) {
        return dateValidation.getMonthName(xmonth);
    }

    public int getPmTime(int hourOfDay) {
        return dateValidation.getPmTime(hourOfDay);
    }

    public void loadCalendar(int selectedMonth, int selectedYear) {
        days.clear();
        data.clear();
        calculateDays(selectedMonth, selectedYear);

        adapter = new CalendarAdapter(context, R.layout.calendar_adapter, days, data, RdayOfMonth, Rmonth, Ryear, showedMonth, showedYear);
        gridView.setAdapter(adapter);
    }

    public int getMonthInt(String selectedMonth) {
        return dateValidation.getMonthInt(dateValidation.getMonthAbreviation(selectedMonth));
    }
}
