package com.chacostak.salim.classexpress.Services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.Data_Base.DB_Notifications_Manager;
import com.chacostak.salim.classexpress.R;
import com.chacostak.salim.classexpress.Utilities.DateValidation;
import com.chacostak.salim.classexpress.Utilities.EventData;
import com.chacostak.salim.classexpress.Utilities.SystemManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Salim on 03/05/2015.
 */
public class CountDownService extends Service {

    ArrayList<EventData> data;
    ArrayList<CountDownTimer> countDowns = new ArrayList<>();

    public static final String EVENTS = "EVENTS";
    public static final String TYPE = "TYPE";
    public static final String ID = "ID";
    public static final String REMAINING_TIME = "REMAINING_TIME";
    public static final String UNIT = "UNIT";

    public static final String COUNTDOWN = "CountDownService";

    Intent filter = new Intent(COUNTDOWN);

    public static boolean isActive = false;

    DB_Notifications_Manager notifications_manager;

    @Override
    public void onDestroy(){
        super.onDestroy();
        isActive = false;
        for(int i = 0; i < countDowns.size(); i++)
            countDowns.get(i).cancel();
        countDowns.clear();
        data.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //If it is already active it will skip all the process below
        if(intent == null)
            return super.onStartCommand(intent, flags, startId);
        else
            isActive = true;

        data = intent.getParcelableArrayListExtra(EVENTS);
        notifications_manager = new DB_Notifications_Manager(getApplicationContext(), DB_Helper.DB_Name, DB_Helper.DB_Version);

        for(int i = 0; i < data.size(); i++){
            final int POS = i;
            countDowns.add(new CountDownTimer(data.get(POS).remainingTime, 60000) {

                Calendar calendar = Calendar.getInstance();
                DateValidation dateValidation = new DateValidation();
                SharedPreferences preferences;
                SystemManager systemManager = new SystemManager(getApplicationContext());

                @Override
                public void onTick(long millisUntilFinished) {
                    long time = 0;
                    long oneDay = 86400000;
                    long hourAndHalf = 5400000;
                    String unit = "";
                    if(millisUntilFinished > oneDay){
                        unit = getString(R.string.d);
                        time = getRemainingDays();
                    } else if(millisUntilFinished > hourAndHalf){
                        unit = getString(R.string.h);
                        time = ((millisUntilFinished/1000)/60)/60;
                    } else if(millisUntilFinished <= hourAndHalf){
                        unit = getString(R.string.m);
                        time = (millisUntilFinished/1000)/60;
                    }

                    switch(data.get(POS).type){
                        case 'H':
                            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if(preferences.getBoolean("notifications_homework", false))
                                sendNotification(data.get(POS).name, millisUntilFinished, time);

                            filter.putExtra(TYPE, 'H');
                            filter.putExtra(ID, data.get(POS).name);
                            filter.putExtra(REMAINING_TIME, String.valueOf(time));
                            filter.putExtra(UNIT, unit);
                            sendBroadcast(filter);
                            break;
                        case 'E':
                            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if(preferences.getBoolean("notifications_exams", false))
                                sendNotification(DB_Notifications_Manager.EXAM_TAG, millisUntilFinished, time);

                            filter.putExtra(TYPE, 'E');
                            filter.putExtra(ID, data.get(POS).name);
                            filter.putExtra(REMAINING_TIME, String.valueOf(time));
                            filter.putExtra(UNIT, unit);
                            sendBroadcast(filter);
                            break;
                    }
                }

                @Override
                public void onFinish() {
                    countDowns.remove(this);
                    if(countDowns.isEmpty())
                        stopService(new Intent(getApplicationContext(), CountDownService.class));
                }

                private int getRemainingDays(){
                    Calendar stored_date = dateValidation.formatDateANDTimeInPm(data.get(POS).initial_date +" "+data.get(POS).initial_time);
                    int aux;
                    int remainingDays = stored_date.get(Calendar.DAY_OF_WEEK) - calendar.get(Calendar.DAY_OF_WEEK);
                    if(remainingDays < 0){
                        aux = 7 - calendar.get(Calendar.DAY_OF_WEEK);
                        remainingDays = stored_date.get(Calendar.DAY_OF_WEEK);
                        remainingDays += aux;
                    }

                    return remainingDays;
                }

                private void sendNotification(String tag, long millisUntilFinished, long time) {
                    Cursor sig_cursor = notifications_manager.searchByTag(tag);
                    while(sig_cursor.moveToNext()) {
                        long t = sig_cursor.getInt(sig_cursor.getColumnIndex(notifications_manager.TIME_BEFORE));
                        String u = sig_cursor.getString(sig_cursor.getColumnIndex(notifications_manager.UNIT_TYPE));
                        if(u.equals("d")){
                            t = TimeUnit.MILLISECONDS.convert(t, TimeUnit.DAYS);
                            u = getString(R.string.days_left);
                            if(time == 23)
                                time = 1;
                        }else if(u.equals("h")){
                            t = TimeUnit.MILLISECONDS.convert(t, TimeUnit.HOURS);
                            u = getString(R.string.hours_left);
                        }else if(u.equals("m")) {
                            t = TimeUnit.MILLISECONDS.convert(t, TimeUnit.MINUTES);
                            u = getString(R.string.minutes_left);
                        }
                        Log.d("milliseconds: ", String.valueOf(millisUntilFinished));
                        Log.d("alarm: ", String.valueOf(t));
                        if(millisUntilFinished < t && millisUntilFinished > t-60100)
                            systemManager.createNotification(data.get(POS).name, time+" "+u);
                    }
                }
            });

            countDowns.get(i).start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
