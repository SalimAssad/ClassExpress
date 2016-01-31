package com.chacostak.salim.classexpress.Calendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;

import com.chacostak.salim.classexpress.Calendar.Data.CalendarData;
import com.chacostak.salim.classexpress.Calendar.Data.VacationData;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 05/04/2015.
 */
public abstract class ChacoCalendar implements ViewTreeObserver.OnGlobalLayoutListener {

    Context context;
    GridView gridView;
    ArrayList<Integer> days;
    Calendar calendar;
    CalendarAdapter adapter;

    ArrayList<CalendarData> data;
    ArrayList<VacationData> vacData;

    DateValidation dateValidation;

    int RdayOfMonth;
    int Rmonth;
    String RmonthName;
    int RlastDayOfMonth;
    int Ryear;

    int showedMonth;
    int showedYear;

    private boolean resized = false;

    public ChacoCalendar(Context xcontext, View v) {
        context = xcontext;
        gridView = (GridView) v.findViewById(R.id.calendarGridView);
        days = new ArrayList<>();
        data = new ArrayList<>();
        vacData = new ArrayList<>();
        calendar = Calendar.getInstance();

        dateValidation = new DateValidation(xcontext);

        RdayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Rmonth = calendar.get(Calendar.MONTH);
        RmonthName = dateValidation.getMonthAbbreviation(Rmonth);
        RlastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Ryear = calendar.get(Calendar.YEAR);

        showedMonth = Rmonth;
        showedYear = Ryear;

        gridView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void calculateDays(int xmonth, int xyear) {
        int dayOfWeek;
        days.clear();
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
        int limit = previousDays+6;
        for(int i = 0; i < limit; i++) //Creates the space
            days.add(Integer.MAX_VALUE);

        Calendar copy = (Calendar) calendar.clone();
        for (int i = previousDays+5; i >= 6; i--) { //Stores the days
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

    public String getRealMonthAbbreviation(){
        return dateValidation.getMonthAbbreviation(Rmonth);
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
        return dateValidation.getMonthAbbreviation(xmonth);
    }

    public String getAbrevMonthName(String xmonthName) {
        return dateValidation.getMonthAbbreviation(xmonthName);
    }

    public int getPmTime(int hourOfDay) {
        return dateValidation.getPmTime(hourOfDay);
    }

    public void loadCalendar(int selectedMonth, int selectedYear) {
        calculateDays(selectedMonth, selectedYear);
        loadEvents(selectedMonth);

        adapter = new CalendarAdapter(context, R.layout.calendar_adapter, days, data, vacData, RdayOfMonth, Rmonth, Ryear, showedMonth, showedYear);
        gridView.setAdapter(adapter);
    }

    abstract void loadEvents(int month);

    public int getMonthInt(String monthAbbreviation) {
        return dateValidation.getMonthInt(monthAbbreviation);
    }

    public String getRealDate() {
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" + getRealMonthAbbreviation() + "/" + getRealYear();
    }

    @Override
    public void onGlobalLayout() {
        if(!resized){
            resized = true;
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = CalendarAdapter.height;
            gridView.setLayoutParams(params);
        }
    }

    public void setResized(boolean value){
        resized = value;
    }
}
