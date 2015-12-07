package com.chacostak.salim.classexpress.Notifications;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Notifications_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.CourseValidation;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;
import com.chacostak.salim.classexpress.Utilities.SystemManager;

import java.util.Calendar;

/**
 * Created by Salim on 08/08/2015.
 */
public class AlarmCourses extends WakefulBroadcastReceiver {

    DB_Notifications_Manager notifications_manager = null;
    DB_Schedule_Manager schedule_manager = null;
    DB_Courses_Manager courses_manager = null;

    DateValidation dateValidation = null;
    CourseValidation courseValidation = new CourseValidation();

    Context activity = null;

    AlarmHandler alarmHandler = null;

    public static final int REQUEST_CODE = 0;

    public static final String COURSE = "COURSE";
    public static final String INITIAL_TIME = "INITIAL_TIME";
    public static final String ALARM = "ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (dateValidation == null)
            dateValidation = new DateValidation(context);
        if (notifications_manager == null)
            notifications_manager = new DB_Notifications_Manager(context, DB_Helper.DB_Name, DB_Helper.DB_Version);
        if (alarmHandler == null)
            alarmHandler = new AlarmHandler();
        if (activity == null)
            activity = context;
        if (schedule_manager == null)
            schedule_manager = new DB_Schedule_Manager(context, DB_Helper.DB_Name, DB_Helper.DB_Version);
        if (courses_manager == null)
            courses_manager = new DB_Courses_Manager(context, DB_Helper.DB_Name, DB_Helper.DB_Version);

        String course = intent.getStringExtra(COURSE);
        String initial_time = intent.getStringExtra(INITIAL_TIME);
        int alarm = intent.getIntExtra(ALARM, 0);

        if (course != null) {
            Cursor cursor = courses_manager.searchByName(course);
            cursor.moveToNext();
            if (courseValidation.stillInCourse(activity, cursor.getString(cursor.getColumnIndex(courses_manager.START)), cursor.getString(cursor.getColumnIndex(courses_manager.END)))) {
                new SystemManager(context).createNotification(course, alarm + " " + context.getString(R.string.minutes_left));
            }
        }

        setNextAlarm(alarm, course, initial_time);
    }

    private void setNextAlarm(int lastAlarm, String lastCourse, String last_initial_time) {
        int nextAlarm = getNextAlarm(lastAlarm);
        EventData data;

        if (nextAlarm == 0) { //If it is equals to 0 then there are no more alarms for that signature
            nextAlarm = getFirstAlarm();
            data = getNextCourseData(nextAlarm);
        } else {
            data = getCourseData(lastCourse, last_initial_time); //If this line executes, you already have the data needed
        }

        if (data == null) {  //If null, then there are no more signatures that day
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            alarmHandler.setCourseAlarm(activity, cal, null, null, 0);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, dateValidation.pmToNormalTime(Integer.parseInt(data.initial_time.split(":")[0]), data.initial_time.split(" ")[1]));
            cal.set(Calendar.MINUTE, Integer.parseInt(data.initial_time.split(":")[1].split(" ")[0]));
            cal.add(Calendar.MINUTE, -nextAlarm);

            if (data.flag) //If it is in the next day...
                cal.add(Calendar.DATE, 1);
            alarmHandler.setCourseAlarm(activity, cal, data.name, data.initial_time, nextAlarm);
        }
    }

    //Just gets the parameters and stores them in an EventData object
    private EventData getCourseData(String lastCourse, String last_initial_time) {
        EventData data = new EventData();
        data.initial_time = last_initial_time;
        data.name = lastCourse;
        data.flag = false;

        return data;
    }

    public EventData getNextCourseData(int lastAlarm) {
        EventData data = getTodaysNextCourseData(lastAlarm);
        if (data == null)
            data = getTomorrowsFirstCourseData(lastAlarm);

        return data;
    }

    public int getFirstAlarm() {
        int nextAlarm = 0;
        Cursor notifications_cursor = notifications_manager.searchByTag(DB_Notifications_Manager.COURSE_TAG);
        while (notifications_cursor.moveToNext()) {
            int aux = notifications_cursor.getInt(notifications_cursor.getColumnIndex(notifications_manager.TIME_BEFORE));
            if (aux > nextAlarm)
                nextAlarm = aux;
        }
        notifications_cursor.close();
        return nextAlarm;
    }

    public int getNextAlarm(int lastAlarm) {
        int nextAlarm = 0;
        Cursor notifications_cursor = notifications_manager.searchByTag(DB_Notifications_Manager.COURSE_TAG);
        while (notifications_cursor.moveToNext()) {
            int aux = notifications_cursor.getInt(notifications_cursor.getColumnIndex(notifications_manager.TIME_BEFORE));
            if (lastAlarm > aux) {
                if (aux > nextAlarm)
                    nextAlarm = aux;
            }
        }
        notifications_cursor.close();
        return nextAlarm;
    }

    private EventData getTodaysNextCourseData(int lastAlarm) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = (Calendar) c1.clone();
        String dayOfWeek = dateValidation.getDayName(c2.get(Calendar.DAY_OF_WEEK));
        EventData nextData = getNextData(dayOfWeek, c1, c2, lastAlarm);

        return nextData;
    }

    private EventData getTomorrowsFirstCourseData(int lastAlarm) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = (Calendar) c1.clone();
        c2.add(Calendar.DATE, 1);
        String dayOfWeek = dateValidation.getDayName(c2.get(Calendar.DAY_OF_WEEK));
        EventData nextData = getNextData(dayOfWeek, c1, c2, lastAlarm);

        return nextData;
    }

    private EventData getNextData(String dayOfWeek, Calendar c1, Calendar c2, int lastAlarm) {
        EventData data = null;
        String course;
        String initial_time;
        String initialTimeArray[];
        Cursor cursor = schedule_manager.searchByDay(dayOfWeek);
        long remainingTime = Long.MAX_VALUE; //Initialized to its max possible value

        while (cursor.moveToNext()) { //Search for the next course based on the remaining time for that course
            long aux;
            initial_time = cursor.getString(cursor.getColumnIndex(schedule_manager.START));

            initialTimeArray = initial_time.split(":");

            c2.set(Calendar.HOUR_OF_DAY, dateValidation.pmToNormalTime(Integer.parseInt(initialTimeArray[0]), initialTimeArray[1].split(" ")[1]));
            c2.set(Calendar.MINUTE, Integer.parseInt(initialTimeArray[1].split(" ")[0]));
            c2.add(Calendar.MINUTE, -lastAlarm);
            if (c2.after(c1)) {
                aux = c2.getTimeInMillis() - c1.getTimeInMillis();
                if (aux < remainingTime) {
                    remainingTime = aux;
                    course = cursor.getString(cursor.getColumnIndex(schedule_manager.COURSE));
                    data = new EventData();
                    data.name = course;
                    data.initial_time = initial_time;
                    if (c1.get(Calendar.DAY_OF_WEEK) == c2.get(Calendar.DAY_OF_WEEK))
                        data.flag = false;
                    else
                        data.flag = true;
                }
            }
        }

        return data;
    }
}