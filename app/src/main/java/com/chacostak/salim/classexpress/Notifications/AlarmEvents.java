package com.chacostak.salim.classexpress.Notifications;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.chacostak.salim.classexpress.Data_Base.DB_Calendar_Notifications_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;
import com.chacostak.salim.classexpress.Utilities.SystemManager;

import java.util.Calendar;

/**
 * Created by Salim on 22/08/2015.
 */
public class AlarmEvents extends WakefulBroadcastReceiver {

    public static final int REQUEST_CODE = -2;

    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String DATE = "DATE";
    public static final String TIME = "ALARM";
    public static final String TYPE = "TYPE";

    Calendar calendar = null, nextAlarmCalendar;

    Context context = null;

    SystemManager systemManager = null;
    AlarmHandler alarmHandler = null;
    DateValidation dateValidation = null;
    DB_Calendar_Notifications_Manager calendar_manager = null;
    static DB_Homework_Manager homework_manager = null;
    static DB_Exams_Manager exam_manager = null;


    @Override
    public void onReceive(Context xcontext, Intent intent) {
        if(systemManager == null)
            systemManager = new SystemManager(xcontext);
        if(calendar_manager == null)
            calendar_manager = new DB_Calendar_Notifications_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
        if(homework_manager == null)
            homework_manager = new DB_Homework_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
        if(exam_manager == null)
            exam_manager = new DB_Exams_Manager(xcontext, DB_Helper.DB_Name, DB_Helper.DB_Version);
        if(context == null)
            context = xcontext;
        if(alarmHandler == null)
            alarmHandler = new AlarmHandler();
        if(dateValidation == null)
            dateValidation = new DateValidation(xcontext);
        if(calendar == null)
            calendar = Calendar.getInstance();

        EventData data;

        String title = intent.getStringExtra(TITLE);
        String description = intent.getStringExtra(DESCRIPTION);
        String date = intent.getStringExtra(DATE);
        String time = intent.getStringExtra(TIME);
        char type = intent.getCharExtra(TYPE, 'X');

        if(type == 'H' && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifications_homework", true)) {
            systemManager.createNotification(title, description);
            calendar_manager.delete(title, date, time, DB_Calendar_Notifications_Manager.HOMEWORK);
        }else if(type == 'E' && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifications_exams", true)) {
            systemManager.createNotification(title, description);
            calendar_manager.delete(date, time, DB_Calendar_Notifications_Manager.EXAM);
        }
        data = getNextAlarmData();
        if(data != null)
            alarmHandler.setEventAlarm(xcontext, nextAlarmCalendar, data.name, data.description, data.initial_date, data.initial_time, data.type);
    }

    public EventData getNextAlarmData() {
        Calendar c1 = null;
        Calendar c2 = Calendar.getInstance();;
        EventData nextData = null;
        boolean flag = false;
        Cursor cursor = calendar_manager.getAll();
        String tag;
        String date;
        String time;
        String type;
        String dateArray[], timeArray[];
        while(cursor.moveToNext()){
            tag = cursor.getString(cursor.getColumnIndex(calendar_manager.TAG));
            date = cursor.getString(cursor.getColumnIndex(calendar_manager.DATE));
            time = cursor.getString(cursor.getColumnIndex(calendar_manager.TIME));
            type = cursor.getString(cursor.getColumnIndex(calendar_manager.TYPE));

            dateArray = date.split("/");
            timeArray = time.split(":");

            if(c1 == null) {    //First iteration
                c1 = Calendar.getInstance();
                c1.set(Integer.parseInt(dateArray[2]), dateValidation.getMonthInt(dateArray[1]), Integer.parseInt(dateArray[0]),
                        dateValidation.pmToNormalTime(Integer.parseInt(timeArray[0]), timeArray[1].split(" ")[1]), Integer.parseInt(timeArray[1].split(" ")[0]));
                nextData = getData(type, context, tag);
                flag = true;
            }else {   //Second iteration
                c2.set(Integer.parseInt(dateArray[2]), dateValidation.getMonthInt(dateArray[1]), Integer.parseInt(dateArray[0]),
                        dateValidation.pmToNormalTime(Integer.parseInt(timeArray[0]), timeArray[1].split(" ")[1]), Integer.parseInt(timeArray[1].split(" ")[0]));
                if (calendar.before(c2) && c2.before(c1)) {
                    c1 = (Calendar) c2.clone();
                    nextData = getData(type, context, tag);
                    flag = true;
                }else
                    flag = false;
            }

            if(nextData != null && flag) {
                nextData.initial_date = date;
                nextData.initial_time = time;
            }
        }

        nextAlarmCalendar = c1;

        return nextData;
    }

    public static EventData getData(String type, Context context, String tag) {
        if(type.equals(DB_Calendar_Notifications_Manager.HOMEWORK))
            return getHomeworkData(context, tag);
        else if(type.equals(DB_Calendar_Notifications_Manager.EXAM))
            return getExamData(context, tag.split(" - ")[0], tag.split(" - ")[1]);
        else
            return null;
    }

    public static EventData getHomeworkData(Context context, String title) {
        if(homework_manager == null)
            homework_manager = new DB_Homework_Manager(context, DB_Helper.DB_Name, DB_Helper.DB_Version);

        EventData data = null;
        Cursor cursor = homework_manager.searchByTitle(title);
        if(cursor.moveToNext()) {
            data = new EventData();
            data.name = cursor.getString(cursor.getColumnIndex(homework_manager.TITLE));
            data.description = cursor.getString(cursor.getColumnIndex(homework_manager.DESCRIPTION));
            data.type = 'H';
        }

        return data;
    }

    public static EventData getExamData(Context context, String date, String time) {
        if(exam_manager == null)
            exam_manager = new DB_Exams_Manager(context, DB_Helper.DB_Name, DB_Helper.DB_Version);

        EventData data = null;
        Cursor cursor = exam_manager.search(date, time);
        if(cursor.moveToNext()) {
            data = new EventData();
            data.name = cursor.getString(cursor.getColumnIndex(exam_manager.COURSE));
            data.description = cursor.getString(cursor.getColumnIndex(exam_manager.DAY_LIMIT)) + " - " + cursor.getString(cursor.getColumnIndex(exam_manager.TIME_LIMIT));
            data.type = 'E';
        }

        return data;
    }
}