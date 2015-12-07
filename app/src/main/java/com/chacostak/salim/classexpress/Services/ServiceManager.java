package com.chacostak.salim.classexpress.Services;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.chacostak.salim.classexpress.Data_Base.DB_Exams_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Homework_Manager;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salim on 14/05/2015.
 */
public class ServiceManager {

    Context activity;
    ArrayList<EventData> countDownServiceData;
    ArrayList<EventData> signatureServiceData;

    DateValidation dateValidation;

    public ServiceManager(Context xactivity) {
        activity = xactivity;
        countDownServiceData = new ArrayList<>();
        signatureServiceData = new ArrayList<>();
        dateValidation = new DateValidation(activity);
    }

    public void addToCountDownService(EventData data) {
        countDownServiceData.add(data);
    }

    public void addToSignatureService(EventData data) {
        signatureServiceData.add(data);
    }

    public void startCountDownService() {
        if (!CountDownService.isActive && !countDownServiceData.isEmpty()) {
            Intent intent = new Intent(activity, CountDownService.class);
            intent.putParcelableArrayListExtra(CountDownService.EVENTS, countDownServiceData);
            activity.startService(intent);
        }
    }

    public void stopCountDownService() {
        if (CountDownService.isActive) {
            activity.stopService(new Intent(activity, CountDownService.class));
            CountDownService.isActive = false; //Is set to false just in case
        }
    }

    public void restartCountDownService() {
        stopCountDownService();
        startCountDownService();
    }

    public void updateCountDownServiceData() {
        countDownServiceData.clear();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                storeHomeworks();
            }
        });
        thread1.start();
        storeExams();

        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        restartCountDownService();
    }

    private void storeHomeworks() {
        DB_Homework_Manager homework_manager = new DB_Homework_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        Cursor hw_cursor = homework_manager.getAllDate_Title_Time();
        while (hw_cursor.moveToNext()) {
            EventData data = new EventData();
            data.name = hw_cursor.getString(hw_cursor.getColumnIndex(homework_manager.TITLE));
            data.initial_date = hw_cursor.getString(hw_cursor.getColumnIndex(homework_manager.DAY_LIMIT));
            data.initial_time = hw_cursor.getString(hw_cursor.getColumnIndex(homework_manager.TIME_LIMIT));
            data.remainingTime = dateValidation.getRemainingTime(dateValidation.formatDateANDTimeInPm(data.initial_date + " " + data.initial_time));
            data.type = 'H';
            if(data.remainingTime > 0)
                countDownServiceData.add(data);
        }
    }

    private void storeExams() {
        DB_Exams_Manager exams_manager = new DB_Exams_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        Cursor exam_cursor = exams_manager.getAllDateAndTime();
        while (exam_cursor.moveToNext()) {
            EventData data = new EventData();
            data.initial_date = exam_cursor.getString(exam_cursor.getColumnIndex(exams_manager.DAY_LIMIT));
            data.initial_time = exam_cursor.getString(exam_cursor.getColumnIndex(exams_manager.TIME_LIMIT));
            data.name = data.initial_date + " - " + data.initial_time;
            data.remainingTime = dateValidation.getRemainingTime(dateValidation.formatDateANDTimeInPm(data.initial_date + " " + data.initial_time));
            data.type = 'E';
            if(data.remainingTime > 0)
                countDownServiceData.add(data);
        }
    }


    public void startSignatureService() {
        if (!CourseService.isActive && !signatureServiceData.isEmpty()) {
            Intent intent = new Intent(activity, CourseService.class);
            intent.putExtra(CourseService.EVENTS, signatureServiceData);
            activity.startService(intent);
        }else if(!RestartService.isActive && !CourseService.isActive)
            activity.startService(new Intent(activity, RestartService.class));
    }

    public void stopSignatureService() {
        if (CourseService.isActive) {
            activity.stopService(new Intent(activity, CourseService.class));
            CourseService.isActive = false; //Is set to false just in case
        }else if(RestartService.isActive){
            activity.stopService(new Intent(activity, RestartService.class));
            RestartService.isActive = false;
        }
    }

    public void restartSignatureService() {
        stopSignatureService();
        startSignatureService();
    }

    //TODO: Decide if this stays
    public void updateSignatureServiceData(String target_day) {
        signatureServiceData.clear();
        //TODO: Decide if this stays - storeSignatures(target_day);
        //TODO: Decide if this stays - restartSignatureService();
    }

    /*
    private void storeSignatures(String target_day) {
        DB_Schedule_Manager schedule_manager = new DB_Schedule_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        DB_Courses_Manager sig_manager = new DB_Courses_Manager(activity, DB_Helper.DB_Name, DB_Helper.DB_Version);
        Cursor schedule_cursor = schedule_manager.searchByDay(target_day);
        CourseValidation signatureValidation = new CourseValidation();
        String signature;
        while (schedule_cursor.moveToNext()) {
            signature = schedule_cursor.getString(schedule_cursor.getColumnIndex(schedule_manager.COURSE));
            Cursor sig_cursor = sig_manager.searchByName(signature);
            sig_cursor.moveToNext();
            if(signatureValidation.stillInCourse(sig_cursor.getString(sig_cursor.getColumnIndex(sig_manager.START)), sig_cursor.getString(sig_cursor.getColumnIndex(sig_manager.END)))) {
                EventData data = new EventData();
                data.name = schedule_cursor.getString(schedule_cursor.getColumnIndex(schedule_manager.DAY_OF_WEEK)) + " " +
                        schedule_cursor.getString(schedule_cursor.getColumnIndex(schedule_manager.START)) + " " +
                        schedule_cursor.getString(schedule_cursor.getColumnIndex(schedule_manager.END));
                data.initial_time = schedule_cursor.getString(schedule_cursor.getColumnIndex(schedule_manager.START));
                data.remainingTime = getRemainingTime(data.initial_time, target_day);
                data.type = 'S';
                if(data.remainingTime > 0)
                    signatureServiceData.add(data);
            }
        }
    }*/

    private long getRemainingTime(String initial_time, String target_day){
        Calendar nextDay = Calendar.getInstance();
        if(nextDay.get(Calendar.DAY_OF_WEEK) == dateValidation.getDayOfWeek(target_day)) //Hoping every time it is different, it is meant to be the next day
            return dateValidation.getRemainingTime(initial_time);
        else {
            nextDay.add(Calendar.DATE, 1);
            nextDay.set(Calendar.HOUR, dateValidation.pmToNormalTime(Integer.parseInt(initial_time.split(":")[0]), initial_time.split(" ")[1])); //Sometimes this changes the day of month, can cause ERRORS
            nextDay.set(Calendar.MINUTE, Integer.parseInt(initial_time.split(":")[1].split(" ")[0]));
            return dateValidation.getRemainingTime(nextDay);
        }
    }
}
