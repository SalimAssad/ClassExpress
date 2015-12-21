package com.chacostak.salim.classexpress.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.chacostak.salim.classexpress.Calendar.Data.CalendarData;
import com.chacostak.salim.classexpress.Calendar.Data.VacationData;
import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;
import com.chacostak.salim.classexpress.Utilities.Sorter;

/**
 * Created by Salim on 22/08/2015.
 */
public class ClassExpressCalendar extends ChacoCalendar {

    DB_Courses_Manager course_manager;
    DB_Homework_Manager homework_manager;
    DB_Vacations_Manager vacations_manager;
    DB_Exams_Manager exams_manager;

    public ClassExpressCalendar(Context xcontext, View v) {
        super(xcontext, v);

        course_manager = new DB_Courses_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
        homework_manager = new DB_Homework_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
        vacations_manager = new DB_Vacations_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
        exams_manager = new DB_Exams_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
    }

    public void loadEvents(int month) {
        data.clear();
        vacData.clear();
        Sorter sorter = new Sorter();

        loadPastMonthEvents(month);
        loadActualMonthEvents(month);
        loadNextMonthEvents(month);

        data = sorter.bubbleSortCalendarData(data);
        vacData = sorter.bubbleSortVacationData(vacData);
    }

    private void loadPastMonthEvents(int month) {
        Cursor cursor;
        String monthAbbreviation = getAbrevMonthName(month-1);
        for(int i = 0; days.get(i) > 1; i++){
            cursor = homework_manager.searchByDayOfMonth(days.get(i), monthAbbreviation);
            if(cursor.moveToNext()){
                CalendarData d = getHomeworkData(cursor);
                data.add(d);
            }
            cursor.close();

            cursor = exams_manager.searchByDayOfMonth(days.get(i), monthAbbreviation);
            if(cursor.moveToNext()){
                CalendarData d = getExamData(cursor);
                data.add(d);
            }
            cursor.close();

            cursor = vacations_manager.searchByDayOfMonth(days.get(i), monthAbbreviation);
            if(cursor.moveToNext()){
                VacationData d = getVacationData(cursor);
                vacData.add(d);
            }
            cursor.close();
        }
    }

    private void loadNextMonthEvents(int month) {
        Cursor cursor;
        String monthAbbreviation = getAbrevMonthName(month+1);
        int start = days.size() - 8;
        for(int i = start; i < days.size(); i++){
            if(days.get(i) == 1)
                start = i;
        }

        for(int i = start; i < days.size(); i++){
            cursor = homework_manager.searchByDayOfMonth(days.get(i), monthAbbreviation);
            if(cursor.moveToNext()){
                CalendarData d = getHomeworkData(cursor);
                data.add(d);
            }
            cursor.close();

            cursor = exams_manager.searchByDayOfMonth(days.get(i), monthAbbreviation);
            if(cursor.moveToNext()){
                CalendarData d = getExamData(cursor);
                data.add(d);
            }
            cursor.close();

            cursor = vacations_manager.searchByDayOfMonth(days.get(i), monthAbbreviation);
            if(cursor.moveToNext()){
                VacationData d = getVacationData(cursor);
                vacData.add(d);
            }
            cursor.close();
        }
    }

    private void loadActualMonthEvents(int month){
        String monthAbbreviation = getAbrevMonthName(month);
        Cursor cursor = homework_manager.searchByMonth(monthAbbreviation);
        while (cursor.moveToNext()) {
            CalendarData d = getHomeworkData(cursor);
            data.add(d);
        }
        cursor.close();

        cursor = exams_manager.searchByMonth(monthAbbreviation);
        while (cursor.moveToNext()) {
            CalendarData d = getExamData(cursor);
            data.add(d);
        }
        cursor.close();

        cursor = vacations_manager.searchByMonth(monthAbbreviation);
        while (cursor.moveToNext()) {
            VacationData d = getVacationData(cursor);
            vacData.add(d);
        }
        cursor.close();
    }

    private CalendarData getHomeworkData(Cursor cursor) {
        CalendarData d = new CalendarData();
        String date;
        String course = cursor.getString(cursor.getColumnIndex(homework_manager.COURSE));
        Cursor course_cursor = course_manager.getCourseColor(course);
        course_cursor.moveToNext();
        d.setColor(course_cursor.getString(0));
        d.setTitle(cursor.getString(cursor.getColumnIndex(homework_manager.TITLE)));
        date = cursor.getString(cursor.getColumnIndex(homework_manager.DAY_LIMIT)) + " " + cursor.getString(cursor.getColumnIndex(homework_manager.TIME_LIMIT));
        d.setInitialDate(dateValidation.formatDateANDTimeInPm(date));
        d.setType('H');
        return d;
    }

    private CalendarData getExamData(Cursor cursor) {
        CalendarData d = new CalendarData();
        String date;
        String course = cursor.getString(cursor.getColumnIndex(exams_manager.COURSE));
        Cursor course_cursor = course_manager.getCourseColor(course);
        course_cursor.moveToNext();
        d.setColor(course_cursor.getString(0));
        d.setTitle("Exam: " + course);
        date = cursor.getString(cursor.getColumnIndex(exams_manager.DAY_LIMIT)) + " " + cursor.getString(cursor.getColumnIndex(exams_manager.TIME_LIMIT));
        d.setInitialDate(dateValidation.formatDateANDTimeInPm(date));
        d.setType('E');
        course_cursor.close();
        return d;
    }

    private VacationData getVacationData(Cursor cursor) {
        VacationData d = new VacationData();
        d.setTitle(cursor.getString(cursor.getColumnIndex(vacations_manager.TITLE)));
        String date1 = cursor.getString(cursor.getColumnIndex(vacations_manager.START));
        String date2 = cursor.getString(cursor.getColumnIndex(vacations_manager.END));
        if (cursor.getInt(cursor.getColumnIndex(vacations_manager.YEARLY)) == 1) {
            d.setYearly(true);
            d.setInitialDate(dateValidation.formatDateNoYear(date1));
            d.setEndingDate(dateValidation.formatDateNoYear(date2));
        } else {
            d.setYearly(false);
            d.setInitialDate(dateValidation.formatDate(date1));
            d.setEndingDate(dateValidation.formatDate(date2));
        }
        d.setType('V');
        d.setColor("#9FEEEEDD");
        return d;
    }

    public void closeDatabases() {
        course_manager.closeDatabase();
        homework_manager.closeDatabase();
        vacations_manager.closeDatabase();
        exams_manager.closeDatabase();
    }
}
