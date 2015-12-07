package com.chacostak.salim.classexpress.Utilities;

import android.content.Context;
import android.util.Log;

import com.chacostak.salim.classexpress.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Salim on 07/04/2015.
 */
public class DateValidation {

    Calendar date1;
    Calendar date2;
    Context activity;

    public DateValidation(){

    }

    public DateValidation(Context xactivity){
        activity = xactivity;
    }

    public boolean isAfter(String after, String before){
        date1 = formatDate(after);
        date2 = formatDate(before);

        if(date1.after(date2))
            return true;
        else
            return false;
    }

    public Calendar formatDate(String date){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public String getMonthName(int i){
        switch (i){
            case 0:
                return activity.getString(R.string.jan);
            case 1:
                return activity.getString(R.string.feb);
            case 2:
                return activity.getString(R.string.mar);
            case 3:
                return activity.getString(R.string.apr);
            case 4:
                return activity.getString(R.string.may);
            case 5:
                return activity.getString(R.string.jun);
            case 6:
                return activity.getString(R.string.jul);
            case 7:
                return activity.getString(R.string.aug);
            case 8:
                return activity.getString(R.string.sep);
            case 9:
                return activity.getString(R.string.oct);
            case 10:
                return activity.getString(R.string.nov);
            case 11:
                return activity.getString(R.string.dec);
            default:
                return "NO MONTH FOUND :'(";
        }
    }

    public String getCompleteMonthName(int i){
        String array[] = activity.getResources().getStringArray(R.array.complete_months);
        switch (i){
            case 0:
                return array[0];
            case 1:
                return array[1];
            case 2:
                return array[2];
            case 3:
                return array[3];
            case 4:
                return array[4];
            case 5:
                return array[5];
            case 6:
                return array[6];
            case 7:
                return array[7];
            case 8:
                return array[8];
            case 9:
                return array[9];
            case 10:
                return array[10];
            case 11:
                return array[11];
            default:
                return "NO MONTH FOUND :'(";
        }
    }

    public int getMonthInt(String abrevName){
        if(abrevName.equals(activity.getString(R.string.jan)))
            return 0;
        else if(abrevName.equals(activity.getString(R.string.feb)))
            return 1;
        else if(abrevName.equals(activity.getString(R.string.mar)))
            return 2;
        else if(abrevName.equals(activity.getString(R.string.apr)))
            return 3;
        else if(abrevName.equals(activity.getString(R.string.may)))
            return 4;
        else if(abrevName.equals(activity.getString(R.string.jun)))
            return 5;
        else if(abrevName.equals(activity.getString(R.string.jul)))
            return 6;
        else if(abrevName.equals(activity.getString(R.string.aug)))
            return 7;
        else if(abrevName.equals(activity.getString(R.string.sep)))
            return 8;
        else if(abrevName.equals(activity.getString(R.string.oct)))
            return 9;
        else if(abrevName.equals(activity.getString(R.string.nov)))
            return 10;
        else if(abrevName.equals(activity.getString(R.string.dec)))
            return 11;
        else
            return -1;
    }

    public boolean timeIsAfter(String after, String before){
        date1 = formatTime(after);
        date2 = formatTime(before);

        if(date1.after(date2))
            return true;
        else
            return false;
    }

    public Calendar formatTime(String date){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public int getPmTime(int hour){
        switch(hour){
            case 13:
                return 1;
            case 14:
                return 2;
            case 15:
                return 3;
            case 16:
                return 4;
            case 17:
                return 5;
            case 18:
                return 6;
            case 19:
                return 7;
            case 20:
                return 8;
            case 21:
                return 9;
            case 22:
                return 10;
            case 23:
                return 11;
            case 0:
                return 12;
            default:
                return hour;
        }
    }

    public int pmToNormalTime(int hour, String extra){
        if(extra.equals("pm")) {
            switch (hour) {
                case 1:
                    return 13;
                case 2:
                    return 14;
                case 3:
                    return 15;
                case 4:
                    return 16;
                case 5:
                    return 17;
                case 6:
                    return 18;
                case 7:
                    return 19;
                case 8:
                    return 20;
                case 9:
                    return 21;
                case 10:
                    return 22;
                case 11:
                    return 23;
                default:
                    return hour;
            }
        }else {
            if (hour == 12)
                return 0;
            else
                return hour;
        }
    }

    //Gets remaining time in milliseconds
    public long getRemainingTime(String sdate1, String sdate2){
        date1 = formatTime(sdate1);
        date2 = formatTime(sdate2);

        return date1.getTimeInMillis() - date2.getTimeInMillis();
    }

    //Gets remaining time in milliseconds
    public long getRemainingTime(String initial_time){
        Calendar ends;
        Calendar begins = Calendar.getInstance();
        initial_time = begins.get(Calendar.DAY_OF_MONTH)+"/"+getMonthName(begins.get(Calendar.MONTH))+"/"+begins.get(Calendar.YEAR)+" "+initial_time;
        ends = formatDateANDTimeInPm(initial_time);

        return ends.getTimeInMillis() - begins.getTimeInMillis();
    }

    //Gets remaining time in milliseconds
    public long getRemainingTime(Calendar ending_date){
        Calendar begins = Calendar.getInstance();

        return ending_date.getTimeInMillis() - begins.getTimeInMillis();
    }

    public Calendar formatDateANDTimeInPm(String date){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy KK:mm a", Locale.US);
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public Calendar formatTimeInPm(String date){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("KK:mm a", Locale.US);
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public int getWeekOfYear(String date) {
        Calendar calendar = formatDateANDTimeInPm(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public int getDayOfWeek(String day){
        String array[] = activity.getResources().getStringArray(R.array.days);
        for(int i = 0; i < array.length; i++) {
            if (day.equals(array[i])) {
                if(i == 6)
                    return 1;
                else
                    return i+2;
            }
        }
        return -1;
    }

    public String getDayName(int day){
        String days[] = activity.getResources().getStringArray(R.array.days);
        switch(day){
            case 1:
                return days[6];
            case 2:
                return days[0];
            case 3:
                return days[1];
            case 4:
                return days[2];
            case 5:
                return days[3];
            case 6:
                return days[4];
            case 7:
                return days[5];
            default:
                return days[6];
        }
    }

    public String getMonthAbreviation(String xmonthName) {
        xmonthName = xmonthName.toLowerCase();
        return xmonthName.substring(0,3);
    }
}
