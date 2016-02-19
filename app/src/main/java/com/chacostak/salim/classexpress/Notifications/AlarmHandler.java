package com.chacostak.salim.classexpress.Notifications;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Salim on 06/08/2015.
 */
public class AlarmHandler extends Fragment {

    AlarmManager alarmManager = null;

    public static String TYPE = "TYPE";

    public void setCourseAlarm(Context activity, Calendar calendar, String course, String initial_time, int time) {
        if(alarmManager == null)
            alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(activity, AlarmCourses.class);
        intent.putExtra(AlarmCourses.COURSE, course);
        intent.putExtra(AlarmCourses.INITIAL_TIME, initial_time);
        intent.putExtra(AlarmCourses.ALARM, time);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, AlarmCourses.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); //TODO: Update this so it works with doze mode on android 6.0
    }

    public void setEventAlarm(Context activity, Calendar calendar, String title, String description, String date, String time, char type) {
        if(alarmManager == null)
            alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(activity, AlarmEvents.class);
        intent.putExtra(AlarmEvents.TITLE, title);
        intent.putExtra(AlarmEvents.DESCRIPTION, description);
        intent.putExtra(AlarmEvents.DATE, date);
        intent.putExtra(AlarmEvents.TIME, time);
        intent.putExtra(AlarmEvents.TYPE, type);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, AlarmEvents.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); //TODO: Update this so it works with doze mode on android 6.0
    }

    public boolean courseAlarmIsSet(Context activity) {
        Intent intent = new Intent(activity, AlarmCourses.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, AlarmCourses.REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent != null)
            return true;
        else
            return false;
    }

    public boolean eventsAlarmIsSet(Context activity) {
        Intent intent = new Intent(activity, AlarmEvents.class);
        if(PendingIntent.getBroadcast(activity, AlarmEvents.REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE) != null)
            return true;
        else
            return false;
    }
}
