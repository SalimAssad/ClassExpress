package com.chacostak.salim.classexpress.Utilities;

import android.content.Context;
import android.database.Cursor;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Vacations_Manager;

import java.util.Calendar;

/**
 * Created by Salim on 28/05/2015.
 */
public class CourseValidation {

    Calendar calendar = Calendar.getInstance();
    DateValidation dateValidation = null;
    DB_Vacations_Manager vacations_manager = null;

    //Validates if the signature is still being coursed
    public boolean stillInCourse(Context xactivity, String starts, String ends){
        if(vacations_manager == null)
            vacations_manager = new DB_Vacations_Manager(xactivity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        if(dateValidation == null)
            dateValidation = new DateValidation(xactivity);

        Cursor vac_cursor = vacations_manager.getAll();
        if(calendar.after(addOneDay(ends)) && calendar.before(dateValidation.formatDate(starts)))
            return false;
        else {
            String s;
            String e;
            String date1[];
            String date2[];
            Calendar cal1;
            Calendar cal2;
            while(vac_cursor.moveToNext()){
                s = vac_cursor.getString(vac_cursor.getColumnIndex(vacations_manager.START));
                e = vac_cursor.getString(vac_cursor.getColumnIndex(vacations_manager.END));

                cal1 = Calendar.getInstance();
                cal2 = Calendar.getInstance();

                date1 = s.split("/");
                date2 = e.split("/");

                cal1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date1[0]));
                cal1.set(Calendar.MONTH, dateValidation.getMonthInt(date1[1]));
                cal1.set(Calendar.HOUR, 0);
                cal1.set(Calendar.MINUTE, 0);
                cal2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date2[0]));
                cal2.set(Calendar.MONTH, dateValidation.getMonthInt(date2[1]));
                cal2.set(Calendar.HOUR, 0);
                cal2.set(Calendar.MINUTE, 0);
                cal2.add(Calendar.DATE, 1);
                if(vac_cursor.getInt(vac_cursor.getColumnIndex(vacations_manager.YEARLY)) == 1) {
                    if(calendar.after(cal1) && calendar.before(cal2))
                        return false;
                }else{
                    cal1.set(Calendar.YEAR, Integer.parseInt(date1[2]));
                    cal2.set(Calendar.YEAR, Integer.parseInt(date2[2]));
                    if(calendar.after(cal1) && calendar.before(cal2))
                        return false;
                }
            }
        }
        return true;
    }

    //Validates if the signature is still being coursed, with a calendar sent for validating a specific date
    public boolean stillInCourse(Context xactivity, Calendar futureDate, String starts, String ends){
        if(vacations_manager == null)
            vacations_manager = new DB_Vacations_Manager(xactivity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        if(dateValidation == null)
            dateValidation = new DateValidation(xactivity);

        Cursor vac_cursor = vacations_manager.getAll();
        if(futureDate.after(addOneDay(ends)) && futureDate.before(dateValidation.formatDate(starts)))
            return false;
        else {
            String s;
            String e;
            String date1[];
            String date2[];
            Calendar cal1;
            Calendar cal2;
            while(vac_cursor.moveToNext()){
                s = vac_cursor.getString(vac_cursor.getColumnIndex(vacations_manager.START));
                e = vac_cursor.getString(vac_cursor.getColumnIndex(vacations_manager.END));

                cal1 = Calendar.getInstance();
                cal2 = Calendar.getInstance();

                date1 = s.split("/");
                date2 = e.split("/");

                cal1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date1[0]));
                cal1.set(Calendar.MONTH, dateValidation.getMonthInt(date1[1]));
                cal1.set(Calendar.HOUR, 0);
                cal1.set(Calendar.MINUTE, 0);
                cal2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date2[0]));
                cal2.set(Calendar.MONTH, dateValidation.getMonthInt(date2[1]));
                cal2.set(Calendar.HOUR, 0);
                cal2.set(Calendar.MINUTE, 0);
                cal2.add(Calendar.DATE, 1);
                if(vac_cursor.getInt(vac_cursor.getColumnIndex(vacations_manager.YEARLY)) == 1) {
                    if(futureDate.after(cal1) && futureDate.before(cal2))
                        return false;
                }else{
                    cal1.set(Calendar.YEAR, Integer.parseInt(date1[2]));
                    cal2.set(Calendar.YEAR, Integer.parseInt(date2[2]));
                    if(futureDate.after(cal1) && futureDate.before(cal2))
                        return false;
                }
            }
        }
        return true;
    }

    private Calendar addOneDay(String date){
        Calendar newDate = dateValidation.formatDate(date);
        newDate.add(Calendar.DATE, 1);
        return newDate;
    }
}
