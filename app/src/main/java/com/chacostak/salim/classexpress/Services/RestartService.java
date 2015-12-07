package com.chacostak.salim.classexpress.Services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.chacostak.salim.classexpress.Data_Base.DB_Courses_Manager;
import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Schedule_Manager;
import com.chacostak.salim.classexpress.Fragment_home;
import com.chacostak.salim.classexpress.Utilities.CourseValidation;
import com.chacostak.salim.classexpress.Utilities.DateValidation;

import java.util.Calendar;

/**
 * Created by Salim on 09/06/2015.
 */
public class RestartService extends Service {

    public static boolean isActive = false;
    CountDownTimer countDownTimer;

    DateValidation dateValidation;
    Calendar calendar = Calendar.getInstance();
    DB_Schedule_Manager schedule_manager;
    DB_Courses_Manager signatures_manager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        long remainingTime = Long.MAX_VALUE;
        long oneDay = 86400000;
        isActive = true;
        dateValidation = new DateValidation(this);
        schedule_manager = new DB_Schedule_Manager(this, DB_Helper.DB_Name, DB_Helper.DB_Version);
        signatures_manager = new DB_Courses_Manager(this, DB_Helper.DB_Name, DB_Helper.DB_Version);

        countDownTimer = new CountDownTimer(remainingTime, oneDay){

            @Override
            public void onTick(long millisUntilFinished) {
                checkNextDay();
            }

            @Override
            public void onFinish() {

            }
        };

        checkNextDay();
        return super.onStartCommand(intent, flags, startId);
    }

    public void checkNextDay(){
        int next_day = calendar.get(Calendar.DAY_OF_WEEK)+1;
        String day_name = dateValidation.getDayName(next_day);
        Cursor cursor = schedule_manager.searchByDay(day_name);
        while(cursor.moveToNext()){
            String signature = cursor.getString(cursor.getColumnIndex(schedule_manager.COURSE));
            Cursor sig_cursor = signatures_manager.searchByName(signature);
            sig_cursor.moveToNext();
            if(new CourseValidation().stillInCourse(this, sig_cursor.getString(sig_cursor.getColumnIndex(signatures_manager.START)), sig_cursor.getString(sig_cursor.getColumnIndex(signatures_manager.END)))) {
                Fragment_home.serviceManager.updateSignatureServiceData(day_name);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActive = false;
        countDownTimer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
